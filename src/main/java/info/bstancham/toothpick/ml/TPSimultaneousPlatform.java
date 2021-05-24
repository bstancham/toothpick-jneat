package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.*;
import info.bschambers.toothpick.actor.TPActor;
import java.util.ArrayList;

/**
 * Runs a number of programs simultaneously.
 *
 * getProgram() returns a dummy program synthesised for display purposes by adding all of
 * the actors from all of the games together.
 *
 * programs all share the same geometry
 *
 *
 *
 * handling display
 *
 * handling update
 *
 *
 *
 */
public class TPSimultaneousPlatform extends TPPlatform {

    private TPGeometry geom;
    private ArrayList<TPProgram> programs = new ArrayList<>();
    private TPSPProgram synthesisProg;

    public TPSimultaneousPlatform(String name, TPGeometry geom) {
        super(name);
        this.geom = geom;
        resetSynthesisProgram();
    }

    protected void resetSynthesisProgram() {
        synthesisProg = new TPSPProgram("synthesis program");
        synthesisProg.setGeometry(geom);
        synthesisProg.setSmearMode(true);
        setProgram(synthesisProg);
    }

    public boolean isSmearMode() {
        return getProgram().isSmearMode();
    }

    public void setSmearMode(boolean val) {
        getProgram().setSmearMode(val);
    }

    /**
     * WARNING! Side effect - sets geometry of the input program before adding it to the
     * program list.
     */
    public void addProgram(TPProgram prog) {
        prog.setGeometry(geom);
        programs.add(prog);

        // add actors to the synthesis program
        prog.updateActorsInPlace();
        for (int i = 0; i < prog.numActors(); i++)
            getProgram().addActor(prog.getActor(i));

        getProgram().updateActorsInPlace();
    }

    public void updateActorsAllPrograms() {
        // remove existing actors
        for (TPActor a : synthesisProg)
            synthesisProg.removeActor(a);
        for (TPProgram p : programs)
            for (TPActor a : p)
                synthesisProg.addActor(a);
        synthesisProg.updateActorsInPlace();
    }

    /**
     * Discard all programs and reset the synthesis program to a clean slate.
     */
    public void discardAllPrograms() {
        resetSynthesisProgram();
        programs.clear();
    }

    public void resetAllPrograms() {
        for (TPProgram prog : programs)
            prog.reset();
    }

    public int numPrograms() {
        return programs.size();
    }

    public TPProgram getProgram(int i) {
        return programs.get(i);
    }

    public void setVisible(int i, boolean val) {
        TPProgram prog = programs.get(i);
        for (TPActor a : prog) {
            if (val) {
                synthesisProg.addActor(a);
            } else {
                synthesisProg.removeActor(a);
            }
        }
    }

    private class TPSPProgram extends TPProgram {

        public TPSPProgram(String title) {
            super(title);
        }

        @Override
        public void update() {
            for (TPProgram prog : programs)
                prog.update();
            updateActorsInPlace();
        }
    }

}
