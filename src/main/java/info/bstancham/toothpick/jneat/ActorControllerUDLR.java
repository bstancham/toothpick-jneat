package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.actor.TPActor;

/**
 * TODO: migrate to ENGINE
 */
public abstract class ActorControllerUDLR {

    public boolean UP = false;
    public boolean DOWN = false;
    public boolean LEFT = false;
    public boolean RIGHT = false;

    public abstract ActorControllerUDLR copy();

    public abstract void update(TPActor a);

    protected void duplicateOutputs(ActorControllerUDLR ac) {
        ac.UP = UP;
        ac.DOWN = DOWN;
        ac.LEFT = LEFT;
        ac.RIGHT = RIGHT;
    }

}
