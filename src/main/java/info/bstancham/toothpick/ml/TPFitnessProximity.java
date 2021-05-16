package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

/**
 * Fitness defined as proximity to the target actor, averaged over the total number of
 * iterations so far.
 */
public class TPFitnessProximity extends ToothpickFitness {

    @Override
    public ToothpickFitness copy() {
        TPFitnessProximity out = new TPFitnessProximity();
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
            
            
            // // scale to be fraction of 1.0
            // double xDistFraction = xDist / 
            // double yDistFraction = yDist / 
            // // proximity as fraction of 1.0
            // double xProximity = 1.0 - xDistFraction;
            // double yProximity = 1.0 - yDistFraction;
            // // combined proximity as fraction of 1.0
            // double currentFitness = (xProximity + yProximity) / 2;


        // if (a != null && target != null) {

        //     double xDist = Math.abs(target.x - a.x);
        //     double yDist = Math.abs(target.y - a.y);
        //     // scale to be fraction of 1.0
        //     double xDistFraction = xDist / (double) prog.getGeometry().getWidth();
        //     double yDistFraction = yDist / (double) prog.getGeometry().getHeight();
        //     // proximity as fraction of 1.0
        //     double xProximity = 1.0 - xDistFraction;
        //     double yProximity = 1.0 - yDistFraction;
        //     // combined proximity as fraction of 1.0
        //     double currentFitness = (xProximity + yProximity) / 2;

        //     accumulatedFitness += currentFitness;
        //     fitness = (accumulatedFitness / numIterations) * getMaxFitness();
        // }

        // accumulatedFitness += 0.5;
        //     fitness = (accumulatedFitness / numIterations) * getMaxFitness();

}
