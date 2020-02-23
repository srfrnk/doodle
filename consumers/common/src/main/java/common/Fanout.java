package common;

import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeDescriptors;

public class Fanout<T> extends PTransform<PCollection<T>, PCollection<T>> {
    private static final long serialVersionUID = 1L;
    private Class<T> tClazz;

    public Fanout(Class<T> tClazz) {
        this.tClazz = tClazz;
    }

    @Override
    public PCollection<T> expand(PCollection<T> input) {
        return input
                .apply(MapElements.into(TypeDescriptors.kvs(TypeDescriptors.integers(),
                        TypeDescriptor.of(this.tClazz))).via((c) -> KV.of(c.hashCode(), c)))
                .apply(GroupByKey.create()).apply(Values.create()).apply(Flatten.iterables());
    }
}
