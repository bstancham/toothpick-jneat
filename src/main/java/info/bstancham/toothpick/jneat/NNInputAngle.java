package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.TPActor;

public class NNInputAngle extends NNInput {

    private String actorID;

    public NNInputAngle(String actorID, double inputScaling) {
        super(inputScaling);
        this.actorID = actorID;
    }

    @Override
    public String getTitle() {
        return super.getTitle() + " (" + actorID + ")";
    }

    @Override
    public double getRawInput(TPProgram prog) {
        TPActor a = prog.getActor(actorID);
        if (a == null) {
            numErrors++;
            return 0;
        }
        return a.angle;
    }

}
