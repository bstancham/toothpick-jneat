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
        out.fitness = fitness;
        out.numIterations = numIterations;
        out.accumulatedFitness = accumulatedFitness;
        return out;
    }

    @Override
    public void updateFitness(TPProgram prog, TPActor a, TPActor target) {
        numIterations++;
        if (a != null) {
            // if not alive, score nothing for this iteration
            if (a.isAlive()) {
                // distance squared (pythagoras theorem)
                // ... always positive - multiplying same sign, result is always positive
                double score = (a.xInertia * a.xInertia) + (a.yInertia * a.yInertia);
                accumulatedFitness += score * score;
            }
        }

        if (accumulatedFitness > 0)
            fitness = accumulatedFitness / numIterations;
        else
            fitness = 0;

    }

}
