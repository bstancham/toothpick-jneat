package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;
import info.bschambers.toothpick.actor.TPPlayer;
import info.bschambers.toothpick.actor.KeyInputHandler;
import info.bschambers.toothpick.actor.KeyInputThrustInertia;

public class TargetSetupPlayer1 extends TargetSetup {

    private KeyInputHandler inputHandler = new KeyInputThrustInertia();
    private boolean firstIteration = true;
    private int count = 0;
    
    public TargetSetupPlayer1() { super("player 1"); }

    @Override
    public void init(TPTrainingParams ttParams) {
        firstIteration = true;
        count = 0;
    }

    @Override
    public void update(TPTrainingParams ttParams) {
        count++;
        if (count == 5) {
        // if (firstIteration) {
        //     firstIteration = false;
            for (TPOrganism tpOrg : ttParams.organisms) {
                TPActor target = MLUtil.getTargetActor(tpOrg.program);
                if (target != null) {

                    // // player.setActor(target);
                    // TPPlayer player = new TPPlayer(target);
                    // player.setInputHandler(inputHandler);
                    // tpOrg.program.clearPlayers();
                    // tpOrg.program.addPlayer(player);

                    // player.setActor(target);

                    tpOrg.program.clearPlayers();

                    TPPlayer player = new TPPlayer();
                    player.setInputHandler(inputHandler);
                    player.setActor(target);

                    tpOrg.program.addPlayer(player);

                }
            }
        }
    }

}
