package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

/**
 * Fitness defined as proximity to the target actor, averaged over the total number of
 * iterations so far.
 */
public class TPFitnessProximity extends ToothpickFitness {

    @Override
    public void updateFitness(TPProgram prog, TPActor a, TPActor target) {
        numIterations++;
        if (a != null && target != null) {
            double width = prog.getGeometry().getWidth();
            double height = prog.getGeometry().getHeight();
            double xDist = Math.abs(target.x - a.x);
            double yDist = Math.abs(target.y - a.y);
            double xFraction = width  - xDist;
            double yFraction = height - yDist;
            // proximity, as fraction of play-area size
            double xProx = xFraction / width;
            double yProx = yFraction / height;
            accumulatedFitness += (xProx + yProx);
            fitness = accumulatedFitness / numIterations;
        }
    }
            
    @Override
    public ToothpickFitness copy() {
        TPFitnessProximity out = new TPFitnessProximity();
        // out.maxFitness = maxFitness;
        out.fitness = fitness;
        out.numIterations = numIterations;
        out.accumulatedFitness = accumulatedFitness;
        return out;
    }

}
