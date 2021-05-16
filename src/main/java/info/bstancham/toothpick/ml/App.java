package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.*;
import info.bschambers.toothpick.actor.*;
import info.bschambers.toothpick.ui.*;
import info.bschambers.toothpick.ui.swing.TPSwingUI;
import java.awt.Color;
import java.io.File;
import java.util.List;
import jneat.gui.MainGui;
import jneat.misc.*;

public class App {

    private TPBase base;
    private ToothpickTrainingParams currentToothpickParams = null;
    private int numEpoch = 5;

    public App() {
        TPSwingUI ui = new TPSwingUI("ToothpickML");
        base = new TPBase();
        base.setUI(ui);
        ui.setVisible(true);

        // base.setProgram(testProg);
        base.setProgram(makeProgNumDrones(5));

        // base.setMenu(makeMenu());
        TPMenu menu = TPSwingUI.makeDefaultMenu(ui);
        menu.add(makeMenuPresetPrograms());
        menu.add(makeMenuSimultaneousPlatform());
        menu.add(makeMenuNeuralNetworkActor());
        menu.add(makeMenuLogicTrainingPrograms());
        menu.add(makeMenuToothpickTrainingPrograms());
        menu.add(makeMenuCurrentToothpickParams());
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
        m.add(new TPMenuItemIncr("num epochs: ", () -> "" + numEpoch,
                                 () -> incrNumEpoch(-1),
                                 () -> incrNumEpoch(1)));
        m.add(new TPMenuItemSimple("train point at stationary", () -> trainPointAtStationary()));
        m.add(new TPMenuItemSimple("train point at mobile", () -> trainPointAtMobile()));
        m.add(new TPMenuItemSimple("train seek stationary", () -> trainSeekStationary()));
        m.add(new TPMenuItemSimple("train seek mobile", () -> trainSeekMobile()));
        m.add(new TPMenuItemSimple("train keep moving/avoid edges", () -> trainKeepMovingAvoidEdges()));
        return m;
    }

    private void incrNumEpoch(int amt) {
        numEpoch += amt;
        if (numEpoch < 1)
            numEpoch = 1;
    }

    private void trainPointAtStationary() {
        TPTrainingParamsPointAt params = new TPTrainingParamsPointAt("Point at Stationary");
        params.mobileEnemy = false;
        trainToothpicks(params);
    }

    private void trainPointAtMobile() {
        TPTrainingParamsPointAt params = new TPTrainingParamsPointAt("Point at Mobile");
        params.mobileEnemy = true;
        trainToothpicks(params);
    }

    private void trainSeekStationary() {
        TPTrainingParamsSeek params = new TPTrainingParamsSeek("Seek Stationary");
        params.mobileEnemy = false;
        trainToothpicks(params);
    }

    private void trainSeekMobile() {
        TPTrainingParamsSeek params = new TPTrainingParamsSeek("Seek Mobile");
        params.mobileEnemy = true;
        trainToothpicks(params);
    }

    private void trainKeepMovingAvoidEdges() {
        TPTrainingParamsAvoidEdges params = new TPTrainingParamsAvoidEdges("Avoid Edges");
        trainToothpicks(params);
    }

    private void trainToothpicks(ToothpickTrainingParams ttParams) {
        MainGui gui = MainGui.launchGui();

        currentToothpickParams = ttParams;

        gui.sendToLogger("App: toothpick-training object - " + ttParams.label);
        gui.setToothpickTrainingParams(ttParams);

        gui.loadJneatParameters(getResourcePath("toothpick-neat-params"));
        gui.writeJneatParameters();

        String sessionParams = ""
            + "; Toothpick Game: " + ttParams.label + "\n"
            + "data_from_toothpick          Y\n"
            + "start_from_genome            Y\n"
            + "genome_file                  " + ttParams.getGenomeFilename() + "\n"
            + "population_file              primitive\n"
            + "epoch                        " + numEpoch + "\n"
            + "activation                   0\n"
            + "prefix_generation_file       generation\n"
            + "prefix_winner                winner\n";

        // NOTE: no need to compile classes because we're using extant classes
        gui.showAndWriteSessionParameters(sessionParams);

        // bring the "start simulation" panel to the front and select "graph champion"
        // from the radio buttons
        gui.showGenerationPanel();
        gui.selectOptionGraphChampion();
        gui.sendToLogger("App: Ready to run " + ttParams.label + "... just press START");
    }

    private TPMenu makeMenuCurrentToothpickParams() {
        TPMenu m = new TPMenu(() -> "CURRENT TOOTHPICK PARAMS (" + safeGetLabel(currentToothpickParams) + ")");
        m.add(makeProgMenu("play against current champion", makeProgAgainstChamp()));
        return m;
    }

    private TPProgram makeProgAgainstChamp() {
        TPProgram prog = MLUtil.makeProgHorizVsVert();
        prog.addBehaviour(champMatchResetBehaviour());
        prog.setResetSnapshot();
        return prog;
    }

    private ProgramBehaviour champMatchResetBehaviour() {

        final App app = this;

        ProgramBehaviour pb = new ProgramBehaviour() {
                @Override
                public void update(TPProgram prog) {

                    ToothpickTrainingParams params = app.currentToothpickParams;
                    if (params == null) {
                        System.out.println("currentToothpickParams = null");
                    } else {
                        if (params.getTheBestOne() == null) {
                            System.out.println("params.getTheBestOne() == null");
                        } else {

                            TPOrganism bestOrganism = params.getTheBestOne();
                            TPActor bestActor = bestOrganism.getActor();
                            System.out.println("the best actor is " + bestActor);

                            NeuralNetworkController nnc = bestOrganism.controller;
                            TPActor horiz = MLUtil.getHorizActor(prog);
                            horiz.addBehaviour(nnc);

                            TPPlayer player = TPFactory.player(MLUtil.getVertActor(prog));
                            prog.setPlayer(player);
                            prog.revivePlayer(true);

                        }
                    }
                }

                @Override
                public ProgramBehaviour copy() {
                    return this;
                }
            };

        return pb;
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
