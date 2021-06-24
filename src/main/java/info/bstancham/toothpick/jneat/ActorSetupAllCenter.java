package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.actor.TPActor;

/**
 * Init ALL actors to center of arena.
 */
public class ActorSetupAllCenter extends ActorSetup {

    @Override
    public String getLabel() {
        return "static (all center)";
    }

    @Override
    public void init(TPTrainingParams ttParams) {
        if (ttParams.organisms.size() > 0) {
            double x = ttParams.getGeometry().getXCenter();
            double y = ttParams.getGeometry().getYCenter();
            for (TPOrganism tpOrg : ttParams.organisms) {
                for (TPActor a : tpOrg.program) {
                    a.x = x;
                    a.y = y;
                }
                tpOrg.setResetSnapshot();
            }
        }
    }

    @Override
    public void update(TPTrainingParams ttParams) {}

}
