package info.bstancham.toothpick.ml;

// import info.bschambers.toothpick.actor.TPFactory;
import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.PBRandActorSetup;
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

    /** An identifying label for this ToothpickTrainingParams instance. */
    public String label;

    public String genomeFilename;

    /**
     * <p>The master-program - an identical copy of this program is made for each of the
     * {@link organisms}, so that they will each have identical starting conditions.</p>
     *
     * <p>Reset is called at the beginning of each generation and then new copies are made
     * for each Organism. Therefore, reset-behaviours can be used to make any random
     * changes to the shared starting conditions for each generation.</p>
     */
    private TPProgram masterProg;

    private ToothpickFitness masterFitness;

    public List<TPOrganism> organisms = new ArrayList<>();

    private TPOrganism theBestOne = null;

    public ToothpickTrainingParams(String label, String genomeFilename) {
        this.label = label;
        this.genomeFilename = genomeFilename;
        masterProg = makeProgram();
        masterFitness = makeFitness();
        // masterController = makeController();
    }
    
    protected abstract TPProgram makeProgram();

    protected abstract ToothpickFitness makeFitness();

    protected abstract NeuralNetworkController makeController();

    public String getGenomeFilename() { return genomeFilename; }

    public int numTPOrganisms() { return organisms.size(); }

    public TPOrganism getTPOrganism(int i) { return organisms.get(i); }

    public void nextGeneration(List<Organism> neatOrganisms) {
        masterProg.reset();
        // TPFactory.setRandHeading(MLUtil.getVertActor(masterProg));
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
        // TPOrganism tpOrg = new TPOrganism(org);
        // tpOrg.program = makeProgramCopy();
        // tpOrg.controller = makeControllerCopy(tpOrg);
        // TPOrganism tpOrg = new TPOrganism(org, makeProgramCopy(), makeControllerCopy());
        TPOrganism tpOrg = new TPOrganism(org, makeProgramCopy(),
                                          makeController(), masterFitness.copy());
        return tpOrg;
    }

    /**
     * <p>Get the fittest actor from the whole training session.</p>
     *
     * <p>WARNING! will be null if training session has not ended properly yet - may be
     * null anyway!</p>
     */
    public TPOrganism getTheBestOne() {
        return theBestOne;
    }

    public void setTheBestOne(TPOrganism a) {
        theBestOne = a;
    }

    // /**
    //  * Updates organism and applies fitness function.
    //  */
    // public void update() {
    //     for (TPOrganism tpOrg : organisms) {
    //         tpOrg.update();
    //     }
    // }



    // public Supplier<NeuralNetworkController> makeControllerFunc;

    // public NNInput[] controllerInputs;
    // public NNOutput[] controllerOutputs;

    // public NeuralNetworkController masterController;

    // public int numOrganisms = 10;



    // private TPProgram makeProgram() {
    //     TPProgram prog = MLUtil.makeProgHorizVsVert();
    //     // set drone-actor to new random position at beginning of each generation
    //     RandomPositionBehaviour randPos =
    //         new RandomPositionBehaviour(500, 1000, 0, 800, new String[] { MLUtil.VERT_NAME });
    //     prog.addResetBehaviour(randPos);
    //     prog.setResetSnapshot();
    //     return prog;
    // }

    // private ToothpickFitness makeFitness() {
    //     return new TPFitnessPointAt();
    // }


    // private NeuralNetworkController makeController() {
    //     return makeControllerFunc.get();
    // }

    // private NeuralNetworkController makeController() {
    //     NeuralNetworkController nnc = new NeuralNetworkController();
    //     for (NNInput x : controllerInputs)

    //     // addInputs(nnc);
    //     // addOutputs(nnc);
    //     return nnc;
    // }

    // private void addInputs(NeuralNetworkController nnc) {
    //     // String self = MLUtil.HORIZ_NAME;
    //     // String target = MLUtil.VERT_NAME;

    //     // own x pos
    //     nnc.addInput((TPProgram prog) -> {
    //             TPActor a = MLUtil.getHorizActor(prog);
    //             if (a != null)
    //                 return a.x;
    //             return 0;
    //         });

    //     // own y pos
    //     nnc.addInput((TPProgram prog) -> {
    //             TPActor a = MLUtil.getHorizActor(prog);
    //             if (a != null)
    //                 return a.y;
    //             return 0;
    //         });

    //     // target x pos
    //     nnc.addInput((TPProgram prog) -> {
    //             TPActor a = MLUtil.getVertActor(prog);
    //             if (a != null)
    //                 return a.x;
    //             return 0;
    //         });

    //     // target y pos
    //     nnc.addInput((TPProgram prog) -> {
    //             TPActor a = MLUtil.getVertActor(prog);
    //             if (a != null)
    //                 return a.y;
    //             return 0;
    //         });

    // }

    // // private void addOutputs(NeuralNetworkController nnc, TPProgram prog) {
    // private void addOutputs(NeuralNetworkController nnc) {

    //     ActorController8WayInertia ac = new ActorController8WayInertia();
    //     nnc.setActorController(ac);
    //     // TPActor horiz = MLUtil.getHorizActor(prog);
    //     // if (horiz != null)
    //     //     horiz.addBehaviour(ac);


    //     // UP
    //     nnc.addOutput((double val, double threshold) -> {
    //             if (val > threshold)
    //                 ac.UP = true;
    //             else
    //                 ac.UP = false;
    //         });

    //     // DOWN
    //     nnc.addOutput((double val, double threshold) -> {
    //             if (val > threshold)
    //                 ac.DOWN = true;
    //             else
    //                 ac.DOWN = false;
    //         });

    //     // LEFT
    //     nnc.addOutput((double val, double threshold) -> {
    //             if (val > threshold)
    //                 ac.LEFT = true;
    //             else
    //                 ac.LEFT = false;
    //         });

    //     // RIGHT
    //     nnc.addOutput((double val, double threshold) -> {
    //             if (val > threshold)
    //                 ac.RIGHT = true;
    //             else
    //                 ac.RIGHT = false;
    //         });
    // }

    // private NeuralNetworkController makeControllerCopy(TPOrganism tpOrg) {
    //     NeuralNetworkController nnc = masterController.copyExceptNetwork();
    //     nnc.setNetwork(tpOrg.org.net);
    //     return nnc;
    // }

    // private NeuralNetworkController makeControllerCopy() {
    //     return masterController.copyExceptNetwork();
    // }



    // public void setProgram(TPProgram prog) { this.prog = prog; }

    // public void getProgram() { return prog; }
    
    //                          List<TTInput> inputs, List<TTOutput> outputs,
    //                           Supplier<Double> fitnessFunction) {
    //     this.label = label;
    //     this.inputs = inputs;
    //     this.outputs = outputs;
    // }

    // public void setFitnessEngine(ToothpickFitness fitness) {
    //     this.fitness = fitness;
    // }

    // public void setFitness(double maxFitness, Supplier<Double> fitnessFunction) {
    //     this.maxFitness = maxFitness;
    //     this.fitnessFunction = fitnessFunction;
    // }

    // /** The maximum value which may be returned by the {@link getFitness}. */
    // public double getMaxFitness() { return maxFitness; }

    // /** Gets the current fitness of the */
    // public double getFitness(inputProg) { return fitnessFunction.invoke(inputProg); }

    // /** The maximum value which may be returned by the {@link getFitness}. */
    // public double getMaxFitness() { return fitness.getMaxFitness(); }

    // /** Gets the current fitness of the */
    // public double getFitness(inputProg) { return fitnessFunction.invoke(inputProg); }


    
    

    // public int arenaWidth = 1000;
    // public int arenaHeight = 800;

    // public double droneXPos = 0;
    // public double droneYPos = 0;
    // public double droneXInertia = 0;
    // public double droneYInertia = 0;
    // public double droneAngle = 0;
    // public double droneAngleInertia = 0;

    // public double droneXPosMAX = 0;
    // public double droneXPosMIN = 0;
    // public double droneYPosMAX = 0;
    // public double droneYPosMIN = 0;
    // public double droneXInertiaMAX = 0;
    // public double droneXInertiaMIN = 0;
    // public double droneYInertiaMAX = 0;
    // public double droneYInertiaMIN = 0;
    // public double droneAngleMAX = 0;
    // public double droneAngleMIN = 0;
    // public double droneAngleInertiaMAX = 0;
    // public double droneAngleInertiaMIN = 0;

    // /**
    //  * TODO: get rid of this
    //  */
    // public ToothpickTraining(String label) {
    //     this.label = label;
    //     this.inputs = new ArrayList<TTInput>();
    //     this.outputs = new ArrayList<TTOutput>();
    // }

    // public ToothpickTraining(String label, TPProgramSnapshot progSnapshot,
    //                          List<TTInput> inputs, List<TTOutput> outputs) {
    //     this.label = label;
    //     this.progSnapshot = progSnapshot;
    //     this.inputs = inputs;
    //     this.outputs = outputs;
    // }

    // public ToothpickTraining(String label, List<TTInput> inputs, List<TTOutput> outputs) {
    //     this.label = label;
    //     this.inputs = inputs;
    //     this.outputs = outputs;
    // }

    // public void setDronePositionRange(double xMin, double xMax, double yMin, double yMax) {
    //     droneXPosMIN = xMin;
    //     droneXPosMAX = xMax;
    //     droneYPosMIN = yMin;
    //     droneYPosMAX = yMax;
    // }
    
    // public void setDroneInertiaRange(double xMin, double xMax, double yMin, double yMax) {
    //     droneXInertiaMIN = xMin;
    //     droneXInertiaMAX = xMax;
    //     droneYInertiaMIN = yMin;
    //     droneYInertiaMAX = yMax;
    // }
    
    // public void setDroneAngleRange(double min, double max) {
    //     droneAngleMIN = min;
    //     droneAngleMAX = max;
    // }
    
    // public void setDroneAngleInertiaRange(double min, double max) {
    //     droneAngleInertiaMIN = min;
    //     droneAngleInertiaMAX = max;
    // }

    // /**
    //  * Sets new random values for drone position, inertia, angle and angle-inertia based
    //  * on the MIN/MAX values.
    //  */
    // public void nextGeneration() {
    //     droneXPos = TPFactory.rand(droneXPosMIN, droneXPosMAX);
    //     droneYPos = TPFactory.rand(droneYPosMIN, droneYPosMAX);
    //     droneXInertia = TPFactory.rand(droneXInertiaMIN, droneXInertiaMAX);
    //     droneYInertia = TPFactory.rand(droneYInertiaMIN, droneYInertiaMAX);
    //     droneAngle = TPFactory.rand(droneAngleMIN, droneAngleMAX);
    //     droneAngleInertia = TPFactory.rand(droneAngleInertiaMIN, droneAngleInertiaMAX);
    // }

    // /**
    //  * Sets drone position, inertia, angle and angle-inertia to the current stored values.
    //  */
    // public void setupDrone(TPActor a) {
    //     a.x = droneXPos;
    //     a.y = droneYPos;
    //     a.xInertia = droneXInertia;
    //     a.yInertia = droneYInertia;
    //     a.angle = droneAngle;
    //     a.angleInertia = droneAngleInertia;
    // }

    // fitness function for protagonist
    // ...

}
