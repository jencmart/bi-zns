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
            calculateRightSideProbability(ruleToResolute);
            knowledgeBase.setAsUsed(ruleToResolute);
            knowledgeBase.impossibleToResolute(impossibleList);
        }
    }

    private void explainEnd() { // explaing - awfully looking good worikng method

        List<Variable> terminals = knowledgeBase.getAllTerminals();

        List<Variable> notFalseTerminals = new ArrayList<>();

        for (Variable v: terminals) {
            if(! v.isFalse())
                notFalseTerminals.add(v);
        }

        // normalizuj pravdepodobnosti platnych zaveru
        double denom = 0.0;

        if(notFalseTerminals.isEmpty() || testEmpty()){
            System.out.println("Result UNKNOWN. ending now...");
            return;
        }

        for (Variable v: notFalseTerminals) {
            denom += v.getProbability();
        }

        for (Variable v: notFalseTerminals) {
            v.setProbability(v.getProbability() / denom);
        }

        // serad podle pravdepodobnosti
        notFalseTerminals.sort(Variable::compareTo);

        // vypis prvni tri (ci mene pokud mene)

        int i = 0;
        for(Variable v: notFalseTerminals) {
            System.out.println(v.toString());
            //todo explain how
            ui.explainHow(v, knowledgeBase.getAllUsedRules(), userAnswers );

            i+= 1;
            if(i ==3)
                break;
        }

        if(notFalseTerminals.size() <= 3){
            System.out.println("Showing: [" + notFalseTerminals.size() + "] results  out of [" + notFalseTerminals.size() + "]");
            System.out.println("Ending now");
            return;
        }

        System.out.println("Showing: [3] results ot of [" + notFalseTerminals.size() + "]");

        boolean res = ui.askMore("show more?");
        if(res){
            i = 0;
            for(Variable v: notFalseTerminals) {
                i+=1;
                if(i>3){
                    System.out.println(v.toString());
                    ui.explainHow(v, knowledgeBase.getAllUsedRules(), userAnswers );
                    //
                }
            }

            }
        else {
            System.out.println("Ending now...");
        }
    }

    private boolean testEmpty() {

        for (Double d :userAnswers.values()
             ) {
            if(d != 0)
                return false;
        }
        return true;
    }

    private void calculateRightSideProbability(Rule ruleToResolute) {

        double numerator = ruleToResolute.variable.getDefaultProbability();
        for(Variable v:  ruleToResolute.formula.getVariableList()){
            // numerator *= ruleToResolute.variable.getProbabilityOf(v.getName()); todo prave udelan fix
            numerator *= v.getProbabilityOf(ruleToResolute.variable.getName());
        }


        double denominator = numerator;
        List<Variable> probabilitySpace = ruleToResolute.variable.getScope();

        for(Variable hj : probabilitySpace){
            if(hj.getName().equals(ruleToResolute.variable.getName()))
                continue;

            double tmp = hj.getDefaultProbability();
            for(Variable v : ruleToResolute.formula.getVariableList()) {
                //tmp *= hj.getProbabilityOf(v.getName());
                tmp *= v.getProbabilityOf(hj.getName());
            }

            denominator += tmp;
        }


        double probability = 0.0;

        if(denominator != 0.0 ) {
            probability = numerator / denominator;
        }

        if(probability > ruleToResolute.variable.getProbability()) { // if current probability is higher
            ruleToResolute.variable.setProbability(probability);
        }
    }


    private boolean queryUserForMissingVariables(Rule possibleRule) throws ActionException {
        for (Variable v : possibleRule.formula.getVariableList()) {
            if(!v.isResoluted()) {
                Double response = queryUser(possibleRule,v);
                userAnswers.put(v.getName(), response);
                v.setProbability(response);
                if(v.isFalse()) {
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

            if (isEvaluable && ! r.formula.isFalse() && r.formula.haveAllIntermediatesSolved())
                decisionList.add(r);

            else if (isEvaluable && r.formula.isFalse() )
                impossibleList.add(r);

            else if ( ! isEvaluable &&  missingVariablesRule == null &&  r.formula.haveAllIntermediatesSolved()) // posible rule is rule which we can ask for more info
                missingVariablesRule = r;

            else if(missingVariablesRule != null &&  ! isEvaluable && r.formula.countMissing() < missingVariablesRule.formula.countMissing() && r.formula.haveAllIntermediatesSolved())
                    missingVariablesRule = r;
        }

        return missingVariablesRule;
    }
}
