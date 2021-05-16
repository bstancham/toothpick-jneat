package info.bstancham.toothpick.ml;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.*;
import info.bschambers.toothpick.geom.Line;
import java.util.ArrayList;
import java.util.List;
import jneat.neat.NNode;
import jneat.neat.Network;

public class NeuralNetworkController implements ActorBehaviour {

    // private ActorControllerUDLR controller = new ActorControllerUDLR();
    private ActorControllerUDLR controller = null;
    private Network net = null;
    private List<NNInput> inputs = new ArrayList<>();
    private List<NNOutput> outputs = new ArrayList<>();

    private boolean printInputOutput = false;
    
    // private int numInputs = 14;
    // private int numOutputs = 4;

    // public NeuralNetworkController(Network inputNet, TPActor target) {
    //     net = inputNet;
    //     this.target = target;

    //     // check input network
    //     // must have correct number of inputs and outputs

    //     int n = net.getInputs().size();
    //     if (n != numInputs)
    //         System.out.println("NeuralNetworkController ERROR: network has " + n
    //                            + " inputs - should have " + numInputs + "!");

    //     n = net.getOutputs().size();
    //     if (n != numOutputs)
    //         System.out.println("NeuralNetworkController ERROR: network has " + n
    //                            + " outputs - should have " + numOutputs + "!");
    // }

    // public NeuralNetworkController copyExceptNetwork() {
    //     NeuralNetworkController nnc = new NeuralNetworkController();
    //     nnc.controller = controller.copy();
    //     nnc.inputs = inputs;
    //     nnc.outputs = outputs;
    //     return nnc;
    // }

    public void setNetwork(Network n) { net = n; }

    public void setActorController(ActorControllerUDLR ac) { controller = ac; }

    public void addInput(NNInput in) { inputs.add(in); }

    public void addOutput(NNOutput out) { outputs.add(out); }

    @Override
    public void update(TPProgram prog, TPActor a) {
    // // public void update(TPProgram prog) {
    // public void update() {

        if (net == null) {
            System.out.println("net == NULL");
        } else if (outputs.size() != net.getOutputs().size()) {
            System.out.println("NeuralNetworkController: num outputs don't match - "
                               + "outputs = " + outputs.size()
                               + ", net outputs = " + net.getOutputs().size());
        } else {

            int net_depth = net.max_depth(); // The max depth of the network to be activated
            boolean success = false;

            // LOAD INPUTS
            double[] in = new double[inputs.size() + 1];
            for (int i = 0; i < inputs.size(); i++)
                in[i] = inputs.get(i).getInput(prog);
            // setting bias
            in[inputs.size()] = 1.0;
            net.load_sensors(in);

            // PROCESS NETWORK
            // first activation from sensor to next layer....
            success = net.activate();
            // next activation while last level is reached !
            // use depth to ensure relaxation
            for (int relax = 0; relax <= net_depth; relax++)
                success = net.activate();

            // GET OUTPUTS, then clear net
            double[] out = new double[outputs.size()];
            for(int i = 0; i < outputs.size(); i++)
                out[i] = ((NNode) net.getOutputs().elementAt(i)).getActivation();
            // clear net
            net.flush();

            // show inputs and outputs
            if (printInputOutput) {
                StringBuilder sb = new StringBuilder();
                sb.append("INPUTS: ");
                for (int i = 0; i < in.length; i++)
                    sb.append(" " + i + ":" + in[i]);
                sb.append(" ----> OUTPUTS: ");
                for (int i = 0; i < out.length; i++)
                    sb.append(" " + i + ":" + out[i]);
                System.out.println(sb);
            }

            // ACT ON OUTPUTS
            if (success) {
            // if (true) {
                double threshold = 0.5;
                for (int i = 0; i < outputs.size(); i++)
                    // if (out[i] > threshold)
                    outputs.get(i).activate(out[i], threshold);

                TPActor horiz = MLUtil.getHorizActor(prog);
                if (horiz != null)
                    controller.update(horiz);
                
            }
        }

    }



        // Line lnA = getLine(a);
        // Line lnB = getLine(target);

        // if (lnA != null && lnB != null) {

            //     inputs[0] = lnA.start.x;
            //     inputs[1] = lnA.start.y;
            //     inputs[2] = lnA.end.x;
            //     inputs[3] = lnA.end.y;
            //     inputs[4] = a.xInertia;
            //     inputs[5] = a.xInertia;
            //     inputs[6] = a.angleInertia;

            //     // relative position of target-line
            //     inputs[7] = lnB.start.x - lnA.start.x;
            //     inputs[8] = lnB.start.y - lnA.start.y;
            //     inputs[9] = lnB.end.x - lnA.end.x;
            //     inputs[10] = lnB.end.y - lnA.end.y;

            //     inputs[11] = target.xInertia;
            //     inputs[12] = target.xInertia;
            //     inputs[13] = target.angleInertia;

            //     // setting bias
            //     inputs[14] = 1.0;

            //     net.load_sensors(inputs);

            //     ////
            //     //// PROCESS NETWORK
            //     ////

            //     // first activation from sensor to next layer....
            //     success = net.activate();

            //     // next activation while last level is reached !
            //     // use depth to ensure relaxation
            //     for (int relax = 0; relax <= net_depth; relax++)
            //         success = net.activate();

            //     ////
            //     //// GET OUTPUTS
            //     ////

            //     double[] outputs = new double[numOutputs];
            //     for(int i = 0; i < numOutputs; i++)
            //         outputs[i] = ((NNode) net.getOutputs().elementAt(i)).getActivation();

            //     // clear net
            //     net.flush();

            //     // show inputs and outputs
            //     StringBuilder sb = new StringBuilder();
            //     sb.append("INPUTS: ");
            //     for (int i = 0; i < inputs.length; i++)
            //         sb.append(i + ":" + inputs[i]);
            //     sb.append(" ----> OUTPUTS: ");
            //     for (int i = 0; i < outputs.length; i++)
            //         sb.append(i + ":" + outputs[i]);
            //     System.out.println(sb);

            //     ////
            //     //// ACT ON OUTPUTS
            //     ////

            //     if (success) {

            //         double threshold = 0.5;

            //         if (outputs[0] > threshold) {

            //         }

            //     }
            // }

        //     controller.update(a);
        
        // }

}
