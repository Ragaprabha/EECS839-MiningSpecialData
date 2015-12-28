package eecs839.datamining.lem2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eecs839.datamining.lem2.model.DecisionRuleSet;

/**
 * This class handles the minimization of the final decision rule set
 * 
 * @author Ragaprabha Chinnaswamy - 2830383
 */
public class RuleSetMinimization {

	private List<String> minimalAttributeList = new ArrayList<String>();
	private Map<String, Set<Integer>> approximationSet = new HashMap<String, Set<Integer>>();
	private List<DecisionRuleSet> decisionRuleSetList = new ArrayList<DecisionRuleSet>();
	private List<Map<String, List<String>>> minimalRuleSetList = new ArrayList<Map<String, List<String>>>();

	/**
	 * Constructor for RuleSetMinimization
	 * 
	 * @param decisionRuleSetList
	 *            - {@link DecisionRuleSet}
	 * @param approximationSet
	 *            - the probabilistic approximation set
	 */
	public RuleSetMinimization(List<DecisionRuleSet> decisionRuleSetList, Map<String, Set<Integer>> approximationSet) {

		this.decisionRuleSetList = decisionRuleSetList;
		this.approximationSet = approximationSet;
	}

	/**
	 * This methods is to make the final decision rule minimal.
	 * 
	 * @return
	 * 
	 * @return minimalRuleSetList - the minimal rule set list for the decision
	 */
	public List<Map<String, List<String>>> getMinimalRuleset() {

		for (DecisionRuleSet decisionRuleSetData : decisionRuleSetList) {
			int i = 0;
			for (Map.Entry<String, Set<Integer>> ruleMapEntry : decisionRuleSetData.getRuleSetMap().entrySet()) {
				if (decisionRuleSetData.getFinalDecisionIntersectionSet().containsAll(ruleMapEntry.getValue())
						&& approximationSet.get(decisionRuleSetData.getDecision()).containsAll(ruleMapEntry.getValue())) {
					minimalAttributeList = new ArrayList<String>();
					minimalAttributeList.add(ruleMapEntry.getKey());
					Map<String, List<String>> minimalRuleSetMap = new HashMap<String, List<String>>();
					minimalRuleSetMap.put(decisionRuleSetData.getDecision(), minimalAttributeList);
					minimalRuleSetList.add(minimalRuleSetMap);
					i++;
					break;
				}
			}

			if (i == 0 && decisionRuleSetData.getRuleSetMap().size() > 2) {
				Map<String, Set<Integer>> tempRuleMap = new HashMap<String, Set<Integer>>(
						decisionRuleSetData.getRuleSetMap());
				while (tempRuleMap.size() > 2) {

					for (int j = 0; j <= tempRuleMap.size(); j++) {
						int k = 0;
						Set<Integer> tempIntersection = null;
						String tempIgnoreKeyValue = null;
						for (Map.Entry<String, Set<Integer>> tempRuleMapEntry : tempRuleMap.entrySet()) {
							if (k != j) {
								if (tempIntersection == null) {
									tempIntersection = new HashSet<Integer>(tempRuleMapEntry.getValue());
								} else {
									tempIntersection.retainAll(tempRuleMapEntry.getValue());
								}

							} else {
								tempIgnoreKeyValue = tempRuleMapEntry.getKey();
							}
							k++;
						}
						if (decisionRuleSetData.getFinalDecisionIntersectionSet().containsAll(tempIntersection)
								&& approximationSet.get(decisionRuleSetData.getDecision())
										.containsAll(tempIntersection)) {
							tempRuleMap.remove(tempIgnoreKeyValue);
							break;
						}
					}
					if (tempRuleMap.size() == decisionRuleSetData.getRuleSetMap().size()) {
						break;
					}
				}
				minimalAttributeList = new ArrayList<String>();
				for (Map.Entry<String, Set<Integer>> ruleMapEntry : tempRuleMap.entrySet()) {
					minimalAttributeList.add(ruleMapEntry.getKey());
				}
				Map<String, List<String>> minimalRuleSetMap = new HashMap<String, List<String>>();
				minimalRuleSetMap.put(decisionRuleSetData.getDecision(), minimalAttributeList);
				minimalRuleSetList.add(minimalRuleSetMap);
			} else if (i == 0 && decisionRuleSetData.getRuleSetMap().size() == 2) {
				minimalAttributeList = new ArrayList<String>();
				for (Map.Entry<String, Set<Integer>> ruleMapEntry : decisionRuleSetData.getRuleSetMap().entrySet()) {
					minimalAttributeList.add(ruleMapEntry.getKey());
				}
				Map<String, List<String>> minimalRuleSetMap = new HashMap<String, List<String>>();
				minimalRuleSetMap.put(decisionRuleSetData.getDecision(), minimalAttributeList);
				minimalRuleSetList.add(minimalRuleSetMap);
			}
		}
		return minimalRuleSetList;
	}
}
