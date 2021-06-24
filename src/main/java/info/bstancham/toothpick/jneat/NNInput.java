package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.TPProgram;

/**
 * A neural network input for use with {@link ToothpickTraining}.
 */
public abstract class NNInput {

    public enum Dim { X, Y };

    public enum Dir { LEFT, RIGHT, TOP, BOTTOM };

    private boolean firstInput = true;
    private double minRawInput = 0;
    private double maxRawInput = 0;
    private double minScaledInput = 0;
    private double maxScaledInput = 0;
    private int numInputs = 0;
    protected int numErrors = 0;

    private double inputScaling;

    public NNInput(double inputScaling) {
        this.inputScaling = inputScaling;
    }

    public String getTitle() {
        return getClass().getSimpleName();
    }

    public double getInput(TPProgram prog) {
        double rawInput = getRawInput(prog);
        monitorRawInput(rawInput);
        double scaledInput = rawInput * inputScaling;
        monitorScaledInput(scaledInput);
        if (firstInput)
            firstInput = false;
        numInputs++;
        return scaledInput;
    }

    protected abstract double getRawInput(TPProgram prog);

    protected void monitorRawInput(double val) {
        if (firstInput || val < minRawInput)
            minRawInput = val;
        if (firstInput || val > maxRawInput)
            maxRawInput = val;
    }

    protected void monitorScaledInput(double val) {
        if (firstInput || val < minScaledInput)
            minScaledInput = val;
        if (firstInput || val > maxScaledInput)
            maxScaledInput = val;
    }

    public String generateReport() {
        return "USAGE REPORT: " + getTitle()
            + "\nnum-inputs       = " + numInputs
            + "\nnum-errors       = " + numErrors
            + "\nmin-raw-input    = " + minRawInput
            + "\nmax-raw-input    = " + maxRawInput
            + "\nmin-scaled-input = " + minScaledInput
            + "\nmax-scaled-input = " + maxScaledInput;

    }

}
