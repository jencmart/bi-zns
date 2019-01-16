package cvut.jencmart;

public class VarAndVal {
    private Variable variable;
    private String value;

    public VarAndVal(Variable variable, String value) {
        this.variable = variable;
        this.value = value;
    }

    public Variable getVariable() {
        return variable;
    }

    public String getValue() {
        return value;
    }

    // TODO - TO STRING

    @Override
    public String toString() {
        return "(" + variable.getName().replace('_', ' ') + " = " + value.replace('_', ' ') + ")" ;
    }

}
