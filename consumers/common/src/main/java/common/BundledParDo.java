package common;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;

public class BundledParDo<I, O extends Serializable>
        extends PTransform<PCollection<I>, PCollection<O>> {
    private static final long serialVersionUID = 1L;
    Class<O[]> clazzOArray;
    Class<O> clazzO;
    ArrayBundler<I, O> bundler;

    @SuppressWarnings("unchecked")
    public BundledParDo(ArrayBundler<I, O> bundler, O[] arrayO) {
        this.bundler = bundler;
        this.clazzOArray = (Class<O[]>) arrayO.getClass();
        this.clazzO = (Class<O>) this.clazzOArray.getComponentType();
    }

    @Override
    public PCollection<O> expand(PCollection<I> input) {
        return input.apply(ParDo.of(this.bundler))
                .setCoder(KvCoder.of(AvroCoder.of(TypeDescriptors.integers()),
                        AvroCoder.of(this.clazzOArray)))
                .apply(GroupByKey.create()).apply(Values.create())
                .apply(ParDo.of(new ArrayExpander<O>()))
                .setCoder(SerializableCoder.of(this.clazzO));
    }

    public static abstract class ArrayBundler<I, O> extends DoFn<I, KV<Integer, O[]>> {
        private static final long serialVersionUID = 1L;
        private int bundleSize;

        public ArrayBundler(int bundleSize) {
            this.bundleSize = bundleSize;
        }

        @ProcessElement
        public void processElement(@Element I input, OutputReceiver<KV<Integer, O[]>> output) {
            O[] dataArray = this.getDataArray(input);
            List<O[]> bundles =
                    Arrays.asList(dataArray).stream().collect(new BundleCollector<O>(bundleSize));
            int key = 0;
            for (O[] bundle : bundles) {
                output.output(KV.of(key++, bundle));
            }
        }

        public abstract O[] getDataArray(I input);
    }

    private static class ArrayExpander<O> extends DoFn<Iterable<O[]>, O> {
        private static final long serialVersionUID = 1L;

        @ProcessElement
        public void processElement(@Element Iterable<O[]> input, OutputReceiver<O> output) {
            input.forEach(bundle -> {
                for (O item : bundle) {
                    output.output(item);
                }
            });
        }
    }

    private static class BundleCollector<O>
            implements java.util.stream.Collector<O, BundleContainer<O>, List<O[]>> {
        private static HashSet<Characteristics> characteristics = new HashSet<Characteristics>();

        static {
            BundleCollector.characteristics.add(Characteristics.UNORDERED);
        }

        private int bundleSize;

        public BundleCollector(int bundleSize) {
            this.bundleSize = bundleSize;
        }

        @Override
        public BiConsumer<BundleContainer<O>, O> accumulator() {
            return (container, item) -> {
                container.addItem(item);
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return BundleCollector.characteristics;
        }

        @Override
        public BinaryOperator<BundleContainer<O>> combiner() {
            return (container1, container2) -> {
                container1.merge(container2);
                return container1;
            };
        }

        @Override
        public Function<BundleContainer<O>, List<O[]>> finisher() {
            return (container) -> {
                return container.getAllBundles();
            };
        }

        @Override
        public Supplier<BundleContainer<O>> supplier() {
            return () -> new BundleContainer<O>(this.bundleSize);
        }
    }

    private static class BundleContainer<O> {
        private int bundleSize;

        private ArrayList<O[]> fullBundles = new ArrayList<O[]>();
        private ArrayList<O> currentBundle = new ArrayList<O>();

        public BundleContainer(int bundleSize) {
            this.bundleSize = bundleSize;
        }

        public void addItem(O item) {
            currentBundle.add(item);
            if (currentBundle.size() == bundleSize) {
                this.newBundle();
            }
        }

        @SuppressWarnings("unchecked")
        private void newBundle() {
            Class<O> clazz = (Class<O>) currentBundle.get(0).getClass();
            O[] newBundle = currentBundle.toArray((O[]) Array.newInstance(clazz, 0));
            this.fullBundles.add(newBundle);
            currentBundle = new ArrayList<O>();
        }

        public void merge(BundleContainer<O> container) {
            this.fullBundles.addAll(container.fullBundles);
            for (O item : container.currentBundle) {
                this.addItem(item);
            }
        }

        public List<O[]> getAllBundles() {
            if (currentBundle.size() > 0) {
                this.newBundle();
            }
            return this.fullBundles;
        }
    }
}
