package cvut.jencmart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InferenceEngine {
    private Map<String, Double> userAnswers;
    private KnowledgeBase knowledgeBase;
    private UserInterface ui;

    public InferenceEngine(KnowledgeBase knowledgeBase, UserInterface ui) {
        this.knowledgeBase = knowledgeBase;
        this.ui = ui;
        userAnswers = new HashMap<>();
    }

    // todo - save order ve got facts

    public void startInference(){
        while(true) {

            // MATCH
            List<Rule> decisionList = new ArrayList<>();
            List<Rule> impossibleList = new ArrayList<>();
            Rule possibleRule = createDecisionList(decisionList, impossibleList);



            // CONFLICT RESOLUTION
            Rule toUse;

            if (decisionList.isEmpty() && possibleRule == null) {
                // we are unable to continue
                ui.explainConvolute();
                break;
            }

            if (decisionList.isEmpty() ) {

                boolean solvable = true;
                for (Term t : possibleRule.conjunctionFormula.getTerms()) {
                    if(!t.evaluable()) {
                        try {
                            Double response = queryUser(possibleRule,t);
                            userAnswers.put(t.getVariable().getName(), response);
                            t.getVariable().setValue(response);
                            if(! t.evaluate()) {
                                solvable = false; // no need to work more, allready unable to continue
                                break;
                            }
                        } catch (ActionException e){ // break exception
                            System.out.println("Ending now...");
                            return;
                        }
                    }
                }

                if(!solvable) // user marked false
                    continue;

                toUse = possibleRule;
            } else {
                toUse = decisionList.get(0);
            }

            // ACTION
            // nastav variables na prave strane jako true
            toUse.variable.setValue(1.0);

            // pozadej o prirazeni pravidla do mnoziny hotovych
            knowledgeBase.setAsUsed(toUse);

            // pokud prava strana je v mnozine koncu, Convolute ( result is .. )
            if(knowledgeBase.inTerminals(toUse.variable) ) {
                ui.explainConvolute(toUse.variable.getName(), knowledgeBase.getAllUsedRules(), userAnswers );
                break;
            }
            // rules z impossible listu dej do nepouzitelnych
            knowledgeBase.setAsNotUsable(impossibleList);

        }
    }

    private Double queryUser(Rule easiestRuleWithMissingInfo, Term t) throws ActionException {
        Double response;
        while( true ) {
            try {
                response = ui.askUser(t.getVariable().getName().replace('_', ' '));
                break;
            } catch (ActionException e) {
                String msg = e.getMessage();
                if(msg.equals("stop")) {
                    throw e;
                } else if (msg.equals("how")) {
                    ui.explainHow(knowledgeBase.getAllUsedRules(), userAnswers);
                } else {
                    ui.explainWhy(easiestRuleWithMissingInfo, t);
                }
            }
        }
        return response;
    }

    private Rule createDecisionList(List<Rule> decisionList, List<Rule> impossibleList) {
        Rule possibleRule = null;

        for( Rule r :  knowledgeBase.getAllUsableRules()) {
            boolean evaluable = r.conjunctionFormula.evaluable();

            if (evaluable && r.conjunctionFormula.evaluate() )
                decisionList.add(r);
            else if (evaluable && ! r.conjunctionFormula.evaluate() )
                impossibleList.add(r);
            else if ( ! evaluable &&  possibleRule == null)
                possibleRule = r;
            else if(! evaluable && r.conjunctionFormula.countMissing() < possibleRule.conjunctionFormula.countMissing())
                    possibleRule = r;
        }

        return possibleRule;
    }
}
