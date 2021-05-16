package jneat.neat;

import java.util.*;
import jneat.common.*;

public class Network extends Neat {

    /**
     * Is a collection of object NNode can be mapped in a Vector container;
     * this collection represent a group of references to input nodes;
     */
    Vector inputs;

    /**
     * Is a collection of object NNode can be mapped in a Vector container;
     * this collection represent a group of references to output nodes;
     */
    Vector outputs;

    /**
     * Is a collection of object NNode can be mapped in a Vector container;
     * this collection represent a group of references to all nodes of this net;
     */
    Vector allnodes;

    /** is a reference to genotype can has  originate this fenotype */
    Genome genotype;

    /** Is a name of this network */
    String name;

    /** Numeric identification of this network */
    int net_id;

    /** Number of NNodes of this net */
    int numnodes;

    /** Number of Link  in this net */
    int numlinks;

    public Vector getInputs() {
        return inputs;
    }

    public void setInputs(Vector inputs) {
        this.inputs = inputs;
    }

    public Vector getOutputs() {
        return outputs;
    }

    public void setOutputs(Vector outputs) {
        this.outputs = outputs;
    }

    public Vector getAllnodes() {
        return allnodes;
    }

    public void setAllnodes(Vector allnodes) {
        this.allnodes = allnodes;
    }

    public Genome getGenotype() {
        return genotype;
    }

