package info.bstancham.toothpick.jneat;

// import info.bschambers.toothpick.PBRandActorSetup;
import info.bschambers.toothpick.TPBase;
// import info.bschambers.toothpick.TPProgram;
// import info.bschambers.toothpick.actor.*;
// import java.awt.Color;
// import java.util.ArrayList;
// import java.util.List;
// import jneat.neat.Organism;

public class TPTrainingParamsSeekIn6Out4FitPlus extends TPTrainingParamsSeekIn6Out4 {

    public TPTrainingParamsSeekIn6Out4FitPlus(TPBase base) {
        super(base, "Seek (in=6, out=4) fit-plus", "genome_in6_out4");
    }

    @Override
    protected ToothpickFitness makeFitness() {
        return new TPFitnessProximityPlus();
    }

}
