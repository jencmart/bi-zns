package cvut.jencmart;

import java.util.Iterator;

public class Rule {
    Formula formula;
    Variable variable;
    Integer id;

    public Rule(Formula formula, Variable variable) {
        this.formula = formula;
        this.variable = variable;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        String s = "IF ";
        Iterator<VarAndVal> iterator = formula.getVariableList().iterator();
        while (iterator.hasNext()) {
            VarAndVal v = iterator.next();
            s += v.toString();
            if (iterator.hasNext()) {
                s += " && ";
            }
        }
        s += " THEN ";
        s += variable.getName();
        return s;
    }
}
