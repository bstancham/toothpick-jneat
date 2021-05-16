package jneat.neat;

import java.text.*;
import java.util.*;
import jneat.common.*;

/**
 *
 */
public class Genome extends Neat {

    /** Is a reference from this genotype to fenotype */
    Network phenotype;

    /** Numeric identification for this genotype */
    public int genome_id;

    /**
     * Each Gene in (3) has a marker telling when it arose historically;
     * Thus, these Genes can be used to speciate the population, and
     * the list of Genes provide an evolutionary history of innovation and link-building
     */
    Vector genes;

    /** parameter conglomerations :Reserved parameter space for future use */
    Vector traits;

    /** Is a collection of NNode mapped in a Vector; */
    Vector nodes;

    /**
     * note are two String for store statistics information when genomes are readed (if
     * exist : null otherwise);
     */
    public String notes;

    public int getGenome_id() {
        return genome_id;
    }

    public void setGenome_id(int genome_id) {
        this.genome_id = genome_id;
    }

    public Vector getGenes() {
        return genes;
    }

    public void setGenes(Vector genes) {
        this.genes = genes;
    }

    public Vector getTraits() {
        return traits;
    }

    public void setTraits(Vector traits) {
        this.traits = traits;
    }

    public Vector getNodes() {
        return nodes;
    }

    public void setNodes(Vector nodes) {
        this.nodes = nodes;
    }

    public Network getPhenotype() {
        return phenotype;
    }

    public void setPhenotype(Network phenotype) {
        this.phenotype = phenotype;
    }

    public Genome duplicate(int new_id) {
        Trait newtrait = null;
        Trait traitptr = null;
        Trait assoc_trait = null;
        NNode newnode = null;
        NNode inode = null;
        NNode onode = null;
        Gene newgene = null;
        Genome newgenome = null;
        Vector traits_dup = new Vector(traits.size(), 0);
        Vector nodes_dup = new Vector(nodes.size(), 0);
        Vector genes_dup = new Vector(genes.size(), 0);
        int _trait_id;

        Iterator itr_trait = traits.iterator();
        Iterator itr_node = nodes.iterator();
        Iterator itr_gene = genes.iterator();

        /*
         * duplicate trait
         */
        itr_trait = traits.iterator();
        while (itr_trait.hasNext()) {
            Trait _trait = ((Trait) itr_trait.next());

            newtrait = new Trait(_trait);
            traits_dup.add(newtrait);

        }

        /*
         * duplicate NNodes
         */
        itr_node = nodes.iterator();
        while (itr_node.hasNext()) {
            NNode _node = ((NNode) itr_node.next());
            assoc_trait = null;

            if (_node.getNodetrait() != null) {
                _trait_id = _node.nodetrait.trait_id;

                itr_trait = traits_dup.iterator();
                while (itr_trait.hasNext()) {
                    Trait _trait = ((Trait) itr_trait.next());
                    if (_trait.trait_id == _trait_id) {
                        assoc_trait = _trait;
                        break;
                    }
                }
            }

            newnode = new NNode(_node, assoc_trait);

            _node.dup = newnode;
            nodes_dup.add(newnode);
        }

        /*
         * duplicate Genes
         */
        itr_gene = genes.iterator();
        while (itr_gene.hasNext()) {

            Gene _gene = ((Gene) itr_gene.next());

            // point to news nodes created  at precedent step
            inode = _gene.lnk.in_node.dup;
            onode = _gene.lnk.out_node.dup;
            traitptr = _gene.lnk.linktrait;

            assoc_trait = null;
            if (traitptr != null) {
                _trait_id = traitptr.getTrait_id();
                itr_trait = traits_dup.iterator();
                while (itr_trait.hasNext()) {
                    Trait _trait = ((Trait) itr_trait.next());
                    if (_trait.trait_id == _trait_id) {
                        assoc_trait = _trait;
                        break;
                    }
                }
            }

            // creation of new gene with a pointer to new node
            newgene = new Gene(_gene, assoc_trait, inode, onode);
            genes_dup.add(newgene);
        }

        // okay all nodes created, the new genome can be generate
        newgenome = new Genome(new_id, traits_dup, nodes_dup, genes_dup);
        return newgenome;
    }

    public Genome(int id, Vector t, Vector n, Vector g) {
        genome_id = id;
        traits = t;
        nodes = n;
        genes = g;
        notes = null;
        phenotype = null;
    }

    public void mutate_link_weight(double power, double rate, int mutation_type) {
        double _weight = 0.0;
        double num; //counts gene placement
        double gene_total;
        double powermod; //Modified power by gene number

        //The power of mutation will rise farther into the genome
        //on the theory that the older genes are more fit since
        //they have stood the test of time

        double randnum;
        double randchoice; //Decide what kind of mutation to do on a gene
        double endpart; //Signifies the last part of the genome
        double gausspoint;
        double coldgausspoint;

        boolean severe; //Once in a while really shake things up

        // for 50% of Prob. // severe is true

        if (NeatRoutine.randfloat() > 0.5)
            severe = true;
        else
            severe = false;

        num = 0.0;
        gene_total = (double) genes.size();
        endpart = gene_total * 0.8;
        powermod = 1.0;

        Iterator itr_gene = genes.iterator();
        itr_gene = genes.iterator();

        while (itr_gene.hasNext()) {

            Gene _gene = ((Gene) itr_gene.next());
            if (severe) {
                gausspoint = 0.3;
                coldgausspoint = 0.1;
            } else {
                // with other 50%.....
                if ((gene_total >= 10.0) && (num > endpart)) {
                    gausspoint = 0.5;
                    coldgausspoint = 0.3;
                } else {
                    if (NeatRoutine.randfloat() > 0.5) {
                        gausspoint = 1.0 - rate;
                        coldgausspoint = 1.0 - rate - 0.1;
                    } else {
                        gausspoint = 1.0 - rate;
                        coldgausspoint = 1.0 - rate;
                    }
                }
            }

            // choise a number from ]-1,+1[
            randnum = NeatRoutine.randposneg() * NeatRoutine.randfloat() * power * powermod;

            if (mutation_type == NeatConstant.GAUSSIAN) {
                randchoice = NeatRoutine.randfloat(); // a number from ]0,1[
                if (randchoice > gausspoint)
                    _gene.lnk.weight += randnum;
                else if (randchoice > coldgausspoint)
                    _gene.lnk.weight = randnum;
            } else if (mutation_type == NeatConstant.COLDGAUSSIAN)
                _gene.lnk.weight = randnum;

            // copy to mutation_num, the current weight
            _gene.mutation_num = _gene.lnk.weight;
            num += 1.0;
        }
    }

    public Network genesis(int id) {

        Network newnet = null;
        Trait curtrait = null;
        // Vector nodes_dup = new Vector(1, 0);
        NNode newnode = null;
        Vector inlist = new Vector(1, 0);
        Vector outlist = new Vector(1, 0);
        Vector all_list = new Vector(nodes.size(), 0);

        Link curlink = null;
        Link newlink = null;
        NNode inode = null;
        NNode onode = null;

        int _trait_id;


        Iterator itr_node = nodes.iterator();
        Iterator itr_gene = genes.iterator();

        itr_node = nodes.iterator();
        while (itr_node.hasNext()) {
            NNode _node = ((NNode) itr_node.next());

            //create a copy of gene node for fenotype
            newnode = new NNode(_node.type, _node.node_id);

            //Derive link's parameters from its Trait pointer
            // of nodetrait

            curtrait = _node.nodetrait;
            newnode.derive_trait(curtrait);
            newnode.inner_level = 0;

            //****ADD 19/02/2002
            newnode.gen_node_label = _node.gen_node_label;
            //****END ADD



            // new field
            newnode.is_traversed = false;

            if (_node.gen_node_label == NeatConstant.INPUT)
                inlist.add(newnode);
            if (_node.gen_node_label == NeatConstant.BIAS)
                inlist.add(newnode);
            if (_node.gen_node_label == NeatConstant.OUTPUT)
                outlist.add(newnode);

            // add to genotype the pointer to phenotype node
            all_list.add(newnode);
            _node.analogue = newnode;
        }

        if (genes.size() == 0) {
            System.out.print("\n ALERT : are a network whitout GENES; the result can unpredictable");
        }

        if (outlist.size() == 0) {
            System.out.print("\n ALERT : are a network whitout OUTPUTS; the result can unpredictable");
            this.op_view();
        }

        itr_gene = genes.iterator();
        while (itr_gene.hasNext()) {
            Gene _gene = ((Gene) itr_gene.next());

            //Only create the link if the gene is enabled
            if (_gene.enable == true) {

                curlink = _gene.lnk;

                inode = curlink.in_node.analogue;
                onode = curlink.out_node.analogue;
                //NOTE: This line could be run through a recurrency check if desired
                // (no need to in the current implementation of NEAT)
                newlink = new Link(curlink.weight, inode, onode, curlink.is_recurrent);
                onode.incoming.add(newlink);
                inode.outgoing.add(newlink);

                //Derive link's parameters from its Trait pointer
                // of linktrait
                curtrait = curlink.linktrait;
                curlink.derive_trait(curtrait);
            }
        }

        //Create the new network
        newnet = new Network(inlist, outlist, all_list, id);
        //Attach genotype and phenotype together:
        //  newnet point to owner genotype (this)
        newnet.setGenotype(this);
        //  genotype point to owner phenotype (newnet)

        phenotype = newnet;
        return newnet;
    }

