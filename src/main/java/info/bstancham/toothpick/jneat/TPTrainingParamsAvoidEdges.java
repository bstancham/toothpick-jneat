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

    protected double inputScalingDistance = 1;
    protected double inputScalingInertia = 10;

    public TPTrainingParamsAvoidEdges(TPBase base) {
        super(base, "Avoid-Edges", "genome_in6_out4");
    }

    /** WARNING! Returns null if actor with name {@link PROTAGONIST_NAME} does not exist. */
    public static TPActor getProtagonist(TPProgram prog) {
        return MLUtil.getHorizActor(prog);
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

        // (self) inertia, x
        nnc.addInput((TPProgram prog) -> {
                TPActor a = getProtagonist(prog);
                if (a == null) return 0;
                return a.xInertia * inputScalingInertia;
            });

        // (self) inertia, y
        nnc.addInput((TPProgram prog) -> {
                TPActor a = getProtagonist(prog);
                if (a == null) return 0;
                return a.yInertia * inputScalingInertia;
            });

        // distance to left hand boundary
        nnc.addInput((TPProgram prog) -> {
                TPActor a = getProtagonist(prog);
                if (a == null) return 0;
                return a.x * inputScalingDistance;
            });

        // distance to right hand boundary
        nnc.addInput((TPProgram prog) -> {
                TPActor a = getProtagonist(prog);
                if (a == null) return 0;
                return (prog.getGeometry().getWidth() - a.x) * inputScalingDistance;
            });

        // distance to bottom boundary
        nnc.addInput((TPProgram prog) -> {
                TPActor a = getProtagonist(prog);
                if (a == null) return 0;
                return a.y * inputScalingDistance;
            });

        // distance to top boundary
        nnc.addInput((TPProgram prog) -> {
                TPActor a = getProtagonist(prog);
                if (a == null) return 0;
                return (prog.getGeometry().getHeight() - a.y) * inputScalingDistance;
            });
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
