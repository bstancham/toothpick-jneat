package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.*;
import info.bschambers.toothpick.actor.*;
import info.bschambers.toothpick.geom.Pt;
import info.bschambers.toothpick.ui.*;
import info.bschambers.toothpick.ui.swing.TPSwingUI;
import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.function.Supplier;
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

        EnvConstant.RESOURCE_DIR = "resources";
        gui.loadJneatParameters(getResourcePath("default-neat-params"));
        gui.writeJneatParameters();

        String sessionParams = ""
            + "; " + label + " logic gate simulation with input from file\n"
            + ";data_from_file               Y\n"
            + "data_from_class              Y\n"
            + "data_input                   jneat.misc.BinaryInput\n"
            + "data_target                  " + targetDataFile + "\n"
            + "class_compute_fitness        jneat.misc.LogicGateFit\n"
            + "start_from_genome            Y\n"
            + "genome_file                  genome_in2_out1\n"
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
        m.add(makeMenuTPTraining(new TPTrainingParamsSeekIn6Out4(base)));
        m.add(makeMenuTPTraining(new TPTrainingParamsAvoidEdges(base)));
        return m;
    }

    private TPMenu makeMenuTPTraining(ToothpickTrainingParams ttParams) {
        TPMenu m = new TPMenu("train " + ttParams.label);
        m.add(makeTrainingPlatformMenu(makeTrainingPlatform(ttParams)));
        m.add(new TPMenuItemIncr("num epochs: ", () -> "" + ttParams.numEpoch,
                                 () -> incrNumEpoch(ttParams, -1),
                                 () -> incrNumEpoch(ttParams, 1)));
        m.add(new TPMenuItemIncr("population size: ", () -> "" + ttParams.populationSize,
                                 () -> incrPopulationSize(ttParams, -1),
                                 () -> incrPopulationSize(ttParams, 1)));
        m.add(new TPMenuItemIncr("iterations per generation: ",
                                 () -> "" + ttParams.iterationsPerGeneration,
                                 () -> incrIterations(ttParams, -100),
                                 () -> incrIterations(ttParams, 100)));
        m.add(makeMenuTargetChooser(ttParams));
        return m;
    }

    private TPMenu makeTrainingPlatformMenu(TPTrainingPlatform platform) {
        TPMenu m = new TPMenu("TRAINING PLATFORM (" + platform.getTitle() + ")");
        m.setInitAction(() -> resetExperiment(platform));
        m.add(new TPMenuItemSimple(() -> runButtonText(platform), () -> runButtonAction(platform)));
        m.add(new TPMenuItemSimple(() -> "extend training by " + platform.getNumGensExtend() + " generations",
                                   () -> {
                                       System.out.println("EXTEND");
                                       platform.extendTraining();
        }));
        m.add(new TPMenuItemIncr("(extend training) for N generations: ",
                                 () -> "" + platform.getNumGensExtend(),
                                 () -> incrNumGensExtend(platform, -1),
                                 () -> incrNumGensExtend(platform, 1)));
        m.add(new TPMenuItemBool("smear-mode: ",
                                 platform::isSmearMode,
                                 platform::setSmearMode));
        m.add(new TPMenuItemSimple(() -> rerunButtonText(platform), () -> rerunButtonAction(platform)));
        m.add(new TPMenuItemIncr("(in re-run) retain N fittest: ", () -> "" + platform.numRetainForRerun,
                                 () -> incrRetainForRerun(platform, -1),
                                 () -> incrRetainForRerun(platform, 1)));
        m.add(makeChampMatchMenu(platform));
        m.add(new TPMenuItemSimple("RESET EXPERIMENT!", () -> resetExperiment(platform)));
        return m;
    }

    private void incrNumGensExtend(TPTrainingPlatform platform, int amt) {
        platform.numGensExtend += amt;
        if (platform.numGensExtend < 1)
            platform.numGensExtend = 1;
    }

    private void incrRetainForRerun(TPTrainingPlatform platform, int amt) {
        platform.numRetainForRerun += amt;
        if (platform.numRetainForRerun < 1)
            platform.numRetainForRerun = 1;
        else if (platform.numRetainForRerun > platform.getParams().numTPOrganisms())
            platform.numRetainForRerun = platform.getParams().numTPOrganisms();
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
            return "START TRAINING (" + platform.getNumEpoch() + " generations of "
                + platform.getParams().iterationsPerGeneration + " iterations)";
        else if (m == Mode.TRAINING)
            return "CONTINUE TRAINING (gen " + platform.getCurrentGeneration() + " of "
                + platform.getNumEpoch() + " | iter "
                + platform.getCurrentIteration() + " of "
                + platform.getParams().iterationsPerGeneration + ")";
        else if (m == Mode.TRAINING_ENDED)
            return "CONTINUE RUNNING (training ended after "
                + platform.getNumEpoch() + " generations of "
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

    private TPMenu makeChampMatchMenu(TPTrainingPlatform platform) {
        // create champ-match program
        TPProgram prog = new TPProgram("play against current champion");
        ChampMatchSetup champSetup = new ChampMatchSetup();
        prog.addResetBehaviour(champSetup);
        // prog.setSmearMode(true);
        prog.init();
        prog.setResetSnapshot();
        // make champ-match menu
        TPMenu m = makeProgMenu("play against current champion", prog);
        m.add(makeMenuSwitchChampion(champSetup, platform));
        return m;
    }

    private class ChampMatchSetup implements ProgramBehaviour {

        private TPOrganism champ = null;

        @Override
        public ChampMatchSetup copy() {
            ChampMatchSetup out = new ChampMatchSetup();
            out.champ = champ;
            return this;
        };

        @Override
        public void update(TPProgram prog) {
            TPTrainingPlatform platform = currentTrainingPlatform;
            if (platform == null) {
                System.out.println("setupChampMatch() --> currentTrainingPlatform = NULL!");
            } else {
                // champion
                if (champ == null) {
                    // champ = platform.getFittestTPOrganism();
                    if (!platform.getFitList().isEmpty())
                        champ = platform.getFitList().get(0);
                }
                if (champ == null) {
                    System.out.println("ERROR! CANNOT FIND A CHAMPION!");
                } else {
                    System.out.println("CREATING CHAMP-MATCH AGAINST: " + champ + " (fitness="
                                       + champ.org.getFitness() + ")");
                    // champ.setDebugMode(true);
                    prog.addActor(champ.getActor());
                }
                // player
                TPPlayer player = TPFactory.playerLine(new Pt(300, 300));
                player.getArchetype().name = MLUtil.VERT_NAME;
                player.getArchetype().setColorGetter(new ColorRandom());
                player.reset();
                if (prog.numPlayers() > 0) {
                    prog.setPlayer(0, player);
                } else {
                    prog.addPlayer(player);
                }
            }
        }

        public void setChampion(TPOrganism tpOrg) {
            champ = tpOrg;
        }
    }

    private TPMenu makeMenuSwitchChampion(ChampMatchSetup champSetup, TPTrainingPlatform platform) {
        TPMenu m = new TPMenu("switch champion");
        m.setInitAction(() -> {
                m.clear();
                int n = 0;
                for (TPOrganism tpo : platform.getFitList()) {
                    n++;
                    m.add(makeChampionSwitcherOption("TOP " + n, tpo, champSetup));
                }
                m.add(TPMenuItem.SPACER);
                n = 0;
                for (TPOrganism tpo : platform.getParams().organisms) {
                    n++;
                    m.add(makeChampionSwitcherOption("CURRENT " + n, tpo, champSetup));
                }
            });
        return m;
    }

    private TPMenuItem makeChampionSwitcherOption(String label, TPOrganism tpo,
                                                  ChampMatchSetup champSetup) {
        return new TPMenuItemSimple(label + ": fitness=" + tpo.org.getFitness(),
                                    () -> {
                                        champSetup.setChampion(tpo);
                                        System.out.println("switched champion to " + tpo
                                                           + " (fitness=" + tpo.org.getFitness() + ")");
        });
    }

    private TPMenu makeMenuTargetChooser(ToothpickTrainingParams ttParams) {
        TPMenu m = new TPMenu(() -> "target: " + ttParams.getTargetSetupLabel());
        m.add(makeTargetChooserItem(ttParams, new TargetSetupStatic()));
        m.add(makeTargetChooserItem(ttParams, new TargetSetupStaticTeleport()));
        m.add(makeTargetChooserItem(ttParams, new TargetSetupMobile()));
        m.add(makeTargetChooserItem(ttParams, new TargetSetupMobileChangeDir()));
        m.add(makeTargetChooserItem(ttParams, new TargetSetupMobileTeleport()));
        return m;
    }

    private TPMenuItem makeTargetChooserItem(ToothpickTrainingParams ttParams,
                                             TargetSetup setup) {
        return new TPMenuItemSimple(setup.getLabel(), () -> {
                ttParams.targetSetup = setup;
                System.out.println("new target-setup: " + ttParams.getTargetSetupLabel());
        });
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
        // String resourcesPath = "resources/main";
        String resourcesPath = EnvConstant.RESOURCE_DIR;
        return new File(resourcesPath + "/" + resourceName).getAbsolutePath();
    }

    public static void main(String[] args) {
        new App().run();
    }

}
