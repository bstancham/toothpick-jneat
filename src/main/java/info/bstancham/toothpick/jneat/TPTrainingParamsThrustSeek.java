package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.PBRandActorSetup;
import info.bschambers.toothpick.TPBase;
import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.*;
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
 * <li>(self) angle</li>
 * </ul>
 *
 * <p>OUTPUTS:</p>
 * <ul>
 * <li>thrust up</li>
 * <li>thrust down</li>
 * <li>rotate left</li>
 * <li>rotate right</li>
 * </ul>
 */
public class TPTrainingParamsThrustSeek extends TPTrainingParams {

    protected static double inputScalingDistance = 0.1;
    protected static double inputScalingInertia = 50;
    protected static double inputScalingAngle = 1;

    private static NNInput INPUT_RELATIVE_POSITION_X
        = new NNInputRelativePosition(getProtagonistID(), getTargetID(), inputScalingDistance, NNInput.Dim.X);
    private static NNInput INPUT_RELATIVE_POSITION_Y
        = new NNInputRelativePosition(getProtagonistID(), getTargetID(), inputScalingDistance, NNInput.Dim.Y);
    private static NNInput INPUT_SELF_INERTIA_X
        = new NNInputInertia(getProtagonistID(), inputScalingInertia, NNInput.Dim.X);
    private static NNInput INPUT_SELF_INERTIA_Y
        = new NNInputInertia(getProtagonistID(), inputScalingInertia, NNInput.Dim.Y);
    private static NNInput INPUT_SELF_ANGLE
        = new NNInputAngle(getProtagonistID(), inputScalingAngle);

    public TPTrainingParamsThrustSeek(TPBase base) {
        super(base, "Thrust-Controller Seek (in=5, out=4)", "genome_in5_out4");
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
        ActorControllerThrustInertia ac = new ActorControllerThrustInertia();
        nnc.setActorController(ac);
        addInputs(nnc);
        addOutputs(nnc, ac);
        return nnc;
    }

    protected void addInputs(NeuralNetworkController nnc) {
        nnc.addInput(INPUT_RELATIVE_POSITION_X);
        nnc.addInput(INPUT_RELATIVE_POSITION_Y);
        nnc.addInput(INPUT_SELF_INERTIA_X);
        nnc.addInput(INPUT_SELF_INERTIA_Y);
        nnc.addInput(INPUT_SELF_ANGLE);
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
