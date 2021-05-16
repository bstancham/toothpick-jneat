package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.*;
// import info.bschambers.toothpick.geom.Line;
// import jneat.neat.Network;
// import jneat.neat.NNode;
import jneat.neat.Organism;

public class TPOrganism {

    public Organism org;
    public NeuralNetworkController controller = null;
    public TPProgram program = null;
    // private String protagonistName; 
    // public boolean winner = false;
    private ToothpickFitness fitFunc;

    public TPOrganism(Organism org, TPProgram prog,
                      // String protagonistName,
                      NeuralNetworkController controller, ToothpickFitness fitFunc) {
        this.org = org;
        this.program = prog;
        // this.protagonistName = protagonistName;
        this.controller = controller;
        this.fitFunc = fitFunc;
        controller.setNetwork(org.net);
        // program.init();
        // TPActor horiz = MLUtil.getVertActor(program);
        // TPActor horiz = MLUtil.getHorizActor(program);
        // horiz.addBehaviour(controller);
        // TPActor a = program.getActor(protagonistName);
        // TPActor a = getActor();
        TPActor a = MLUtil.getHorizActor(program);
        a.addBehaviour(controller);
    }

    public boolean isWinner() { return org.winner; }

    public double getFitness() { return org.getFitness(); }

    public TPActor getActor() {
        return MLUtil.getHorizActor(program);
        // return program.getActor(protagonistName);
    }
        
    public void update() {

        // for (TPOrganism tpOrg : organisms)
        //     update();

        // not a behaviour, so must update manually
        // controller.update(program);

        // TPActor horiz = MLUtil.getHorizActor(program);
        // TPActor vert = MLUtil.getVertActor(program);

        // TPActor horiz = getActor();
        // TPActor vert = MLUtil.getVertActor(program);

        fitFunc.updateFitness(program, getActor(), MLUtil.getVertActor(program));
        org.setFitness(fitFunc.getFitness());

        // if (horiz != null && vert == null) {
        //     org.winner = true;
        // }
    }

}
