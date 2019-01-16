package cvut.jencmart;

public class Term {
    private Variable variable;
    private boolean usedNegation;

    public Term(Variable variable, boolean usedNegation) {
        this.variable = variable;
        this.usedNegation = usedNegation;
    }

    public Variable getVariable() {
        return variable;
    }

    public boolean evaluable() {
        return !variable.getValue().equals(-1.0);
    }

    public boolean isFalse() {
        boolean val = variable.getValue().equals(0.0);

        if( usedNegation)
            return  ! val;
        else
            return val;
    }

    public boolean evaluate() {
        boolean val = variable.getValue().equals(1.0);

        if( usedNegation)
            return  ! val;
        else
            return val;
    }

}
