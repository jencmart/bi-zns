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

    public void startInference() throws  ActionException{
        while(true) {
            // MATCH - get all currently solvable rules
            List<Rule> decisionList = new ArrayList<>();
            List<Rule> impossibleList = new ArrayList<>();
            Rule missingVariablesRule = createDecisionList(decisionList, impossibleList); // missing var rule is "simplest rule" ; if more simplest, first loaded used...

            // -- end --
            if (decisionList.isEmpty() && missingVariablesRule == null) {
                explainEnd();
                return;
            }

            // CONFLICT RESOLUTION - select rule
            Rule ruleToResolute;
            if (decisionList.isEmpty()) {
                if (!  queryUserForMissingVariables(missingVariablesRule) )
                    continue;
                ruleToResolute = missingVariablesRule;
            } else {
                ruleToResolute = decisionList.get(0); // pick first currently solvable rule (doesen't matter which one if not asking user...)
            }

            // ACTION
            resoluteRightSide(ruleToResolute);
            knowledgeBase.setAsUsed(ruleToResolute);
            knowledgeBase.impossibleToResolute(impossibleList);
        }
    }

    //todo
    private void explainEnd() { // explaing - awfully looking good worikng method

        List<Variable> terminals = knowledgeBase.getAllTerminals();

        List<Variable> notFalseTerminals = new ArrayList<>();

        for (Variable v: terminals) {
            if(! v.rightSideIsFalse())
                notFalseTerminals.add(v);
        }

        if(notFalseTerminals.isEmpty() || testEmpty()){
            System.out.println("Result UNKNOWN. ending now...");
            return;
        }

        // vypis prvni - jen jeden
        for(Variable v: notFalseTerminals) {
            System.out.println(v.toString());

            //todo explain how
            ui.explainHow(v, knowledgeBase.getAllUsedRules(), userAnswers );
            break;
        }

        System.out.println("Ending now...");
    }

    private boolean testEmpty() {
        for (Double d : userAnswers.values()
        ) {
            if (d != 0)
                return false;
        }
        return true;
    }

    // todo
    private void resoluteRightSide(Rule ruleToResolute) {

        double resoluted_value = 1.0;

        for (VarAndVal varAndVal : ruleToResolute.formula.getVariableList()) {
            if(! varAndVal.getVariable().valueInsideFuzzySet(varAndVal.getValue()) ) {
                ruleToResolute.variable.setRightSideFalse();
            }
            resoluted_value = Math.min(resoluted_value, varAndVal.getVariable().getDefuzzValue());
        }

        ruleToResolute.variable.serRightSideTrue(resoluted_value);
    }

    private boolean queryUserForMissingVariables(Rule possibleRule) throws ActionException {
        for (VarAndVal varAndVal : possibleRule.formula.getVariableList()) {
            if(!varAndVal.getVariable().isResoluted()) {
                Double response = queryUser(possibleRule,varAndVal.getVariable());
                userAnswers.put(varAndVal.getVariable().getName(), response);
                varAndVal.getVariable().setUserInput(response);

                if( ! varAndVal.getVariable().valueInsideFuzzySet(varAndVal.getValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    private Double queryUser(Rule easiestRuleWithMissingInfo, Variable v) throws ActionException {
        Double response;
        while( true ) {
            try {
                response = ui.askUser(v.getName().replace('_', ' '));
                break;
            } catch (ActionException e) {
                String msg = e.getMessage();
                if(msg.equals("stop")) {
                    throw e;
                } else if (msg.equals("how")) {
                    if(knowledgeBase.getAllUsedRules().isEmpty())
                    {
                        System.out.println("Nothing to explain yet...");
                        continue;
                    }
                    ui.explainHow(knowledgeBase.getAllUsedRules().get(knowledgeBase.getAllUsedRules().size()-1).variable ,knowledgeBase.getAllUsedRules(), userAnswers);
                } else {
                    ui.explainWhy(easiestRuleWithMissingInfo, v);
                }
            }
        }
        return response;
    }

    private Rule createDecisionList(List<Rule> decisionList, List<Rule> impossibleList) {
        Rule missingVariablesRule = null;

        for( Rule r :  knowledgeBase.getAllUsableRules()) {
            boolean isEvaluable = r.formula.isEvaluable(); // all resoluted or one or more false

            if (isEvaluable && ! r.formula.isFalse() )
                decisionList.add(r);

            else if (isEvaluable && r.formula.isFalse() )
                impossibleList.add(r);

            else if ( ! isEvaluable &&  missingVariablesRule == null ) // posible rule is rule which we can ask for more info
                missingVariablesRule = r;

            else if(missingVariablesRule != null &&  ! isEvaluable && r.formula.countMissing() < missingVariablesRule.formula.countMissing() )
                    missingVariablesRule = r;
        }

        return missingVariablesRule;
    }
}
