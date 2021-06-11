package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

public class TargetSetupMobileChangeDir extends TargetSetup {

    private double max = 2.0;
    private double xInertia = 0;
    private double yInertia = 0;
    private int period = 1000;
    private int countdown = period;

    public TargetSetupMobileChangeDir() { super("mobile-change-direction"); }

    @Override
    public void init(TPTrainingParams ttParams) {
        xInertia = randInertia();
        yInertia = randInertia();
    }

    @Override
    public void update(TPTrainingParams ttParams) {
        countdown--;
        if (countdown <= 0) {
            init(ttParams);
            countdown = period;
            for (TPOrganism tpOrg : ttParams.organisms) {
                TPActor target = MLUtil.getTargetActor(tpOrg.program);
                if (target != null) {
                    target.xInertia = xInertia;
                    target.yInertia = yInertia;
                }
            }
        }
    }

    private double randInertia() {
        return -max + (Math.random() * (2 * max));
    }

}