    public void setGenotype(Genome genotype) {
        this.genotype = genotype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNet_id() {
        return net_id;
    }

    public void setNet_id(int net_id) {
        this.net_id = net_id;
    }

    public int getNumnodes() {
        return numnodes;
    }

    public void setNumnodes(int numnodes) {
        this.numnodes = numnodes;
    }

    public int getNumlinks() {
        return numlinks;
    }

    public void setNumlinks(int numlinks) {
        this.numlinks = numlinks;
    }

    public Network(Vector in, Vector out, Vector all, int xnet_id) {
        inputs = in;
        outputs = out;
        allnodes = all;
        name = null;
        numnodes = -1;
        numlinks = -1;
        net_id = xnet_id;
        status = 0;

    }

    public boolean activate() {
        Iterator itr_node;
        Iterator itr_link;
        double tmpsum = 0.0;
        double add_amount = 0.0; //For adding to the activesum
        boolean onetime = false; //Make sure we at least activate once
        int abortcount = 0; //Used in case the output is somehow
        double tmpdouble = 0.0;

        while (outputsoff() || !onetime) {

            ++abortcount;
            if (abortcount >= 30) {
                System.out.print("\n *ERROR* Inputs disconnected from output!");

                // 05.06.2002 commented
                //			   genotype.op_view();
                //			   System.exit(4);
                return false;
            }

            // For each node, compute the sum of its incoming activation
            itr_node = allnodes.iterator();
            while (itr_node.hasNext()) {

                NNode _node = ((NNode) itr_node.next());

                if (_node.type != NeatConstant.SENSOR) {

                    _node.activesum = 0.0; // reset activation value
                    _node.active_flag = false; // flag node disabled

                    itr_link = _node.incoming.iterator();

                    while (itr_link.hasNext()) {
                        Link _link = ((Link) itr_link.next());

                        if (!_link.time_delay) {

                            add_amount = _link.weight * _link.in_node.get_active_out();
                            if (_link.in_node.active_flag || _link.in_node.type == NeatConstant.SENSOR)
                                _node.active_flag = true;

                            _node.activesum += add_amount;
                        } else {
                                add_amount = _link.weight * _link.in_node.get_active_out_td();
                                _node.activesum += add_amount;
                            }
                    } //End for over incoming links
                } //End if _node.type !=SENSOR
            } //End for over all nodes

            // Now activate all the non-sensor nodes off their incoming activation

            itr_node = allnodes.iterator();

            while (itr_node.hasNext()) {
                NNode _node = ((NNode) itr_node.next());

                if (_node.type != NeatConstant.SENSOR) {
                    //Only activate if some active input came in
                    if (_node.active_flag) {
                        _node.last_activation2 = _node.last_activation;
                        _node.last_activation = _node.activation;

                        if (_node.ftype == NeatConstant.SIGMOID)
                            _node.activation = NeatRoutine.fsigmoid(_node.activesum, 4.924273, 2.4621365);
                        _node.activation_count += 1.0;
                    }
                }
            }
            onetime = true;
        }

        return true;
    }

    public void flush() {
        Iterator itr_node;

        /*
        // old version
        itr_node = outputs.iterator();
        while (itr_node.hasNext())
        {
        NNode _node = ((NNode) itr_node.next());
        _node.flushback();
        }
        */
        // new version : the number of connection >> num of node defined
        // thus is good to reset all nodes without respect connection

        itr_node = allnodes.iterator();
        while (itr_node.hasNext()) {
                NNode _node = ((NNode) itr_node.next());
                _node.resetNNode();
            }
    }

    public void load_sensors(double[] sensvals) {
        int counter = 0;
        Iterator itr_node;
        itr_node = inputs.iterator();
        while (itr_node.hasNext()) {
            NNode _node = ((NNode) itr_node.next());
            if (_node.type == NeatConstant.SENSOR)
                _node.sensor_load(sensvals[counter++]);
        }
    }

    /**
     * Find the maximum number of neurons between an ouput and an input
     */
    public int max_depth() {

        NNode _node;
        Iterator itr_node;

        int cur_depth = 0;
        int max = 0;

        for (int j = 0; j < allnodes.size(); j++) {
            _node = (NNode) allnodes.elementAt(j);
            _node.inner_level = 0;
            _node.is_traversed = false;
        }

        itr_node = outputs.iterator();
        while (itr_node.hasNext()) {
                _node = ((NNode) itr_node.next());
                cur_depth = _node.depth(0, this, max);
                if (cur_depth > max)
                    max = cur_depth;
            }

        return max;
    }

    public boolean outputsoff() {
        Iterator itr_node = outputs.iterator();
        while (itr_node.hasNext()) {
                NNode _node = ((NNode) itr_node.next());
                if (_node.activation_count == 0)
                    return true;
            }
        return false;
    }

    public void viewAllNodes(String s) {
        System.out.println(s);
        System.out.print("\n\t - List of all nodes -");
        Iterator itr_node = allnodes.iterator();
        while (itr_node.hasNext()) {
                NNode _node = ((NNode) itr_node.next());
                if (_node.active_flag)
                    _node.op_view();
            }
        System.out.print("\n\t - end list of all nodes -");
    }

    /**
     * are user for working scope ; at this moment is utilized for returning code of a
     * search if recurrency if == 8 , the net has a loop ;
     */
    int status;
    public int count_motor() {
        int counter = 0;
        Iterator itr_node = outputs.iterator();
        // count # motor
        while (itr_node.hasNext()) {
            NNode _node = ((NNode) itr_node.next());
            counter++;
        }
        return counter;
    }

    public int count_sensor() {
        int counter = 0;
        Iterator itr_node = inputs.iterator();
        while (itr_node.hasNext()) {
            NNode _node = ((NNode) itr_node.next());
            if (_node.type == NeatConstant.SENSOR)
                counter++;
        }
        return counter;
    }

    public boolean has_a_path(NNode potin, NNode potout, int level, int threshold) {
        NNode _node = null;

        // reset all link to state no traversed
        for (int j = 0; j < allnodes.size(); j++) {
            _node = (NNode) allnodes.elementAt(j);
            _node.is_traversed = false;
        }

        // call the control if has a link intra node potin , potout
        return is_recur(potin, potout, level, threshold);
    }

    /**
      * This module control if has at leat one link from out and all sensor It flow in all
      * link and if at end are one sensor not 'marked' , return false
      */
    public boolean is_minimal() {

        boolean rc = false;
        boolean ret_code = true;
        NNode _node	= null;
        Iterator itr_node;

        // reset all pending situation
        itr_node = allnodes.iterator();
        while (itr_node.hasNext()) {
            _node = ((NNode) itr_node.next());
            _node.is_traversed = false;
        }

        // attempted to excite all sensor
        itr_node = outputs.iterator();
        while (itr_node.hasNext()) {
            _node = ((NNode) itr_node.next());
            rc = _node.mark(0, this);
            // the false conditions is for a net with loop
            // or an output without connection direct or indirect
            //
            if (rc == false)
                return false;
        }

        // okay the virtual signal is flowed,
        // now control if all sensor of fenotype are touched :

        itr_node = inputs.iterator();
        while (itr_node.hasNext()) {
            _node = ((NNode) itr_node.next());
            if (!_node.is_traversed)
                ret_code = false;
        }

        return ret_code;
    }

    public boolean is_recur(NNode potin_node, NNode potout_node, int level, int thresh) {

        Iterator itr_link = null;
        level++;

        if (level > thresh) {
            status = 8;
            return false;
        }

        if (potin_node == potout_node)
            return true;

        else {
            itr_link = potin_node.incoming.iterator();
            while (itr_link.hasNext()) {
                Link _link = ((Link) itr_link.next());
                if (!_link.is_recurrent) {

                    if (!_link.in_node.is_traversed) {
                        _link.in_node.is_traversed = true;
                        if (is_recur(_link.in_node, potout_node, level, thresh))
                            return true;
                    }
                }
            }
            potin_node.is_traversed=true;
            return false;
        }
    }

    /**
      * starting from sensor , send a signal forward the net after all nodes are active ,
      * control if last activation is == current activation in output node if activation
      * of output nodes remain stable for 'period' interval , return the difference from
      * total cycle and time passed from fist level stable.
      */
    public int is_stabilized(int period) {
        Iterator itr_node;
        Iterator itr_link;
        double tmpsum = 0.0;
        double add_amount = 0.0; //For adding to the activesum
        boolean onetime = false; //Make sure we at least activate once
        int abortcount = 0; //Used in case the output is somehow
        double tmpdouble = 0.0;

        NNode _node = null;

        if (period == 0)
            period = 30;

        //first step : activation of sensor nodes
        //
        itr_node = inputs.iterator();
        while (itr_node.hasNext()) {
            _node = ((NNode) itr_node.next());
            if (_node.type == NeatConstant.SENSOR) {
                _node.last_activation2 = _node.last_activation;
                _node.last_activation = _node.activation;
                _node.activation_count++;
                _node.activation = 1;
            }
        }

        // activation of net
        //
        boolean done = false;
        int time_passed = 0;
        int  counter_stable = 0;
        int level = 0;
        int limit = period + 90;

        while (!done) {
            // if loop exit with signal of error
            if (time_passed >= limit)  {
                //		 	System.out.print("\n *ALERT* this net is not STABLE after "+limit);
                return 0;
            }

            // For each node, compute the sum of its incoming activation
            itr_node = allnodes.iterator();
            while (itr_node.hasNext()) {

                _node = ((NNode) itr_node.next());
                if (_node.type != NeatConstant.SENSOR) {

                    _node.activesum = 0.0;
                    _node.active_flag = false; // flag node disabled

                    itr_link = _node.incoming.iterator();

                    while (itr_link.hasNext()) {
                        Link _link = ((Link) itr_link.next());
                        if (!_link.time_delay) {
                            add_amount = _link.in_node.get_active_out();
                            if (_link.in_node.active_flag || _link.in_node.type == NeatConstant.SENSOR) {
                                _node.active_flag = true;
                            }
                            _node.activesum += add_amount;
                        } else {
                            add_amount = _link.in_node.get_active_out_td();
                            _node.activesum += add_amount;
                        }
                    } //End for over incoming links
                } //End if _node.type !=SENSOR
            } //End for over all nodes

            // Now activate all the non-sensor nodes off their incoming activation
            itr_node = allnodes.iterator();
            while (itr_node.hasNext()) {
                _node = ((NNode) itr_node.next());
                if (_node.type != NeatConstant.SENSOR) {
                    //Only activate if some active input came in
                    if (_node.active_flag) {
                        _node.last_activation2 = _node.last_activation;
                        _node.last_activation = _node.activation;
                        _node.activation = _node.activesum;
                        _node.activation_count += 1.0;
                    }
                }
            }

            onetime = true;

            if (!outputsoff()) {
                //
                // verify is has a changement in any output(j-esimo)
                //
                boolean has_changed = false;
                itr_node = outputs.iterator();
                while (itr_node.hasNext()) {
                    _node = ((NNode) itr_node.next());
                    if (_node.last_activation != _node.activation) {
                        has_changed = true;
                        break;
                    }
                }

                if (!has_changed) {
                    // if has not changement, increment the counter
                    // of no change....
                    counter_stable +=1;
                    // if counter no change  >= 'period' parameter (virtual depth) , the net is relaxed
                    // and stable , thus can be return the 'delta' passed
                    if (counter_stable >= period) {
                        done = true;
                        level = time_passed;
                        break;
                    }
                }

                // must be re-start until correct exit or abort for loop
                //
                else
                    counter_stable  = 0;
            }
            time_passed++;
        }

        // return delta = total time passed (real) - period (depth virtual)
        return (level-period+1);
    }

}
