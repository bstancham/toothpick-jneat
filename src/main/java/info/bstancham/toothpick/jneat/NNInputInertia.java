package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

public class NNInputInertia extends NNInput {

    private String actorID;
    private Dim dim;

    public NNInputInertia(String actorID, double inputScaling, Dim dim) {
        super(inputScaling);
        this.actorID = actorID;
        this.dim = dim;
    }

    @Override
    public String getTitle() {
        return super.getTitle() + " (" + actorID + ", " + dim + ")";
    }

    @Override
    public double getRawInput(TPProgram prog) {
        TPActor a = prog.getActor(actorID);
        if (a == null) {
            numErrors++;
            return 0;
        }
        if (dim == Dim.X)
            return a.xInertia;
        else
            return a.yInertia;
    }

}
