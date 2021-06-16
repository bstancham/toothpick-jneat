package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import jneat.neat.Organism;

public class TPOrganism {

    public Organism org;
    public NeuralNetworkController controller = null;
    public TPProgram program = null;
    private ToothpickFitness fitFunc;
    private List<TPActor> initActors = new ArrayList<>();

    public TPOrganism(Organism org, TPProgram prog,
                      NeuralNetworkController controller, ToothpickFitness fitFunc) {
        this.org = org;
        program = prog;
        this.controller = controller;
        this.fitFunc = fitFunc;
        controller.setNetwork(org.net);
        getActor().addBehaviour(controller);
        setResetSnapshot();
    }

    public boolean isWinner() { return org.winner; }

    public double getFitness() { return org.getFitness(); }

    public TPActor getActor() {
        return program.getActor(TPTrainingParams.getProtagonistID());
    }

    public void update() {
        fitFunc.updateFitness(program, getActor(), MLUtil.getVertActor(program));
        org.setFitness(fitFunc.getFitness());
    }

    public void setDebugMode(boolean val) {
        if (controller != null)
            controller.setDebugMode(val);
    }

    public void setResetSnapshot() {
        initActors.clear();
        for (TPActor a : program)
            initActors.add(a.copy());
    }

    /**
     * Resets all actors in program to initial starting positions. Used by
     * TPTrainingPlatform to facilitate re-running the generation.
     */
    public void resetPosition() {
        for (TPActor a : program)
            program.removeActor(a);
        for (TPActor a : initActors)
            program.addActor(a.copy());
        program.updateActorsInPlace();
    }

    public static class FitnessComparator implements Comparator<TPOrganism> {
        @Override
        public int compare(TPOrganism tpOrgA, TPOrganism tpOrgB) {
            double a = tpOrgA.org.getFitness();
            double b = tpOrgB.org.getFitness();
            if (a < b) return -1;
            if (a > b) return 1;
            return 0;
        }
    }

}