    /**
     *     This function gives a measure of compatibility between
     *     two Genomes by computing a linear combination of 3
     *     characterizing variables of their compatibilty.
     *     The 3 variables represent PERCENT DISJOINT GENES,
     *     PERCENT EXCESS GENES, MUTATIONAL DIFFERENCE WITHIN
     *     MATCHING GENES.  So the formula for compatibility
     *     is:  disjoint_coeff*pdg+excess_coeff*peg+mutdiff_coeff*mdmg.
     *     The 3 coefficients are global system parameters
     */
    public double compatibility(Genome g) {

        //Innovation numbers
        double p1innov;
        double p2innov;

        //Intermediate value
        double mut_diff;

        //Set up the counters
        double num_disjoint = 0.0;
        double num_excess = 0.0;
        double mut_diff_total = 0.0;
        double num_matching = 0.0; //Used to normalize mutation_num differences

        Gene _gene1 = null;
        Gene _gene2 = null;

        double max_genome_size; //Size of larger Genome

        //Get the length of the longest Genome for percentage computations
        int size1 = genes.size();
        int size2 = g.genes.size();
        max_genome_size = Math.max(size1, size2);
        //Now move through the Genes of each potential parent
        //until both Genomes end
        int j = 0;
        int j1 = 0;
        int j2 = 0;

        for (j = 0; j < max_genome_size; j++) {

            if (j1 >= size1) {
                num_excess += 1.0;
                j2++;
            } else if (j2 >= size2) {
                num_excess += 1.0;
                j1++;
            } else {
                _gene1 = (Gene) genes.elementAt(j1);
                _gene2 = (Gene) g.genes.elementAt(j2);

                //Extract current innovation numbers
                p1innov = _gene1.innovation_num;
                p2innov = _gene2.innovation_num;

                if (p1innov == p2innov) {
                    num_matching += 1.0;
                    mut_diff = Math.abs(_gene1.mutation_num - _gene2.mutation_num);
                    mut_diff_total += mut_diff;
                    j1++;
                    j2++;
                } else if (p1innov < p2innov) {
                    j1++;
                    num_disjoint += 1.0;
                } else if (p2innov < p1innov) {
                    j2++;
                    num_disjoint += 1.0;
                }
            }
        }

        // Return the compatibility number using compatibility formula
        // Note that mut_diff_total/num_matching gives the AVERAGE
        // difference between mutation_nums for any two matching Genes
        // in the Genome.
        // Look at disjointedness and excess in the absolute (ignoring size)

        return ( Neat.p_disjoint_coeff * (num_disjoint / 1.0) + Neat.p_excess_coeff * (num_excess / 1.0)
                 + Neat.p_mutdiff_coeff * (mut_diff_total / num_matching));
    }

    public double get_last_gene_innovnum() {
        return (((Gene) genes.lastElement()).innovation_num + 1);
    }

    public int get_last_node_id() {
        return (((NNode) nodes.lastElement()).node_id + 1);
    }

    public void op_view() {

        System.out.print("\n GENOME START   id=" + genome_id);
        System.out.print("\n  genes are :" + genes.size());
        System.out.print("\n  nodes are :" + nodes.size());
        System.out.print("\n  trait are :" + traits.size());

        Iterator itr_node = nodes.iterator();
        itr_node = nodes.iterator();

        while (itr_node.hasNext()) {
            NNode _node = ((NNode) itr_node.next());

            if (_node.getGen_node_label() == NeatConstant.INPUT)
                System.out.print("\n Input ");
            if (_node.getGen_node_label() == NeatConstant.OUTPUT)
                System.out.print("\n Output");
            if (_node.getGen_node_label() == NeatConstant.HIDDEN)
                System.out.print("\n Hidden");
            if (_node.getGen_node_label() == NeatConstant.BIAS)
                System.out.print("\n Bias  ");
            _node.op_view();

        }

        Iterator itr_gene = genes.iterator();
        itr_gene = genes.iterator();

        while (itr_gene.hasNext()) {
            Gene _gene = ((Gene) itr_gene.next());
            _gene.op_view();

        }

        System.out.print("\n");
        System.out.print(" Traits:\n");

        Iterator itr_trait = traits.iterator();
        itr_trait = traits.iterator();

        while (itr_trait.hasNext()) {
            Trait _trait = ((Trait) itr_trait.next());
            _trait.op_view();
        }
        System.out.print("\n");
        System.out.print(" GENOME END");
    }

    public boolean verify() {

        NNode inode = null;
        NNode onode = null;
        int i1 = 0;
        int o1 = 0;
        boolean r1 = false;
        boolean disab = false;
        int last_id = 0;

        Iterator itr_gene = genes.iterator();
        Iterator itr_gene1 = genes.iterator();
        Iterator itr_node = nodes.iterator();

        if (genes.size() == 0) {
            //         System.out.print("\n DEBUG genome.costructor.genome.random");
            //         System.out.println("\n *ERROR* are not genes");
            return false;
        }

        if (nodes.size() == 0) {
            //         System.out.print("\n DEBUG genome.costructor.genome.random");
            //         System.out.println("\n *ERROR* are not nodes");
            return false;
        }

        if (traits.size() == 0) {
            //         System.out.print("\n DEBUG genome.costructor.genome.random");
            //         System.out.println(" *ERROR*\n are not traits");
            return false;
        }

        // control if nodes in gene are defined and are the same nodes il nodes list
        itr_gene = genes.iterator();
        while (itr_gene.hasNext()) {

            Gene _gene = ((Gene) itr_gene.next());

            inode = _gene.lnk.in_node;
            onode = _gene.lnk.out_node;

            if (inode == null) {
                System.out.println(" *ERROR* inode = null in genome #" + genome_id);
                return false;
            }
            if (onode == null) {
                System.out.println(" *ERROR* onode = null in genome #" + genome_id);
                return false;
            }
            if (!nodes.contains(inode)) {
                System.out.println(
                                   "Missing inode:  node defined in gene not found in Vector nodes of genome #"
                                   + genome_id);
                System.out.print("\n the inode is=" + inode.node_id);
                return false;
            }
            if (!nodes.contains(onode)) {
                System.out.println(
                                   "Missing onode:  node defined in gene not found in Vector nodes of genome #"
                                   + genome_id);
                System.out.print("\n the onode is=" + onode.node_id);
                return false;
            }
        }

        // verify if list nodes is ordered
        itr_node = nodes.iterator();
        while (itr_node.hasNext()) {
            NNode _node = ((NNode) itr_node.next());
            if (_node.node_id < last_id) {
                System.out.println("ALERT: NODES OUT OF ORDER : ");
                System.out.println(
                                   " last node_id is= " + last_id + " , current node_id=" + _node.node_id);
                return false;
            }
            last_id = _node.node_id;
        }

        // control in genes are gene duplicate for contents

        itr_gene = genes.iterator();
        while (itr_gene.hasNext()) {
            Gene _gene = ((Gene) itr_gene.next());
            i1 = _gene.lnk.in_node.node_id;
            o1 = _gene.lnk.out_node.node_id;
            r1 = _gene.lnk.is_recurrent;

            itr_gene1 = itr_gene;
            while (itr_gene1.hasNext()) {
                Gene _gene1 = ((Gene) itr_gene1.next());
                if (_gene1.lnk.in_node.node_id == i1
                    && _gene1.lnk.out_node.node_id == o1
                    && _gene1.lnk.is_recurrent == r1)
                    {
                        System.out.print(" \n  ALERT: DUPLICATE GENES :");
                        System.out.print("  inp_node=" + i1 + " out_node=" + o1);
                        System.out.print("  in GENOME id -->" + genome_id);
                        System.out.print("  gene1 is : ");
                        _gene.op_view();
                        System.out.print("  gene2 is : ");
                        _gene1.op_view();

                        return false;
                    }
            }
        }

        if (nodes.size() >= 500) {
            disab = false;
            itr_gene = genes.iterator();
            while (itr_gene.hasNext()) {
                Gene _gene = ((Gene) itr_gene.next());

                if (!_gene.enable && disab) {
                    System.out.print("\n ALERT: 2 DISABLES IN A ROW: " + _gene.lnk.in_node.node_id);
                    System.out.print(" inp node=" + _gene.lnk.in_node.node_id);
                    System.out.print(" out node=" + _gene.lnk.out_node.node_id);
                    System.out.print(" for GENOME " + genome_id);
                    System.out.print("\n Gene is :");
                    _gene.op_view();
                }

                if (!_gene.enable)
                    disab = true;
                else
                    disab = false;
            }
        }

        return true;
    }

    public void print_to_filename(String xNameFile) {
         // write to file genome in native format (for re-read)
         IOseq xFile;

        xFile = new IOseq(xNameFile);
        xFile.IOseqOpenW(false);

        try {
            print_to_file(xFile);
        } catch (Throwable e) {
            System.err.println(e);
        }

        xFile.IOseqCloseW();
    }

