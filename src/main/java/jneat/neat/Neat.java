package jneat.neat;

import java.lang.reflect.*;
import java.util.*;
import jneat.gui.*;
import jneat.common.*;

/**
 * Is a superclass for definition of all parameters , threshold and others values.
 *
 * ANALYSIS:
 *
 * long list of public static fields - all int or double
 * long list of public static strings - number of strings is equal to num double + num int
 * number field names are prefixed with p_
 * string field names are prefixed with d_
 * strings are descriptions for the number fields
 * none of these fields are initialised
 * initBase method initialises all of these fields
 *
 * getDescription uses reflection to get description from name of parameter if input string matches
 *
 */
public class Neat {

    /** Probability  of mutating a single trait param */
    public static double p_trait_param_mut_prob;

    /** Power of mutation on a signle trait param */
    public static double p_trait_mutation_power;

    /** Amount that mutation_num changes for a trait change inside a link */
    public static double p_linktrait_mut_sig;

    /** Amount a mutation_num changes on a link connecting a node that changed its trait */
    public static double p_nodetrait_mut_sig;

    /** The power of a linkweight mutation */
    public static double p_weight_mut_power;

    /** Probability  that a link mutation which doesn't have to be recurrent will be made recurrent */
    public static double p_recur_prob;

    /** factor multiply for gene not equal */
    public static double p_disjoint_coeff;

    /** factor multiply for gene excedeed */
    public static double p_excess_coeff;

    /** factor multiply weight difference */
    public static double p_mutdiff_coeff;

    /** threshold under which two Genomes are the same species */
    public static double p_compat_threshold;

    /** How much does age matter in epoch cycle */
    public static double p_age_significance;

    /** Percent of ave fitness for survival */
    public static double p_survival_thresh;

    /** Probability  of a non-mating reproduction */
    public static double p_mutate_only_prob;

    /** Probability  of mutate trait */
    public static double p_mutate_random_trait_prob;

    /** Probability  of mutate link trait */
    public static double p_mutate_link_trait_prob;

    /** Probability  of mutate node trait */
    public static double p_mutate_node_trait_prob;

    /** Probability  of mutate link weight */
    public static double p_mutate_link_weights_prob;

    /** Probability  of mutate status ena->dis | dis-ena of gene */
    public static double p_mutate_toggle_enable_prob;

    /** Probability  of switch status to ena of gene */
    public static double p_mutate_gene_reenable_prob;

    /** Probability  of add a node to struct of genome */
    public static double p_mutate_add_node_prob;

    /** Probability  of add a link to struct of genome */
    public static double p_mutate_add_link_prob;

    /** Probability  of a mate being outside species */
    public static double p_interspecies_mate_rate;

    /** Probability  of cross in a many point of two genome */
    public static double p_mate_multipoint_prob;

    /** Probability  of cross in a many point of two genome with media */
    public static double p_mate_multipoint_avg_prob;

    /** Probability  of cross in a single point of two genome */
    public static double p_mate_singlepoint_prob;

    /** Probability  of mating without mutation */
    public static double p_mate_only_prob;

    /** Probability of forcing selection of ONLY links that are naturally recurrent */
    public static double p_recur_only_prob;

    /** Size of population */
    public static int p_pop_size;

    /** Age where Species starts to be penalized */
    public static int p_dropoff_age;

    /** Number of tries mutate_add_link will attempt to find an open link */
    public static int p_newlink_tries;

    /** Tells to print population to file every n generations */
    public static int p_print_every;

    /** The number of babies to siphen off to the champions */
    public static int p_babies_stolen;

    /** The number of runs for an experiment */
    public static int p_num_runs;

    /** number of a trait */
    public static int p_num_trait_params;

    public static String d_trait_param_mut_prob;
    public static String d_trait_mutation_power;
    public static String d_linktrait_mut_sig;
    public static String d_nodetrait_mut_sig;
    public static String d_recur_prob;
    public static String d_weight_mut_power;
    public static String d_disjoint_coeff;
    public static String d_excess_coeff;
    public static String d_mutdiff_coeff;
    public static String d_compat_threshold;
    public static String d_age_significance;
    public static String d_survival_thresh;
    public static String d_mutate_only_prob;
    public static String d_mutate_random_trait_prob;
    public static String d_mutate_link_trait_prob;
    public static String d_mutate_node_trait_prob;
    public static String d_mutate_link_weights_prob;
    public static String d_mutate_toggle_enable_prob;
    public static String d_mutate_gene_reenable_prob;
    public static String d_mutate_add_node_prob;
    public static String d_mutate_add_link_prob;
    public static String d_interspecies_mate_rate;
    public static String d_mate_multipoint_prob;
    public static String d_mate_multipoint_avg_prob;
    public static String d_mate_singlepoint_prob;
    public static String d_mate_only_prob;
    public static String d_recur_only_prob;
    public static String d_pop_size;
    public static String d_dropoff_age;
    public static String d_newlink_tries;
    public static String d_print_every;
    public static String d_babies_stolen;
    public static String d_num_runs;
    public static String d_num_trait_params;

