package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;
import info.bschambers.toothpick.actor.TPPlayer;
import info.bschambers.toothpick.actor.KeyInputHandler;
import info.bschambers.toothpick.actor.KeyInputThrustInertia;

/**
 * PROTAGONIST and TARGET both init to random position, TARGET is equipped with a
 * key-input controller for manual control.
 */
public class ActorSetupPlayer1 extends ActorSetupStatic {

    private KeyInputHandler inputHandler = new KeyInputThrustInertia();

    @Override
    public String getLabel() {
        return "target is player (thrust-inertia controller)";
    }

    @Override
    public void init(TPTrainingParams ttParams) {
        super.init(ttParams);
        // add key-input controller to each target-actor
        for (TPOrganism tpOrg : ttParams.organisms) {
            TPActor target = MLUtil.getTargetActor(tpOrg.program);
            if (target != null) {
                tpOrg.program.clearPlayers();
                TPPlayer player = new TPPlayer();
                player.setInputHandler(inputHandler);
                player.setActor(target);
                tpOrg.program.addPlayer(player);
            }
        }
    }

    @Override
    public void update(TPTrainingParams ttParams) {}

}