    public Genome mate_multipoint(Genome g, int genomeid, double fitness1, double fitness2) {

        Genome new_genome = null;
        boolean disable = false; //Set to true if we want to disabled a chosen gene
        boolean done = false;
        boolean done_in = false;
        boolean done_out = false;

        //   int h = 0;
        int traitnum = 0;
        int nodetraitnum = 0;
        int len = 0;

        Gene _curgene2 = null;
        Gene newgene = null;
        NNode inode = null;
        NNode onode = null;
        NNode new_inode = null;
        NNode new_onode = null;
        NNode curnode = null;

        Gene chosengene = null;
        Gene _p1gene = null;
        Gene _p2gene = null;
        Trait newtrait = null;
        Trait _trait1 = null;
        Trait _trait2 = null;
        double p1innov = 0;
        double p2innov = 0;

        int j;
        int j1;
        int j2;


        //Tells if the first genome (this one) has better fitness or not
        boolean skip = false;

        //First, average the Traits from the 2 parents to form the baby's Traits
        //It is assumed that trait vectors are the same length
        //In the future, may decide on a different method for
        //trait mating (corrispondenza)

        int len_traits = traits.size();

        Vector newtraits = new Vector(len_traits, 0);

        for (j = 0; j < len_traits; j++) {
            _trait1 = (Trait) traits.elementAt(j);
            _trait2 = (Trait) g.traits.elementAt(j);
            newtrait = new Trait(_trait1, _trait2);
            newtraits.add(newtrait);

        }

        //Figure out which genome is better
        //The worse genome should not be allowed to add extra structural baggage
        //If they are the same, use the smaller one's disjoint and excess genes only

        boolean p1better = false;

        int size1 = genes.size();
        int size2 = g.genes.size();

        if (fitness1 > fitness2) {
            p1better = true;
        } else if (fitness1 == fitness2) {
            if (size1 < size2)
                p1better = true;
        }

        int len_genome = Math.max(size1, size2);
        int len_nodes = nodes.size();

        Vector newgenes = new Vector(len_genome, 0);
        Vector newnodes = new Vector(len_nodes, 0);

        Iterator itr_newgenes;

        j1=0;
        j2=0;

        int control_disable = 0;
        int exist_disable = 0;

        while (j1 < size1 || j2 < size2) {
            //
            //  chosen of 'just' gene
            //

            skip = false; //Default to not skipping a chosen gene
            if (j1 >= size1) {
                chosengene = (Gene) g.genes.elementAt(j2);
                j2++;
                if (p1better)
                    skip = true; //Skip excess from the worse genome
            } else if (j2 >= size2) {
                chosengene = (Gene) genes.elementAt(j1);
                j1++;
                if (!p1better)
                    skip = true; //Skip excess from the worse genome
            } else {

                _p1gene = (Gene) genes.elementAt(j1);
                _p2gene = (Gene) g.genes.elementAt(j2);

                p1innov = _p1gene.innovation_num;
                p2innov = _p2gene.innovation_num;
                if (p1innov == p2innov) {
                    if (NeatRoutine.randfloat() < 0.5)
                        chosengene = _p1gene;
                    else
                        chosengene = _p2gene;

                    //If one is disabled, the corresponding gene in the offspring
                    //will likely be disabled
                    disable = false;
                    if ((_p1gene.enable == false) || (_p2gene.enable == false)) {
                        exist_disable++;
                        if (NeatRoutine.randfloat() < 0.75) {
                            disable = true;
                            control_disable++;
                        }
                    }
                    j1++;
                    j2++;

                } else if (p1innov < p2innov) {
                    chosengene = _p1gene;
                    j1++;
                    if (!p1better)
                        skip = true;
                } else if (p2innov < p1innov) {
                    chosengene = _p2gene;
                    j2++;
                    if (p1better)
                        skip = true;
                }
            }// end chosen gene

            //
            //
            //Check to see if the chosengene conflicts with an already chosen gene
            //i.e. do they represent the same link
            //

            itr_newgenes = newgenes.iterator();

            while (itr_newgenes.hasNext()) {
                _curgene2 = ((Gene) itr_newgenes.next());

                if (_curgene2.lnk.in_node.node_id == chosengene.lnk.in_node.node_id
                    && _curgene2.lnk.out_node.node_id == chosengene.lnk.out_node.node_id
                    && _curgene2.lnk.is_recurrent == chosengene.lnk.is_recurrent)
                    {
                        skip = true;
                        break;
                    }
                if (_curgene2.lnk.in_node.node_id == chosengene.lnk.out_node.node_id
                    && _curgene2.lnk.out_node.node_id == chosengene.lnk.in_node.node_id
                    && !_curgene2.lnk.is_recurrent
                    && !chosengene.lnk.is_recurrent)
                    {
                        skip = true;
                        break;
                    }
            }

            //
            //
            //
            if (!skip) {
                //Now add the chosengene to the baby
                //First, get the trait pointer
                int first_traitnum = ((Trait) traits.firstElement()).trait_id;

                if (chosengene.lnk.linktrait == null)
                    traitnum = first_traitnum;
                else
                    traitnum = chosengene.lnk.linktrait.trait_id - first_traitnum;

                //Next check for the nodes, add them if not in the baby Genome already
                inode = chosengene.lnk.in_node;
                onode = chosengene.lnk.out_node;

                //Check for inode in the newnodes list

                //
                //Check for inode, onode in the newnodes list
                //


                //--------------------------------------------------------------------------------
                boolean found;
                if (inode.node_id < onode.node_id) {
                    //
                    // search the inode
                    //
                    found = false;
                    for (int ix = 0; ix < newnodes.size(); ix++) {
                        curnode = (NNode) newnodes.elementAt(ix);
                        if (curnode.node_id == inode.node_id) {
                            found = true;
                            break;
                        }
                    }

                    // if exist , point to exitsting version
                    if (found)
                        new_inode = curnode;

                    // else create the inode
                    else
                        {
                            if (inode.nodetrait == null)
                                nodetraitnum = 0;
                            else
                                nodetraitnum = inode.nodetrait.trait_id  - first_traitnum;

                            newtrait = (Trait) newtraits.elementAt(nodetraitnum);
                            new_inode = new NNode(inode, newtrait);
                            //insert in newnodes list
                            node_insert(newnodes, new_inode);
                        }



                    //
                    // search the onode
                    //
                    found = false;
                    for (int ix = 0; ix < newnodes.size(); ix++) {
                        curnode = (NNode) newnodes.elementAt(ix);
                        if (curnode.node_id == onode.node_id) {
                            found = true;
                            break;
                        }
                    }

                    // if exist , point to exitsting version
                    if (found)
                        new_onode = curnode;

                    // else create the onode
                    else
                        {
                            if (onode.nodetrait == null)
                                nodetraitnum = 0;
                            else
                                nodetraitnum = onode.nodetrait.trait_id  - first_traitnum;

                            newtrait = (Trait) newtraits.elementAt(nodetraitnum);
                            new_onode = new NNode(onode, newtrait);
                            //insert in newnodes list
                            node_insert(newnodes, new_onode);
                        }

                    // end block : inode.node_id < onode.node_id
                } else {

                    //
                    // search the onode
                    //
                    found = false;
                    for (int ix = 0; ix < newnodes.size(); ix++) {
                        curnode = (NNode) newnodes.elementAt(ix);
                        if (curnode.node_id == onode.node_id) {
                            found = true;
                            break;
                        }
                    }

                    // if exist , point to exitsting version
                    if (found)
                        new_onode = curnode;

                    // else create the onode
                    else
                        {
                            if (onode.nodetrait == null)
                                nodetraitnum = 0;
                            else
                                nodetraitnum = onode.nodetrait.trait_id  - first_traitnum;

                            newtrait = (Trait) newtraits.elementAt(nodetraitnum);
                            new_onode = new NNode(onode, newtrait);
                            //insert in newnodes list
                            node_insert(newnodes, new_onode);
                        }




                    //
                    // search the inode
                    //
                    found = false;
                    for (int ix = 0; ix < newnodes.size(); ix++) {
                        curnode = (NNode) newnodes.elementAt(ix);
                        if (curnode.node_id == inode.node_id) {
                            found = true;
                            break;
                        }
                    }

                    // if exist , point to exitsting version
                    if (found)
                        new_inode = curnode;

                    // else create the inode
                    else
                        {
                            if (inode.nodetrait == null)
                                nodetraitnum = 0;
                            else
                                nodetraitnum = inode.nodetrait.trait_id  - first_traitnum;

                            newtrait = (Trait) newtraits.elementAt(nodetraitnum);
                            new_inode = new NNode(inode, newtrait);
                            //insert in newnodes list
                            node_insert(newnodes, new_inode);
                        }
                }

                //--------------------------------------------------------------------------------

                //Add the Gene
                newtrait = (Trait) newtraits.elementAt(traitnum);
                newgene = new Gene(chosengene, newtrait, new_inode, new_onode);
                if (disable) {
                    newgene.enable = false;
                    disable = false;
                }
                newgenes.add(newgene);
            }

        } // end block genome (while)

        new_genome = new Genome(genomeid, newtraits, newnodes, newgenes);






        // ----------------------------------------------------------------------------------------

        //	boolean h = new_genome.verify();
        boolean found = false;
        for (int ix = 0; ix < newnodes.size(); ix++) {
            curnode = (NNode) newnodes.elementAt(ix);
            if (curnode.gen_node_label == NeatConstant.OUTPUT)
                {
                    found = true;
                    break;
                }
        }

        if (!found) {
            System.out.print("\n *--------------- not found output node ----------------------------");
            System.out.print("\n * during mate_multipoint : please control the following's *********");
            System.out.print("\n * control block : ");
            System.out.print("\n Genome A= ");
            this.op_view();
            System.out.print("\n Genome B= ");
            g.op_view();
            System.out.print("\n Result = ");
            new_genome.op_view();
            System.exit(0);
        }
        // ----------------------------------------------------------------------------------------

        return new_genome;
    }

