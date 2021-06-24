package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.TPProgram;
import info.bstancham.toothpick.actor.TPActor;

public class TPFitnessProximityPlus extends ToothpickFitness {

    private double proximityScaling = 1.0;
    private double intertiaScaling = 1.0;
    private double proximityEachScaling = 1.0;
    private double intertiaEachScaling = 1.0;

    private double inertiaMax = 2.0;

    // private double accumulatedProximity = 0;

    @Override
    public void updateFitness(TPProgram prog, TPActor a, TPActor target) {
        numIterations++;
        if (a != null && target != null) {

            // PROXIMITY (each)
            double width = prog.getGeometry().getWidth();
            double height = prog.getGeometry().getHeight();
            double xDist = Math.abs(target.x - a.x);
            double yDist = Math.abs(target.y - a.y);
            double xFraction = width  - xDist;
            double yFraction = height - yDist;
            // proximity, as fraction of play-area size
            double xProx = xFraction / width;
            double yProx = yFraction / height;

            // INERTIA (each)
            double xInertiaDiff = Math.abs(target.xInertia - a.xInertia);
            double yInertiaDiff = Math.abs(target.yInertia - a.yInertia);

            // PROXIMITY (actual)
            // double combinedProx = xProx + yProx;
            double hypotenuseSquared = (xProx * xProx) + (yProx * yProx);
            double prox = Math.sqrt(hypotenuseSquared);
            // double fitnessProx = prox *

            // INERTIA (combined)



            // fitness = accumulatedFitness / numIterations;

            fitness = 1.0;
        }
    }

    @Override
    public TPFitnessProximityPlus copy() {
        TPFitnessProximityPlus out = new TPFitnessProximityPlus();
        // out.maxFitness = maxFitness;
        out.fitness = fitness;
        out.numIterations = numIterations;
        out.accumulatedFitness = accumulatedFitness;
        return out;
    }

}