    /**
     * Uses reflection to get description from name of parameter if input string matches,
     * otherwise returns null.
     */
    public static String getDescription(String xkey) {
        try {
            Class c = Class.forName("jneat.neat.Neat");
            Field f = c.getField("d_" + xkey);
            return (String) f.get(c);
        }
        catch (Throwable e) {
            return null;
        }
    }

    /**
     * Initialises all parameters and their descriptions.
     */
    public static void initbase() {
        p_trait_param_mut_prob = 0;
        p_trait_mutation_power = 0;
        p_linktrait_mut_sig = 0;
        p_nodetrait_mut_sig = 0;
        p_recur_prob = 0;
        p_weight_mut_power = 0;
        p_disjoint_coeff = 0;
        p_excess_coeff = 0;
        p_mutdiff_coeff = 0;
        p_compat_threshold = 0.1;
        p_age_significance = 0;
        p_survival_thresh = 0;
        p_mutate_only_prob = 0;
        p_mutate_random_trait_prob = 0;
        p_mutate_link_trait_prob = 0;
        p_mutate_node_trait_prob = 0;
        p_mutate_link_weights_prob = 0;
        p_mutate_toggle_enable_prob = 0;
        p_mutate_gene_reenable_prob = 0;
        p_mutate_add_node_prob = 0.2;
        p_mutate_add_link_prob = 0;
        p_interspecies_mate_rate = 0;
        p_mate_multipoint_prob = 0;
        p_mate_multipoint_avg_prob = 0;
        p_mate_singlepoint_prob = 0;
        p_mate_only_prob = 0;
        p_recur_only_prob = 0;
        p_pop_size = 500;
        p_dropoff_age = 0;
        p_newlink_tries = 0;
        p_print_every = 0;
        p_babies_stolen = 0;
        p_num_runs = 1;
        p_num_trait_params = 8;
        //
        // setting the description  of all parameters
        //
        d_trait_param_mut_prob = "Prob. of mutating a single trait param";
        d_trait_mutation_power = "Power of mutation on a single trait param";
        d_linktrait_mut_sig =
            "Amount that mutation_num changes for a trait change inside a link";
        d_nodetrait_mut_sig =
            "Amount a mutation_num changes on a link connecting a node that changed its trait";
        d_recur_prob =
            "Prob. that a link mutation which doesn't have to be recurrent will be made recurrent";
        d_weight_mut_power = "The power of a link weight mutation";
        d_disjoint_coeff = "factor multiply for gene not equal";
        d_excess_coeff = "factor multiply for gene excedeed";
        d_mutdiff_coeff = "factor multply weight difference";
        d_compat_threshold = "threshold under which two Genomes are the same species";
        d_age_significance = "How much does age matter in epoch cycle";
        d_survival_thresh = "Percent of average fitness for survival";
        d_mutate_only_prob = "Probability of a non-mating reproduction";
        d_mutate_random_trait_prob = "Probability of mutate trait";
        d_mutate_link_trait_prob = "Probability of mutate link trait";
        d_mutate_node_trait_prob = "Probability of mutate node trait";
        d_mutate_link_weights_prob = "Probability of mutate link weight";
        d_mutate_toggle_enable_prob =
            "Probability of mutate status ena->dis | dis-ena of gene";
        d_mutate_gene_reenable_prob = "Probability of switch status to ena of gene";
        d_mutate_add_node_prob = "Probability of add a node to struct of genome";
        d_mutate_add_link_prob = "Probability of add a link to struct of genome";
        d_interspecies_mate_rate = "Probability of a mate being outside species";
        d_mate_multipoint_prob = "Probability of cross in a many point of two genome";
        d_mate_multipoint_avg_prob =
            "Probability of cross in a many point of two genome with media";
        d_mate_singlepoint_prob = "Probability of cross in a single point of two genome";
        d_mate_only_prob = "Probability of mating without mutation";
        d_recur_only_prob =
            "Probability of forcing selection of ONLY links that are naturally recurrent";
        d_pop_size = "Size of population";
        d_dropoff_age = "Age where Species starts to be penalized";
        d_newlink_tries =
            "Number of tries mutate_add_link will attempt to find an open link";
        d_print_every = "Tells to print population to file every n generations";
        d_babies_stolen = "The number of babies to siphen off to the champions";
        d_num_runs = "The number of runs for an experiment";
        d_num_trait_params = "number of a trait";
    }

    /**
     * Strips the first two characters off the input string - i.e. strips the prefix 'p_'
     * or 'd_' off of field names.
     */
    public static String normalizeName(String s) {
        return s.substring(2);
    }

