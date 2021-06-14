package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPBase;

public class TPTrainingParamsSeekIn6Out4FitPlus extends TPTrainingParamsSeekIn6Out4 {

    public TPTrainingParamsSeekIn6Out4FitPlus(TPBase base) {
        super(base, "Seek (in=6, out=4) fit-plus", "genome_in6_out4");
    }

    @Override
    protected ToothpickFitness makeFitness() {
        return new TPFitnessProximityPlus();
    }

}
