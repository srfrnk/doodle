package london_police;

import java.io.IOException;
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
import java.util.stream.Collectors;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.WebClient;
import common.WebClient.WebResponseException;

public class ReadNeighbourhoods
        extends PTransform<PCollection<ForceResponse>, PCollection<Neighbourhood>> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReadNeighbourhoods.class);
    private String apiPoliceUrl;

    public ReadNeighbourhoods(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<Neighbourhood> expand(PCollection<ForceResponse> input) {
        return input
                .apply("Read Neighbourhoods",
                        ParDo.of(new ReadNeighbourhoodsDoFn(this.apiPoliceUrl)))
                .setCoder(KvCoder.of(AvroCoder.of(TypeDescriptors.integers()),
                        AvroCoder.of(Neighbourhood[].class)))
                .apply(GroupByKey.create()).apply(Values.create())
                .apply(ParDo.of(new ArrayExpander<Neighbourhood>()))
                .setCoder(SerializableCoder.of(Neighbourhood.class));
    }

    private static class ReadNeighbourhoodsDoFn extends ArrayBundler<ForceResponse, Neighbourhood> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadNeighbourhoodsDoFn(String apiPoliceUrl) {
            super(10);
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @Override
        public Neighbourhood[] getDataArray(ForceResponse force) {
            try {
                LOG.info(String.format("Reading: %s", force.name));
                NeighbourhoodResponse[] neighbourhoodRespones =
                        ApiReader.getJson(
                                String.format("%s/%s/neighbourhoods", this.apiPoliceUrl,
                                        WebClient.urlEncode(force.id)),
                                NeighbourhoodResponse[].class);
                Neighbourhood[] neighbourhoods =
                        Arrays.asList(neighbourhoodRespones).stream().map(neighbourhoodResponse -> {
                            Neighbourhood neighbourhood = new Neighbourhood();
                            neighbourhood.id = neighbourhoodResponse.id;
                            neighbourhood.name = neighbourhoodResponse.name;
                            neighbourhood.force = force;
                            return neighbourhood;
                        }).collect(Collectors.toList()).toArray(new Neighbourhood[0]);
                return neighbourhoods;
            } catch (WebResponseException | IOException | InterruptedException e) {
                LOG.error("Reading", e);
                return new Neighbourhood[0];
            }
        }
    }

    private static class ArrayExpander<T> extends DoFn<Iterable<T[]>, T> {
        private static final long serialVersionUID = 1L;

        @ProcessElement
        public void processElement(@Element Iterable<T[]> input, OutputReceiver<T> output) {
            input.forEach(bundle -> {
                for (T item : bundle) {
                    output.output(item);
                }
            });
        }
    }

    private static abstract class ArrayBundler<I, O> extends DoFn<I, KV<Integer, O[]>> {
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

    private static class BundleCollector<T>
            implements java.util.stream.Collector<T, BundleContainer<T>, List<T[]>> {
        private static HashSet<Characteristics> characteristics = new HashSet<Characteristics>();

        static {
            BundleCollector.characteristics.add(Characteristics.UNORDERED);
        }

        private int bundleSize;

        public BundleCollector(int bundleSize) {
            this.bundleSize = bundleSize;
        }

        @Override
        public BiConsumer<BundleContainer<T>, T> accumulator() {
            return (container, item) -> {
                container.addItem(item);
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return BundleCollector.characteristics;
        }

        @Override
        public BinaryOperator<BundleContainer<T>> combiner() {
            return (container1, container2) -> {
                container1.merge(container2);
                return container1;
            };
        }

        @Override
        public Function<BundleContainer<T>, List<T[]>> finisher() {
            return (container) -> {
                return container.getAllBundles();
            };
        }

        @Override
        public Supplier<BundleContainer<T>> supplier() {
            return () -> new BundleContainer<T>(this.bundleSize);
        }
    }

    private static class BundleContainer<T> {
        private int bundleSize;

        private ArrayList<T[]> fullBundles = new ArrayList<T[]>();
        private ArrayList<T> currentBundle = new ArrayList<T>();

        public BundleContainer(int bundleSize) {
            this.bundleSize = bundleSize;
        }

        public void addItem(T item) {
            currentBundle.add(item);
            if (currentBundle.size() == bundleSize) {
                this.newBundle();
            }
        }

        @SuppressWarnings("unchecked")
        private void newBundle() {
            Class<T> clazz = (Class<T>) currentBundle.get(0).getClass();
            T[] newBundle = currentBundle.toArray((T[]) Array.newInstance(clazz, 0));
            this.fullBundles.add(newBundle);
            currentBundle = new ArrayList<T>();
        }

        public void merge(BundleContainer<T> container) {
            this.fullBundles.addAll(container.fullBundles);
            for (T item : container.currentBundle) {
                this.addItem(item);
            }
        }

        public List<T[]> getAllBundles() {
            if (currentBundle.size() > 0) {
                this.newBundle();
            }
            return this.fullBundles;
        }
    }
}
