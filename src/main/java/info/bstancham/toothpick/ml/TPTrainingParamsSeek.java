package info.bstancham.toothpick.ml;

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
public class TPTrainingParamsSeek extends ToothpickTrainingParams {

    private double inputScalingDistance = 0.1;
    private double inputScalingInertia = 10;

    public TPTrainingParamsSeek(TPBase base) {
        super(base, "Seek", "genome_toothpick_4_4");
    }

    @Override
    protected TPProgram makeMasterProgram() {
        TPProgram prog = MLUtil.makeProgHorizVsVertNoCollision();
        // set drone-actor to new random position at beginning of each generation
        PBRandActorSetup randDroneSetup = new PBRandActorSetup();
        randDroneSetup.setTarget(MLUtil.HORIZ_NAME, MLUtil.VERT_NAME);
        randDroneSetup.initBoundsRightHandSide(prog.getGeometry());
        if (targetIsMobile())
            randDroneSetup.initInertia(-1, 1);
        prog.addResetBehaviour(randDroneSetup);
        prog.setResetSnapshot();
        return prog;
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

    private void addInputs(NeuralNetworkController nnc) {

        // (target) relative position, x
        nnc.addInput((TPProgram prog) -> {
                TPActor self = MLUtil.getHorizActor(prog);
                TPActor target = MLUtil.getVertActor(prog);
                if (self == null || target == null)
                    return 0;
                double dist = target.x - self.x;
                return dist * inputScalingDistance;
            });

        // (target) relative position, y
        nnc.addInput((TPProgram prog) -> {
                TPActor self = MLUtil.getHorizActor(prog);
                TPActor target = MLUtil.getVertActor(prog);
                if (self == null || target == null)
                    return 0;
                double dist = target.y - self.y;
                return dist * inputScalingDistance;
            });

        // (self) inertia, x
        nnc.addInput((TPProgram prog) -> {
                TPActor self = MLUtil.getHorizActor(prog);
                if (self == null) return 0;
                return self.xInertia * inputScalingInertia;
            });

        // (self) inertia, y
        nnc.addInput((TPProgram prog) -> {
                TPActor self = MLUtil.getHorizActor(prog);
                if (self == null) return 0;
                return self.yInertia * inputScalingInertia;
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
