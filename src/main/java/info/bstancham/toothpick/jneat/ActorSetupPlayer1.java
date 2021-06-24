package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.actor.TPPlayer;
import info.bstancham.toothpick.actor.KeyInputHandler;
import info.bstancham.toothpick.actor.KeyInputThrustInertia;

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
            if (fetchTarget(ttParams, tpOrg.program)) {
                tpOrg.program.clearPlayers();
                TPPlayer player = new TPPlayer();
                player.setInputHandler(inputHandler);
                player.setActor(target);
                tpOrg.program.addPlayer(player);

                tpOrg.setResetSnapshot();
            }
        }
    }

    @Override
    public void update(TPTrainingParams ttParams) {}

}
