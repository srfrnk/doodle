package common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.options.PipelineOptions;

public abstract class SplitReadersSource<T, S> extends BoundedSource<T> {
    private static final long serialVersionUID = 1L;

    public SplitReadersSource() {
    }

    @Override
    public List<? extends BoundedSource<T>> split(long desiredBundleSizeBytes,
            PipelineOptions options) throws Exception {
        ArrayList<SplitSource<T, S>> splits = new ArrayList<>();
        S[] splitsData = getSplitArray(options);
        for (S splitData : splitsData) {
            SplitSource<T, S> source = new SplitSource<T, S>(splitData, (_splitData) -> {
                return this.readSplit(_splitData);
            });
            splits.add(source);
        }
        return splits;
    }

    public abstract S[] getSplitArray(PipelineOptions options);

    public abstract T[] readSplit(S splitData);

    @Override
    public abstract Coder<T> getOutputCoder();

    @Override
    public BoundedReader<T> createReader(PipelineOptions options) throws IOException {
        return null;
    }

    private static class SplitSource<T, S> extends BoundedSource<T> {
        private static final long serialVersionUID = 1L;
        private SplitReader<T, S> reader;

        public SplitSource(S splitData, Function<S, T[]> readSplit) {
            this.reader = new SplitReader<>(this, splitData, readSplit);
        }

        @Override
        public List<? extends BoundedSource<T>> split(long desiredBundleSizeBytes,
                PipelineOptions options) throws Exception {
            return null;
        }

        @Override
        public long getEstimatedSizeBytes(PipelineOptions options) throws Exception {
            return 0L;
        }

        @Override
        public BoundedReader<T> createReader(PipelineOptions options) throws IOException {
            return this.reader;
        }
    }

    private static class SplitReader<T, S> extends BoundedReader<T> {
        private SplitSource<T, S> source;
        private S splitData;
        private T[] data;
        private int current;
        private Function<S, T[]> readSplit;

        public SplitReader(SplitSource<T, S> source, S splitData, Function<S, T[]> readSplit) {
            this.source = source;
            this.splitData = splitData;
            this.readSplit = readSplit;
        }

        @Override
        public BoundedSource<T> getCurrentSource() {
            return this.source;
        }

        @Override
        public boolean start() throws IOException {
            this.data = this.readSplit.apply(this.splitData);
            this.current = 0;
            return this.data != null && this.data.length > 0;
        }

        @Override
        public boolean advance() throws IOException {
            this.current++;
            return this.current < this.data.length;
        }

        @Override
        public T getCurrent() throws NoSuchElementException {
            return this.data[this.current];
        }

        @Override
        public void close() throws IOException {
            this.current = 0;
            this.data = null;
        }
    }
}
