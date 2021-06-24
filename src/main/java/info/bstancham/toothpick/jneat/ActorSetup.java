package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.TPProgram;
import info.bstancham.toothpick.actor.TPActor;

/**
 * Setup actors before a training session and then manage them during.
 */
public abstract class ActorSetup {

    protected TPActor protagonist = null;
    protected TPActor target = null;

    /** Returns name of class - child classes may want to override.*/
    public String getLabel() {
        return getClass().getSimpleName();
    }

    /**
     * Attempts to fetch PROTAGONIST actor from {@code prog} and store it in
     * {@link protagonist}.
     * @return True, if successful, false otherwise.
     */
    protected boolean fetchProtagonist(TPTrainingParams ttParams, TPProgram prog) {
        protagonist = prog.getActor(TPTrainingParams.getProtagonistID());
        if (protagonist == null) {
            ttParams.log(getClass().getSimpleName() + ".fetchProtagonist: NOT FOUND!");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Attempts to fetch TARGET actor from {@code prog} and store it in {@link target}.
     * @return True, if successful, false otherwise.
     */
    protected boolean fetchTarget(TPTrainingParams ttParams, TPProgram prog) {
        target = prog.getActor(TPTrainingParams.getTargetID());
        if (target == null) {
            ttParams.log(getClass().getSimpleName() + ".fetchTarget: NOT FOUND!");
            return false;
        } else {
            return true;
        }
    }

    public abstract void init(TPTrainingParams ttParams);

    public abstract void update(TPTrainingParams ttParams);

}
