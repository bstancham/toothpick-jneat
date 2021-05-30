package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

/**
 * Fitness defined as ability to kill the target actor quickly, with modest rewards for
 * keeping moving and big penalty for dying.
 */
public class TPFitnessKillAndDontDie extends ToothpickFitness {

    // maximum score given for velocity - should be smaller incentive than kill
    private static final double VELOCITY_MAX = 1.0;

    // bonus awarded for every iteration that target has been killed
    private static final double KILL_BONUS = 2.0;

    private double velocityScoreScaling = 0.5;

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
                double score = (a.xInertia + a.yInertia) * velocityScoreScaling;
                if (score > VELOCITY_MAX)
                    score = VELOCITY_MAX;
                if (!target.isAlive())
                    score += KILL_BONUS;
                accumulatedFitness += score;
                fitness = accumulatedFitness / numIterations;
            }
        }
    }

}
