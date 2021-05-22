package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.*;
import info.bschambers.toothpick.actor.*;
import info.bschambers.toothpick.geom.Pt;
import info.bschambers.toothpick.ui.*;
import info.bschambers.toothpick.ui.swing.TPSwingUI;
import java.awt.Color;
import java.io.File;
import java.util.List;
import jneat.common.EnvConstant;
import jneat.gui.MainGui;
import jneat.misc.*;
import static info.bstancham.toothpick.ml.TPTrainingPlatform.Mode;

public class App {

    private TPBase base;
    private MainGui neatGui = null;

    private TPTrainingPlatform currentTrainingPlatform = null;

    public App() {
        TPSwingUI ui = new TPSwingUI("ToothpickML");
        base = new TPBase();
        base.setUI(ui);
        ui.setVisible(true);

        base.setProgram(makeProgNumDrones(5));

        TPMenu menu = TPSwingUI.makeDefaultMenu(ui);
        menu.add(makeMenuPresetPrograms());
        menu.add(makeMenuSimultaneousPlatform());
        menu.add(makeMenuNeuralNetworkActor());
        menu.add(makeMenuLogicTrainingPrograms());
        menu.add(makeMenuToothpickTrainingPrograms());
        base.setMenu(menu);
    }

    public void run() {
        base.run();
    }

    private TPMenu makeMenuPresetPrograms() {
        TPMenu m = new TPMenu("BASIC PROGRAMS");
        m.add(makeProgMenu("2 drones", makeProgNumDrones(2)));
        m.add(makeProgMenu("5 drones", makeProgNumDrones(5)));
        m.add(makeProgMenu("horiz vs vert", makeProgHorizVsVert()));
        m.add(makeProgMenu("horiz vs vert: seeker", makeProgHorizVertSeeker()));
        m.add(makeProgMenu("horiz vs vert: avoider", makeProgHorizVertAvoider()));
        return m;
    }

    private TPMenu makeProgMenu(String title, TPProgram prog) {
        return TPMenuFactory.makeProgramMenu(() -> title, prog, base);
    }

    private TPProgram makeProgNumDrones(int num) {
        TPProgram prog = new TPProgram();
        PBMaintainDronesNum dronesNum = new PBMaintainDronesNum();
        dronesNum.setDronesGoal(num);
        prog.addBehaviour(dronesNum);
        prog.addBehaviour(new PBToothpickPhysics());
        return prog;
    }

    private TPProgram makeProgHorizVsVert() {
        TPProgram prog = MLUtil.makeProgHorizVsVert();
        for (TPActor a : prog)
            TPFactory.setRandHeading(a);
        prog.setResetSnapshot();
        return prog;
    }

    private TPProgram makeProgHorizVertSeeker() {
        TPProgram prog = makeProgHorizVsVert();
        MLUtil.getHorizActor(prog).addBehaviour(new SeekerBehaviour());
        prog.setResetSnapshot();
        return prog;
    }

    private TPProgram makeProgHorizVertAvoider() {
        TPProgram prog = makeProgHorizVsVert();
        MLUtil.getVertActor(prog).addBehaviour(new AvoiderBehaviour());
        prog.setResetSnapshot();
        return prog;
    }

    private TPMenu makeMenuSimultaneousPlatform() {
        TPMenu m = new TPMenu("test SIMULTANEOUS PLATFORM");
        m.add(makeSimultaneousPlatformMenu("Simultaneous Platform - random direction", simultaneousPlatformRandDir()));
        m.add(makeSimultaneousPlatformMenu("Simultaneous Platform - random direction & rotation", simultaneousPlatformRandDirAndRotation()));
        m.add(makeSimultaneousPlatformMenu("Simultaneous Platform - seekers", simultaneousPlatformSeekers()));
        m.add(makeSimultaneousPlatformMenu("Simultaneous Platform - avoiders", simultaneousPlatformAvoiders()));
        return m;
    }

    private TPMenu makeSimultaneousPlatformMenu(String title, TPSimultaneousPlatform platform) {
        TPMenu m = new TPMenu(title);
        m.setInitAction(() -> {
                base.setPlatform(platform);
            });
        m.add(new TPMenuItemSimple("RUN", () -> {
                    base.hideMenu();
        }));
        m.add(new TPMenuItemBool("smear-mode",
                                 platform::isSmearMode,
                                 platform::setSmearMode));
        return m;
    }

    private TPSimultaneousPlatform simultaneousPlatformRandDir() {
        int numPrograms = 5;
        TPGeometry geom = new TPGeometry();
        geom.setupAndCenter(1000, 800);
        TPSimultaneousPlatform sp
            = new TPSimultaneousPlatform("Simultaneous Platform - random directions", geom);
        for (int i = 0; i < numPrograms; i++) {
            TPProgram prog = makeProgHorizVsVert();
            MLUtil.setActorColorRandGraduated(prog);
            prog.setResetSnapshot();
            sp.addProgram(prog);
        }
        return sp;
    }

