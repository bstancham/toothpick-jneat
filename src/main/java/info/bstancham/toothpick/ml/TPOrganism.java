package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.*;
import jneat.neat.Organism;

public class TPOrganism {

    public Organism org;
    public NeuralNetworkController controller = null;
    public TPProgram program = null;
    private ToothpickFitness fitFunc;

    public TPOrganism(Organism org, TPProgram prog,
                      NeuralNetworkController controller, ToothpickFitness fitFunc) {
        this.org = org;
        this.program = prog;
        this.controller = controller;
        this.fitFunc = fitFunc;
        controller.setNetwork(org.net);
        TPActor a = MLUtil.getHorizActor(program);
        a.addBehaviour(controller);
    }

    public boolean isWinner() { return org.winner; }

    public double getFitness() { return org.getFitness(); }

    public TPActor getActor() {
        return MLUtil.getHorizActor(program);
    }

    public void update() {
        fitFunc.updateFitness(program, getActor(), MLUtil.getVertActor(program));
        org.setFitness(fitFunc.getFitness());
    }

    public void setDebugMode(boolean val) {
        if (controller != null)
            controller.setDebugMode(val);
    }

}
