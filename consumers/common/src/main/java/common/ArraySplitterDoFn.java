package common;

import java.io.Serializable;
import org.apache.beam.sdk.io.range.OffsetRange;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.splittabledofn.RestrictionTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArraySplitterDoFn<T extends Serializable> extends DoFn<T[], T> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ArraySplitterDoFn.class);

    public ArraySplitterDoFn() {
    }

    @ProcessElement
    public void processElement(@Element T[] array, OutputReceiver<T> output,
            RestrictionTracker<OffsetRange, Long> tracker) {
        int numSplits = getNumSplits(array);
        long from = tracker.currentRestriction().getFrom();
        long to = tracker.currentRestriction().getTo();
        LOG.info(String.format("Process %d", from));
        for (long i = from; i < to; i++) {
            tracker.tryClaim(i);
            for (int j = (int) i; j < array.length; j += numSplits) {
                output.output(array[j]);
            }
        }
    }

    private int getNumSplits(T[] array) {
        return (int) Math.min(100, Math.max(1, (Math.log10(array.length) * 10)));
    }

    @GetInitialRestriction
    public OffsetRange getInitialRestriction(T[] array) {
        int numSplits = getNumSplits(array);
        LOG.info(String.format("Splitting to %d", numSplits));
        return new OffsetRange(0, numSplits);
    }

    @SplitRestriction
    public void splitRestriction(T[] array, OffsetRange restriction,
            OutputReceiver<OffsetRange> receiver) {
        int numSplits = getNumSplits(array);
        for (long i = 0; i < numSplits; i++) {
            receiver.output(new OffsetRange(i, i + 1));
        }
        LOG.info("Splits Done");
    }
}
