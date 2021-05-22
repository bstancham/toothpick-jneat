package jneat.neat;

/**
 * Organisms are Genomes and Networks with fitness information i.e. The genotype and
 * phenotype together.
 */
public class Organism extends Neat {

    /** The Organism's genotype */
    public Genome genome;

    /** The Organism's phenotype */
    public Network net;

    /** Win marker (if needed for a particular task) */
    public  boolean winner;

    /** A measure of fitness for the Organism */
    double fitness;

    /** A fitness measure that won't change during adjustments */
    double orig_fitness;

    /** Used just for reporting purposes */
    double error;

    /** The Organism's Species */
    Species species;

    /** Number of children this Organism may have */
    double expected_offspring;

    /** Tells which generation this Organism is from */
    int generation;

    /** Marker for destruction of inferior Organisms */
    boolean eliminate;

    /** Marks the species champ */
    boolean champion;

    /** Number of reserved offspring for a population leader */
    int super_champ_offspring;

    /** Marks the best in population */
    boolean pop_champ;

    /** Marks the duplicate child of a champion (for tracking purposes) */
    boolean pop_champ_child;

    /** DEBUG variable- high fitness of champ */
    double high_fit;

    /** has a change in a structure of baby ? */
    boolean mut_struct_baby;

    /** has a mating in baby ? */
    boolean mate_baby;

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getOrig_fitness() {
        return orig_fitness;
    }

    public void setOrig_fitness(double orig_fitness) {
        this.orig_fitness = orig_fitness;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public boolean getWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public Network getNet() {
        return net;
    }

    public void setNet(Network net) {
        this.net = net;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public double getExpected_offspring() {
        return expected_offspring;
    }

    public void setExpected_offspring(double expected_offspring) {
        this.expected_offspring = expected_offspring;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public boolean getEliminate() {
        return eliminate;
    }

    public void setEliminate(boolean eliminate) {
        this.eliminate = eliminate;
    }

    public boolean getChampion() {
        return champion;
    }

    public void setChampion(boolean champion) {
        this.champion = champion;
    }

    public int getSuper_champ_offspring() {
        return super_champ_offspring;
    }

    public void setSuper_champ_offspring(int super_champ_offspring) {
        this.super_champ_offspring = super_champ_offspring;
    }

    public boolean getPop_champ() {
        return pop_champ;
    }

    public void setPop_champ(boolean pop_champ) {
        this.pop_champ = pop_champ;
    }

    public boolean getPop_champ_child() {
        return pop_champ_child;
    }

    public void setPop_champ_child(boolean pop_champ_child) {
        this.pop_champ_child = pop_champ_child;
    }

    public double getHigh_fit() {
        return high_fit;
    }

    public void setHigh_fit(double high_fit) {
        this.high_fit = high_fit;
    }

    public boolean getMut_struct_baby() {
        return mut_struct_baby;
    }

    public void setMut_struct_baby(boolean mut_struct_baby) {
        this.mut_struct_baby = mut_struct_baby;
    }

    public boolean getMate_baby() {
        return mate_baby;
    }

    public void setMate_baby(boolean mate_baby) {
        this.mate_baby = mate_baby;
    }

    public Organism(double xfitness, Genome xgenome, int xgeneration) {
        fitness = xfitness;
        orig_fitness = xfitness;
        genome = xgenome;
        net = genome.genesis(xgenome.genome_id);
        species = null;
        expected_offspring = 0;
        generation = xgeneration;
        eliminate = false;
        error = 0;
        winner = false;
        champion = false;
        super_champ_offspring = 0;
        pop_champ = false;
        pop_champ_child = false;
        high_fit = 0;
        mut_struct_baby = false;
        mate_baby = false;
    }

    public Genome getGenome() {
        return genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public void viewtext() {
        System.out.print("\n-ORGANISM -[genomew_id=" + genome.genome_id + "]");
        System.out.print(" Champ(" + champion + ")");
        System.out.print(", fit=" + fitness);
        System.out.print(", Elim=" + eliminate);
        System.out.print(", offspring=" + expected_offspring);
    }

}
