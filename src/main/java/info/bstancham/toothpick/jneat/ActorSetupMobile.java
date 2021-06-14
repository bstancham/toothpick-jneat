package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.actor.TPActor;

/**
 * PROTAGONIST and TARGET both init to random position, TARGET moves in a straight line in
 * a randomly chosen direction.
 */
public class ActorSetupMobile extends ActorSetupStatic {

    private double max = 2.0;

    @Override
    public String getLabel() {
        return "mobile target";
    }

    @Override
    public void init(TPTrainingParams ttParams) {
        super.init(ttParams);
        double xInertia = randInertia();
        double yInertia = randInertia();
        for (TPOrganism tpOrg : ttParams.organisms) {
            TPActor target = tpOrg.program.getActor(TPTrainingParams.getTargetID());
            if (target == null) {
                    System.out.println("ActorSetupMobile: TARGET is NULL!");
            } else {
                target.xInertia = xInertia;
                target.yInertia = yInertia;
            }
        }
    }

    private double randInertia() {
        return -max + (Math.random() * (2 * max));
    }

}
