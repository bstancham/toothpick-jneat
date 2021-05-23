package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.PBRandActorSetup;
import info.bschambers.toothpick.TPBase;
import info.bschambers.toothpick.TPGeometry;
import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import jneat.neat.Organism;

/**
 * Agregates a bunch of parameters for running jneat with toothpick system.
 *
 * The following parameters must all be configured:
 * {@link masterProg}
 * {@link masterFitness}
 * {@link masterController}
 * {@link numOrganisms}
 *
 * HOW IT WORKS
 *
 * - Create and set up ToothpickTrainingParams ttp
 * - invoke nextGeneration(), to create a new batch of TPOrganisms
 * - invoke update() for each iteration of the generation
 */
public abstract class ToothpickTrainingParams {

    private TPBase base;

    /** An identifying label for this ToothpickTrainingParams instance. */
    public String label;

    public String genomeFilename;

    public int numEpoch = 6;

    public int populationSize = 10;

    public int iterationsPerGeneration = 1500;

    public boolean mobileTarget = true;

    /**
     * <p>The master-program - an identical copy of this program is made for each of the
     * {@link organisms}, so that they will each have identical starting conditions.</p>
     *
     * <p>Reset is called at the beginning of each generation and then new copies are made
     * for each Organism. Therefore, reset-behaviours can be used to make any random
     * changes to the shared starting conditions for each generation.</p>
     */
    protected TPProgram masterProg;

    private ToothpickFitness masterFitness;

    public List<TPOrganism> organisms = new ArrayList<>();

    public ToothpickTrainingParams(TPBase base, String label, String genomeFilename) {
        this.base = base;
        this.label = label;
        this.genomeFilename = genomeFilename;
        masterProg = makeMasterProgram();
        masterFitness = makeFitness();
        // masterController = makeController();
    }

    public void init() {
        masterProg = makeMasterProgram();
        masterFitness = makeFitness();
    }

    protected abstract TPProgram makeMasterProgram();

    protected abstract ToothpickFitness makeFitness();

    protected abstract NeuralNetworkController makeController();

    public String getGenomeFilename() { return genomeFilename; }

    public int numTPOrganisms() { return organisms.size(); }

    public TPOrganism getTPOrganism(int i) { return organisms.get(i); }

    public void nextGeneration(List<Organism> neatOrganisms) {
        masterProg.reset();
        masterProg.setResetSnapshot();
        organisms.clear();
        for (Organism org : neatOrganisms) {
            TPOrganism tpOrg = newTPOrganism(org);
            organisms.add(tpOrg);
        }
    }

    /**
     * Makes an identical copy, but gives actors different color every time.
     */
    private TPProgram makeProgramCopy() {
        TPProgram p = new TPProgram(masterProg.getResetSnapshot());
        MLUtil.setActorColorRandGraduated(p);
        p.setResetSnapshot();
        return p;
    }

    public TPOrganism newTPOrganism(Organism org) {
        TPOrganism tpOrg = new TPOrganism(org, makeProgramCopy(),
                                          makeController(), masterFitness.copy());
        return tpOrg;
    }

    // /**
    //  * <p>Get the fittest actor from the whole training session.</p>
    //  *
    //  * <p>WARNING! will be null if training session has not ended properly yet - may be
    //  * null anyway!</p>
    //  */
    // public TPOrganism getTheBestOne() {
    //     return theBestOne;
    // }

    // /**
    //  * ToothpickTrainingRunner uses this...
    //  */
    // public void setTheBestOne(TPOrganism a) {
    //     theBestOne = a;
    // }

    public String makeSessionParams() {
        return ""
            + "; Toothpick Game: " + label + "\n"
            + "data_from_toothpick          Y\n"
            + "start_from_genome            Y\n"
            + "genome_file                  " + genomeFilename + "\n"
            + "population_file              primitive\n"
            + "epoch                        " + numEpoch + "\n"
            + "activation                   0\n"
            + "prefix_generation_file       generation\n"
            + "prefix_winner                winner\n";
    }

    public boolean targetIsMobile() { return mobileTarget; }

    public void setTargetIsMobile(boolean val) { mobileTarget = val; }

    public TPGeometry getGeometry() { return masterProg.getGeometry(); }

    public TPBase getBase() { return base; }

}
