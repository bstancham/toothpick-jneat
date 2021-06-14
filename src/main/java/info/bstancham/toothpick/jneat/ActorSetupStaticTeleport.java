package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

/**
 * PROTAGONIST and TARGET both init to random static position, TARGET teleports
 * periodically.
 */
public class ActorSetupStaticTeleport extends ActorSetupStatic {

    private int period = 1000;
    private int countdown = period;

    @Override
    public String getLabel() {
        return "static target, teleports every " + period + " iterations";
    }

    @Override
    public void update(TPTrainingParams ttParams) {
        countdown--;
        if (countdown <= 0) {
            double x = Math.random() * ttParams.getGeometry().getWidth();
            double y = Math.random() * ttParams.getGeometry().getHeight();
            for (TPOrganism tpOrg : ttParams.organisms) {
                TPActor target = MLUtil.getTargetActor(tpOrg.program);
                if (target != null) {
                    target.x = x;
                    target.y = y;
                }
            }
            countdown = period;
        }
    }

}
