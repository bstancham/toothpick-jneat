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
 * <li>(target) relative position, x</li>
 * <li>(target) relative position, y</li>
 * <li>(self) velocity, x</li>
 * <li>(self) velocity, y</li>
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
public class TPTrainingParamsSeek extends TPTrainingParams {

    protected static double inputScalingDistance = 0.1;
    protected static double inputScalingInertia = 50;

    private static NNInput INPUT_SELF_INERTIA_X
        = new NNInputInertia(getProtagonistID(), inputScalingInertia, NNInput.Dim.X);
    private static NNInput INPUT_SELF_INERTIA_Y
        = new NNInputInertia(getProtagonistID(), inputScalingInertia, NNInput.Dim.Y);
    private static NNInput INPUT_RELATIVE_POSITION_X
        = new NNInputRelativePosition(getProtagonistID(), getTargetID(), inputScalingDistance, NNInput.Dim.X);
    private static NNInput INPUT_RELATIVE_POSITION_Y
        = new NNInputRelativePosition(getProtagonistID(), getTargetID(), inputScalingDistance, NNInput.Dim.Y);

    public TPTrainingParamsSeek(TPBase base) {
        this(base, "Seek (in=4, out=4)", "genome_in4_out4");
    }

    public TPTrainingParamsSeek(TPBase base, String label, String genomeFilename) {
        super(base, label, genomeFilename);
    }

    @Override
    protected TPProgram makeMasterProgram() {
        return makeMasterProg1Target();
    }

    @Override
    protected ToothpickFitness makeFitness() {
        return new TPFitnessProximity();
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

    protected void addInputs(NeuralNetworkController nnc) {
        nnc.addInput(INPUT_SELF_INERTIA_X);
        nnc.addInput(INPUT_SELF_INERTIA_Y);
        nnc.addInput(INPUT_RELATIVE_POSITION_X);
        nnc.addInput(INPUT_RELATIVE_POSITION_Y);
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