    private TPSimultaneousPlatform simultaneousPlatformRandDirAndRotation() {
        int numPrograms = 5;
        TPGeometry geom = new TPGeometry();
        geom.setupAndCenter(1000, 800);
        TPSimultaneousPlatform sp
            = new TPSimultaneousPlatform("Simultaneous Platform - random direction & rotation", geom);
        for (int i = 0; i < numPrograms; i++) {
            TPProgram prog = makeProgHorizVsVert();
            MLUtil.setActorColorRandGraduated(prog);
            for (TPActor a : prog)
                a.angleInertia = TPFactory.randAngleInertia();
            sp.addProgram(prog);
        }
        return sp;
    }

    private TPSimultaneousPlatform simultaneousPlatformSeekers() {
        int numPrograms = 10;
        TPGeometry geom = new TPGeometry();
        geom.setupAndCenter(1000, 800);
        TPSimultaneousPlatform sp
            = new TPSimultaneousPlatform("Simultaneous Platform - random directions", geom);
        for (int i = 0; i < numPrograms; i++) {
            TPProgram prog = MLUtil.makeProgHorizVsVert();
            MLUtil.setActorColorRandGraduated(prog);
            TPFactory.setRandHeading(MLUtil.getVertActor(prog));
            MLUtil.getHorizActor(prog).addBehaviour(new SeekerBehaviour());
            sp.addProgram(prog);
        }
        return sp;
    }

    private TPSimultaneousPlatform simultaneousPlatformAvoiders() {
        TPGeometry geom = new TPGeometry();
        geom.setupAndCenter(1000, 800);
        TPSimultaneousPlatform sp
            = new TPSimultaneousPlatform("Simultaneous Platform - random directions", geom);
        int numPrograms = 10;
        for (int i = 0; i < numPrograms; i++) {
            TPProgram prog = makeProgHorizVsVert();
            MLUtil.setActorColorRandGraduated(prog);
            MLUtil.getVertActor(prog).addBehaviour(new AvoiderBehaviour());
            sp.addProgram(prog);
        }
        return sp;
    }

    private TPMenu makeMenuNeuralNetworkActor() {
        TPMenu m = new TPMenu("test NEURAL NETWORK CONTROLLER");
        m.add(makeProgMenu("neural network test (1)", makeProgNeuralNetworkTest1()));
        return m;
    }

    private TPProgram makeProgNeuralNetworkTest1() {
        TPProgram prog = MLUtil.makeProgHorizVsVert();

        TPActor horiz = MLUtil.getHorizActor(prog);
        TPActor vert = MLUtil.getVertActor(prog);

        TPFactory.setRandHeading(vert);

        NeuralNetworkController nnc = new NeuralNetworkController();
        horiz.addBehaviour(nnc);

        prog.setResetSnapshot();
        return prog;
    }

    private TPMenu makeMenuLogicTrainingPrograms() {
        TPMenu m = new TPMenu("jneat training: LOGIC GATES");
        m.add(new TPMenuItemSimple("jneat gui - train XOR", () -> trainXOR()));
        m.add(new TPMenuItemSimple("jneat gui - train AND", () -> trainAND()));
        m.add(new TPMenuItemSimple("jneat gui - train OR", () -> trainOR()));
        m.add(new TPMenuItemSimple("jneat gui - train XNOR", () -> trainXNOR()));
        return m;
    }

    private void trainXOR() {
        trainLogicGate("XOR", "jneat.misc.XorOut");
    }

    private void trainAND() {
        trainLogicGate("AND", "jneat.misc.AndOut");
    }

    private void trainOR() {
        trainLogicGate("OR", "jneat.misc.OrOut");
    }

    private void trainXNOR() {
        trainLogicGate("XNOR", "jneat.misc.XnorOut");
    }

    private void trainLogicGate(String label, String targetDataFile) {
        MainGui gui = MainGui.launchGui();

        gui.loadJneatParameters(getResourcePath("standard-neat-params"));
        gui.writeJneatParameters();

        String sessionParams = ""
            + "; " + label + " logic gate simulation with input from file\n"
            + ";data_from_file               Y\n"
            + "data_from_class              Y\n"
            + "data_input                   jneat.misc.BinaryInput\n"
            + "data_target                  " + targetDataFile + "\n"
            + "class_compute_fitness        jneat.misc.LogicGateFit\n"
            + "start_from_genome            Y\n"
            + "genome_file                  genome\n"
            + ";start_from_random_population Y\n"
            + ";start_from_old_population    Y\n"
            + "population_file              primitive\n"
            + ";maximum_unit                 5\n"
            + ";recursion                    N\n"
            + ";probability_of_connection    20\n"
            + "epoch                        100\n"
            + "activation                   0\n"
            + ";prefix_genome_random         genome.rnd \n"
            + "prefix_generation_file       generation\n"
            + "prefix_winner                winner\n";

        // NOTE: no need to compile classes because we're using extant classes
        gui.showAndWriteSessionParameters(sessionParams);

        // bring the "start simulation" panel to the front and select "graph champion"
        // from the radio buttons
        gui.showGenerationPanel();
        gui.selectOptionGraphChampion();
        gui.sendToLogger("Ready to train " + label + "... just press START");
    }

