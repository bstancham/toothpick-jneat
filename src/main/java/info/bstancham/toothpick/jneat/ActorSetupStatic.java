package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

/**
 * PROTAGONIST and TARGET both init to random static position.
 */
public class ActorSetupStatic implements ActorSetup {

    @Override
    public String getLabel() {
        return "static target";
    }

    @Override
    public void init(TPTrainingParams ttParams) {
        if (ttParams.organisms.size() > 0) {
            double ax = Math.random() * ttParams.getGeometry().getWidth();
            double ay = Math.random() * ttParams.getGeometry().getHeight();
            double tx = Math.random() * ttParams.getGeometry().getWidth();
            double ty = Math.random() * ttParams.getGeometry().getHeight();

            for (TPOrganism tpOrg : ttParams.organisms) {
                TPProgram prog = tpOrg.program;
                TPActor a = prog.getActor(TPTrainingParams.getProtagonistID());
                TPActor target = prog.getActor(TPTrainingParams.getTargetID());

                if (a == null || target == null) {
                    System.out.println("ActorSetupStatic: TARGET or PROTAGONIST is NULL!");
                } else {
                    a.x = ax;
                    a.y = ay;
                    target.x = tx;
                    target.y = ty;
                }
            }
        }
    }

    @Override
    public void update(TPTrainingParams ttParams) {}

}
