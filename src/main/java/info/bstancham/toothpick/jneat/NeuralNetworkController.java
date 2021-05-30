package info.bstancham.toothpick.jneat;

import info.bschambers.toothpick.TPProgram;
import info.bschambers.toothpick.actor.*;
import info.bschambers.toothpick.geom.Line;
import java.util.ArrayList;
import java.util.List;
import jneat.neat.NNode;
import jneat.neat.Network;

public class NeuralNetworkController implements ActorBehaviour {

    private ActorControllerUDLR controller = null;
    private Network net = null;
    private List<NNInput> inputs = new ArrayList<>();
    private List<NNOutput> outputs = new ArrayList<>();
    private boolean printDebugInfo = false;

    public void setNetwork(Network n) { net = n; }

    public void setActorController(ActorControllerUDLR ac) { controller = ac; }

    public void addInput(NNInput in) { inputs.add(in); }

    public void addOutput(NNOutput out) { outputs.add(out); }

    public void setDebugMode(boolean val) { printDebugInfo = val; }

    @Override
    public void update(TPProgram prog, TPActor a) {

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
            // if (printInputOutput) {
            if (printDebugInfo) {
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

}
