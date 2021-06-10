package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.PBRandActorSetup;
import info.bschambers.toothpick.PBMisc;
import info.bschambers.toothpick.ProgramBehaviour;
import info.bschambers.toothpick.TPBase;
import info.bschambers.toothpick.TPGeometry;
import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import jneat.neat.Organism;

/**
 * <p>Agregates parameters and methods required for running jneat with toothpick system.</p>
 *
 * The following parameters need to be configured:
 * - abstract method makeMasterProgram()
 * - abstract method makeFitness()
 * - abstract method makeController()
 * - targetSetup
 *
 * HOW IT WORKS
 *
 * - Create and set up ToothpickTrainingParams ttp
 * - invoke nextGeneration(), to create a new batch of TPOrganisms
 * - invoke update() for each iteration of the generation
 */
public abstract class TPTrainingParams {

    public static final String PROTAGONIST_NAME = MLUtil.HORIZ_NAME;
    public static final String TARGET_NAME = MLUtil.VERT_NAME;

    /** An identifying label for this ToothpickTrainingParams instance. */
    public String label;

    private TPBase base;
    public String genomeFilename;
    public int numEpoch = 6;
    public int populationSize = 10;
    public int iterationsPerGeneration = 3000;

    /** Manages the target-actor throughout the running of a generation. */
    public TargetSetup targetSetup = new TargetSetupStatic();
    public String getTargetSetupLabel() { return targetSetup.getLabel(); }

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

    public TPTrainingParams(TPBase base, String label, String genomeFilename) {
        this.base = base;
        this.label = label;
        this.genomeFilename = genomeFilename;
        masterProg = makeMasterProgram();
        masterFitness = makeFitness();
    }

    public void init() {
        masterProg = makeMasterProgram();
        masterFitness = makeFitness();
    }

    public static String getProtagonistID() { return PROTAGONIST_NAME; }

    public static String getTargetID() { return TARGET_NAME; }

    public TPBase getBase() { return base; }

    public TPGeometry getGeometry() { return masterProg.getGeometry(); }

    public String getGenomeFilename() { return genomeFilename; }

    public int numTPOrganisms() { return organisms.size(); }

    public TPOrganism getTPOrganism(int i) { return organisms.get(i); }

    protected abstract TPProgram makeMasterProgram();

    protected abstract ToothpickFitness makeFitness();

    protected abstract NeuralNetworkController makeController();

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
        TPActor horiz = MLUtil.getHorizActor(p);
        if (horiz != null)
            MLUtil.setActorColorNotRed(horiz);
        p.setResetSnapshot();
        return p;
    }

    public TPOrganism newTPOrganism(Organism org) {
        TPOrganism tpOrg = new TPOrganism(org, makeProgramCopy(),
                                          makeController(), masterFitness.copy());
        return tpOrg;
    }

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

    protected TPProgram makeMasterProg1Target() {
        TPProgram prog = new TPProgram("Training Program: 1 target");
        prog.setBGColor(new Color(50, 150, 100));
        prog.setSmearMode(true);
        prog.setShowIntersections(true);

        // larger than usual play area
        prog.getGeometry().setupAndCenter(3000, 2400);
        prog.getGeometry().scale = 0.333;

        // create the two actors
        TPActor protagonist = MLUtil.makeLineActor(-50, 0, 50, 0, 200, 350);
        protagonist.setColorGetter(ColorMono.GREEN);
        protagonist.setVertexColorGetter(ColorMono.WHITE);
        protagonist.name = PROTAGONIST_NAME;
        prog.addActor(protagonist);
        TPActor target = MLUtil.makeLineActor(0, -50, 0, 50, 800, 350);
        target.setColorGetter(ColorMono.RED);
        target.setVertexColorGetter(ColorMono.WHITE);
        target.name = TARGET_NAME;
        prog.addActor(target);

        // set both actors to new random position at beginning of each generation
        PBRandActorSetup randDroneSetup = new PBRandActorSetup();
        randDroneSetup.setTarget(PROTAGONIST_NAME, TARGET_NAME);
        randDroneSetup.initBounds(prog.getGeometry());
        prog.addResetBehaviour(randDroneSetup);

        prog.init();
        prog.setResetSnapshot();
        return prog;
    }

    protected TPProgram makeMasterProgNoTarget() {
        TPProgram prog = new TPProgram("Training Program: no target");
        prog.setBGColor(new Color(50, 150, 100));
        prog.setSmearMode(true);
        prog.setShowIntersections(true);
        // larger than usual play area
        prog.getGeometry().setupAndCenter(3000, 2400);
        prog.getGeometry().scale = 0.333;
        // create the protagonist
        TPActor a = MLUtil.makeLineActor(-50, 0, 50, 0, 200, 350);
        a.setBoundaryBehaviour(TPActor.BoundaryBehaviour.DIE_AT_BOUNDS);
        a.setColorGetter(ColorMono.GREEN);
        a.setVertexColorGetter(ColorMono.WHITE);
        a.name = PROTAGONIST_NAME;
        prog.addActor(a);
        // set actor to new random position at beginning of each generation
        PBMisc centerSetup = new PBMisc((TPProgram tpp) -> {
                TPActor tpa = MLUtil.getHorizActor(tpp);
                if (tpa != null) {
                    tpa.x = tpp.getGeometry().getXCenter();
                    tpa.y = tpp.getGeometry().getYCenter();
                }
        });
        prog.addResetBehaviour(centerSetup);

        prog.init();
        prog.setResetSnapshot();
        return prog;
    }

    /**
     * <p>Sorts the {@link organisms} list by fitness, with the fittest first.</p>
     */
    public void sortTPOrganismsByFitness() {
        organisms.sort(new TPOrganism.FitnessComparator());
        System.out.println("Sorted TPOrganisms by fitness:");
        for (int i = 0; i < organisms.size(); i++)
            System.out.println("... " + i + ": fitness=" + organisms.get(i).getFitness()
                               + ", instance=" + organisms.get(i));
    }

}
