package jneat.misc;

public class XnorOut implements DataTarget {

    @Override
    public int getNumUnit() { return 1; }

    @Override
    public double getTarget(int _plist[]) {

        int _index = _plist[0];
        int _col   = _plist[1];

        if ( _index < 0 )
            _index = - _index;

        if ( _index >= 4 )
            _index = _index % 4;

        double d[] = new double[4];

        d[0] = 1;
        d[1] = 0;
        d[2] = 0;
        d[3] = 1;

        return d[_index];
    }

}
