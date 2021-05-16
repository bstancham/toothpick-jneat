package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.actor.TPActor;

public class ActorController8WayInertia extends ActorControllerUDLR {

    private double xyStep = 0.005;
    private double xyMax = 2.0;

    @Override
    public ActorController8WayInertia copy() {
        ActorController8WayInertia out = new ActorController8WayInertia();
        duplicateOutputs(out);
        out.xyStep = xyStep;
        out.xyMax = xyMax;
        return out;
    }

    @Override
    public void update(TPActor a) {

        if (UP)    a.yInertia -= xyStep;
        if (DOWN)  a.yInertia += xyStep;
        if (LEFT)  a.xInertia -= xyStep;
        if (RIGHT) a.xInertia += xyStep;

        if (a.yInertia < -xyMax) a.yInertia = -xyMax;
        if (a.yInertia > xyMax)  a.yInertia = xyMax;
        if (a.xInertia < -xyMax) a.xInertia = -xyMax;
        if (a.xInertia > xyMax)  a.xInertia = xyMax;
    }

}
