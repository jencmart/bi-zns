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


    public FileParser() {
        variableMap = new HashMap<>();
        terminalMap = new HashMap<>();
    }

    public KnowledgeBase generateKnowledgeBaseFromFile() {
        Map<Integer,Rule> usableRules = new HashMap<>();
        String filename = "resources/rules-fuzzy.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            // read variables
            while (true) {
                line = br.readLine();

                if(line.trim().equals("") || line.trim().startsWith("//") ) //empty lines allowed
                    continue;

                if(line.trim().startsWith("{R"))
                    break;


                // nacti terminaly
                if( line.trim().startsWith("{T")) // probability section "terminaly"
                {
                    while (true) {
                        line = br.readLine();
                        if (line.trim().equals("") || line.trim().startsWith("//")) //empty lines allowed
                            continue;
                        if (line.trim().startsWith("}"))
                            break;
                        Variable newVar =  new Variable(line.trim() );

                        variableMap.put(line.trim(), newVar);
                        terminalMap.put(line.trim(), newVar);                                        // vlozime terminal var do terminal
                    }
                }
                if (line.trim().startsWith("}"))
                    continue;


                                                                                                                        // NACTI FUUZY MNOZINY
                // rozdel radku podle carek
                String[] elemList = line.trim().split(",");
                // pokud promenna neexistuje - pridej ji
                if( ! variableMap.containsKey( elemList[0].trim() ))
                    variableMap.put(elemList[0], new Variable(elemList[0].trim()));
                // pridej ji danou fuzzy mnozinu
                variableMap.get(elemList[0].trim()).addFuzySet(elemList[1].trim(), Integer.parseInt(elemList[2].trim()),
                        Integer.parseInt(elemList[3].trim()), Integer.parseInt(elemList[4].trim()), Integer.parseInt(elemList[5].trim()) );

            }

            // read rules with ParseRule
            Integer ruleId = 0;
            while(true) {
                line = br.readLine();

                if(line.trim().equals("") || line.trim().startsWith("//")) //empty lines allowed
                    continue;

                if(line.trim().startsWith("}")) // end section
                    break;

                Rule r = parseRule(line.trim()); // todo
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

    // IF a-0.1 && bla_bla-0.4 THEN c  // P (a|c) = 0.1 P(bla_bla|c) = 0.4
    // IF c THEN a=X && bla_bla=Y

    // all variables should already exist
    public Rule parseRule(String line) throws IOException {

        // sytax error
        if(!line.startsWith("IF ") || !line.contains(" THEN ")){
            System.err.println("Line doesn't start with \"IF \" or doesn't contain \" THEN \" clauses (separated by spaces).");
            return null;
        }

        String ifPart = line.split("THEN ")[1].trim();
        String thenPart = line.split("IF ")[1].split(" THEN")[0].trim(); // todo I know it is reversed; but  it is for better readibility of txt file
        Variable thenVar = variableMap.get(thenPart); // variable allready created when reading variables

        if(thenVar == null){
            System.err.println("variable not found" + thenPart);
            return null;
        }

        Formula formula = parseFormula(ifPart, thenVar);
        return  new Rule(formula, thenVar);
    }


    //TODO
    // find then variable
    // list list variables a jejich kyzenych zaveru
    private Formula parseFormula(String formula, Variable thenVar) { // for each var add probability to right side

        List<VarAndVal> variableAndValArrayList = new ArrayList<>();

        String[] data = formula.split("&&");

        for ( String varName_and_Value : data) {
            varName_and_Value = varName_and_Value.trim();

            String varName = varName_and_Value.split("=")[0].trim();
            String var_Value = varName_and_Value.split("=")[1].trim();

            VarAndVal newVarAndVal = new VarAndVal(variableMap.get(varName), var_Value);

            variableAndValArrayList.add(newVarAndVal);
        }

        return new Formula(variableAndValArrayList);
    }
}
