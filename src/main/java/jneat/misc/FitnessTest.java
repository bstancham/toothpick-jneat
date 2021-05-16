package jneat.misc;

public interface FitnessTest {

    public double getMaxFitness();

    public double[] computeFitness(int _sample, int _num_nodes,
                                   double _out[][], double _tgt[][]);

}
