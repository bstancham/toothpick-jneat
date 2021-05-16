package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.*;
import info.bschambers.toothpick.actor.*;
import info.bschambers.toothpick.geom.*;
import java.awt.Color;

// public class MLFactory extends TPProgram {
public class MLUtil {

    public static final String HORIZ_NAME = "Horizontal";
    public static final String VERT_NAME = "Vertical";

    // private TPActor horiz;
    // private TPActor vert;

    // private int count = 0;

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
        prog.addBehaviour(new PBToothpickPhysics());
        prog.setResetSnapshot();
        return prog;
    }
    
    public static TPProgram makeProgHorizVsVertNoCollision() {
        // super("Horizontal vs Vertical");
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

        // TPFactory.setRandHeading(horiz, 0.5, 1);
        // TPFactory.setRandHeading(vert, 0.5, 1);

        // prog.addActor(horiz);
        // prog.addActor(vert);
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
        ColorGetter vertexColor = new ColorMono(Color.WHITE);
        Color randColor = ColorGetter.randColor();
        for (int i = 0; i < num; i++) {
            brightness -= step;
            Color c = ColorGetter.setBrightness(randColor, brightness);
            prog.getActor(i).setColorGetter(new ColorMono(c));
            prog.getActor(i).setVertexColorGetter(vertexColor);
        }
    }

    /** WARNING! Returns null if actor with name {@link HORIZ_NAME} does not exist. */
    public static TPActor getHorizActor(TPProgram prog) {
        return prog.getActor(HORIZ_NAME);
    }

    /** WARNING! Returns null if actor with name {@link VERT_NAME} does not exist. */
    public static TPActor getVertActor(TPProgram prog) {
        return prog.getActor(VERT_NAME);
    }

    /**
     * Gets the first Line in actor's form, or returns NULL if there is no Line.
     *
     * WARNING! may return NULL.
     */
    private Line getFirstLine(TPActor a) {
        TPForm form = a.getForm();
        if (form.numParts() > 0) {
            TPPart part = form.getPart(0);
            if (part instanceof TPLine)
                return  ((TPLine) part).getLine();
        }
        return null;
    }

    // @Override
    // public void initProgram() {
    //     super.initProgram();
    //     reconnectHorizAndVert();
    // }

    // @Override
    // public void reset() {
    //     super.reset();
    //     reconnectHorizAndVert();
    // }

    // /**
    //  * init() uses this to reconnect references to the HORIZ and VERT actors.
    //  *
    //  * Need to maintain references to these two, but they get lost each time we call init
    //  * because the initial-actors are recopied from the initial-actors list.
    //  */
    // private void reconnectHorizAndVert() {
    //     for (TPActor a : actors) {
    //         if (a.name.equals(horizName)) {
    //             horiz = a;
    //             // System.out.println("HorizVsVert: reconnect actor - " + a.name);
    //         }
    //         if (a.name.equals(vertName)) {
    //             vert = a;
    //             // System.out.println("HorizVsVert: reconnect actor - " + a.name);
    //         }
    //     }
    // }

    // @Override
    // public void init() {
    //     super.init();

    //     // create the two actors

    //     TPActor horiz = makeLineActor(-50, 0, 50, 0, 200, 350);
    //     horiz.setColorGetter(new ColorSmoothMono(Color.RED));
    //     addActor(horiz);

    //     TPActor vert = makeLineActor(0, -50, 0, 50, 800, 350);
    //     vert.setColorGetter(new ColorSmoothMono(Color.BLUE));
    //     addActor(vert);

    //     TPFactory.setRandHeading(horiz);
    //     TPFactory.setRandHeading(vert);
    // }

    public static TPActor makeLineActor(double x1, double y1, double x2, double y2,
                                        double posX, double posY) {
        TPForm form = new TPForm(new TPPart[] { new TPLine(new Line(x1, y1, x2, y2)) });
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

    // public TPActor getHorizActor() { return horiz; }

    // public TPActor getVertActor() { return vert; }

    // @Override
    // public void update() {
    //     super.update();

    //     // System.out.println(count++ + ": " + aliveStr(horiz) + " - " + aliveStr(vert));

    //     if (!isFinished()) {

    //         String msg = "";

    //         if (!horiz.isAlive()) {
    //             setFinished(true);
    //             msg = "VERTICAL WINS!";
    //         }

    //         if (!vert.isAlive()) {
    //             setFinished(true);
    //             msg = "HORIZONTAL WINS!";
    //         }

    //         if (isFinished()) {
    //             System.out.println(msg);
    //             TPActor ta = TPFactory.textActor(this, msg);
    //             ta.x = getGeometry().getWidth() / 2;
    //             ta.y = 100;
    //             // ta.yInertia = 0.2;
    //             addActor(ta);
    //         }
    //     }

    // }

    // private String aliveStr(TPActor a) {
    //     return a.name + "=" + (a.isAlive() ? "alive" : "dead");
    // }

}
