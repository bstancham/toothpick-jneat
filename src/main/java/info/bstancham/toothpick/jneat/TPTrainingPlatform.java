package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.*;
import java.util.ArrayList;
import java.util.List;
import jneat.gui.MainGui;
import jneat.gui.Generation;
import jneat.common.EnvConstant;
import jneat.neat.Organism;

public class TPTrainingPlatform extends TPSimultaneousPlatform {

    public enum Mode {
        READY_TO_TRAIN,
        TRAINING,
        TRAINING_ENDED,
        READY_TO_RERUN,
        RERUN
    };

    private Mode mode = Mode.READY_TO_TRAIN;

    private TPBase base;
    private TPTrainingParams ttParams;

    private Generation genPanel = null;
    private Generation.EpochParams ep = null;

    private int counter = 1;
    private int generationCounter = 1;

    private boolean initialised = false;
    private boolean finished = false;

    private TPOrganism generationFirstWinner = null;

    private int fitListMaxSize = 5;
    private List<TPOrganism> fitList = new ArrayList<>();
    private TPOrganism.FitnessComparator fitComp = new TPOrganism.FitnessComparator();

    private boolean savedSmearMode = true;

    private boolean initNeeded = true;

    public int numRetainForRerun = 5;
    public int numGensExtend = 1;
    private int extendedGenerations = 0;

    public TPTrainingPlatform(TPTrainingParams ttParams, TPBase base) {
        super(ttParams.label, ttParams.getGeometry());
        this.ttParams = ttParams;
        this.base = base;
        numRetainForRerun = ttParams.numTPOrganisms();
    }

    public Mode getMode() { return mode; }

    public TPTrainingParams getParams() { return ttParams; }

    public int getNumEpoch() { return getParams().numEpoch + extendedGenerations; }

    public List<TPOrganism> getFitList() { return fitList; }

    public int getCurrentGeneration() { return generationCounter; }

    public int getCurrentIteration() { return counter; }

    public int getNumGensExtend() { return numGensExtend; }

    /**
     * Extends training by {@link numGensExtend} generations.
     */
    public void extendTraining() {
        extendedGenerations += numGensExtend;
        if (generationCounter < getNumEpoch()) {
            finished = false;
            if (mode != Mode.READY_TO_TRAIN)
                mode = Mode.TRAINING;
        }
    }

    public void initTraining(MainGui neatGui) {
        System.out.println("TPTrainingPlatform.initTraining()");
        genPanel = neatGui.getGenerationPanel();

        // init jneat process

        EnvConstant.TYPE_OF_SIMULATION = EnvConstant.SIMULATION_FROM_TOOTHPICK;
        EnvConstant.TYPE_OF_START = EnvConstant.START_FROM_GENOME;

        // genPanel.initAllMap();
        EnvConstant.FORCE_RESTART = false;

        // System.out.println("ttParams.genomeFilename = " + ttParams.genomeFilename);
        EnvConstant.NAME_GENOMEA = ttParams.genomeFilename;

        genPanel.logInfoBeforeStart();
        ep = genPanel.startNeat_START();
        genPanel.spawnPopulation(ep);

        initGeneration();

        counter = 1;
        generationCounter = 1;
        initialised = true;
        finished = false;
        fitList.clear();
        mode = Mode.READY_TO_TRAIN;
    }

    private void initGeneration() {
        ttParams.targetSetup.init(ttParams);
        discardAllPrograms();
        ttParams.nextGeneration(ep.pop.organisms);
        for (int i = 0; i < ep.pop.organisms.size(); i++) {
            addProgram(ttParams.getTPOrganism(i).program);
        }
        generationFirstWinner = null;
        // turn off smear-mode (just for first iteration)
        savedSmearMode = isSmearMode();
        setSmearMode(false);
    }

    private void resetGeneration() {
        for (TPOrganism tpo : ttParams.organisms)
            tpo.resetPosition();
        updateActorsAllPrograms();
        // turn off smear-mode (just for first iteration)
        savedSmearMode = isSmearMode();
        setSmearMode(false);
        counter = 1;
    }

