package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.TPProgram;
import info.bstancham.toothpick.actor.TPActor;

public class NNInputBoundaryDist extends NNInput {

    private String actorID;
    private Dir dir;

    public NNInputBoundaryDist(String actorID, double inputScaling, Dir dir) {
        super(inputScaling);
        this.actorID = actorID;
        this.dir = dir;
    }

    @Override
    public String getTitle() {
        return super.getTitle() + " (" + actorID + ", " + dir + ")";
    }

    @Override
    public double getRawInput(TPProgram prog) {
        TPActor a = prog.getActor(actorID);
        if (a == null) {
            numErrors++;
            return 0;
        }
        if (dir == Dir.LEFT)
            return a.x;
        else if (dir == Dir.RIGHT)
            return prog.getGeometry().getWidth() - a.x;
        else if (dir == Dir.BOTTOM)
            return a.y;
        else // TOP
            return prog.getGeometry().getHeight() - a.y;
    }

}
