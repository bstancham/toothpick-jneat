package info.bstancham.toothpick.jneat;

/**
 * A neural network output for use with {@link ToothpickTraining}.
 */
@FunctionalInterface
public interface NNOutput {

    public void activate(double val, double threshold);

}
