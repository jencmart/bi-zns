package cvut.jencmart;

import java.util.List;

public class ConjunctionFormula {
    private List<Term> terms;

    public ConjunctionFormula(List<Term> terms) {
        this.terms = terms;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public boolean evaluable() {
        boolean evaluable = true;
        for (Term t: terms
             ) {
            if(t.isFalse())
                return true;
            if(! t.evaluable())
                evaluable = false;
        }
        return evaluable;
    }

    public boolean evaluate() {
        for (Term t: terms
        ) {
            if(t.isFalse())
                return false;
        }

        return true;
    }

    public int countMissing() {
        int i = 0;
        for (Term t: terms
        ) {
            if(! t.evaluable())
                i += 1;
        }
        return i;
    }
}
