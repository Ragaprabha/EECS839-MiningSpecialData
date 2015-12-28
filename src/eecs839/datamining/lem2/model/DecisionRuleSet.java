package eecs839.datamining.lem2.model;

import java.util.Map;
import java.util.Set;

/**
 * The model class to store the final decision rule set.
 * 
 * @author Ragaprabha Chinnaswamy - 2830383
 */
public class DecisionRuleSet {

	String decision;
	Map<String, Set<Integer>> ruleSetMap;
	Set<Integer> finalDecisionIntersectionSet;

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	public Map<String, Set<Integer>> getRuleSetMap() {
		return ruleSetMap;
	}

	public void setRuleSetMap(Map<String, Set<Integer>> ruleSetMap) {
		this.ruleSetMap = ruleSetMap;
	}

	public Set<Integer> getFinalDecisionIntersectionSet() {
		return finalDecisionIntersectionSet;
	}

	public void setFinalDecisionIntersectionSet(Set<Integer> finalDecisionIntersectionSet) {
		this.finalDecisionIntersectionSet = finalDecisionIntersectionSet;
	}

}