    public Genome mate_multipoint_avg(Genome g, int genomeid, double fitness1, double fitness2) {

        Genome new_genome = null;
        boolean disable = false; //Set to true if we want to disabled a chosen gene
        boolean done = false;
        boolean done_in = false;
        boolean done_out = false;
        int traitnum = 0;
        int nodetraitnum = 0;
        int len = 0;

        int control_disable = 0;
        int exist_disable = 0;

        Gene chosengene = null;
        Gene _curgene2 = null;
        Gene newgene = null;
        NNode inode = null;
        NNode onode = null;
        NNode new_inode = null;
        NNode new_onode = null;
        NNode curnode = null;

        Gene _p1gene = null;
        Gene _p2gene = null;
        Trait newtrait = null;
        Trait _trait1 = null;
        Trait _trait2 = null;
        double p1innov = 0;
        double p2innov = 0;

        int j;
        int j1;
        int j2;
        boolean skip = false;


        //Set up the avgene
        Gene avgene =
            new Gene((Trait) null, 0.0, (NNode) null, (NNode) null, false, 0.0, 0.0);
        //First, average the Traits from the 2 parents to form the baby's Traits
        //It is assumed that trait vectors are the same length
        //In the future, may decide on a different method for
        //trait mating (corrispondenza)
        int len_traits = traits.size();

        Vector newtraits = new Vector(len_traits, 0);
        for (j = 0; j < len_traits; j++) {
            _trait1 = (Trait) traits.elementAt(j);
            _trait2 = (Trait) g.traits.elementAt(j);
            newtrait = new Trait(_trait1, _trait2);
            newtraits.add(newtrait);

        }

        //Figure out which genome is better
        //The worse genome should not be allowed to add extra structural baggage
        //If they are the same, use the smaller one's disjoint and excess genes only

        boolean p1better = false;
        int size1 = genes.size();
        int size2 = g.genes.size();

        if (fitness1 > fitness2)
            p1better = true;
        else
            if (fitness1 == fitness2) {
                if (size1 < size2)
                    p1better = true;
            }
        int len_genome = Math.max(size1, size2);
        int len_nodes = nodes.size();

        Vector newgenes = new Vector(len_genome, 0);
        Vector newnodes = new Vector(len_nodes, 0);

        Iterator itr_newgenes;

        j1 = 0;
        j2 = 0;

        while (j1 < size1 || j2 < size2)

            //while (newgenes.size() < len_genome)

            {
                //
                //  chosen of 'just' gene
                //
                avgene.enable = true; //Default to enabled
                skip = false; //Default to not skipping a chosen gene

                if (j1 >= size1) {
                    chosengene = (Gene) g.genes.elementAt(j2);
                    j2++;
                    if (p1better)
                        skip = true; //Skip excess from the worse genome
                }
                else
                    if (j2 >= size2) {
                        chosengene = (Gene) genes.elementAt(j1);
                        j1++;
                        if (!p1better)
                            skip = true; //Skip excess from the worse genome
                    }
                    else {

                        _p1gene = (Gene) genes.elementAt(j1);
                        _p2gene = (Gene) g.genes.elementAt(j2);
                        p1innov = _p1gene.innovation_num;
                        p2innov = _p2gene.innovation_num;
                        if (p1innov == p2innov) {

                            if (NeatRoutine.randfloat() > 0.5)
                                avgene.lnk.linktrait = _p1gene.lnk.linktrait;
                            else
                                avgene.lnk.linktrait = _p2gene.lnk.linktrait;

                            //WEIGHTS AVERAGED HERE
                            avgene.lnk.weight = (_p1gene.lnk.weight + _p2gene.lnk.weight) / 2.0;

                            if (NeatRoutine.randfloat() > 0.5)
                                avgene.lnk.in_node = _p1gene.lnk.in_node;
                            else
                                avgene.lnk.in_node = _p2gene.lnk.in_node;

                            if (NeatRoutine.randfloat() > 0.5)
                                avgene.lnk.out_node = _p1gene.lnk.out_node;
                            else
                                avgene.lnk.out_node = _p2gene.lnk.out_node;

                            if (NeatRoutine.randfloat() > 0.5)
                                avgene.lnk.is_recurrent = _p1gene.lnk.is_recurrent;
                            else
                                avgene.lnk.is_recurrent = _p2gene.lnk.is_recurrent;

                            avgene.innovation_num = _p1gene.innovation_num;
                            avgene.mutation_num = (_p1gene.mutation_num + _p2gene.mutation_num) / 2.0;

                            //If one is disabled, the corresponding gene in the offspring
                            //will likely be disabled
                            disable = false;
                            if ((_p1gene.enable == false) || (_p2gene.enable == false)) {
                                exist_disable++;

                                if (NeatRoutine.randfloat() < 0.75) {
                                    disable = true;
                                    control_disable++;
                                }
                            }

                            chosengene = avgene;

                            j1++;
                            j2++;

                        }
                        else
                            if (p1innov < p2innov) {
                                chosengene = _p1gene;
                                j1++;
                                if (!p1better)
                                    skip = true;
                            }
                            else
                                if (p2innov < p1innov) {
                                    chosengene = _p2gene;
                                    j2++;
                                    if (p1better)
                                        skip = true;
                                }
                    } // end chosen gene

                //
                //
                //Check to see if the chosengene conflicts with an already chosen gene
                //i.e. do they represent the same link
                //

                itr_newgenes = newgenes.iterator();

                while (itr_newgenes.hasNext()) {
                    _curgene2 = ((Gene) itr_newgenes.next());

                    if (_curgene2.lnk.in_node.node_id == chosengene.lnk.in_node.node_id
                        && _curgene2.lnk.out_node.node_id == chosengene.lnk.out_node.node_id
                        && _curgene2.lnk.is_recurrent == chosengene.lnk.is_recurrent) {
                        skip = true;
                        break;
                    }
                    if (_curgene2.lnk.in_node.node_id == chosengene.lnk.out_node.node_id
                        && _curgene2.lnk.out_node.node_id == chosengene.lnk.in_node.node_id
                        && !_curgene2.lnk.is_recurrent
                        && !chosengene.lnk.is_recurrent) {
                        skip = true;
                        break;
                    }

                }

                //
                //
                //

                if (!skip) {
                    //Now add the chosengene to the baby
                    //First, get the trait pointer
                    int first_traitnum = ((Trait) traits.firstElement()).trait_id;

                    if (chosengene.lnk.linktrait == null)
                        traitnum = first_traitnum;
                    else
                        traitnum = chosengene.lnk.linktrait.trait_id - first_traitnum;

                    //Next check for the nodes, add them if not in the baby Genome already
                    inode = chosengene.lnk.in_node;
                    onode = chosengene.lnk.out_node;

                    //Check for inode in the newnodes list
                    //

                    //--------------------------------------------------------------------------------
                    boolean found;
                    if (inode.node_id < onode.node_id) {
                        //
                        // search the inode
                        //
                        found = false;
                        for (int ix = 0; ix < newnodes.size(); ix++) {
                            curnode = (NNode) newnodes.elementAt(ix);
                            if (curnode.node_id == inode.node_id) {
                                found = true;
                                break;
                            }

                        }

                        // if exist , point to exitsting version
                        if (found)
                            new_inode = curnode;

                        // else create the inode
                        else {
                            if (inode.nodetrait == null)
                                nodetraitnum = 0;
                            else
                                nodetraitnum = inode.nodetrait.trait_id - first_traitnum;

                            newtrait = (Trait) newtraits.elementAt(nodetraitnum);
                            new_inode = new NNode(inode, newtrait);
                            //insert in newnodes list
                            node_insert(newnodes, new_inode);
                        }

                        //
                        // search the onode
                        //
                        found = false;
                        for (int ix = 0; ix < newnodes.size(); ix++) {
                            curnode = (NNode) newnodes.elementAt(ix);
                            if (curnode.node_id == onode.node_id) {
                                found = true;
                                break;
                            }

                        }

                        // if exist , point to exitsting version
                        if (found)
                            new_onode = curnode;

                        // else create the onode
                        else {
                            if (onode.nodetrait == null)
                                nodetraitnum = 0;
                            else
                                nodetraitnum = onode.nodetrait.trait_id - first_traitnum;

                            newtrait = (Trait) newtraits.elementAt(nodetraitnum);
                            new_onode = new NNode(onode, newtrait);
                            //insert in newnodes list
                            node_insert(newnodes, new_onode);
                        }

                    } // end block : inode.node_id < onode.node_id

                    else {

                        //
                        // search the onode
                        //
                        found = false;
                        for (int ix = 0; ix < newnodes.size(); ix++) {
                            curnode = (NNode) newnodes.elementAt(ix);
                            if (curnode.node_id == onode.node_id) {
                                found = true;
                                break;
                            }

                        }

                        // if exist , point to exitsting version
                        if (found)
                            new_onode = curnode;

                        // else create the onode
                        else {
                            if (onode.nodetrait == null)
                                nodetraitnum = 0;
                            else
                                nodetraitnum = onode.nodetrait.trait_id - first_traitnum;

                            newtrait = (Trait) newtraits.elementAt(nodetraitnum);
                            new_onode = new NNode(onode, newtrait);
                            //insert in newnodes list
                            node_insert(newnodes, new_onode);
                        }

                        //
                        // search the inode
                        //
                        found = false;
                        for (int ix = 0; ix < newnodes.size(); ix++) {
                            curnode = (NNode) newnodes.elementAt(ix);
                            if (curnode.node_id == inode.node_id) {
                                found = true;
                                break;
                            }

                        }

                        // if exist , point to exitsting version
                        if (found)
                            new_inode = curnode;

                        // else create the inode
                        else {
                            if (inode.nodetrait == null)
                                nodetraitnum = 0;
                            else
                                nodetraitnum = inode.nodetrait.trait_id - first_traitnum;

                            newtrait = (Trait) newtraits.elementAt(nodetraitnum);
                            new_inode = new NNode(inode, newtrait);
                            //insert in newnodes list
                            node_insert(newnodes, new_inode);
                        }

                    }

                    //--------------------------------------------------------------------------------

                    //Add the Gene
                    newtrait = (Trait) newtraits.elementAt(traitnum);
                    newgene = new Gene(chosengene, newtrait, new_inode, new_onode);
                    if (disable) {
                        newgene.enable = false;
                        disable = false;
                    }
                    newgenes.add(newgene);

                }

            } // end block genome

        new_genome = new Genome(genomeid, newtraits, newnodes, newgenes);

        return new_genome;
    }

