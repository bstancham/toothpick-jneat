package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

public class TargetSetupMobile extends TargetSetup {

    private double max = 2.0;
    private double x = 0;
    private double y = 0;

    public TargetSetupMobile() { super("mobile"); }

    @Override
    public void init(TPTrainingParams ttParams) {
        x = randInertia();
        y = randInertia();
    }

    @Override
    public void update(TPTrainingParams ttParams) {
        for (TPOrganism tpOrg : ttParams.organisms) {
            TPActor target = MLUtil.getTargetActor(tpOrg.program);
            if (target != null) {
                target.xInertia = x;
                target.yInertia = y;
            }
        }
    }

    private double randInertia() {
        return -max + (Math.random() * (2 * max));
    }

}
