package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.ProgRandActorSetup;
import info.bstancham.toothpick.TPBase;
import info.bstancham.toothpick.TPProgram;
import info.bstancham.toothpick.actor.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import jneat.neat.Organism;

/**
 * <p>Controller has 4 inputs and 4 outputs:</p>
 *
 * <p>INPUTS:</p>
 * <ul>
 * <li>(self) position - x</li>
 * <li>(self) position - y</li>
 * <li>(self) inertia - x</li>
 * <li>(self) inertia - y</li>
 * </ul>
 *
 * <p>OUTPUTS:</p>
 * <ul>
 * <li>thrust up</li>
 * <li>thrust down</li>
 * <li>thrust left</li>
 * <li>thrust right</li>
 * </ul>
 */
public class TPTrainingParamsAvoidEdges extends TPTrainingParams {

    protected static double inputScalingDistance = 0.01;
    protected static double inputScalingInertia = 10;

    private static NNInput INPUT_SELF_INERTIA_X
        = new NNInputInertia(getProtagonistID(), inputScalingInertia, NNInput.Dim.X);
    private static NNInput INPUT_SELF_INERTIA_Y
        = new NNInputInertia(getProtagonistID(), inputScalingInertia, NNInput.Dim.Y);
    private static NNInput INPUT_SELF_BOUNDARY_DIST_LEFT
        = new NNInputBoundaryDist(getProtagonistID(), inputScalingDistance, NNInput.Dir.LEFT);
    private static NNInput INPUT_SELF_BOUNDARY_DIST_RIGHT
        = new NNInputBoundaryDist(getProtagonistID(), inputScalingDistance, NNInput.Dir.RIGHT);
    private static NNInput INPUT_SELF_BOUNDARY_DIST_BOTTOM
        = new NNInputBoundaryDist(getProtagonistID(), inputScalingDistance, NNInput.Dir.BOTTOM);
    private static NNInput INPUT_SELF_BOUNDARY_DIST_TOP
        = new NNInputBoundaryDist(getProtagonistID(), inputScalingDistance, NNInput.Dir.TOP);

    public TPTrainingParamsAvoidEdges(TPBase base) {
        super(base, "Avoid-Edges", "genome_in6_out4");
        actorSetup = new ActorSetupAllCenter();
    }

    /** WARNING! Returns null if actor with name {@link PROTAGONIST_NAME} does not exist. */
    @Deprecated
    public static TPActor getProtagonist(TPProgram prog) {
        return prog.getActor(getProtagonistID());
    }

    @Override
    protected TPProgram makeMasterProgram() {
        return makeMasterProgNoTarget();
    }

    @Override
    protected ToothpickFitness makeFitness() {
        return new TPFitnessVelocityAndDontDie();
    }

    @Override
    protected NeuralNetworkController makeController() {
        NeuralNetworkController nnc = new NeuralNetworkController();
        ActorController8WayInertia ac = new ActorController8WayInertia();
        nnc.setActorController(ac);
        addInputs(nnc);
        addOutputs(nnc, ac);
        return nnc;
    }

    private void addInputs(NeuralNetworkController nnc) {
        nnc.addInput(INPUT_SELF_INERTIA_X);
        nnc.addInput(INPUT_SELF_INERTIA_Y);
        nnc.addInput(INPUT_SELF_BOUNDARY_DIST_LEFT);
        nnc.addInput(INPUT_SELF_BOUNDARY_DIST_RIGHT);
        nnc.addInput(INPUT_SELF_BOUNDARY_DIST_TOP);
        nnc.addInput(INPUT_SELF_BOUNDARY_DIST_BOTTOM);
    }

    private void addOutputs(NeuralNetworkController nnc, ActorControllerUDLR ac) {

        // UP
        nnc.addOutput((double val, double threshold) -> {
                if (val > threshold)
                    ac.UP = true;
                else
                    ac.UP = false;
            });

        // DOWN
        nnc.addOutput((double val, double threshold) -> {
                if (val > threshold)
                    ac.DOWN = true;
                else
                    ac.DOWN = false;
            });

        // LEFT
        nnc.addOutput((double val, double threshold) -> {
                if (val > threshold)
                    ac.LEFT = true;
                else
                    ac.LEFT = false;
            });

        // RIGHT
        nnc.addOutput((double val, double threshold) -> {
                if (val > threshold)
                    ac.RIGHT = true;
                else
                    ac.RIGHT = false;
            });
    }

}
