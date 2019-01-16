package cvut.jencmart;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KnowledgeBase {
    private Map<Integer,Rule> usableRules;

    private Map<Integer,Rule> usedRules;
    private Map<Integer,Rule> notUsableRules;

    private Map<String,Variable> variableMap;
    private Map<String, Variable> terminalMap;

    public KnowledgeBase() {
        usableRules = new HashMap<>();
        usedRules = new HashMap<>();
        notUsableRules = new HashMap<>();

        variableMap = new HashMap<>();
        terminalMap = new HashMap<>();
        readRulesFromFile();
    }




    public List<Rule> getAllUsableRules() {
        List<Rule> rules = new ArrayList<>();

        for( Rule r : usableRules.values()) {
            rules.add(r);
        }

        return rules;
    }

    public List<Rule> getAllUsedRules() {
        List<Rule> rules = new ArrayList<>();

        for( Rule r : usedRules.values()) {
            rules.add(r);
        }

        return rules;
    }



    @Override
    public String toString() {
        String s = "";
        for( Rule r : usableRules.values()) {
            s += r.toString();
            s += '\n';
        }
        return s;
    }

    private void readRulesFromFile() {

        String filename = "resources/rules.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            Integer ruleId = 0;
            while ((line = br.readLine()) != null) {
                Rule r = parseRule(line.trim());
                if (r != null) {
                    r.setId(ruleId);
                    usableRules.put(ruleId, r);
                    ruleId += 1;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(KnowledgeBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Rule parseRule(String line) throws IOException {
        if(!line.startsWith("IF ") || !line.contains(" THEN ")){
            System.err.println("Line doesn't start with \"IF \" or doesn't contain \" THEN \" clauses (separated by spaces).");
            return null;
        }

        String ifPart = line.split("IF ")[1].split(" THEN")[0].trim();
        String thenPart = line.split("THEN ")[1].trim();


        Variable newVar;
        if(ifPart.startsWith("NAME_")) {
            ifPart = ifPart.split("NAME_")[1].trim();
            newVar = createVariable(ifPart);
            if(! terminalMap.containsKey(ifPart)) {
                terminalMap.put(ifPart, newVar);
            }
        }else {
            newVar = createVariable(ifPart);
        }

        ConjunctionFormula conjunctionFormula = parseFormula(thenPart);
        return  new Rule(conjunctionFormula, newVar);

    }

    // todo - still not sytax error proof
    private ConjunctionFormula parseFormula(String formula) {

        List<Term> terms = new ArrayList<>();

        String[] data = formula.split("&&");
        for ( String varName : data) {

            varName = varName.trim();
            Boolean negationUsed = false;
            if (varName.startsWith("NOT "))
                varName = varName.split("IF ")[1].trim();

            Variable newVariable = createVariable(varName);
            Term newTerm = new Term(newVariable, negationUsed);
            terms.add(newTerm);
        }

        return new ConjunctionFormula(terms);
    }

    // Sort of singleton type variables
    private Variable createVariable(String varName) {
        if (variableMap.containsKey(varName)) {
            return variableMap.get(varName);
        }
        else {
            Variable newVar = new Variable(varName);
            variableMap.put(varName, newVar);
            return newVar;
        }
    }

    public boolean inTerminals(Variable variable) {
        return terminalMap.containsKey(variable.getName());
    }

    public void setAsUsed(Rule toUse) {
        usableRules.remove(toUse.id);
        usedRules.put(toUse.id, toUse);

    }

    public void setAsNotUsable(List<Rule> impossibleList) {
        for (Rule r: impossibleList
             ) {
            usableRules.remove(r.id);
            notUsableRules.put(r.id, r);
        }
    }
}
