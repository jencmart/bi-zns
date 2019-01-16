package cvut.jencmart;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserInterface {
    private final Scanner reader = new Scanner(System.in);




    public  boolean askMore(String question) {
        System.out.println(question + "? [y/n]" + System.lineSeparator() +
                "Answer: ");

        String userInput = reader.next();

        if (userInput != null && userInput.length() > 0 &&
                (userInput.toLowerCase().matches("yes|y|no|n") )) {

            boolean answer;
            if (userInput.toLowerCase().matches("yes|y"))
                answer = true;
             else
                answer = false;

            return answer;
        }
        else {
            System.err.println("Answer in wrong format... try again:");
            return askMore(question);
        }
    }


    public Double askUser(String question) throws ActionException {
        System.out.println("Is it true, that character " + question + "? [y/n] [yes/no] [idk] (0.0 ... 1.0)" + System.lineSeparator() +
                "Answer: ");

        String userInput = reader.next();

        if (userInput != null && userInput.length() > 0 &&
                (userInput.toLowerCase().matches("yes|y|no|n|idk|stop|why|how") ||
                        userInput.toLowerCase().matches("[-+]?[0-9]*\\.?[0-9]+"))) {

            Double answer;
            if (userInput.toLowerCase().matches("yes|y")) {
                answer = 1.0;
            } else if (userInput.toLowerCase().matches("no|n")) {
                answer = 0.0;
            } else if (userInput.toLowerCase().matches("idk")) {
                answer = 0.5;
            } else if (userInput.toLowerCase().matches("stop")) {
                throw new ActionException("stop");
            } else if (userInput.toLowerCase().matches("why")) {
                throw new ActionException("why");
            } else if (userInput.toLowerCase().matches("how")) {
                throw new ActionException("how");
            } else {
                answer = Double.valueOf(userInput);
            }
            return answer;
        }
        else {
            System.err.println("Answer in wrong format... try again:");
            return askUser(question);
        }
    }

    public void explainWhy(Rule rule, Variable t) {
        rule.toString();
        System.out.println("WHY? >>> Because I'm trying to proove:\t" + rule.toString()); // todo - print why question --> because of this rule.
    }

    private void explainRecursively(Rule current,int tabSpaces, List<Rule> allUsedRules, Map<String, Double> userAnswers) {

        for(int i = 0 ; i < tabSpaces; i++)
            System.out.print("\t");

        System.out.println(current.variable.getName() + " BECAUSE: " + current.toString());

        for(Variable v : current.formula.getVariableList()) {
            String name = v.getName();

            if(userAnswers.containsKey(name)) {
                for(int i = 0 ; i < tabSpaces+1; i++)
                    System.out.print("\t");

                System.out.println(name + " BECAUSE: " + "Is it true, that character " + name + "? Answer: " + userAnswers.get(name) );
            } else {

                for( Rule r : allUsedRules) {
                    if(r.variable.getName().equals(name)) {
                        explainRecursively(r, tabSpaces + 1, allUsedRules, userAnswers);
                        break;
                    }
                }
            }
        }
    }

    public void explainHow(Variable result, List<Rule> allUsedRules, Map<String, Double> userAnswers) {

        System.out.println(">>>> HOW? >>>>>"); // todo --> how we got here -> because these rules activated  ( because of these answers) -- but only path

        for( Rule r : allUsedRules) {
            if (r.variable.getName().equals(result.getName())) {
                explainRecursively(r, 1, allUsedRules, userAnswers);
                break;
            }
        }

        System.out.println(">>>>>>>>>>>>>>>");
    }

    public void explainConvolute() {
        System.err.println("Result: UNKNOWN.");
        System.out.println("Reason: Cannot expand any rules. ");
        System.out.println("Ending now...");
    }

    public void explainConvolute(Variable result, List<Rule> allUsedRules, Map<String, Double> userAnswers) {
        explainHow(result, allUsedRules,userAnswers);
    }
}
