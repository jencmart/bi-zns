package cvut.jencmart;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserInterface {
    private final Scanner reader = new Scanner(System.in);

    public Double askUser(String question) throws ActionException {
        System.out.println("Is it true, that character " + question + "?" + System.lineSeparator() +
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

    public void explainWhy(Rule rule, Term t) {
        rule.toString();
        System.out.println("WHY? >>> Because I'm trying to proove:\t" + rule.toString()); // todo - print why question --> because of this rule.
    }

    public void explainHow(List<Rule> allUsedRules, Map<String, Double> userAnswers) {
        System.out.println("HOW? Because:"); // todo --> how we got here -> because these rules activated  ( because of these answers) -- but only path
        // vem soucasne pravidlo
        // vypis ho
        // pro kazdy term v pravidlu
            // pokud odpovedel user - napis ze odpovedel user
            // jinak najdi spravne pravidlo -> -> pro kazdy term v pravidlu

    }

    public void explainConvolute() {
        System.err.println("Result: UNKNOWN.");
        System.out.println("Reason: Cannot expand any rules. ");
        System.out.println("Ending now...");
    }

    public void explainConvolute(String result, List<Rule> allUsedRules, Map<String, Double> userAnswers) {
        System.out.println("Result: " + result.replace('_', ' '));
        explainHow(allUsedRules,userAnswers);
    }
}
