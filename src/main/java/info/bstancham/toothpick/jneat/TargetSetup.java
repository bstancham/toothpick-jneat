package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;

public abstract class TargetSetup {

    private String label;

    public TargetSetup(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    public abstract void init(TPTrainingParams ttParams);

    public abstract void update(TPTrainingParams ttParams);

}
