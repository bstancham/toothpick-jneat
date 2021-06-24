package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.actor.TPActor;

public class ActorControllerThrustInertia extends ActorControllerUDLR {

    public double xyStep = 0.008;
    public double angleStep;

    public ActorControllerThrustInertia() {
        angleStep = 0.005;
    }

    @Override
    public ActorControllerThrustInertia copy() {
        ActorControllerThrustInertia out = new ActorControllerThrustInertia();
        duplicateOutputs(out);
        out.xyStep = xyStep;
        out.angleStep = angleStep;
        return out;
    }

    @Override
    public void update(TPActor a) {
        if (UP) {
            a.xInertia += Math.sin(Math.PI * a.angle) * xyStep;
            a.yInertia -= Math.cos(Math.PI * a.angle) * xyStep;
        }
        if (DOWN) {
            a.xInertia -= Math.sin(Math.PI * a.angle) * xyStep;
            a.yInertia += Math.cos(Math.PI * a.angle) * xyStep;
        }
        updateAngle(a);
    }

    protected void updateAngle(TPActor a) {
        if (LEFT)            a.angleInertia = -angleStep;
        if (RIGHT)           a.angleInertia = angleStep;
        if (!LEFT && !RIGHT) a.angleInertia = 0;
    }

}
