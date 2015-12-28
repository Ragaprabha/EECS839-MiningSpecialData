package eecs839.datamining.lem2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import eecs839.datamining.lem2.model.DecisionRuleSet;

/**
 * This class handles the execution of LEM2 algorithm
 * 
 * @author Ragaprabha Chinnaswamy - 2830383
 */
public class MLem2AlgorithmExecution {

	private List<String> independentAttributes;
	private List<String> numericAttributes;
	private boolean isNumericDataInAttributes;
	private Map<String, Map<String, Set<Integer>>> attributeValueBlockMap = new HashMap<String, Map<String, Set<Integer>>>();
	private Map<String, Set<Integer>> approximationSet = new HashMap<String, Set<Integer>>();
	private Set<Integer> goal;
	private Set<Integer> subGoal;
	private Map<String, Map<String, Set<Integer>>> tempApproximationAttributeSet = new HashMap<String, Map<String, Set<Integer>>>();
	private List<String> specialCharAttributeList = new ArrayList<String>();
	private List<DecisionRuleSet> decisionRuleSetList = new ArrayList<DecisionRuleSet>();
	private DecisionRuleSet decisionRuleSet;

	/**
	 * Constructor for MLem2AlgorithmExecution
	 * 
	 * @param independentAttributes
	 *            - List of Attributes
	 * @param attributeValueBlockMap
	 *            - the attribute Value block
	 * @param approximationSet
	 *            - the probabilistic approximation set
	 * @param numericAttributes
	 *            - List of numeric attributes
	 * @param isNumericDataInAttributes
	 *            - defines if the input data has numeric attributes or not
	 * @param specialCharAttributeList
	 *            - the attribute list if it contains one of these * or ? or -
	 */
	public MLem2AlgorithmExecution(List<String> independentAttributes,
			Map<String, Map<String, Set<Integer>>> attributeValueBlockMap, Map<String, Set<Integer>> approximationSet,
			List<String> numericAttributes, boolean isNumericDataInAttribute, List<String> specialCharAttributeList) {

		this.independentAttributes = independentAttributes;
		this.attributeValueBlockMap = attributeValueBlockMap;
		this.approximationSet = approximationSet;
		this.numericAttributes = numericAttributes;
		this.isNumericDataInAttributes = isNumericDataInAttribute;
		this.specialCharAttributeList = specialCharAttributeList;
	}

