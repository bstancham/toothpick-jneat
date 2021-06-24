package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.TPProgram;
import info.bstancham.toothpick.actor.TPActor;
import info.bstancham.toothpick.geom.Geom;

/**
 * Fitness defined as accuracy at pointing in the direction of the target actor, averaged
 * over the total number of iterations so far.
 */
public class TPFitnessPointAt extends ToothpickFitness {

    // maximum possible angle in radians
    private static final double ANGLE_MAX = Math.PI * 2;

    @Override
    public ToothpickFitness copy() {
        TPFitnessPointAt out = new TPFitnessPointAt();
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
            // how accurately are we pointing at target
            double targetAngle = Geom.angle(a.x, a.y, target.x, target.y);
            // make sure that angle is not 'wrapped-around'
            double currentAngle = a.angle % ANGLE_MAX;
            double diff = Math.abs(targetAngle - currentAngle);
            // angles in radians, so max possible difference is 1.0
            double score = ANGLE_MAX - diff;
            accumulatedFitness += score;
            fitness = accumulatedFitness / numIterations;
        }
    }

}
