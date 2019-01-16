package cvut.jencmart;

import java.util.List;

public class Formula {
    private List<Variable> variableList;

    public Formula(List<Variable> terms) {
        this.variableList = terms;
    }

    public List<Variable> getVariableList() {
        return variableList;
    }

    public boolean isEvaluable() {
        boolean evaluable = true;
        for (Variable v: variableList
             ) {
            if(v.isFalse())
                return true;
            if(! v.isResoluted())
                evaluable = false;
        }

        return evaluable;
    }

    public boolean isFalse() {
        for (Variable v: variableList) {
            if(v.isFalse())
                return true;
        }

        return false;
    }

    public int countMissing() {
        int i = 0;
        for (Variable v: variableList
        ) {
            if(! v.isResoluted())
                i += 1;
        }
        return i;
    }

    public boolean haveAllIntermediatesSolved() { // return true if all of the variables which are also on the right side are allready Resoluted
        for (Variable v: variableList) {
            if( ! v.isLeaf() && !v.isResoluted())
                return false;
        }

        return true;
    }
}
