package common;

import java.io.Serializable;
import org.apache.beam.sdk.io.range.OffsetRange;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.splittabledofn.RestrictionTracker;

public class ArraySplitterDoFn<T extends Serializable> extends DoFn<T[], T> {
    private static final long serialVersionUID = 1L;

    @ProcessElement
    public void processElement(@Element T[] array, OutputReceiver<T> output,
            RestrictionTracker<OffsetRange, Long> tracker) {
        long from = tracker.currentRestriction().getFrom();
        long to = tracker.currentRestriction().getTo();
        for (long i = from; i < to; i++) {
            tracker.tryClaim(i);
            output.output(array[(int) i]);
        }
    }

    @GetInitialRestriction
    public OffsetRange getInitialRestriction(T[] array) {
        return new OffsetRange(0, array.length);
    }

    @SplitRestriction
    public void splitRestriction(T[] array, OffsetRange restriction,
            DoFn.OutputReceiver<OffsetRange> receiver) {
        for (long i = 0; i < array.length; i++) {
            receiver.output(new OffsetRange(i, i + 1));
        }
    }
}
