package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

/**
 * Fitness defined as velocity averaged over total number of iterations so far, with big
 * penalty for dying.
 */
public class TPFitnessVelocityAndDontDie extends ToothpickFitness {

    @Override
    public ToothpickFitness copy() {
        TPFitnessVelocityAndDontDie out = new TPFitnessVelocityAndDontDie();
        // out.maxFitness = maxFitness;
        out.fitness = fitness;
        out.numIterations = numIterations;
        out.accumulatedFitness = accumulatedFitness;
        return out;
    }

    @Override
    public void updateFitness(TPProgram prog, TPActor a, TPActor target) {
        numIterations++;
        if (a != null && target != null) {
            // if not alive, score nothing for this iteration
            if (a.isAlive()) {
                double score = a.xInertia + a.yInertia;
                accumulatedFitness += score;
                fitness = accumulatedFitness / numIterations;
            }
        }
    }

}
