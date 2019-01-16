package cvut.jencmart;

import java.util.*;

public class Variable  implements Comparable<Variable>{
    private String name;
    private Double probability;
    private Double defaultProbability;
    private boolean resoluted;
    private boolean leaf;
    private Set<Variable> probabilitySpace; // others variables from same probability space
    private Map<String, Double> conditionProbabilities; // probabilites P (e|this)

    public Variable(String name) {
        this.name = name;
        this.probability = -1.0;
        this.defaultProbability = 1.0; // zbytecne pokud znalostni baze dobre napsana
        this.resoluted = false;
        this.leaf = true;
        conditionProbabilities =  new HashMap<>();

    }

    public Double getDefaultProbability() {
        return defaultProbability;
    }

    public Variable(String name, double probability) {
        this.name = name;
        this.probability = -1.0;
        this.defaultProbability = probability;
        this.resoluted = false;
        this.leaf = false;
        conditionProbabilities =  new HashMap<>();
    }

    public boolean isLeaf(){
        return this.leaf;
    }
    public void addConditionProbability(String withVar, double probability) {
        conditionProbabilities.put(withVar, probability);
    }

    public void setProbabilitySpace(Set<Variable> probabilitySpace) {
        this.probabilitySpace = probabilitySpace;
    }

    public String getName() {
        return name;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
        this.resoluted = true;
    }

    public boolean isResoluted() { return this.resoluted; }

    public boolean isFalse() {
        return this.probability.equals(0.0);
    }

    @Override
    public String toString() {
        String v = "[";
        if ( isResoluted() ){
            v += getProbability();
            v += "]";
        } else
            v += "?]";

        return "(" + name.replace('_', ' ') + ")" + v;
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

    // returns all other variables from same probability space ( disjunction is expected)
    public List<Variable> getScope() {
        List<Variable> probabilites = new ArrayList<>();

        for (Variable v: probabilitySpace
             ) {
            if(v != this)
                probabilites.add(v);
        }
        return probabilites;
    }

    // returns  P(name | this)
    public double getProbabilityOf(String name) {
        Double x;
        x = conditionProbabilities.get(name);

        if(x == null)
           return 0.124321;

        return x;
    }

    @Override
    public int compareTo(Variable variable) {
        //let's sort the employee based on id in ascending order
        //returns a negative integer, zero, or a positive integer as this employee id
        //is less than, equal to, or greater than the specified object.
        double res = this.probability - variable.getProbability();
        if(res > 0)
            return -1;
        if(res < 0)
            return 1;
        else
            return 0;

    }
}
