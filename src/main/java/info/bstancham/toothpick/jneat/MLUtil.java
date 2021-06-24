package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.*;
import info.bstancham.toothpick.actor.*;
import info.bstancham.toothpick.geom.*;
import java.awt.Color;

public class MLUtil {

    public static final String HORIZ_NAME = "Horizontal";
    public static final String VERT_NAME = "Vertical";

    /**
     * <p>Makes a program with two named actors, a horizontal line and a vertical line, as
     * a suitable starting point for jneat training. The names of the two players are
     * {@link HORIZ_NAME} and {@link VERT_NAME}.</p>
     *
     * <p>There is a reset-behaviour which repositions the vertical actor to a random
     * starting point on the right hand side of the arena.</p>
     */
    public static TPProgram makeProgHorizVsVert() {
        TPProgram prog = makeProgHorizVsVertNoCollision();
        prog.addBehaviour(new ProgToothpickPhysics());
        prog.setResetSnapshot();
        return prog;
    }

    public static TPProgram makeProgHorizVsVertNoCollision() {
        TPProgram prog = new TPProgram("Horizontal vs Vertical");

        // create the two actors (horiz & vert)

        TPActor horiz = makeLineActor(-50, 0, 50, 0, 200, 350);
        horiz.xInertia = 0;
        horiz.yInertia = 0;
        // horiz.setColorGetter(new ColorSmoothMono(Color.RED));
        horiz.setColorGetter(new ColorMono(Color.RED));
        horiz.setVertexColorGetter(new ColorMono(Color.WHITE));
        horiz.name = HORIZ_NAME;
        prog.addActor(horiz);

        TPActor vert = makeLineActor(0, -50, 0, 50, 800, 350);
        vert.xInertia = 0;
        vert.yInertia = 0;
        // vert.setColorGetter(new ColorSmoothMono(Color.BLUE));
        vert.setColorGetter(new ColorMono(Color.BLUE));
        vert.setVertexColorGetter(new ColorMono(Color.WHITE));
        vert.name = VERT_NAME;
        prog.addActor(vert);

        prog.setBGColor(new Color(50, 150, 100));
        prog.setSmearMode(true);
        prog.setShowIntersections(true);

        prog.init();
        prog.setResetSnapshot();
        return prog;
    }

    public static void setActorColorRandGraduated(TPProgram prog) {
        int num = prog.numActors();
        double brightness = 1.0;
        double step = 0.2;
        // adjust params for greater number of actors
        if (num > 9) {
            step = 1.0 / num;
        } else if (num > 4) {
            step = 0.1;
        }
        // set color
        ColorGetter vertexColor = ColorMono.WHITE;
        Color randColor = ColorGetter.randColor();
        for (int i = 0; i < num; i++) {
            brightness -= step;
            Color c = ColorGetter.setBrightness(randColor, brightness);
            prog.getActor(i).setColorGetter(new ColorMono(c));
            prog.getActor(i).setVertexColorGetter(vertexColor);
        }
    }

    /**
     * Target is RED - so this sets any random color, but not too close to red.
     */
    public static void setActorColorNotRed(TPActor a, boolean vertexColor) {
        int r = 0;
        int g = 0;
        int b = 0;
        // make sure that color is not too close to black
        while (r + g + b < 70) {
            r = randInt(100);
            g = randInt(255);
            b = randInt(255);
        }
        a.setColorGetter(new ColorMono(new Color(r, g, b)));
        if (vertexColor)
            a.setVertexColorGetter(ColorMono.WHITE);
        else
            a.setVertexColorGetter(null);
    }

    private static int randInt(int max) {
        return (int) (Math.random() * max);
    }

    /** WARNING! Returns null if actor with name {@link HORIZ_NAME} does not exist. */
    public static TPActor getHorizActor(TPProgram prog) {
        return prog.getActor(HORIZ_NAME);
    }

    /** WARNING! Returns null if actor with name {@link VERT_NAME} does not exist. */
    public static TPActor getVertActor(TPProgram prog) {
        return prog.getActor(VERT_NAME);
    }

    /** WARNING! Returns null if actor with name {@link TARGET_NAME} does not exist. */
    public static TPActor getTargetActor(TPProgram prog) {
        return prog.getActor(TPTrainingParams.TARGET_NAME);
    }

    public static TPActor makeLineActor(double x1, double y1, double x2, double y2,
                                        double posX, double posY) {
        TPForm form = new TPForm();
        form.addPart(new TPLink(new Node(x1, y1), new Node(x2, y2)));
        form.housekeeping();
        TPActor actor = new TPActor(form);
        actor.setBoundaryBehaviour(TPActor.BoundaryBehaviour.WRAP_AT_BOUNDS);
        actor.setPos(new Pt(posX, posY));
        return actor;
    }

    public static TPActor makeLineActor(double x1, double y1, double x2, double y2) {
        Pt start = new Pt(x1, y1);
        Pt end = new Pt(x2, y2);
        Pt pos = Geom.midPoint(start, end);
        start = start.add(pos.invert());
        end = end.add(pos.invert());
        return makeLineActor(start.x, start.y, end.x, end.y, pos.x, pos.y);
    }

}
