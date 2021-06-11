package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

public class TargetSetupMobileTeleport extends TargetSetup {

    private double xPos = 0;
    private double yPos = 0;
    private double xInertia = 0;
    private double yInertia = 0;
    private double maxInertia = 2.0;
    private int period = 1000;
    private int countdown = period;

    public TargetSetupMobileTeleport() { super("mobile-teleport"); }

    @Override
    public void init(TPTrainingParams ttParams) {
        xInertia = randInertia();
        yInertia = randInertia();
        xPos = Math.random() * ttParams.getGeometry().getWidth();
        yPos = Math.random() * ttParams.getGeometry().getHeight();
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
                    target.x = xPos;
                    target.y = yPos;
                    target.xInertia = xInertia;
                    target.yInertia = yInertia;
                }
            }
        }
    }

    private double randInertia() {
        return -maxInertia + (Math.random() * (2 * maxInertia));
    }

}
