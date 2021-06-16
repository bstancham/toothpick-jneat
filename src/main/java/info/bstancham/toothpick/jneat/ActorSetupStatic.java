package info.bstancham.toothpick.jneat;

/**
 * PROTAGONIST and TARGET both init to random static position.
 */
public class ActorSetupStatic extends ActorSetup {

    @Override
    public String getLabel() {
        return "static target";
    }

    @Override
    public void init(TPTrainingParams ttParams) {
        if (ttParams.organisms.size() > 0) {
            double px = Math.random() * ttParams.getGeometry().getWidth();
            double py = Math.random() * ttParams.getGeometry().getHeight();
            double tx = Math.random() * ttParams.getGeometry().getWidth();
            double ty = Math.random() * ttParams.getGeometry().getHeight();
            for (TPOrganism tpOrg : ttParams.organisms) {
                if (fetchProtagonist(ttParams, tpOrg.program)
                    && fetchTarget(ttParams, tpOrg.program)) {
                    protagonist.x = px;
                    protagonist.y = py;
                    target.x = tx;
                    target.y = ty;
                    tpOrg.setResetSnapshot();
                }
            }
        }
    }

    @Override
    public void update(TPTrainingParams ttParams) {}

}