	/**
	 * This methods executes the MLEM2 Algorithm to find all possible rules for
	 * the decision approximation
	 * 
	 * @return decisionRuleListMap - the rule set for the decision
	 */
	public List<DecisionRuleSet> execute() {

		for (Map.Entry<String, Set<Integer>> approximationEntry : approximationSet.entrySet()) {
			goal = new HashSet<Integer>(approximationEntry.getValue());
			subGoal = new HashSet<Integer>(approximationEntry.getValue());

			List<String> tempDecisionList = new ArrayList<String>();
			List<Set<Integer>> tempIntegerSetList = new ArrayList<Set<Integer>>();
			Set<Integer> unionGoalSet = null;
			Map<String, Set<Integer>> ruleSetMap = new HashMap<String, Set<Integer>>();
			while (!subGoal.isEmpty()) {
				for (String attribute : independentAttributes) {
					Map<String, Set<Integer>> tempAttributeSet = new HashMap<String, Set<Integer>>();
					for (Map.Entry<String, Set<Integer>> attributeblockEntry : attributeValueBlockMap.get(attribute)
							.entrySet()) {
						Set<Integer> tempSet2 = new HashSet<Integer>();
						if (tempDecisionList.isEmpty()) {
							Set<Integer> tempSet1 = new HashSet<Integer>(subGoal);
							tempSet2 = new HashSet<Integer>(attributeblockEntry.getValue());
							tempSet2.retainAll(tempSet1);
						} else {
							boolean istempIgnoreString = false;
							for (String tempIgnoreString : tempDecisionList) {
								if ((!isNumericDataInAttributes)
										|| (isNumericDataInAttributes && !numericAttributes.contains(attribute))) {
									String[] tempStringArray = tempIgnoreString.split(Pattern.quote(","));
									if ((attribute.equalsIgnoreCase(tempStringArray[0]) && attributeblockEntry.getKey()
											.equalsIgnoreCase(tempStringArray[1]))
											|| (specialCharAttributeList != null && !specialCharAttributeList.isEmpty() && specialCharAttributeList
													.contains(tempStringArray[0]))) {
										istempIgnoreString = true;
									}
								} else {
									String[] tempStringArray = tempIgnoreString.split(Pattern.quote(","));
									String[] tempNumberArrayString = tempStringArray[1].split(Pattern.quote(".."));
									String[] attributeblockEntryTempStringArray = attributeblockEntry.getKey().split(
											Pattern.quote(".."));
									if (attribute.equalsIgnoreCase(tempStringArray[0])
											&& (attributeblockEntry.getKey().equalsIgnoreCase(tempStringArray[1]) || tempNumberArrayString[1]
													.equalsIgnoreCase(attributeblockEntryTempStringArray[0]))) {
										istempIgnoreString = true;
									}
								}

							}
							if (!istempIgnoreString) {
								Set<Integer> tempSet1 = new HashSet<Integer>(subGoal);
								tempSet2 = new HashSet<Integer>(attributeblockEntry.getValue());
								tempSet2.retainAll(tempSet1);
							}
						}
						tempAttributeSet.put(attributeblockEntry.getKey(), tempSet2);

					}
					tempApproximationAttributeSet.put(attribute, tempAttributeSet);
				}
				List<Map<String, String>> attributeSizeList = new ArrayList<Map<String, String>>();
				Map<String, String> attributeSizeMap;
				int size = 0;
				for (String attribute : independentAttributes) {
					for (Map.Entry<String, Set<Integer>> tempAttributeSet : tempApproximationAttributeSet
							.get(attribute).entrySet()) {
						attributeSizeMap = new HashMap<String, String>();
						attributeSizeMap.put(attribute, tempAttributeSet.getKey());
						if (size == 0 && !tempAttributeSet.getValue().isEmpty()) {
							size = tempAttributeSet.getValue().size();
							attributeSizeList = new ArrayList<Map<String, String>>();
							attributeSizeList.add(attributeSizeMap);
						} else if (size != 0 && tempAttributeSet.getValue().size() > size) {
							size = tempAttributeSet.getValue().size();
							attributeSizeList = new ArrayList<Map<String, String>>();
							attributeSizeList.add(attributeSizeMap);
						} else if (size != 0 && tempAttributeSet.getValue().size() == size) {
							attributeSizeList.add(attributeSizeMap);
						}
					}
				}
				Set<Integer> tempSubset = new HashSet<Integer>();
				String tempDecisionString = null;
				if (attributeSizeList.size() == 1 && size != 0) {
					Map<String, String> tempSubsetMap = new HashMap<String, String>(attributeSizeList.get(0));
					for (Map.Entry<String, String> subsetEntry : tempSubsetMap.entrySet()) {
						tempSubset = new HashSet<Integer>(attributeValueBlockMap.get(subsetEntry.getKey()).get(
								subsetEntry.getValue()));
						tempDecisionString = subsetEntry.getKey() + "," + subsetEntry.getValue();
					}

				} else if (size != 0) {
					int j = 0, lowSize = 0;
					for (Map<String, String> tempSubsetMap : attributeSizeList) {
						for (Map.Entry<String, String> subsetEntry : tempSubsetMap.entrySet()) {
							if (j == 0) {
								lowSize = attributeValueBlockMap.get(subsetEntry.getKey()).get(subsetEntry.getValue())
										.size();
								j++;
							} else if (attributeValueBlockMap.get(subsetEntry.getKey()).get(subsetEntry.getValue())
									.size() < lowSize) {
								lowSize = attributeValueBlockMap.get(subsetEntry.getKey()).get(subsetEntry.getValue())
										.size();
							}
						}
					}
					j = 0;
					for (String attribute : independentAttributes) {
						for (Map.Entry<String, Set<Integer>> tempAttributeValueBloackMap : attributeValueBlockMap.get(
								attribute).entrySet()) {
							if (tempAttributeValueBloackMap.getValue().size() == lowSize) {
								boolean issetAvailable = false;
								for (Map<String, String> tempSubsetMap : attributeSizeList) {

									if (tempSubsetMap.containsKey(attribute)
											&& tempSubsetMap.get(attribute).equalsIgnoreCase(
													tempAttributeValueBloackMap.getKey())) {
										issetAvailable = true;
										break;
									}
								}
								if (issetAvailable) {
									tempSubset = new HashSet<Integer>(attributeValueBlockMap.get(attribute).get(
											tempAttributeValueBloackMap.getKey()));
									tempDecisionString = attribute + "," + tempAttributeValueBloackMap.getKey();
									j = 100;
									break;
								}
							}
						}
						if (j == 100) {
							break;
						}
					}
				}
				tempDecisionList.add(tempDecisionString);
				ruleSetMap.put(tempDecisionString, tempSubset);
				if (tempIntegerSetList.isEmpty() && goal.containsAll(tempSubset)) {
					if (unionGoalSet == null) {
						unionGoalSet = new HashSet<Integer>(tempSubset);
					} else {
						unionGoalSet.addAll(tempSubset);
					}
					Set<Integer> tempGoal = new HashSet<Integer>(goal);
					for (Integer value : goal) {
						if (unionGoalSet.contains(value)) {
							tempGoal.remove(value);
						}
					}
					subGoal = new HashSet<Integer>(tempGoal);
					decisionRuleSet = new DecisionRuleSet();
					decisionRuleSet.setDecision(approximationEntry.getKey());
					decisionRuleSet.setFinalDecisionIntersectionSet(tempSubset);
					decisionRuleSet.setRuleSetMap(ruleSetMap);
					decisionRuleSetList.add(decisionRuleSet);
					ruleSetMap = new HashMap<String, Set<Integer>>();
					tempIntegerSetList = new ArrayList<Set<Integer>>();
					tempDecisionList = new ArrayList<String>();
				} else {
					if (!tempSubset.isEmpty()) {
						tempIntegerSetList.add(tempSubset);
						Set<Integer> tempSet1 = null;
						for (Set<Integer> tempIntegerSet : tempIntegerSetList) {
							if (tempSet1 == null) {
								tempSet1 = new HashSet<Integer>(tempIntegerSet);
							} else {
								tempSet1.retainAll(tempIntegerSet);
							}
						}
						if (goal.containsAll(tempSet1)) {

							if (unionGoalSet == null) {
								unionGoalSet = new HashSet<Integer>(tempSet1);
							} else {
								unionGoalSet.addAll(tempSet1);
							}
							Set<Integer> tempGoal = new HashSet<Integer>(goal);
							for (Integer value : goal) {
								if (unionGoalSet.contains(value)) {
									tempGoal.remove(value);
								}
							}
							subGoal = new HashSet<Integer>(tempGoal);
							decisionRuleSet = new DecisionRuleSet();
							decisionRuleSet.setDecision(approximationEntry.getKey());
							decisionRuleSet.setFinalDecisionIntersectionSet(tempSet1);
							decisionRuleSet.setRuleSetMap(ruleSetMap);
							decisionRuleSetList.add(decisionRuleSet);
							ruleSetMap = new HashMap<String, Set<Integer>>();
							tempIntegerSetList = new ArrayList<Set<Integer>>();
							tempDecisionList = new ArrayList<String>();
						} else {
							Set<Integer> tempSet2 = null;
							for (Set<Integer> tempIntegerSet : tempIntegerSetList) {
								if (tempSet2 == null) {
									tempSet2 = new HashSet<Integer>(tempIntegerSet);
								} else {
									tempSet2.retainAll(tempIntegerSet);
								}
							}
							subGoal = new HashSet<Integer>(goal);
							subGoal.retainAll(tempSet2);
						}
					} else {

						Set<Integer> tempSet1 = null;
						for (Set<Integer> tempIntegerSet : tempIntegerSetList) {
							if (tempSet1 == null) {
								tempSet1 = new HashSet<Integer>(tempIntegerSet);
							} else {
								tempSet1.retainAll(tempIntegerSet);
							}
						}
						if (unionGoalSet == null) {
							unionGoalSet = new HashSet<Integer>(tempSet1);
						} else {
							unionGoalSet.addAll(tempSet1);
						}
						Set<Integer> tempGoal = new HashSet<Integer>(goal);
						for (Integer value : goal) {
							if (unionGoalSet.contains(value)) {
								tempGoal.remove(value);
							}
						}
						subGoal = new HashSet<Integer>(tempGoal);
						tempIntegerSetList = new ArrayList<Set<Integer>>();
						tempDecisionList = new ArrayList<String>();
						ruleSetMap = new HashMap<String, Set<Integer>>();
					}
				}
			}
		}
		return decisionRuleSetList;
	}
}
