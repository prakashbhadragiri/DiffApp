package xyz.foobar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The object representing a diff.
 * Implement this class as you see fit. 
 *
 */
public class Diff<T extends Serializable> {
    private T val;
    private List<Differences> differencesList = new ArrayList<Differences>();

    public void setVal(T val) {
        this.val = val;
    }

    public T getVal() {
        return val;
    }

    public List<Differences> getDifferencesList() {
        return differencesList;
    }

    public void addDiff(Differences difference) {
        differencesList.add(difference);
    }

    @Override
    public String toString() {
        return "Diff{" +
                "val=" + val +
                ", differencesList=" + differencesList +
                '}';
    }
}
