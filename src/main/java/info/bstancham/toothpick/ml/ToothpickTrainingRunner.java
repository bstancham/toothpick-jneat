package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.*;
import info.bschambers.toothpick.actor.*;
import info.bschambers.toothpick.ui.swing.TPSwingUI;
import java.awt.Color;
import jneat.neat.*;

public class ToothpickTrainingRunner {

    private String title;
    private TPSimultaneousPlatform platform;
    private ToothpickTrainingParams params;

    private TPOrganism generationFirstWinner = null;
    private double highestFitness = 0;
    private TPOrganism fittestOrganism = null;

    public ToothpickTrainingRunner(ToothpickTrainingParams params) {
        this.params = params;
        title = "Toothpick Training Runner: " + params.label;
        platform  = new TPSimultaneousPlatform(title, params.getGeometry());
    }

    public ToothpickTrainingParams getParams() { return params; }

    public void show() {
        params.getBase().setPlatform(platform);
        params.getBase().hideMenu();
    }

    public void pause() {
        params.getBase().showMenu();
    }

    public void setTitle(String title) {
        this.title = title;
        params.getBase().setWindowTitle(title);
    }

    public TPOrganism getFittestTPOrganism() {
        return fittestOrganism;
    }

    public void evaluateGeneration(Population pop) {

        platform.discardAllPrograms();

        params.nextGeneration(pop.organisms);

        int num = pop.organisms.size();
        for (int i = 0; i < num; i++) {
            platform.addProgram(params.getTPOrganism(i).program);
        }

        runGeneration();

        // search for fittest
        for (int i = 0; i < params.numTPOrganisms(); i++) {
            TPOrganism tpOrg = params.getTPOrganism(i);
            if (tpOrg.getFitness() > highestFitness) {
                fittestOrganism = tpOrg;
                highestFitness = tpOrg.getFitness();
                // params.setTheBestOne(tpOrg);
                System.out.println("NEW FITTEST: fitness=" + highestFitness + " organism=" + tpOrg);
            }
        }
    }

    private void runGeneration() {
        int numIterations = params.iterationsPerGeneration;
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
            params.getBase().iterate();

            if (iteration == 1)
                platform.setSmearMode(true);

            // update stats & stuff
            for (int i = 0; i < params.numTPOrganisms(); i++) {

                TPOrganism tpOrg = params.getTPOrganism(i);
                tpOrg.update();

                if (tpOrg.isWinner() && generationFirstWinner == null)
                    generationFirstWinner = tpOrg;
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

}
