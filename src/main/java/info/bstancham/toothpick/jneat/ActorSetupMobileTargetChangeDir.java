package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.geom.Geom;

/**
 * PROTAGONIST init to random position. TARGET init to a set distance from PROTAGONIST in
 * a random direction. TARGET moves in a straight line directly away and then periodically
 * changes to move in a new randomly chosen direction.
 */
public class ActorSetupMobileTargetChangeDir extends ActorSetup {

    private double minVelocity = 1.0;
    private double maxVelocity = 2.5;
    private double maxDirChange = Math.PI * 0.3;
    private double startDist = 3000;
    private int period = 1000;
    private int countdown = period;

    @Override
    public String getLabel() {
        return "mobile target, change direction every " + period + " iterations";
    }

    @Override
    public void init(TPTrainingParams ttParams) {
        countdown = period; // reset countdown
        if (ttParams.organisms.size() > 0) {

            // random position for PROTAGONIST
            double px = Math.random() * ttParams.getGeometry().getWidth();
            double py = Math.random() * ttParams.getGeometry().getHeight();
            // TARGET starts at set distance and moves directly away at full speed
            double angle = Math.random() * (Math.PI * 2);
            double tx = px + (Math.sin(angle) * startDist);
            double ty = py + (Math.cos(angle) * startDist);
            double xInertia = Math.sin(angle) * maxVelocity;
            double yInertia = Math.cos(angle) * maxVelocity;

            for (TPOrganism tpOrg : ttParams.organisms) {
                TPProgram prog = tpOrg.program;
                if (fetchProtagonist(ttParams, prog) && fetchTarget(ttParams, prog)) {
                    protagonist.x = px;
                    protagonist.y = py;
                    target.x = tx;
                    target.y = ty;
                    target.xInertia = xInertia;
                    target.yInertia = yInertia;
                    tpOrg.setResetSnapshot();
                }
            }
        }
    }

    private void changeDir(TPTrainingParams ttParams) {
        if (ttParams.numTPOrganisms() > 0) {
            if (fetchTarget(ttParams, ttParams.getTPOrganism(0).program)) {

                // choose new direction by randomly turning within the allowed range
                // (each TARGET is identical, so can just use the first one)
                double currentDir = Geom.angle(0, 0, target.xInertia, target.yInertia);
                double newDir = currentDir + (-maxDirChange + (Math.random() * (maxDirChange * 2)));
                double velocity = minVelocity + (Math.random() * (maxVelocity - minVelocity));
                double xInertia = Math.sin(newDir) * velocity;
                double yInertia = Math.cos(newDir) * velocity;

                for (TPOrganism tpOrg : ttParams.organisms) {
                    if (fetchTarget(ttParams, tpOrg.program)) {
                        target.xInertia = xInertia;
                        target.yInertia = yInertia;
                    }
                }
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

}
