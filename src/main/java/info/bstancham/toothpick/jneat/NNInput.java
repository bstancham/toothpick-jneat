package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;

/**
 * A neural network input for use with {@link ToothpickTraining}.
 */
@FunctionalInterface
public interface NNInput {

    public double getInput(TPProgram prog);
    
}
