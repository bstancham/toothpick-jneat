package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

/**
 * PROTAGONIST and TARGET both init to random position, TARGET moves in a straight line in
 * a randomly chosen direction and also teleports periodically.
 */
public class ActorSetupMobileTeleport extends ActorSetupStatic {

    private double maxInertia = 2.0;
    private int period = 1000;
    private int countdown = period;

    @Override
    public String getLabel() {
        return "mobile target, teleports every " + period + " iterations";
    }

    @Override
    public void init(TPTrainingParams ttParams) {
        super.init(ttParams);
        initTarget(ttParams);
    }

    private void initTarget(TPTrainingParams ttParams) {
        double xPos = Math.random() * ttParams.getGeometry().getWidth();
        double yPos = Math.random() * ttParams.getGeometry().getHeight();
        double xInertia = randInertia();
        double yInertia = randInertia();
        for (TPOrganism tpOrg : ttParams.organisms) {
            TPActor target = tpOrg.program.getActor(TPTrainingParams.getTargetID());
            if (target == null) {
                System.out.println("ActorSetupMobileTeleport: TARGET is NULL!");
            } else {
                target.x = xPos;
                target.y = yPos;
                target.xInertia = xInertia;
                target.yInertia = yInertia;
            }
        }
    }

    @Override
    public void update(TPTrainingParams ttParams) {
        countdown--;
        if (countdown <= 0) {
            initTarget(ttParams);
            countdown = period;
        }
    }

    private double randInertia() {
        return -maxInertia + (Math.random() * (2 * maxInertia));
    }

}