    private TPMenu makeMenuToothpickTrainingPrograms() {
        TPMenu m = new TPMenu("jneat training: TOOTHPICK");
        m.add(new TPMenuItemSimple("Test platform (no training)", () -> System.out.println("... todo...")));
        m.add(makeMenuTPTraining(new TPTrainingParamsPointAt(base)));
        m.add(makeMenuTPTraining(new TPTrainingParamsSeek(base)));
        m.add(makeMenuTPTraining(new TPTrainingParamsAvoidEdges(base)));
        return m;
    }

    private TPMenu makeMenuTPTraining(ToothpickTrainingParams ttParams) {
        ToothpickTrainingRunner runner = new ToothpickTrainingRunner(ttParams);
        TPMenu m = new TPMenu("train " + ttParams.label);
        // m.add(new TPMenuItemSimple("START TRAINING", () -> trainToothpicks(runner)));
        // m.add(new TPMenuItemSimple("START TRAINING (NEW VERSION)",
        //                            () -> trainToothpicksNEW(new TPTrainingRunner(runner.getParams()))));
        // m.add(new TPMenuItemSimple("START TRAINING (NEW SIMULTANEOUS PLATFORM VERSION)",
        //                            () -> trainToothpicksSP(ttParams)));
        m.add(makeTrainingPlatformMenu(makeTrainingPlatform(ttParams)));
        m.add(new TPMenuItemIncr("num epochs: ", () -> "" + ttParams.numEpoch,
                                 () -> incrNumEpoch(ttParams, -1),
                                 () -> incrNumEpoch(ttParams, 1)));
        m.add(new TPMenuItemIncr("population size: ", () -> "" + ttParams.populationSize,
                                 () -> incrPopulationSize(ttParams, -1),
                                 () -> incrPopulationSize(ttParams, 1)));
        m.add(new TPMenuItemBool("mobile target: ",
                                 ttParams::targetIsMobile,
                                 ttParams::setTargetIsMobile));
        m.add(new TPMenuItemIncr("iterations per generation: ",
                                 () -> "" + ttParams.iterationsPerGeneration,
                                 () -> incrIterations(ttParams, -100),
                                 () -> incrIterations(ttParams, 100)));
        return m;
    }

    private TPMenu makeTrainingPlatformMenu(TPTrainingPlatform platform) {
        TPMenu m = new TPMenu("TRAINING PLATFORM (" + platform.getTitle() + ")");
        m.setInitAction(() -> resetExperiment(platform));
        m.add(new TPMenuItemSimple(() -> runButtonText(platform), () -> runButtonAction(platform)));
        m.add(new TPMenuItemBool("smear-mode: ",
                                 platform::isSmearMode,
                                 platform::setSmearMode));
        m.add(new TPMenuItemSimple(() -> rerunButtonText(platform), () -> rerunButtonAction(platform)));
        m.add(makeProgMenu("play against current champion", makeProgChampMatch(platform)));
        m.add(new TPMenuItemSimple("reset experiment", () -> resetExperiment(platform)));
        return m;
    }

    private void resetExperiment(TPTrainingPlatform platform) {
        System.out.println("reset experiment --> TPTrainingPlatform: " + platform.getTitle());

        if (neatGui == null)
            neatGui = MainGui.launchGui();

        neatGui.setToothpickTrainingParams(platform.getParams());
        // EnvConstant.TOOTHPICK_TRAINING_PARAMS = platform.getParams();

        // write jneat params & session params
        neatGui.loadJneatParameters(getResourcePath("toothpick-neat-params"));
        neatGui.writeJneatParameters();
        neatGui.showAndWriteSessionParameters(platform.getParams().makeSessionParams());

        // bring the "start simulation" panel to the front and select "graph champion"
        // from the radio buttons
        neatGui.showGenerationPanel();
        neatGui.selectOptionGraphChampion();

        // doing this before initTraining seems to sometimes create some exceptions relating to the GUI...
        // ... init params before initTraining, so that mobile-target will take effect in first iteration
        // ... otherwise, mobile target won't take effect till initGeneration is called in second gen
        platform.getParams().init();

        platform.initTraining(neatGui);
        base.setPlatform(platform);

        // used in creating champ-match
        currentTrainingPlatform = platform;
    }

