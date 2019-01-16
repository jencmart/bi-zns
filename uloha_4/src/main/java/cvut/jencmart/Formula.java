package cvut.jencmart;

import java.util.List;

public class Formula {
    private List<VarAndVal> variableList;

    public Formula(List<VarAndVal> terms) {
        this.variableList = terms;
    }

    public List<VarAndVal> getVariableList() {
        return variableList;
    }

    public boolean isEvaluable() {

        for (VarAndVal v: variableList
             ) {
            if(! v.getVariable().isResoluted()) {
                return  false;
            }
        }
        return true;
    }

    public boolean isFalse() {
        for (VarAndVal v: variableList) {
            if(! v.getVariable().isResoluted())
                continue;

            if(! v.getVariable().valueInsideFuzzySet(v.getValue()))
                return true;
        }

        return false;
    }

    public int countMissing() {
        int i = 0;
        for (VarAndVal v: variableList
        ) {
            if(! v.getVariable().isResoluted())
                i += 1;
        }
        return i;
    }
}