    public Genome mate_singlepoint(Genome g, int genomeid) {
        String mask4 = " 0000";
        DecimalFormat fmt4 = new DecimalFormat(mask4);
        Genome new_genome = null;
        Gene chosengene = null;
        Trait _trait1 = null;
        Trait _trait2 = null;
        Trait newtrait = null;
        int stopA = 0;
        int stopB = 0;
        int j;
        int j1;
        int j2;
        int control_addnode = 0;
        int len_traits = traits.size();
        int size1 = genes.size();
        int size2 = g.genes.size();
        int crosspoint = 0;
        int len_genome = 0;
        int len = 0;

        double p1innov = 0.0;
        double p2innov = 0.0;

        Iterator itr_newgenes;

        NNode curnode = null;
        NNode inode = null;
        NNode onode = null;
        NNode new_inode = null;
        NNode new_onode = null;
        Gene newgene = null;
        boolean disable = false;

        int traitnum = 0;
        int nodetraitnum = 0;

        Vector genomeA;
        Vector genomeB;
        Gene geneA = null;
        Gene geneB = null;
        Gene _curgene2 = null;
        int genecounter = 0; //Ready to count to crosspoint
        boolean skip = false; //Default to not skip a Gene
        boolean done_in = false;
        boolean done_out = false;

        Vector newtraits = new Vector(len_traits, 0);

        for (j = 0; j < len_traits; j++)
            {
                _trait1 = (Trait) traits.elementAt(j);
                _trait2 = (Trait) g.traits.elementAt(j);
                newtrait = new Trait(_trait1, _trait2);
                newtraits.add(newtrait);

            }

        //Set up the avgene
        Gene avgene =
            new Gene((Trait) null, 0.0, (NNode) null, (NNode) null, false, 0.0, 0.0);

        Vector newgenes = new Vector(genes.size(), 0);
        Vector newnodes = new Vector(nodes.size(), 0);

        if (size1 < size2)
            {
                crosspoint = NeatRoutine.randint(0, size1 - 1);
                stopA = size1;
                stopB = size2;
                len_genome = size2;
                genomeA = genes;
                genomeB = g.genes;
            }
        else
            {
                crosspoint = NeatRoutine.randint(0, size2 - 1);
                stopA = size2;
                stopB = size1;
                len_genome = size1;
                genomeA = g.genes;
                genomeB = genes;
            }

        //System.out.print("\n crossing point is :"+crosspoint);

        genecounter = 0;

        boolean doneA = false;
        boolean doneB = false;
        boolean done = false;
        double v1 = 0.0;
        double v2 = 0.0;
        double vmax = 0.0;
        double cellA = 0.0;
        double cellB = 0.0;

        j1 = 0;
        j2 = 0;
        j = 0;

        //
        // compute what is the hight innovation
        //

        double last_innovB = ((Gene) genomeB.elementAt(stopB - 1)).innovation_num;
        double cross_innov = 0;

        while (!done)
            {

                doneA = false;
                doneB = false;
                skip = false;
                avgene.enable = true; //Default to true

                if (j1 < stopA)
                    {
                        geneA = (Gene) genomeA.elementAt(j1);
                        v1 = geneA.innovation_num;
                        doneA = true;
                    }
                if (j2 < stopB)
                    {
                        geneB = (Gene) genomeB.elementAt(j2);
                        v2 = geneB.innovation_num;
                        doneB = true;
                    }

                if (doneA && doneB)
                    {
			//
                        if (v1 < v2)
                            {
                                cellA = v1;
                                cellB = 0.0;
                                j1++;
                            }
                        else if (v1 == v2)
                            {
                                cellA = v1;
                                cellB = v1;
                                j1++;
                                j2++;
                            }
                        else
                            {
                                cellA = 0.0;
                                cellB = v2;
                                j2++;
                            }
                    }


                else
                    {
                        if (doneA && !doneB)
                            {
                                cellA = v1;
                                cellB = 0.0;
                                j1++;
                            }
                        else if (!doneA && doneB)
                            {
                                cellA = 0.0;
                                cellB = v2;
                                j2++;
                            }
                        else
                            done = true;
                    }





                if (!done)
                    {

			// -------------------------------------------------------------------------------
			//                        innovA = innovB
			// -------------------------------------------------------------------------------

                        if (cellA == cellB)
                            {
                                if (genecounter < crosspoint)
                                    {
                                        chosengene = geneA;
                                        genecounter++;
                                    }
                                else if (genecounter == crosspoint)
                                    {
                                        if (NeatRoutine.randfloat() > 0.5)
                                            avgene.lnk.linktrait = geneA.lnk.linktrait;
                                        else
                                            avgene.lnk.linktrait = geneB.lnk.linktrait;

                                        //WEIGHTS AVERAGED HERE
                                        avgene.lnk.weight = (geneA.lnk.weight + geneB.lnk.weight) / 2.0;

                                        if (NeatRoutine.randfloat() > 0.5)
                                            avgene.lnk.in_node = geneA.lnk.in_node;
                                        else
                                            avgene.lnk.in_node = geneB.lnk.in_node;

                                        if (NeatRoutine.randfloat() > 0.5)
                                            avgene.lnk.out_node = geneA.lnk.out_node;
                                        else
                                            avgene.lnk.out_node = geneB.lnk.out_node;

                                        if (NeatRoutine.randfloat() > 0.5)
                                            avgene.lnk.is_recurrent = geneA.lnk.is_recurrent;
                                        else
                                            avgene.lnk.is_recurrent = geneB.lnk.is_recurrent;

                                        avgene.innovation_num = geneA.innovation_num;
                                        avgene.mutation_num = (geneA.mutation_num + geneB.mutation_num) / 2.0;

                                        //If one is disabled, the corresponding gene in the offspring
                                        //will likely be disabled

                                        if ((geneA.enable == false) || (geneB.enable == false))
                                            avgene.enable = false;

                                        chosengene = avgene;
                                        genecounter++;
                                        cross_innov = cellA;
                                    }
                                else if (genecounter > crosspoint)
                                    {
                                        chosengene = geneB;
                                        genecounter++;
                                    }
                            }

                        // -------------------------------------------------------------------------------
                        //                        innovA < innovB
                        // -------------------------------------------------------------------------------

                        else if (cellA != 0 && cellB == 0)
                            {
                                if (genecounter < crosspoint)
                                    {
                                        chosengene = geneA; //make geneA
                                        genecounter++;
                                    }
                                else if (genecounter == crosspoint)
                                    {
                                        chosengene = geneA;
                                        genecounter++;
                                        cross_innov = cellA;
                                    }
                                else if (genecounter > crosspoint)
                                    {
                                        if (cross_innov > last_innovB)
                                            {
						chosengene = geneA;
						genecounter++;
                                            }
                                        else
                                            {
						skip = true;
                                            }
                                    }
                            }

                        // -------------------------------------------------------------------------------
                        //                        innovA > innovB
                        // -------------------------------------------------------------------------------

                        else
                            {
                                if (cellA == 0 && cellB != 0)
                                    {
                                        if (genecounter < crosspoint)
                                            {
						skip = true; //skip geneB
                                            }
                                        else if (genecounter == crosspoint)
                                            {
						skip = true; //skip an illogic case
                                            }
                                        else if (genecounter > crosspoint)
                                            {
						if (cross_innov > last_innovB)
                                                    {
                                                        chosengene = geneA; //make geneA
                                                        genecounter++;
                                                    }
						else
                                                    {
                                                        chosengene = geneB; //make geneB : this is a pure case o single crossing
                                                        genecounter++;
                                                    }
                                            }

                                    }
                            }

                        itr_newgenes = newgenes.iterator();

                        while (itr_newgenes.hasNext())
                            {
                                _curgene2 = ((Gene) itr_newgenes.next());

                                if (_curgene2.lnk.in_node.node_id == chosengene.lnk.in_node.node_id
                                    && _curgene2.lnk.out_node.node_id == chosengene.lnk.out_node.node_id
                                    && _curgene2.lnk.is_recurrent == chosengene.lnk.is_recurrent)
                                    {
                                        skip = true;
                                        break;
                                    }

                                if (_curgene2.lnk.in_node.node_id == chosengene.lnk.out_node.node_id
                                    && _curgene2.lnk.out_node.node_id == chosengene.lnk.in_node.node_id
                                    && !_curgene2.lnk.is_recurrent
                                    && !chosengene.lnk.is_recurrent)
                                    {
                                        skip = true;
                                        break;
                                    }

                            } // and else for control of position in gennomeA/B

                        if (!skip)
                            {
                                //Now add the chosengene to the baby
                                //First, get the trait pointer
                                int first_traitnum = ((Trait) traits.firstElement()).trait_id;

                                if (chosengene.lnk.linktrait == null)
                                    traitnum = first_traitnum;
                                else
                                    traitnum = chosengene.lnk.linktrait.trait_id - first_traitnum;

                                //Next check for the nodes, add them if not in the baby Genome already

                                inode = chosengene.lnk.in_node;
                                onode = chosengene.lnk.out_node;

                                //
                                //Check for inode, onode in the newnodes list
                                //
                                boolean found;
                                if (inode.node_id < onode.node_id)
                                    {
                                        //
                                        // search the inode
                                        //
                                        found = false;
                                        for (int ix = 0; ix < newnodes.size(); ix++)
                                            {
						curnode = (NNode) newnodes.elementAt(ix);
						if (curnode.node_id == inode.node_id)
                                                    {
                                                        found = true;
                                                        break;
                                                    }
                                            }
                                        // if exist , point to exitsting version
                                        if (found)
                                            new_inode = curnode;
                                        // else create the inode
                                        else
                                            {
						if (inode.nodetrait == null)
                                                    nodetraitnum = 0;
						else
                                                    nodetraitnum = inode.nodetrait.trait_id - first_traitnum;
						newtrait = (Trait) newtraits.elementAt(nodetraitnum);
						new_inode = new NNode(inode, newtrait);
                                                //insert in newnodes list
						node_insert(newnodes, new_inode);
                                            }

                                        //
                                        // search the onode
                                        //
                                        found = false;
                                        for (int ix = 0; ix < newnodes.size(); ix++)
                                            {
						curnode = (NNode) newnodes.elementAt(ix);
						if (curnode.node_id == onode.node_id)
                                                    {
                                                        found = true;
                                                        break;
                                                    }
                                            }
                                        // if exist , point to exitsting version
                                        if (found)
                                            new_onode = curnode;
                                        // else create the onode
                                        else
                                            {
						if (onode.nodetrait == null)
                                                    nodetraitnum = 0;
						else
                                                    nodetraitnum = onode.nodetrait.trait_id - first_traitnum;
						newtrait = (Trait) newtraits.elementAt(nodetraitnum);
						new_onode = new NNode(onode, newtrait);
                                                //insert in newnodes list
						node_insert(newnodes, new_onode);
                                            }
                                    } // end block : inode.node_id < onode.node_id


                                else
                                    {
                                        //
                                        // search the onode
                                        //
                                        found = false;
                                        for (int ix = 0; ix < newnodes.size(); ix++)
                                            {
						curnode = (NNode) newnodes.elementAt(ix);
						if (curnode.node_id == onode.node_id)
                                                    {
                                                        found = true;
                                                        break;
                                                    }
                                            }
                                        // if exist , point to exitsting version
                                        if (found)
                                            new_onode = curnode;
                                        // else create the onode
                                        else
                                            {
						if (onode.nodetrait == null)
                                                    nodetraitnum = 0;
						else
                                                    nodetraitnum = onode.nodetrait.trait_id - first_traitnum;
						newtrait = (Trait) newtraits.elementAt(nodetraitnum);
						new_onode = new NNode(onode, newtrait);
                                                //insert in newnodes list
						node_insert(newnodes, new_onode);
                                            }
                                        //
                                        // search the inode
                                        //
                                        found = false;
                                        for (int ix = 0; ix < newnodes.size(); ix++)
                                            {
						curnode = (NNode) newnodes.elementAt(ix);
						if (curnode.node_id == inode.node_id)
                                                    {
                                                        found = true;
                                                        break;
                                                    }

                                            }

                                        // if exist , point to exitsting version
                                        if (found)
                                            new_inode = curnode;
                                        // else create the inode
                                        else
                                            {
						if (inode.nodetrait == null)
                                                    nodetraitnum = 0;
						else
                                                    nodetraitnum = inode.nodetrait.trait_id - first_traitnum;

						newtrait = (Trait) newtraits.elementAt(nodetraitnum);
						new_inode = new NNode(inode, newtrait);

                                                //insert in newnodes list
						node_insert(newnodes, new_inode);
                                            }
                                    }

                                //Add the Gene
                                newtrait = (Trait) newtraits.elementAt(traitnum);
                                newgene = new Gene(chosengene, newtrait, new_inode, new_onode);
                                newgenes.add(newgene);

                            } // end of block gene creation if !skip

                    }
                j++;

            }

        new_genome = new Genome(genomeid, newtraits, newnodes, newgenes);
        //
        // search the existence of output node
        // if no dump
        //

        return new_genome;
    }

