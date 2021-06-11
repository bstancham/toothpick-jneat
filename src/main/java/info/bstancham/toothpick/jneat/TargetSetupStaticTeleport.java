package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPGeometry;
import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

public class TargetSetupStaticTeleport extends TargetSetup {

    private int period = 1000;
    private int countdown = period;

    public TargetSetupStaticTeleport() { super("static-teleport"); }

    @Override
    public void init(TPTrainingParams ttParams) {}

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
