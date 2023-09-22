package org.micromanager.lightsheetmanager.model.autofocus;

/**
 * The result of running built-in Micro-Manager autofocus routine.
 */
public class AutofocusResult {

    public final boolean success_;
    public final double galvoPosition_;
    public final double piezoPosition_;
    public final double offsetDelta_;
    public final double rSquared_;
    public final double channelOffset_;

    public AutofocusResult(boolean success, double galvoPosition, double piezoPosition,
                           double offsetDelta, double rSquared, double channelOffset) {
        success_ = success;
        galvoPosition_ = galvoPosition;
        piezoPosition_ = piezoPosition;
        offsetDelta_ = offsetDelta;     // amount in um that the offset will shift, could be positive or negative
        channelOffset_ = channelOffset;
        rSquared_ = rSquared;
    }
}
