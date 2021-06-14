package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

/**
 * PROTAGONIST and TARGET both init to random position, TARGET moves in a straight line in
 * a randomly chosen direction and periodically changes direction.
 */
public class ActorSetupMobileChangeDir implements ActorSetup {

    private double max = 2;
    private double xInertia = 0;
    private double yInertia = 0;
    private int period = 1000;
    private int countdown = period;

    private double startDist = 3000;

    @Override
    public String getLabel() {
        return "mobile target, change direction every " + period + " iterations";
    }

    @Override
    public void init(TPTrainingParams ttParams) {

        double ax = 0;
        double ay = 0;

        if (ttParams.organisms.size() > 0) {
            ax = Math.random() * ttParams.getGeometry().getWidth();
            ay = Math.random() * ttParams.getGeometry().getHeight();
        }

        // double angle = Math.PI;
        double angle = Math.random() * (Math.PI * 2);

        double tx = ax + (Math.sin(angle) * startDist);
        double ty = ay + (Math.cos(angle) * startDist);

        double xInertia = Math.sin(angle) * max;
        double yInertia = Math.cos(angle) * max;

        for (TPOrganism tpOrg : ttParams.organisms) {
            TPProgram prog = tpOrg.program;
            TPActor a = prog.getActor(TPTrainingParams.getProtagonistID());
            TPActor target = prog.getActor(TPTrainingParams.getTargetID());
            if (a == null) {
                System.out.println("ActorSetupMobileChangeDir: a=null");
            } else if (target == null) {
                System.out.println("ActorSetupMobileChangeDir: target=null");
            } else {
                // PROTAGONIST
                a.x = ax;
                a.y = ay;
                // TARGET
                target.x = tx;
                target.y = ty;
                target.xInertia = xInertia;
                target.yInertia = yInertia;
            }
        }
        countdown = period;
    }

    private void changeDir(TPTrainingParams ttParams) {
        xInertia = randInertia();
        yInertia = randInertia();
        for (TPOrganism tpOrg : ttParams.organisms) {
            TPActor target = MLUtil.getTargetActor(tpOrg.program);
            if (target != null) {
                target.xInertia = xInertia;
                target.yInertia = yInertia;
            }
        }
    }

    @Override
    public void update(TPTrainingParams ttParams) {
        countdown--;
        if (countdown <= 0) {
            changeDir(ttParams);
            countdown = period;
        }
    }

    private double randInertia() {
        return -max + (Math.random() * (2 * max));
    }

}