    public void initRerun() {
        System.out.println("TPTrainingPlatform.initRerun()");
        mode = Mode.READY_TO_RERUN;
        // sort by fitness BEFORE reset-generation
        ttParams.sortTPOrganismsByFitness();
        resetGeneration();
        // hide actors, except for the n fittest
        int n = numRetainForRerun;
        System.out.println("TPTrainingPlatform: hide selected programs...");
        for (int i = 0; i < numPrograms(); i++) {
            if (i >= numPrograms() - n) {
                System.out.println("... " + i + ": KEEP (fitness=" + ttParams.getTPOrganism(i).getFitness() + ")");
            } else {
                System.out.println("... " + i + ": hide (fitness=" + ttParams.getTPOrganism(i).getFitness() + ")");
                setVisible(i, false);
            }
        }
    }

    public void cancelRerun() {
        System.out.println("TPTrainingPlatform.cancelRerun()");
        mode = Mode.TRAINING;
        resetGeneration();
    }

    @Override
    public void update() {

        if (mode == Mode.READY_TO_TRAIN) {
            mode = Mode.TRAINING;
        } else if (mode == Mode.READY_TO_RERUN) {
            mode = Mode.RERUN;
        }

        super.update();
        ttParams.targetSetup.update(ttParams);

        if (mode == Mode.TRAINING) {
            updateTRAINING();
        } else if (mode == Mode.RERUN) {
            updateRERUN();
        }
    }

    private void updateTRAINING() {

        if (initialised && !finished) {

            // smear-mode is off for the first iteration, to clear the screen
            if (counter == 1)
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

                if (generationCounter >= getNumEpoch()) {
                    System.out.println("END OF TRAINING");
                    updateFitList();
                    printFitList();
                    // System.out.println("... greatest fitness = " + greatestFitness
                    //                    + " (" + fittestTPOrganism + ")");
                    finished = true;
                    mode = Mode.TRAINING_ENDED;
                    base.showMenu();
                } else {

                    // NEXT GENERATION

                    // keep track of all-time fittest
                    updateFitList();

                    Organism firstWinner = null;
                    if (generationFirstWinner != null)
                        firstWinner = generationFirstWinner.org;
                    genPanel.afterEpochForToothpick(ep, firstWinner);

                    initGeneration();

                    generationCounter++;
                    counter = 1;
                }
            }
        }
    }

    private void updateRERUN() {
        if (counter == 1)
            setSmearMode(savedSmearMode);
        counter++;
    }

    private void updateFitList() {
        boolean modified = false;
        for (int i = 0; i < ttParams.numTPOrganisms(); i++) {
            TPOrganism tpOrg = ttParams.getTPOrganism(i);

            fitListCheck:
            if (fitList.size() == 0) {
                fitList.add(tpOrg);
                modified = true;
            } else {

                int f = 0;
                for (f = 0; f < fitList.size(); f++) {
                    if (tpOrg.org.getFitness() > fitList.get(f).org.getFitness()) {
                        // insert in front of current item
                        fitList.add(f, tpOrg);
                        // trim list if too long
                        if (fitList.size() > fitListMaxSize) {
                            fitList = fitList.subList(0, fitListMaxSize);
                        }
                        modified = true;
                        break fitListCheck;
                    }
                }

                if (f < fitListMaxSize) {
                    // fitList is not full - add to end
                    fitList.add(tpOrg);
                    modified = true;
                }
            }
        }
        if (modified) {
            System.out.print("FITTEST ORGANISMS UPDATED --> ");
            printFitList();
        }
    }

    public void printFitList() {
        System.out.println("FITTEST ORGANISMS:");
        int n = 1;
        for (TPOrganism tpo : fitList)
            System.out.println(n++ + ": fitness=" + tpo.org.getFitness() + " (" + tpo + ")");
    }

}
