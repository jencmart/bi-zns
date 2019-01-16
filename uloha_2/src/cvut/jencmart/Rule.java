package cvut.jencmart;

import java.util.Iterator;

public class Rule {
    ConjunctionFormula conjunctionFormula;
    Variable variable;
    Integer id;

    public Rule(ConjunctionFormula conjunctionFormula, Variable variable) {
        this.conjunctionFormula = conjunctionFormula;
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
        Iterator<Term> iterator = conjunctionFormula.getTerms().iterator();
        while (iterator.hasNext()) {
            Term t = iterator.next();
            s += t.getVariable().getName();
            s += '[';
            if (t.evaluable()) {
                s += "TRUE";
            } else {
                s += "???";
            }

            s += ']';
            if (iterator.hasNext()) {
                s += " && ";
            }
        }
        s += " THEN ";
        s += variable.getName();
        return s;
    }
}
