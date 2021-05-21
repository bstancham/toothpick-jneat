package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.*;
import java.util.ArrayList;
import jneat.gui.MainGui;
import jneat.gui.Generation;
import jneat.common.EnvConstant;
import jneat.neat.Organism;

public class TPTrainingPlatform extends TPSimultaneousPlatform {

    public enum Mode { READY_TO_TRAIN, TRAINING, TRAINING_ENDED, RE_RUN };

    private Mode mode = Mode.READY_TO_TRAIN;

    private TPBase base;
    private ToothpickTrainingParams ttParams;

    private Generation genPanel = null;
    private Generation.EpochParams ep = null;

    private int counter = 0;
    private int generationCounter = 1;

    private boolean initialised = false;
    private boolean finished = false;

    private TPOrganism generationFirstWinner = null;

    private TPOrganism fittestTPOrganism = null;
    private double greatestFitness = 0;

    private boolean savedSmearMode = true;

    private boolean initNeeded = true;

    public TPTrainingPlatform(ToothpickTrainingParams ttParams, TPBase base) {
        super(ttParams.label, ttParams.getGeometry());
        this.ttParams = ttParams;
        this.base = base;
    }

    public Mode getMode() { return mode; }

    public ToothpickTrainingParams getParams() { return ttParams; }

    public TPOrganism getFittestTPOrganism() { return fittestTPOrganism; }

    public int getCurrentGeneration() { return generationCounter; }

    public int getCurrentIteration() { return counter; }

    public void initTraining(MainGui neatGui) {
        System.out.println("TPTrainingPlatform.initTraining()");
        genPanel = neatGui.getGenerationPanel();

        // init jneat process

        EnvConstant.TYPE_OF_SIMULATION = EnvConstant.SIMULATION_FROM_TOOTHPICK;
        EnvConstant.TYPE_OF_START = EnvConstant.START_FROM_GENOME;

        genPanel.initAllMap();
        EnvConstant.FORCE_RESTART = false;

        // System.out.println("ttParams.genomeFilename = " + ttParams.genomeFilename);
        EnvConstant.NAME_GENOMEA = ttParams.genomeFilename;
        // EnvConstant.NAME_GENOMEA = ttParams.genomeFilename;

        genPanel.logInfoBeforeStart();

        // startNeat
        // Generation.EpochParams ep = genPanel.startNeat_START();
        ep = genPanel.startNeat_START();

        // startNeat_MIDDLE

        genPanel.spawnPopulation(ep);

        // initGeneration(ep);
        initGeneration();

        counter = 0;
        initialised = true;
        finished = false;
        fittestTPOrganism = null;
        greatestFitness = 0;
    }

    // private void initGeneration(Generation.EpochParams ep) {
    private void initGeneration() {
        discardAllPrograms();
        ttParams.nextGeneration(ep.pop.organisms);
        for (int i = 0; i < ep.pop.organisms.size(); i++) {
            addProgram(ttParams.getTPOrganism(i).program);
        }
        generationFirstWinner = null;
        savedSmearMode = isSmearMode();
        setSmearMode(false);
    }

    public void resetGeneration() {
        resetAllPrograms();
        counter = 0;
        savedSmearMode = isSmearMode();
        setSmearMode(false);
    }

    @Override
    public void update() {

        // if (initNeeded) {
        //     initTraining();
        //     initNeeded = false;
        // }

        mode = Mode.TRAINING;

        super.update();

        if (initialised && !finished) {

            // smear-mode is off for the first iteration, to clear the screen
            if (counter == 1)
                // setSmearMode(true);
                setSmearMode(savedSmearMode);

            // update fitness etc
            for (int i = 0; i < ttParams.numTPOrganisms(); i++) {
                TPOrganism tpOrg = ttParams.getTPOrganism(i);
                tpOrg.update();
                if (tpOrg.isWinner() && generationFirstWinner == null)
                    generationFirstWinner = tpOrg;
            }

            // do countdown
            counter++;
            // System.out.println("... generation " + generationCounter + ", iteration " + counter);

            if (counter >= ttParams.iterationsPerGeneration) {

                System.out.println("END OF GENERATION (" + generationCounter + ")");

                // if (generationCounter >= EnvConstant.NUMBER_OF_EPOCH) {
                if (generationCounter >= ttParams.numEpoch) {
                    System.out.println("END OF TRAINING");
                    System.out.println("... greatest fitness = " + greatestFitness
                                       + " (" + fittestTPOrganism + ")");
                    finished = true;
                    mode = Mode.TRAINING_ENDED;
                    base.showMenu();
                } else {

                    // keep track of all-time fittest
                    for (int i = 0; i < ttParams.numTPOrganisms(); i++) {
                        TPOrganism tpOrg = ttParams.getTPOrganism(i);
                        if (tpOrg.org.getFitness() > greatestFitness) {
                            fittestTPOrganism = tpOrg;
                            greatestFitness = tpOrg.org.getFitness();
                        }
                    }

                    Organism firstWinner = null;
                    if (generationFirstWinner != null)
                        firstWinner = generationFirstWinner.org;
                    genPanel.afterEpochForToothpick(ep, firstWinner);

                    // initGeneration(ep);
                    initGeneration();

                    generationCounter++;
                    counter = 0;
                }
            }
        }
    }

}
