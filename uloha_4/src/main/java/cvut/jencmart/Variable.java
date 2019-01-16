package cvut.jencmart;

import java.util.*;

public class Variable  implements Comparable<Variable>{
    private String name;
    private Double userInput;
    private Double defuzzValue;
    private boolean resoluted;

    private boolean right_side_value;
    private double right_side_val;

    private Map<String, List<Integer>> fuzzySets;

    public Variable(String name) {
        this.name = name;
        this.resoluted = false;
        this.fuzzySets = new HashMap<>();
    }

    public String getName() { return name; }

    public Double getDefuzzValue() { return defuzzValue; }

    public Double getUserInput() {return userInput; }

    public boolean isResoluted() { return this.resoluted; }

    public boolean valueInsideFuzzySet(String setName) {

        List<Integer> values = fuzzySets.get(setName);
        if(values == null)
            return false;

        // levy kraj
        if(values.get(0).equals(values.get(1))) {
            return defuzzValue <= values.get(3);
        }

        // pravy kraj
        else if(values.get(2).equals(values.get(3))) {
            return defuzzValue >= values.get(0);
        }

        // cely lichobeznik
        else {
            return values.get(0) <= defuzzValue && defuzzValue <= values.get(3);
        }
    }

    public void setUserInput(Double userInput) {
        this.userInput = userInput;
        fuzzifyAndDeffuztify();
        this.resoluted = true;
    }

    private static double min_of_three(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }

    private static double max_of_three(double a, double b, double c) {
        return Math.max(Math.max(a, b), c);
    }

    private void fuzzifyAndDeffuztify() {

        List<Double> results = new ArrayList<>();

        // FUZZIFY
        for (Map.Entry<String, List<Integer>>  entry : fuzzySets.entrySet()) {
            List<Integer> setDefinition = entry.getValue();
            if(setDefinition.get(0).equals(setDefinition.get(1))) {
                // levy kraj
                if ( userInput <= setDefinition.get(2))
                    results.add( 1.0 );
                else if( userInput >= setDefinition.get(3))
                    results.add(0.0);
                else{
                    double right_part =  ( setDefinition.get(3) - userInput ) / (setDefinition.get(3) -  setDefinition.get(2) );
                    results.add(right_part);
                }
            }

            else if(setDefinition.get(2).equals(setDefinition.get(3))) {
                // pravy kraj
                if ( userInput <= setDefinition.get(0))
                    results.add( 0.0 );
                else if( userInput >= setDefinition.get(1))
                    results.add(1.0);
                else{
                    double left_part  = ( userInput - setDefinition.get(0) ) / (setDefinition.get(1) -  setDefinition.get(1) );
                    results.add(left_part);
                }
            }
            else {
                // cela
                double left_part  = ( userInput - setDefinition.get(0) ) / (setDefinition.get(1) -  setDefinition.get(1) );
                double middle = 1.0;
                double right_part =  ( setDefinition.get(3) - userInput ) / (setDefinition.get(3) -  setDefinition.get(2) );
                double x = min_of_three( left_part, middle, right_part);
                results.add( Math.max( x , 0.0) );
            }
        }

        // DEFUZZYFI
        // foreach result teziste = userIn*result
        this.defuzzValue  = 0.0;
        for (Double res: results) {
            this.defuzzValue += userInput*res;
        }

        double denomin = 0;

        for (Double res: results) {
            denomin += res;
        }

        this.defuzzValue =  this.defuzzValue  / denomin;

    }

    public void addFuzySet(String fuzzySetName, int a, int b, int c, int d) {
        List<Integer> x = new ArrayList<Integer>();
        x.add(a);
        x.add(b);
        x.add(c);
        x.add(d);
        fuzzySets.put(fuzzySetName, x);
    }

    @Override
    public String toString() {
        return "(" + name.replace('_', ' ') + ")" ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(Variable variable) {
        double res = this.userInput - variable.getDefuzzValue();
        if(res > 0)
            return -1;
        if(res < 0)
            return 1;
        else
            return 0;
    }

    public void setRightSideFalse() {
        right_side_value = false;
        right_side_val = 0.0;
    }

    public void serRightSideTrue(Double resolutedValue) {
        right_side_value = true;
        right_side_val = resolutedValue;
    }

    public boolean rightSideIsFalse() {
        return !right_side_value;
    }

    public String getSets() {
        String s = "";

        for (Map.Entry<String, List<Integer>>  entry : fuzzySets.entrySet()) {

            if (valueInsideFuzzySet(entry.getKey())) {
                s += entry.getKey();
                s += ", ";
            }
        }

        if(s.length() > 0) {
            s = s.substring(0, s.length() - 2);
        }

        return s;
    }
}
