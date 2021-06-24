package info.bstancham.toothpick.jneat;

import info.bstancham.toothpick.TPBase;

/**
 * <p>Controller has 4 inputs and 4 outputs:</p>
 *
 * <p>INPUTS:</p>
 * <ul>
 * <li>(self/target) relative position, x</li>
 * <li>(self/target) relative position, y</li>
 * <li>(self) velocity, x</li>
 * <li>(self) velocity, y</li>
 * <li>(target) velocity, x</li>
 * <li>(target) velocity, y</li>
 * </ul>
 *
 * <p>OUTPUTS:</p>
 * <ul>
 * <li>thrust up</li>
 * <li>thrust down</li>
 * <li>thrust left</li>
 * <li>thrust right</li>
 * </ul>
 */
public class TPTrainingParamsSeekIn6Out4 extends TPTrainingParamsSeek {

    private static NNInput INPUT_TARGET_INERTIA_X
        = new NNInputInertia(getTargetID(), inputScalingInertia, NNInput.Dim.X);
    private static NNInput INPUT_TARGET_INERTIA_Y
        = new NNInputInertia(getTargetID(), inputScalingInertia, NNInput.Dim.Y);

    public TPTrainingParamsSeekIn6Out4(TPBase base) {
        super(base, "Seek (in=6, out=4)", "genome_in6_out4");
    }

    protected TPTrainingParamsSeekIn6Out4(TPBase base, String label, String genomeFilename) {
        super(base, label, genomeFilename);
    }

    @Override
    protected void addInputs(NeuralNetworkController nnc) {
        super.addInputs(nnc);
        nnc.addInput(INPUT_TARGET_INERTIA_X);
        nnc.addInput(INPUT_TARGET_INERTIA_Y);
    }

}
