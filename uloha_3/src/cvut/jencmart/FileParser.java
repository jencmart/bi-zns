package cvut.jencmart;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileParser {
    private Map<String,Variable> variableMap;
    private Map<String, Variable> terminalMap;
    private Map<String, Variable> startsMap;

    public FileParser() {
        variableMap = new HashMap<>();
        terminalMap = new HashMap<>();
        startsMap = new HashMap<>();
    }

    public KnowledgeBase generateKnowledgeBaseFromFile() {
        return readRulesFromFile();
    }

    private KnowledgeBase readRulesFromFile() {
        Map<Integer,Rule> usableRules = new HashMap<>();
        String filename = "resources/rules.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            Integer ruleId = 0;

            // read variables
            while (true) {
                line = br.readLine();

                if(line.trim().equals("")) //empty lines allowed
                    continue;

                if(line.trim().startsWith("//")) //comment
                    continue;

                if(line.trim().startsWith("{R"))
                    break;

                else if(line.trim().equals("{")) // probability section "mezivysledky"
                    readVariablesDefinitionSection(br, false, false);

                else if( line.trim().startsWith("{T")) // probability section "terminaly"
                    readVariablesDefinitionSection(br, true, false);

                else if(line.trim().startsWith("{S")) // probability section "zacatky"
                    readVariablesDefinitionSection(br, false, true);
            }

            // read rules
            while(true) {
                line = br.readLine();

                if(line.trim().equals("")) //empty lines allowed
                    continue;

                if(line.trim().startsWith("//")) //comment
                    continue;

                if(line.trim().startsWith("}")) // end section
                    break;

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


        return new KnowledgeBase(usableRules, variableMap, terminalMap);
    }



    // all variables added to variableMap
    // terminal variable added to terminalMap
    // all variables share it's scope (all neighbours)
    private void readVariablesDefinitionSection(BufferedReader br, boolean terminals, boolean starts) throws IOException {
        Set<Variable> probabilitySpace = new HashSet<>();

        while(true) { // read section
            String line = br.readLine();


            if(line.trim().equals("")) //empty lines allowed
                continue;

            if(line.trim().startsWith("//")) //comment
                continue;

            // end of the section
            if(line.trim().equals("}")) {

                if(starts) //add to starts map
                {
                    for (Variable v : probabilitySpace) // add to starts map
                        startsMap.put(v.getName(),v);
                    return;
                }

                if(terminals)
                    for (Variable v : probabilitySpace) // add to terminal map
                        terminalMap.put(v.getName(),v);

                for (Variable v : probabilitySpace)  // set all probabilites to know their mates from same space
                    v.setProbabilitySpace(probabilitySpace);

                return;
            }

            // not end - create new variable  - with default probability
            if(!starts) {
                String name = line.split("#")[0].trim();
                double probability = Double.valueOf(line.split("#")[1].trim());
                Variable newVar = new Variable(name,probability);

                probabilitySpace.add(newVar);
                variableMap.put(newVar.getName(), newVar);
            }

            // not end - create new variable  - no probability
            else {
                String name = line.trim();
                Variable newVar = new Variable(name);
                variableMap.put(newVar.getName(), newVar);
                probabilitySpace.add(newVar);
            }
        }
    }

    // IF a-0.1 && bla_bla-0.4 THEN c  // P (a|c) = 0.1 P(bla_bla|c) = 0.4
    // all variables should already exist
    public Rule parseRule(String line) throws IOException {

        // sytax error
        if(!line.startsWith("IF ") || !line.contains(" THEN ")){
            System.err.println("Line doesn't start with \"IF \" or doesn't contain \" THEN \" clauses (separated by spaces).");
            return null;
        }

        // parse IF and THEN
        // find then variable
        String thenPart = line.split("IF ")[1].split(" THEN")[0].trim(); // todo I know it is reversed; but  it is for better readibility of txt file
        String ifPart = line.split("THEN ")[1].trim();
        Variable thenVar = variableMap.get(thenPart); // variable allready created when reading variables

        if(thenVar == null){
            System.err.println("variable not found" + thenPart);
            return null;
        }

        Formula formula = parseFormula(ifPart, thenVar);
        return  new Rule(formula, thenVar);
    }

    private Formula parseFormula(String formula, Variable thenVar) { // for each var add probability to right side
        List<Variable> variableArrayList = new ArrayList<>();
        String[] data = formula.split("&&");

        for ( String varName_and_prob : data) {
            varName_and_prob = varName_and_prob.trim();
            String varName = varName_and_prob.split("#")[0].trim();
            double probability_with_right_side = Double.valueOf(varName_and_prob.split("#")[1].trim());

            variableMap.get(varName).addConditionProbability(thenVar.getName(), probability_with_right_side);
            variableArrayList.add(variableMap.get(varName));
        }

        return new Formula(variableArrayList);
    }
}