    public void mutate_gene_reenable() {
        Iterator itr_gene;
        itr_gene = genes.iterator();
        Gene _gene = null;
        while (itr_gene.hasNext()) {
            _gene = ((Gene) itr_gene.next());
            if (!_gene.enable) {
                _gene.enable = true;
                break;
            }
        }
    }

    /**
     * This chooses a random gene, extracts the link from it,
     * and repoints the link to a random trait
     */
    public void mutate_link_trait(int times) {
        int traitnum;
        int genenum;
        int count;
        int loop;
        Gene _gene = null;
        Trait _trait = null;

        for (loop = 1; loop <= times; loop++) {

            //Choose a random traitnum
            traitnum = NeatRoutine.randint(0, (traits.size()) - 1);
            //Choose a random linknum
            genenum = NeatRoutine.randint(0, genes.size() - 1);
            //set the link to point to the new trait
            _gene = (Gene) genes.elementAt(genenum);
            _trait = (Trait) traits.elementAt(traitnum);
            _gene.lnk.linktrait = _trait;

            //TRACK INNOVATION- future use
            //(*thegene)->mutation_num+=randposneg()*randfloat()*linktrait_mut_sig;
        }
    }

    /**
     * This chooses a random node
     * and repoints the node to a random trait
     */
    public void mutate_node_trait(int times) {
        int traitnum;
        int nodenum;
        int count;
        int loop;
        NNode _node = null;
        Trait _trait = null;

        for (loop = 1; loop <= times; loop++) {
            //Choose a random traitnum
            traitnum = NeatRoutine.randint(0, (traits.size()) - 1);
            //Choose a random nodenum
            nodenum = NeatRoutine.randint(0, nodes.size() - 1);
            //set the link to point to the new trait
            _node = (NNode) nodes.elementAt(nodenum);
            _trait = (Trait) traits.elementAt(traitnum);
            _node.nodetrait = (Trait) traits.elementAt(traitnum);

            //TRACK INNOVATION! - possible future use
            //for any gene involving the mutated node, perturb that gene's
            //mutation number
            //for(thegene=genes.begin();thegene!=genes.end();++thegene) {
            //  if (((((*thegene)->lnk)->in_node)==(*thenode))
            //  ||
            //  ((((*thegene)->lnk)->out_node)==(*thenode)))
            //(*thegene)->mutation_num+=randposneg()*randfloat()*nodetrait_mut_sig;
            //}
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (24/01/2002 9.03.36)
     */
    public void mutate_random_trait() {
        int traitnum;

        //Choose a random traitnum
        traitnum = NeatRoutine.randint(0, (traits.size()) - 1);
        //Retrieve the trait and mutate it
        Trait _trait = (Trait) traits.elementAt(traitnum);
        _trait.mutate();

        //TRACK INNOVATION? (future possibility)
    }

    /**
     * Insert the method's description here.
     * Creation date: (24/01/2002 9.03.36)
     */
    //
    // Toggle genes from enable on to enable off or
    //   vice versa.  Do it times times.
    //
    public void mutate_toggle_enable(int times) {
        int genenum;
        int count;
        int genecount;
        Gene _gene = null;
        Gene _jgene = null;

        int len_gene = genes.size();
        boolean done = false;

        for (count = 1; count <= times; count++) {

            //Choose a random genenum
            genenum = NeatRoutine.randint(0, genes.size() - 1);
            //find the gene
            _gene = (Gene) genes.elementAt(genenum);
            //Toggle the enable on this gene

            if (_gene.enable) {
                //We need to make sure that another gene connects out of the in-node
                //Because if not a section of network will break off and become isolated
                done = false;
                for (int j = 0; j < len_gene; j++) {
                    _jgene = (Gene) genes.elementAt(j);
                    if ((_gene.lnk.in_node == _jgene.lnk.in_node)
                        &&  _jgene.enable
                        && (_jgene.innovation_num != _gene.innovation_num))
                        {
                            done = true;
                            break;
                        }
                }
                //Disable the gene if it's safe to do so
                if (done)
                    _gene.enable = false;

            }
            else
                _gene.enable = true;
        }
    }

    public void node_insert(Vector nlist, NNode n) {
        int j;
        int id = n.node_id;
        int sz = nlist.size();
        for (j = 0; j < sz; j++) {
            if (((NNode) nlist.elementAt(j)).node_id >= id)
                break;
        }
        nlist.insertElementAt(n, j);
    }

    public boolean mutate_add_link(Population pop, int tries) {
        boolean done = false;
        boolean do_recur = false;
        boolean loop_recur = false;
        boolean recur_flag = false;
        boolean found = false;
        boolean bypass = false;
        boolean recurflag = false;

        int first_nonsensor;
        int trycount = 0;

        int thresh = (nodes.size()) * (nodes.size());
        int count = 0;
        int nodenum1;
        int nodenum2;
        long lcount;
        int traitnum;
        double new_weight;

        NNode thenode1 = null;
        NNode thenode2 = null;
        Gene new_gene = null;
        Gene _gene = null;

        Iterator itr_gene = null;
        Iterator itr_node = null;
        Iterator itr_innovation = null;


        //Make attempts to find an unconnected pair
        trycount = 0;

        //Decide whether to make this recurrent
        if (NeatRoutine.randfloat() < Neat.p_recur_only_prob)
            do_recur = true;
        else
            do_recur = false;

        //Find the first non-sensor so that the to-node won't look at sensors as
        //possible destinations

        itr_node = nodes.iterator();
        first_nonsensor = 0;

        while (itr_node.hasNext())
            {
                thenode1 = ((NNode) itr_node.next());
                if (thenode1.type != NeatConstant.SENSOR)
                    break;
                first_nonsensor++;

            }
        found = false;
        while (trycount < tries)
            {
                //
                // recurrency case .........
                //

                if (do_recur)
                    {
			//
			// at this point :
			//50% of prob to decide a loop recurrency( node X to node X)
			// 50% a normal recurrency ( node X to node Y)
                        if (NeatRoutine.randfloat() > 0.5)
                            loop_recur = true;
                        else
                            loop_recur = false;

                        if (loop_recur)
                            {
                                nodenum1 = NeatRoutine.randint(first_nonsensor, nodes.size() - 1);
                                nodenum2 = nodenum1;
                            }
                        else
                            {
                                nodenum1 = NeatRoutine.randint(0, nodes.size() - 1);
                                nodenum2 = NeatRoutine.randint(first_nonsensor, nodes.size() - 1);
                            }

                    }
                //
                // no recurrency case .........
                //
                else
                    {
                        nodenum1 = NeatRoutine.randint(0, nodes.size() - 1);
                        nodenum2 = NeatRoutine.randint(first_nonsensor, nodes.size() - 1);

                    }

                //
                // now point to object's nodes
                //
                thenode1 = (NNode) nodes.elementAt(nodenum1);
                thenode2 = (NNode) nodes.elementAt(nodenum2);

                //
                // verify if the possible new gene already EXIST
                //
                bypass = false;
                for (int j = 0; j < genes.size(); j++)
                    {
                        _gene = (Gene) genes.elementAt(j);
                        if (thenode2.type == NeatConstant.SENSOR)
                            {
                                bypass = true;
                                break;
                            }
                        if (_gene.lnk.in_node == thenode1
                            && _gene.lnk.out_node == thenode2
                            && _gene.lnk.is_recurrent
                            && do_recur)
                            {
                                bypass = true;
                                break;
                            }

                        if (_gene.lnk.in_node == thenode1
                            && _gene.lnk.out_node == thenode2
                            && !_gene.lnk.is_recurrent
                            && !do_recur)
                            {
                                bypass = true;
                                break;
                            }

                    }

                if (!bypass)
                    {

                        phenotype.status = 0;
                        recurflag =
                            phenotype.has_a_path(thenode1.analogue, thenode2.analogue, 0, thresh);

                        if (phenotype.status == 8)
                            {
                                System.out.println(
                                                   "\n  network.mutate_add_link : LOOP DETECTED DURING A RECURRENCY CHECK");
                                return false;
                            }

                        if ((!recurflag && do_recur) || (recurflag && !do_recur))
                            trycount++;
                        else
                            {
                                trycount = tries;
                                found = true;
                            }

                    } // end block bypass

                //
                // if bypass is true, this gene is not good
                // and skip to next cycle
                //
                else
                    trycount++;

            } // end block trycount

        if (found)
            {

                //Check to see if this innovation already occured in the population
                itr_innovation = pop.innovations.iterator();

                done = false;
                while (!done)
                    {

                        if (!itr_innovation.hasNext())
                            {

                                //If the phenotype does not exist, exit on false,print error
                                //Note: This should never happen- if it does there is a bug
                                if (phenotype == null)
                                    {
                                        System.out.print("ERROR: Attempt to add link to genome with no phenotype");
                                        return false;
                                    }

                                //Choose a random trait
                                traitnum = NeatRoutine.randint(0, traits.size() - 1);

                                //Choose the new weight
                                //newweight=(gaussrand())/1.5;  //Could use a gaussian
                                new_weight = NeatRoutine.randposneg() * NeatRoutine.randfloat() * 10.0;

                                // read from population current innovation value

                                // read curr innovation with postincrement
                                double curr_innov = pop.getCurr_innov_num_and_increment();
                                //Create the new gene
                                new_gene = new Gene((Trait) traits.elementAt(traitnum), new_weight, thenode1, thenode2, do_recur, curr_innov, new_weight);
                                //Add the innovation
                                pop.innovations.add(new Innovation(thenode1.node_id, thenode2.node_id, curr_innov, new_weight, traitnum));
                                done = true;

                            }

                        //OTHERWISE, match the innovation in the innovs list
                        else
                            {
                                Innovation _innov = ((Innovation) itr_innovation.next());
                                if ((_innov.innovation_type == NeatConstant.NEWLINK)
                                    && (_innov.node_in_id == thenode1.node_id)
                                    && (_innov.node_out_id == thenode2.node_id)
                                    && (_innov.recur_flag == do_recur))
                                    {

                                        new_gene = new Gene((Trait) traits.elementAt(_innov.new_traitnum), _innov.new_weight, thenode1, thenode2, do_recur, _innov.innovation_num1, 0);
                                        done = true;
                                    }
                            }
                    }

                genes.add(new_gene);
                return true;
            }


        return false;
    }

    public boolean mutate_add_node(Population pop) {

        Iterator itr_gene;
        Gene _gene = null;

        Link thelink = null;
        double oldweight = 0;

        Gene newgene1 = null;
        Gene newgene2 = null;
        NNode in_node = null;
        NNode out_node = null;
        NNode new_node = null;
        Iterator itr_innovation;
        Trait traitptr = null;

        int j;
        int genenum = 0;
        int trycount = 0;

        boolean found = false;
        boolean bypass = false;
        boolean step1 = true;
        boolean step2 = false;
        double gene_innov1;
        double gene_innov2;

        if (genes.size() < 15)
            {

                step2 = false;
                for (j = 0; j < genes.size(); j++)
                    {
                        _gene = (Gene) genes.elementAt(j);
                        if (_gene.enable && (_gene.lnk.in_node.gen_node_label != NeatConstant.BIAS))
                            break;
                    }

                for (; j < genes.size(); j++)
                    {
                        _gene = (Gene) genes.elementAt(j);
                        if ((NeatRoutine.randfloat() >= 0.3)
                            && (_gene.lnk.in_node.gen_node_label != NeatConstant.BIAS))
                            {
                                step2 = true;
                                break;
                            }
                    }

                if ((step2) && (_gene.enable))
                    {
                        found = true;

                    }

            }
        else
            {
                while ((trycount < 20) && (!found))
                    {
			//Pure random splittingNeatRoutine.randint
                        genenum = NeatRoutine.randint(0, genes.size() - 1);
                        _gene = (Gene) genes.elementAt(genenum);
                        if (_gene.enable && (_gene.lnk.in_node.gen_node_label != NeatConstant.BIAS))
                            found = true;
                        ++trycount;

                    }
            }

        if (!found)
            return false;

        _gene.enable = false;

        //Extract the link
        thelink = _gene.lnk;
        //Extract the weight;
        oldweight = thelink.weight;
        //Get the old link's trait
        traitptr = thelink.linktrait;

        //Extract the nodes
        in_node = thelink.in_node;
        out_node = thelink.out_node;

        boolean done = false;
        itr_innovation = pop.innovations.iterator();

        while (!done)
            {
                //Check to see if this innovation already occured in the population
                if (!itr_innovation.hasNext())
                    {

			//The innovation is totally novel
			//Create the new Genes
			//Create the new NNode
			//By convention, it will point to the first trait
			// get the current node id with postincrement

                        int curnode_id = pop.getCur_node_id_and_increment();

			// pass this current nodeid to newnode and create the new node
                        new_node = new NNode(NeatConstant.NEURON, curnode_id, NeatConstant.HIDDEN);
                        new_node.nodetrait = (Trait) traits.firstElement();

			// get the current gene inovation with post increment
                        gene_innov1 = pop.getCurr_innov_num_and_increment();

			// create gene with the current gene inovation
                        newgene1 = new Gene(traitptr, 1.0, in_node, new_node, thelink.is_recurrent, gene_innov1, 0);

			// re-read the current innovation with increment
                        gene_innov2 = pop.getCurr_innov_num_and_increment();

			// create the second gene with this innovation incremented
                        newgene2 = new Gene(traitptr, oldweight, new_node, out_node, false, gene_innov2, 0);

                        pop.innovations.add(new Innovation(in_node.node_id, out_node.node_id, gene_innov1, gene_innov2, new_node.node_id, _gene.innovation_num));
                        done = true;
                    }
                // end for new innovation case
                else
                    {
                        Innovation _innov = ((Innovation) itr_innovation.next());

                        if ((_innov.innovation_type == NeatConstant.NEWNODE)
                            && (_innov.node_in_id == in_node.node_id)
                            && (_innov.node_out_id == out_node.node_id)
                            && (_innov.old_innov_num == _gene.innovation_num))
                            {
                                // Create the new Genes
                                // pass this current nodeid to newnode
                                new_node = new NNode(NeatConstant.NEURON, _innov.newnode_id, NeatConstant.HIDDEN);
                                new_node.nodetrait = (Trait) traits.firstElement();

                                newgene1 = new Gene(traitptr, 1.0,in_node, new_node, thelink.is_recurrent, _innov.innovation_num1, 0);
                                newgene2 = new Gene(traitptr, oldweight, new_node, out_node, false, _innov.innovation_num2, 0);
                                done = true;

                            }
                    }

            }

        //Now add the new NNode and new Genes to the Genome

        genes.add(newgene1);
        genes.add(newgene2);
        node_insert(nodes, new_node);

        return true;
    }

    /**
     * Creation of a new random genome with :
     * new_id   = numerical identification of genome
     *      i   = number of input nodes
     *      o   = number of output nodes
     *      n   = number of hidden nodes
     *   nmax   = number max of node
     *            this number must be >= (i + n + o)
     *      r   = the network can have a nodes recurrent ?
     * linkprob = probability of a link from nodes ( must be in interval  ]0,1[);
     */
    public Genome(int new_id, int i, int o, int n, int nmax, boolean r, double linkprob) {
        int totalnodes = 0;
        int matrixdim = 0;
        int maxnode = 0;
        int first_output = 0;
        int count = 0;
        int ccount = 0;
        int innov_number = 0;
        int col = 0;
        int row = 0;
        int fnd = 0;
        int pointer = 0;
        int gene_number = 0;

        boolean flag_recurrent = false;
        boolean create_gene = false;

        double new_weight = 0.0;

        Trait newtrait = null;
        NNode newnode = null;
        NNode in_node = null;
        NNode out_node = null;
        Gene newgene = null;

        notes = null;


        Iterator itr_node;


        //
        //    i i i n n n n n n n n n n n n n n n n . . . . . . . . o o o o
        //    |                                   |                 ^     |
        //    |<----------- maxnode ------------->|                 |     |
        //    |                                                     |     |
        //    |<-----------------------total nodes -----------------|---->|
        //                                                          |
        //                                                          |
        //     first output ----------------------------------------+
        //
        //
        totalnodes = i + o + nmax;

        traits = new Vector(Neat.p_num_trait_params, 0);
        nodes = new Vector(totalnodes, 0);
        genes = new Vector(totalnodes, 0);

        matrixdim = totalnodes * totalnodes;

        boolean[] cm = new boolean[matrixdim]; //Dimension the connection matrix
        boolean[] cmp;

        maxnode = i + n;
        first_output = totalnodes - o + 1;

        //Assign the id
        genome_id = new_id;

        //Create a dummy trait (this is for future expansion of the system)

        newtrait = new Trait(1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        traits.add(newtrait);

        //Build the input nodes
        for (count = 1; count <= i; count++)
            {
                if (count < i)
                    newnode = new NNode(NeatConstant.SENSOR, count, NeatConstant.INPUT);
                else
                    newnode = new NNode(NeatConstant.SENSOR, count, NeatConstant.BIAS);

                newnode.nodetrait = newtrait;
                //Add the node to the list of nodes
                nodes.add(newnode);
            }

        //Build the hidden nodes
        for (count = i + 1; count <= i + n; count++)
            {
                newnode = new NNode(NeatConstant.NEURON, count, NeatConstant.HIDDEN);
                newnode.nodetrait = newtrait;

                //Add the node to the list of nodes
                nodes.add(newnode);
            }

        //Build the output nodes
        for (count = first_output; count <= totalnodes; count++)
            {
                newnode = new NNode(NeatConstant.NEURON, count, NeatConstant.OUTPUT);
                newnode.nodetrait = newtrait;

                //Add the node to the list of nodes
                nodes.add(newnode);
            }

        boolean done = false;
        boolean rc1 = false;
        boolean rc2 = false;
        boolean rc3 = false;

        int min_required = i * o;
        double forced_probability = 0.5;
        int abort = 0;


        while (!done)
            {

                abort++;
                if (abort >= 20)
                    {
			//	   		if (abort == 10)
			//		    	System.out.print("\n ALERT  force new probability from 0.5 to 1 step .01");
                        linkprob = forced_probability;
                        forced_probability += .01;
                    }

                if (abort >= 700)
                    {
                        System.out.print("\n SEVERE ERROR in genome random creation costructor : genome has not created");
                        System.exit(12);
                    }


                //
                //creation of connections matrix
                //Step through the connection matrix, randomly assigning bits
                //

                cmp = cm;
                ccount = 0;
                for (count = 0; count < matrixdim; count++)
                    {
                        if (NeatRoutine.randfloat() < linkprob)
                            {
                                ccount++;
                                cmp[count] = true;
                            }
                        else
                            cmp[count] = false;
                    }



                //Connect the nodes

                innov_number = 0; //counter for labelling the innov_num  of genes
                gene_number = 0;  //counter gene created

                //Step through the connection matrix, creating connection genes

                cmp = cm;

                for (col = 1; col <= totalnodes; col++)
                    {
                        for (row = 1; row <= totalnodes; row++)
                            {

                                if ((cmp[innov_number] && (col > i))
                                    && ((col <= maxnode) || (col >= first_output))
                                    && ((row <= maxnode) || (row >= first_output)))
                                    {
                                        //If it isn't recurrent, create the connection no matter what


                                        create_gene = true;
                                        if (col > row)
                                            flag_recurrent = false;
                                        else
                                            {
						if (!r)
                                                    create_gene = false;
						flag_recurrent = true;
                                            }


                                        if (create_gene)
                                            {
						itr_node = nodes.iterator(); //Retrieve the in_node , out_node
						fnd = 0;
						while (itr_node.hasNext() && (fnd < 2))
                                                    {
                                                        NNode _node = ((NNode) itr_node.next());
                                                        if (_node.node_id == row)
                                                            {
                                                                fnd++;
                                                                in_node = _node;
                                                            }
                                                        if (_node.node_id == col)
                                                            {
                                                                fnd++;
                                                                out_node = _node;
                                                            }
                                                    }
                                                //Create the gene + link
						new_weight = NeatRoutine.randposneg() * NeatRoutine.randfloat();
						newgene = new Gene(newtrait, new_weight, in_node, out_node, flag_recurrent, innov_number, new_weight);
                                                //Add the gene to the genome
						genes.add(newgene);
                                            }
                                    } // end condition for a correct link in genome
                                innov_number++;
                            }
                    }

                rc1 = verify();

                //		System.out.print("\n      -> +rc1 = "+rc1);

                if (rc1)
                    {
                        Network net = this.genesis(genome_id);
                        rc2 =  net.is_minimal();

			//	 		System.out.print("\n         -> +rc2 = "+rc2);


                        if (rc2)
                            {
                                int lx = net.max_depth();
                                int dx = net.is_stabilized(lx);


                                //				System.out.print("\n        lx = " + lx);
                                //				System.out.print(", dx = " + dx);

                                if (((dx == lx) && (!r)) || ((lx > 0) && (r) && (dx == 0))   )
                                    done = true;


                            }
                        net.genotype = null;
                        this.phenotype = null;
                    }
                if (!done)
                    genes.clear();
                //			else
                //			   System.out.print("\n * CREATION Genome #"+genome_id+" okay");
            }
    }

    public Genome(int id, IOseq xFile) {

        StringTokenizer st;
        String curword;
        String xline;
        boolean done = false;
        // notes = null;

        genome_id = id;

        // System.out.print("\n genome id current is "+ genome_id);

        traits = new Vector(3, 0);
        nodes = new Vector(3, 0);
        genes = new Vector(3, 0);

        while (!done) {
            xline = xFile.IOseqRead();
            System.out.println("Genome - init line: " + xline);
            if (!xline.isEmpty()) {
                st = new StringTokenizer(xline);
                curword = st.nextToken();
                if (curword.equalsIgnoreCase("genomeend")) {
                    curword = st.nextToken();
                    if (Integer.parseInt(curword) != genome_id)
                        System.out.println(" *ERROR* id mismatch in genome");
                    done = true;
                } else if (curword.equals("/*")) {
                    curword = st.nextToken();
                    while (!curword.equals("*/"))
                        curword = st.nextToken();
                } else if (curword.equals("trait")) {
                    Trait newtrait;
                    newtrait = new Trait(xline);
                    traits.addElement(newtrait);
                } else if (curword.equals("node")) {
                    NNode newnode;
                    newnode = new NNode(xline, traits);
                    nodes.addElement(newnode);
                } else if (curword.equals("gene")) {
                    Gene newgene;
                    newgene = new Gene(xline, traits, nodes);
                    genes.addElement(newgene);
                }
            }
        }
    }

    /**
     * Write to file genome in native format (for re-read).
     */
    public void print_to_file(IOseq xFile) {
        String line = "genomestart  " + genome_id;
        xFile.IOseqWrite(line);

        Iterator itr_trait = traits.iterator();
        itr_trait = traits.iterator();

        while (itr_trait.hasNext()) {
            Trait _trait = ((Trait) itr_trait.next());
            _trait.print_to_file(xFile);
        }

        Iterator itr_node = nodes.iterator();
        itr_node = nodes.iterator();

        while (itr_node.hasNext()) {
            NNode _node = ((NNode) itr_node.next());
            _node.print_to_file(xFile);
        }

        Iterator itr_gene = genes.iterator();
        itr_gene = genes.iterator();

        while (itr_gene.hasNext()) {
            Gene _gene = ((Gene) itr_gene.next());
            _gene.print_to_file(xFile);
        }

        line = "genomeend " + genome_id;
        xFile.IOseqWrite(line);
    }

    public void View_mate_singlepoint(Genome g, int genomeid) {

        String mask4 = " 0000";
        DecimalFormat fmt4 = new DecimalFormat(mask4);

        Genome new_genome = null;
        int stopA = 0;
        int stopB = 0;
        int j;
        int j1;
        int j2;


        int size1 = genes.size();
        int size2 = g.genes.size();

        int crosspoint = 0;



        Vector genomeA;
        Vector genomeB;
        int genecounter = 0; //Ready to count to crosspoint

        if (size1 < size2)
            {
                stopA = size1;
                stopB = size2;
                genomeA = genes;
                genomeB = g.genes;
            }
        else
            {
                stopA = size2;
                stopB = size1;
                genomeA = g.genes;
                genomeB = genes;
            }

        double v3[][] = new double[size2*2][2];
        double vr[] = new double[size2*2];



        for (crosspoint=0; crosspoint < stopA; crosspoint++) {

            genecounter = 0;

            boolean doneA = false;
            boolean doneB = false;
            boolean done = false;
            double v1 = 0.0;
            double v2 = 0.0;
            double vmax = 0.0;
            j1=0;
            j2=0;
            j =0;

            double cross_innov =0;
            //
            // compute what is the hight innovation
            //

            double last_innovB = ((Gene)genomeB.elementAt(stopB-1)).innovation_num;

            while(!done) {

                doneA = false;
                doneB = false;

                if (j1 < stopA)
                    {
                        v1 = ((Gene)genomeA.elementAt(j1)).innovation_num;
                        doneA = true;
                    }
                if (j2 < stopB)
                    {
                        v2 = ((Gene)genomeB.elementAt(j2)).innovation_num;
                        doneB = true;
                    }

                if (doneA && doneB)
                    {
                        //
                        if (v1 < v2)
                            {
                                v3[j][0] = v1;
                                v3[j][1] = 0.0;
                                j1++;
                            }
                        else if (v1 == v2)
                            {
                                v3[j][0] = v1;
                                v3[j][1] = v1;
                                j1++;
                                j2++;
                            }
                        else
                            {
                                v3[j][0] = 0.0;
                                v3[j][1] = v2;
                                j2++;
                            }
                    }

                else
                    {
                        if (doneA && !doneB)
                            {
                                v3[j][0] = v1;
                                v3[j][1] = 0.0;
                                j1++;
                            }
                        else if (!doneA && doneB)
                            {
                                v3[j][0] = 0.0;
                                v3[j][1] = v2;
                                j2++;
                            }
                        else
                            done = true;
                    }



                if (!done)
                    {



                        // -------------------------------------------------------------------------------
                        //                        innovA = innovB
                        // -------------------------------------------------------------------------------


                        if (v3[j][0] == v3[j][1])
                            {
                                if (genecounter < crosspoint)
                                    {
                                        vr[j] = 1;
                                        genecounter++;
                                    }
                                else if (genecounter == crosspoint)
                                    {
                                        vr[j] = 3;
                                        genecounter++;
                                        cross_innov = v3[j][0];
                                    }
                                else if (genecounter > crosspoint)
                                    {
                                        vr[j] = 2;
                                        genecounter++;
                                    }
                            }

                        // -------------------------------------------------------------------------------
                        //                        innovA < innovB
                        // -------------------------------------------------------------------------------


                        else if (v3[j][0] !=0 &&  v3[j][1] == 0)
                            {
                                if (genecounter < crosspoint)
                                    {
                                        vr[j] = 1;                //  v3[j][0];
                                        genecounter++;
                                    }
                                else if (genecounter == crosspoint)
                                    {
                                        vr[j] = 1;					// v3[j][1])
                                        genecounter++;
                                        cross_innov = v3[j][0];
                                    }
                                else if (genecounter > crosspoint)
                                    {

                                        if (cross_innov > last_innovB)
                                            {
                                                vr[j] = 1;
                                                genecounter++;
                                            }
                                    }
                            }

                        // -------------------------------------------------------------------------------
                        //                        innovA > innovB
                        // -------------------------------------------------------------------------------


                        else if (v3[j][0] == 0 &&  v3[j][1] != 0)
                            {
                                if (genecounter < crosspoint)
                                    {
                                        vr[j] = 0;                //  skip v3[j][0];
                                    }
                                else if (genecounter == crosspoint)
                                    {
                                        vr[j] = 0;					// skip
                                    }

                                else if (genecounter > crosspoint)
                                    {
                                        if (cross_innov > last_innovB)
                                            {
                                                vr[j] = 1;					// v3[j][1];
                                                genecounter++;
                                            }
                                        else
                                            {
                                                vr[j] = 2;
                                                genecounter++;
                                            }
                                    }

                            }
                    }
                j++;

            }


            int len_max = --j;

            //
            // only for debug  : view innov's genomeA,B
            //
            System.out.print("\n\n CROSSING SINGLE at index " + crosspoint);
            System.out.print("\n -- index -- ");
            int column =0;
            for (j2=0; j2 < len_max; j2++)
                {
                    if (v3[j2][0] > 0.0)
                        System.out.print(fmt4.format((long)column++));
                    else
                        System.out.print("     ");
                }
            System.out.print("\n ----------- ");
            for (j2=0; j2 < len_max; j2++)
                System.out.print("-----");
            for (j1=0; j1< 2; j1++)
                {
                    System.out.print("\n Genome  ["+j1+"] ");
                    for (j2=0; j2 < len_max; j2++)
                        System.out.print(fmt4.format((long)v3[j2][j1]));
                }
            System.out.print("\n newgene [X] ");
            for (j2=0; j2 < len_max; j2++)
                {
                    if (vr[j2] == 1)
                        System.out.print("  AA ");
                    else if (vr[j2] == 2)
                        System.out.print("  BB ") ;
                    else if (vr[j2] == 3)
                        System.out.print("  XX ");
                    else if (vr[j2] == 4)
                        System.out.print("  MM ");
                    else if (vr[j2] == 0)
                        System.out.print("  -- ");
                }
            System.out.print("\n");
        }
    }

    public void print_to_filename(String xNameFile,String descr) {
        //
        // write to file genome in native format (for re-read)
        //
        IOseq xFile;

        xFile = new IOseq(xNameFile);
        xFile.IOseqOpenW(false);

        try {

            String riga = descr;
            xFile.IOseqWrite(riga);

            print_to_file(xFile);

        } catch (Throwable e) {
            System.err.println(e);
        }

        xFile.IOseqCloseW();
    }

}
