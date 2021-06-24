package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.TPProgram;
import info.bstancham.toothpick.actor.TPActor;

public class NNInputRelativePosition extends NNInput {

    private String actorID;
    private String targetID;
    private Dim dim;

    public NNInputRelativePosition(String actorID, String targetID, double inputScaling, Dim dim) {
        super(inputScaling);
        this.actorID = actorID;
        this.targetID = targetID;
        this.dim = dim;
    }

    @Override
    public String getTitle() {
        return super.getTitle() + " (" + actorID + "/" + targetID + ", " + dim + ")";
    }

    @Override
    public double getRawInput(TPProgram prog) {
        TPActor a = prog.getActor(actorID);
        TPActor target = prog.getActor(targetID);
        if (a == null || target == null) {
            numErrors++;
            return 0;
        }
        if (dim == Dim.X)
            return prog.getGeometry().xDistWrapped(a.x, target.x);
        else // Y
            return prog.getGeometry().yDistWrapped(a.y, target.y);
    }

}
