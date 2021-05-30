package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.actor.TPActor;

public class ActorControllerThrustAndAngleInertia extends ActorControllerThrustInertia {

    public ActorControllerThrustAndAngleInertia() {
        angleStep = 0.0005;
    }

    @Override
    public ActorControllerThrustAndAngleInertia copy() {
        ActorControllerThrustAndAngleInertia out = new ActorControllerThrustAndAngleInertia();
        duplicateOutputs(out);
        out.xyStep = xyStep;
        out.angleStep = angleStep;
        return out;
    }

    @Override
    protected void updateAngle(TPActor a) {
        // System.out.println("update angle");

        a.angleInertia = angleStep;

        if (LEFT)  a.angleInertia -= angleStep;
        if (RIGHT) a.angleInertia += angleStep;
    }

}