    private String runButtonText(TPTrainingPlatform platform) {
        Mode m = platform.getMode();
        if (m == Mode.READY_TO_TRAIN)
            return "START TRAINING (" + platform.getParams().numEpoch + " generations of "
                + platform.getParams().iterationsPerGeneration + " iterations)";
        else if (m == Mode.TRAINING)
            return "CONTINUE TRAINING (gen " + platform.getCurrentGeneration() + " of "
                + platform.getParams().numEpoch + " | iter "
                + platform.getCurrentIteration() + " of "
                + platform.getParams().iterationsPerGeneration + ")";
        else if (m == Mode.TRAINING_ENDED)
            return "CONTINUE RUNNING (training ended after "
                + platform.getParams().numEpoch + " generations of "
                + platform.getParams().iterationsPerGeneration + " iterations)";
        else if (m == Mode.READY_TO_RERUN)
            return "START RE-RUN (generation " + platform.getCurrentGeneration() + ")";
        else if (m == Mode.RERUN)
            return "CONTINUE RE-RUN (gen " + platform.getCurrentGeneration() + " | iter "
                + platform.getCurrentIteration() + ")";
        else
            return "ERROR! TPTrainingPlatform.Mode " + m + " not recognised!";
    }

    private void runButtonAction(TPTrainingPlatform platform) {
        base.setPlatform(platform);
        base.hideMenu();
    }

    private String rerunButtonText(TPTrainingPlatform platform) {
        Mode m = platform.getMode();
        if (m == Mode.READY_TO_TRAIN) {
            return "can't re-run - training has not started";
        } else if (m == Mode.TRAINING || m == Mode.TRAINING_ENDED) {
            return "re-run current generation";
        } else if (m == Mode.READY_TO_RERUN || m == Mode.RERUN) {
            return "return to training";
        } else {
            return "ERROR! TPTrainingPlatform.Mode " + m + " not recognised!";
        }
    }

    private void rerunButtonAction(TPTrainingPlatform platform) {
        Mode m = platform.getMode();
        if (m == Mode.READY_TO_TRAIN) {
            return;
        } else if (m == Mode.TRAINING || m == Mode.TRAINING_ENDED) {
            platform.initRerun();
        } else if (m == Mode.READY_TO_RERUN || m == Mode.RERUN) {
            platform.cancelRerun();
        } else {
            System.out.println("ERROR! TPTrainingPlatform.Mode " + m + " not recognised!");
        }
    }

    private TPTrainingPlatform makeTrainingPlatform(ToothpickTrainingParams ttParams) {
        return new TPTrainingPlatform(ttParams, base);
    }

    private TPProgram makeProgChampMatch(TPTrainingPlatform platform) {
        TPProgram prog = new TPProgram("play against current champion");
        prog.addResetBehaviour(new PBMisc(this::setupChampMatch));
        prog.init();
        prog.setResetSnapshot();
        return prog;
    }

    private void setupChampMatch(TPProgram prog) {
        TPTrainingPlatform platform = currentTrainingPlatform;
        if (platform == null) {
            System.out.println("setupChampMatch() --> currentTrainingPlatform = NULL!");
        } else {
            // champion
            TPOrganism champ = platform.getFittestTPOrganism();
            System.out.println("CREATING CHAMP-MATCH AGAINST: " + champ);
            if (champ != null) {
                // champ.setDebugMode(true);
                prog.addActor(champ.getActor());
            }
            // player
            TPPlayer player = TPFactory.playerLine(new Pt(300, 300));
            player.getArchetype().name = MLUtil.VERT_NAME;
            player.reset();
            prog.setPlayer(player);
        }
    }

    private void incrNumEpoch(ToothpickTrainingParams params, int amt) {
        params.numEpoch += amt;
        if (params.numEpoch < 1)
            params.numEpoch = 1;
    }

    private void incrPopulationSize(ToothpickTrainingParams params, int amt) {
        params.populationSize += amt;
        if (params.populationSize < 1)
            params.populationSize = 1;
    }

    private void incrIterations(ToothpickTrainingParams params, int amt) {
        params.iterationsPerGeneration += amt;
        if (params.iterationsPerGeneration < 100)
            params.iterationsPerGeneration = 100;
    }

    private String safeGetLabel(ToothpickTrainingParams params) {
        if (params == null)
            return "NULL";
        return params.label;
    }

    private String getResourcePath(String resourceName) {
        String resourcesPath = "resources/main";
        return new File(resourcesPath + "/" + resourceName).getAbsolutePath();
    }

    public static void main(String[] args) {
        new App().run();
    }

}
