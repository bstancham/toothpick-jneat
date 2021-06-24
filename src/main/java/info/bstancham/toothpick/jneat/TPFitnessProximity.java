package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.TPProgram;
import info.bstancham.toothpick.actor.TPActor;

/**
 * Fitness defined as proximity to the TARGET actor, averaged over the total number of
 * iterations so far.
 */
public class TPFitnessProximity extends ToothpickFitness {

    @Override
    public void updateFitness(TPProgram prog, TPActor a, TPActor target) {
        numIterations++;
        if (a != null && target != null) {
            double xDist = Math.abs(prog.getGeometry().xDistWrapped(a.x, target.x));
            double yDist = Math.abs(prog.getGeometry().yDistWrapped(a.y, target.y));
            double xFraction = prog.getGeometry().getWidth() - xDist;
            double yFraction = prog.getGeometry().getHeight() - yDist;
            // proximity, as fraction of play-area size
            double xProx = xFraction / prog.getGeometry().getWidth();
            double yProx = yFraction / prog.getGeometry().getHeight();
            accumulatedFitness += (xProx + yProx);
            fitness = accumulatedFitness / numIterations;
        }
    }

    @Override
    public ToothpickFitness copy() {
        TPFitnessProximity out = new TPFitnessProximity();
        out.fitness = fitness;
        out.numIterations = numIterations;
        out.accumulatedFitness = accumulatedFitness;
        return out;
    }

}