    /**
     * Writes all parameters to a file with the given name.
     */
    public static void writeParam(String xNameFile) {
        //
        // write to file xpar all parameters.....
        //
        IOseq xFile;

        xFile = new IOseq(xNameFile);
        xFile.IOseqOpenW(false);

        try {

            Class c = Class.forName("jneat.neat.Neat");
            Field[] fieldlist = c.getDeclaredFields();

            for (int i = 0; i < fieldlist.length; i++) {
                Field f1 = fieldlist[i];
                String x1 = f1.getName();

                if (x1.startsWith("p_")) {
                    Field f2 = c.getField("d_" + Neat.normalizeName(x1));
                    Object s1 = f1.get(c);
                    Object s2 = f2.get(c);
                    //	   String riga = s1 + "  " + s2;
                    String riga = x1 + "  " + s1;
                    xFile.IOseqWrite(riga);
                }
            }

        }
        catch (Throwable e) {
            System.err.println(e);
        }

        xFile.IOseqCloseW();

    }

    public static boolean readParam(String xNomeFile) {

        boolean ret = true;
        String xline;
        IOseq xFile;
        StringTokenizer st;
        String s1;
        String s2;
        Object m1;

        xFile = new IOseq(xNomeFile);
        if (!xFile.fileExists())
            System.err.println("\nNeat.readParam : file does not exist " + xNomeFile);
        
        ret = xFile.IOseqOpenR();
        
        if (ret) {
            try {
                Class c = Class.forName("jneat.neat.Neat");
                // Class c = Class.forName("Neat");
                Field[] fieldlist = c.getDeclaredFields();

                int number_params = fieldlist.length /2;

                for (int i = 0; i < number_params; i++) {
                    Field f1 = fieldlist[i];
                    String x1 = f1.getName();
                    Object x2 = f1.getType();
                    xline = xFile.IOseqRead();

                    st = new StringTokenizer(xline);
                    //skip comment

                    s1 = st.nextToken();
                    // real value
                    s1 = st.nextToken();

                    if (x1.startsWith("p_")) {
                        if (x2.toString().equals("double")) {
                            double n1 = Double.parseDouble(s1);
                            f1.set(c, (new Double(n1)));
                        }
                        if (x2.toString().equals("int")) {
                            int n1 = Integer.parseInt(s1);
                            f1.set(c, (new Integer(n1)));
                        }
                    }
                }
            } catch (Throwable e) {
                System.err.println(e);
            }

            xFile.IOseqCloseR();
        }

        else
            System.err.println("\nNeat.readParam : error during open " + xNomeFile);

        return ret;
    }

    public static void getParam(vectTableModel _model) {

        String xline;
        StringTokenizer st = null;
        String s1 = null;
        String s2 = null;
        Object m1 = null;
        Field fx = null;
        int j = 0;

        try {
            Class c = Class.forName("jneat.neat.Neat");
            Field[] fieldlist = c.getDeclaredFields();

            int number_params = fieldlist.length / 2;


            j = 0;
            for (int i = 0; i < number_params; i++)
                {
                    Field f1 = fieldlist[i];
                    String nomeF = f1.getName();
                    String nomeF1 = nomeF.substring(2);
                    if (nomeF.substring(0,2).equalsIgnoreCase("p_"))
                        {
                            Object o1 = nomeF1;
                            fx = c.getField("p_" + nomeF1);
                            Object o2 = fx.get(c);

                            _model.setValueAt(o1,j,0);
                            _model.setValueAt(o2,j,1);

                            j++;
                        }



                }

        }
        catch (Throwable e) {
            System.err.println(e);
        }

        _model.rows = j;
        return;
    }
    
    public static void updateParam(vectTableModel _model) {
        //
        // write to file xpar all parameters.....
        //

        for (int j = 0; j < _model.data.size(); j++) {
            try {
                Class c = Class.forName("jneat.neat.Neat");
                ParamValue ox = (ParamValue) _model.data.elementAt(j);

                Object k =  _model.getValueAt(j,0);
                Object kv =  _model.getValueAt(j,1);

                String xkey =  k.toString();
                String xval =  kv.toString();
                /*
                  System.out.print("\n j = "+j+" xkey = "+xkey);
                  System.out.print(" xval = "+xval);
                */
                Field f1 = c.getField("p_" + xkey);
                Object fty = f1.getType();

                if (fty.toString().equals("double")) {
                    double n1 = Double.parseDouble(xval);
                    f1.set(c, (new Double(n1)));
                }
                if (fty.toString().equals("int")) {
                    int n1 = Integer.parseInt(xval);
                    f1.set(c, (new Integer(n1)));
                }

            }
            catch (Throwable e) {
                System.out.print("\n errore su jneat.updateParam = "+e);
            }

        }

    }
}
