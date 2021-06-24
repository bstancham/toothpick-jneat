package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.actor.TPActor;
import info.bstancham.toothpick.TPProgram;

public abstract class ToothpickFitness {

    // protected double maxFitness = 1.0;
    protected double fitness = 0;
    protected int numIterations = 0;
    protected double accumulatedFitness = 0;

    public abstract ToothpickFitness copy();

    // /** The maximum fitness which may be returned by {@link getFitness}. */
    // public double getMaxFitness() { return maxFitness; }

    /** The current fitness. */
    public double getFitness() { return fitness; }

    /**
     * NOTE: must be able to handle null input for either actor.
     */
    public abstract void updateFitness(TPProgram prog, TPActor a, TPActor target);

}
