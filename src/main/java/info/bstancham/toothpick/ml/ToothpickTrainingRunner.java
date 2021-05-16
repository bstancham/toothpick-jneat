package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.*;
import info.bschambers.toothpick.actor.*;
import info.bschambers.toothpick.ui.swing.TPSwingUI;
import java.awt.Color;
import jneat.neat.*;

public class ToothpickTrainingRunner {

    private String title;
    TPSwingUI ui;
    TPBase base;
    TPSimultaneousPlatform platform;

    // List<ToothpickFitness> fitness = new ArrayList<>();

    ToothpickTrainingParams params;

    private int numIterations = 2000;

    TPOrganism generationFirstWinner = null;

    private double highestFitness = 0;
    private TPOrganism fittestOrganism = null;

    public ToothpickTrainingRunner(TPGeometry geom, ToothpickTrainingParams params) {
        this.params = params;

        title = "";
        ui = new TPSwingUI(title);
        base = new TPBase();
        platform  = new TPSimultaneousPlatform(title, geom);
        // platform.setSmearMode(true);
        base.setPlatform(platform);
        base.setUI(ui);
        ui.setVisible(true);
        // base.run();
    }

    // public TPGroupEvaluator(TPGeometry geom, TPBase base) {
    //     this.base = base;
    //     // title = "Group Evaluator";
    //     // ui = new TPSwingUI(title);
    //     // base = new TPBase();
    //     platform  = new TPSimultaneousPlatform("Group Evaluator", geom);
    //     // // platform.setSmearMode(true);
    //     // base.setPlatform(platform);
    //     // base.setUI(ui);
    //     // ui.setVisible(true);
    //     // // base.run();
    // }

    public void setTitle(String title) {
        this.title = title;
        ui.setTitle(title);
    }

    public TPOrganism getFittestTPOrganism() {
        return fittestOrganism;
    }

    public void evaluateGeneration(Population pop) {

        platform.discardAllPrograms();
        // fitness.clear();
        params.nextGeneration(pop.organisms);

        int num = pop.organisms.size();
        // // HorizVsVert[] programs = new HorizVsVert[num];
        // TPProgram[] programs = new TPProgram[num];
        // ToothpickFitness[] fitness = new ToothpickFitness[num];
        for (int i = 0; i < num; i++) {
        //     // programs[i] = makeProgram(pop.getOrganisms().get(i));
        //     // platform.addProgram(makeProgram((Organism) pop.organisms.get(i)));
        //     platform.addProgram(params.newProgramCopy((Organism) pop.organisms.get(i)));
        //     // fitness.add(params.newFitnessEngine());
        //     fitness[i] = params.newFitnessEngine();
            platform.addProgram(params.getTPOrganism(i).program);
        }

        // runGeneration(pop, programs, fitness);
        // runGeneration(pop, fitness);
        runGeneration();

        // search for fittest
        for (int i = 0; i < params.numTPOrganisms(); i++) {
            TPOrganism tpOrg = params.getTPOrganism(i);
            if (tpOrg.getFitness() > highestFitness) {
                fittestOrganism = tpOrg;
                highestFitness = tpOrg.getFitness();
                System.out.println("NEW FITTEST: fitness=" + highestFitness + " organism=" + tpOrg);
            }
        }
    }

    // private void runGeneration(Population pop, HorizVsVert[] programs) {
    // private void runGeneration(Population pop, ToothpickFitness[] fitness) {
    private void runGeneration() {

        System.out.println("ToothpickTrainingRunner (" + title + ") runGeneration() --- "
                           + numIterations + " iterations");

        // reset some variables
        generationFirstWinner = null;

        // run programs simultaneously
        // do fixed number of iterations
        // clear screen on first iteration
        platform.setSmearMode(false);
        for (int iteration = 1; iteration < numIterations; iteration++) {
            // System.out.println("ToothpickTrainingRunner (" + title
            //                    + ") runGeneration() --- iteration " + iteration);

            // step the action on
            base.iterate();

            if (iteration == 1)
                platform.setSmearMode(true);

            // update stats & stuff
            // for (int i = 0; i < platform.numPrograms(); i++) {
            for (int i = 0; i < params.numTPOrganisms(); i++) {

                TPOrganism tpOrg = params.getTPOrganism(i);
                tpOrg.update();
                // tpOrg.updateFitness();
                
                if (tpOrg.isWinner() && generationFirstWinner == null)
                    generationFirstWinner = tpOrg;

                // TPProgram prog = platform.getProgram(i);
                // // HorizVsVert prog = (HorizVsVert) platform.getProgram(i);
                // Organism org = (Organism) pop.organisms.get(i);
                // fitness[i].updateFitness(prog);

                // if (!prog.getVertActor().isAlive()) {
                //     org.winner = true;
                //     if (generationFirstWinner == null) {
                //         generationFirstWinner = org;
                //     }
                // }
                
            }
        }
    }

    /**
     * Returns the first winner of the last generation, or NULL if there were on winners
     * in that generation.
     */
    public Organism getGenerationFirstWinner() {
        if (generationFirstWinner != null)
            return generationFirstWinner.org;
        return null;
    }
    

    // // private HorizVsVert makeProgram(Organism org, double xInertia, double yInertia) {
    // private HorizVsVert makeProgram(Organism org) {
    //     HorizVsVert prog = new HorizVsVert();
    //     TPActor horiz = prog.getHorizActor();
    //     TPActor vert = prog.getVertActor();

    //     Color hc = ColorGetter.randColor();
    //     Color vc = ColorGetter.setBrightness(hc, 0.8);
    //     horiz.setColorGetter(new ColorMono(hc));
    //     vert.setColorGetter(new ColorMono(vc));

    //     // set up neural network controller for horizontal actor
    //     horiz.addBehaviour(new NeuralNetworkController(org.net, vert));
        
    //     // // set inertia for vertical actor
    //     // vert.xInertia = xInertia;
    //     // vert.yInertia = yInertia;
    //     params.setupDrone(vert);
        
    //     return prog;
    // }

}
