package jneat.misc;

/**
 * Two inputs - each is a binary value - may be 1 or 0.
 *
 * This class maps all of the 4 possible combinations of inputs.
 */
public class BinaryInput implements DataInput {

    @Override
    public int getNumSamples() { return 4; }

    @Override
    public int getNumUnit() { return 2; }

    /**
     * Gets a double value from the table of possible inputs:
     *
     * 0, 0
     * 1, 0
     * 0, 1
     * 1, 1
     *
     * @param _plist An integer array of two elements, representing the row and column of
     * the table of inputs.
     */
    @Override
    public double getInput(int _plist[]) {

        int _index = _plist[0];
        int _col   = _plist[1];

        if (_index < 0)
            _index = - _index;

        if (_index >= 4)
            _index = _index % 4;

        double d[][] = new double[4][2];

        // 0, 0
        d[0][0] = 0;
        d[0][1] = 0;

        // 1, 0
        d[1][0] = 1;
        d[1][1] = 0;

        // 0, 1
        d[2][0] = 0;
        d[2][1] = 1;

        // 1, 1
        d[3][0] = 1;
        d[3][1] = 1;

        return d[_index][_col];
    }

}
