package info.bstancham.toothpick.ml;

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
    private ToothpickTrainingParams ttParams;

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

    public int numRetainForRerun;

    public TPTrainingPlatform(ToothpickTrainingParams ttParams, TPBase base) {
        super(ttParams.label, ttParams.getGeometry());
        this.ttParams = ttParams;
        this.base = base;
        numRetainForRerun = ttParams.numTPOrganisms();
    }

    public Mode getMode() { return mode; }

    public void initRerun() {
        System.out.println("TPTrainingPlatform.initRerun()");
        mode = Mode.READY_TO_RERUN;
        resetGeneration();
    }

    public void cancelRerun() {
        System.out.println("TPTrainingPlatform.cancelRerun()");
        mode = Mode.TRAINING;
        resetGeneration();
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

    public ToothpickTrainingParams getParams() { return ttParams; }

    // public TPOrganism getFittestTPOrganism() { return fittestTPOrganism; }

    public List<TPOrganism> getFitList() { return fitList; }

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

    @Override
    public void update() {

        if (mode == Mode.READY_TO_TRAIN) {
            mode = Mode.TRAINING;
        } else if (mode == Mode.READY_TO_RERUN) {
            mode = Mode.RERUN;
        }

        super.update();

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

                if (generationCounter >= ttParams.numEpoch) {
                    System.out.println("END OF TRAINING");
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

                    // initGeneration(ep);
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

    // private void updateFitList(TPOrganism tpOrg) {
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

    private void printFitList() {
        System.out.println("FITTEST ORGANISMS:");
        int n = 1;
        for (TPOrganism tpo : fitList)
            System.out.println(n++ + ": fitness=" + tpo.org.getFitness() + " (" + tpo + ")");
    }

}
