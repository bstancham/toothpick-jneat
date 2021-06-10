package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPBase;
import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.PBRandActorSetup;
import info.bschambers.toothpick.actor.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import jneat.neat.Organism;

/**
 * <p>Controller has 3 inputs and 2 outputs:</p>
 *
 * <p>INPUTS:</p>
 * <ul>
 * <li>(self) angle</li>
 * <li>(target) relative position, x</li>
 * <li>(target) relative position, y</li>
 * </ul>
 *
 * <p>OUTPUTS:</p>
 * <ul>
 * <li>decrease angle-inertia</li>
 * <li>increase angle-inertia</li>
 * </ul>
 */
public class TPTrainingParamsPointAt extends TPTrainingParams {

    protected static double inputScalingAngle = 1.0;
    protected static double inputScalingDistance = 0.1;

    private static NNInput INPUT_SELF_ANGLE
        = new NNInputAngle(getProtagonistID(), inputScalingAngle);
    private static NNInput INPUT_RELATIVE_POSITION_X
        = new NNInputRelativePosition(getProtagonistID(), getTargetID(), inputScalingDistance, NNInput.Dim.X);
    private static NNInput INPUT_RELATIVE_POSITION_Y
        = new NNInputRelativePosition(getProtagonistID(), getTargetID(), inputScalingDistance, NNInput.Dim.Y);

    public TPTrainingParamsPointAt(TPBase base) {
        super(base, "Point-At", "genome_in3_out2");
    }

    @Override
    protected TPProgram makeMasterProgram() {
        return makeMasterProg1Target();
    }

    @Override
    protected ToothpickFitness makeFitness() {
        return new TPFitnessPointAt();
    }

    @Override
    protected NeuralNetworkController makeController() {
        NeuralNetworkController nnc = new NeuralNetworkController();
        ActorControllerUDLR ac = new ActorControllerThrustAndAngleInertia();
        nnc.setActorController(ac);
        addInputs(nnc);
        addOutputs(nnc, ac);
        return nnc;
    }

    private void addInputs(NeuralNetworkController nnc) {
        nnc.addInput(INPUT_SELF_ANGLE);
        nnc.addInput(INPUT_RELATIVE_POSITION_X);
        nnc.addInput(INPUT_RELATIVE_POSITION_Y);
    }

    private void addOutputs(NeuralNetworkController nnc, ActorControllerUDLR ac) {

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
