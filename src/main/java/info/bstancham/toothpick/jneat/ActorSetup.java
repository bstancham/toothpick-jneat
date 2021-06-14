package info.bstancham.toothpick.jneat;

/**
 * Setup actors during a training session.
 */
public interface ActorSetup {

    public String getLabel();

    public void init(TPTrainingParams ttParams);

    public void update(TPTrainingParams ttParams);

}
