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

    public KnowledgeBase(Map<Integer,Rule> usableRules, Map<String,Variable> variableMap, Map<String,Variable> terminalMap) {
        this.usableRules = usableRules ;
        this.variableMap = variableMap ;
        this.terminalMap = terminalMap ;

        usedRules = new HashMap<>();
        notUsableRules = new HashMap<>();
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

    public boolean inTerminals(Variable variable) {
        return terminalMap.containsKey(variable.getName());
    }

    public void setAsUsed(Rule toUse) {
        usableRules.remove(toUse.id);
        usedRules.put(toUse.id, toUse);
    }

    public void impossibleToResolute(List<Rule> impossibleList) {
        for (Rule r: impossibleList
             ) {
            usableRules.remove(r.id);
            notUsableRules.put(r.id, r);
        }
    }

    public List<Variable> getAllTerminals() {
        List<Variable> terminals = new ArrayList<>();
        for(Variable v : terminalMap.values()){
            terminals.add(v);
        }
        return terminals;
    }
}
