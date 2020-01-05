package common;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.options.PipelineOptions;

public abstract class ReadAllAndSplitSource<T extends Serializable> extends BoundedSource<T> {
    private static final long serialVersionUID = 1L;
    private int splitSize;

    public ReadAllAndSplitSource(int splitSize) {
        this.splitSize = splitSize;
    }

    @Override
    public List<? extends BoundedSource<T>> split(long desiredBundleSizeBytes,
            PipelineOptions options) throws Exception {
        ArrayList<SplitSource<T>> splits = new ArrayList<>();
        int from = 0;
        T[] dataArray = getDataArray(options);
        while (from < dataArray.length) {
            int to = Math.min(dataArray.length, from + this.splitSize);
            SplitSource<T> source = new SplitSource<>(dataArray, from, to);
            splits.add(source);
            from = to;
        }
        return splits;
    }

    public abstract T[] getDataArray(PipelineOptions options);

    @Override
    public BoundedReader<T> createReader(PipelineOptions options) throws IOException {
        return null;
    }

    private static class SplitSource<T> extends BoundedSource<T> {
        private static final long serialVersionUID = 1L;
        private SplitReader<T> reader;

        public SplitSource(T[] dataArray, int from, int to) {
            this.reader = new SplitReader<>(this, dataArray, from, to);
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

    private static class SplitReader<T> extends BoundedReader<T> {
        private SplitSource<T> source;
        private T[] dataArray;
        private int from;
        private int to;
        private int current;

        public SplitReader(SplitSource<T> source, T[] dataArray, int from, int to) {
            this.source = source;
            this.dataArray = dataArray;
            this.from = from;
            this.to = to;
        }

        @Override
        public BoundedSource<T> getCurrentSource() {
            return this.source;
        }

        @Override
        public boolean start() throws IOException {
            this.current = from;
            return true;
        }

        @Override
        public boolean advance() throws IOException {
            this.current++;
            return this.current < this.to;
        }

        @Override
        public T getCurrent() throws NoSuchElementException {
            return this.dataArray[this.current];
        }

        @Override
        public void close() throws IOException {
            this.current = 0;
            this.dataArray = null;
        }
    }
}
