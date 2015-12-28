package eecs839.datamining.lem2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eecs839.datamining.lem2.model.DataText;

/**
 * This class handles the following,
 * <ol>
 * <li>Calculates upper and lower approximation for Singleton</li>
 * <li>Calculates upper and lower approximation for Subset</li>
 * <li>Calculates upper and lower approximation for Concept</li>
 * </ol>
 * 
 * @author Ragaprabha Chinnaswamy - 2830383
 */
public class AlgorithmExecution {

	private List<DataText> approximationDataText;
	private Map<Integer, Set<Integer>> characteristicSet = new HashMap<Integer, Set<Integer>>();
	private Map<String, Map<Integer, Float>> probabilityApproximationSet = new HashMap<String, Map<Integer, Float>>();
	private Map<String, Set<Integer>> decisionApproximationSet = new HashMap<String, Set<Integer>>();
	private Map<String, Set<Integer>> approximationSet = new HashMap<String, Set<Integer>>();

	/**
	 * Constructor for AlgorithmExecution
	 * 
	 * @param approximationDataText
	 *            - {@link DataText}
	 * @param characteristicSet
	 *            - the characteristic set
	 */
	public AlgorithmExecution(List<DataText> approximationDataText, Map<Integer, Set<Integer>> characteristicSet,
			int algorithmType, float alphaValue) {
		this.approximationDataText = approximationDataText;
		this.characteristicSet = characteristicSet;
		getDecisionApproximationSet();
	}

	/**
	 * This method gets the approximation set for decision
	 * 
	 * @return decisionApproximationSet - the decision approximation set
	 */
	public Map<String, Set<Integer>> getDecisionApproximationSet() {
		Set<String> decisionSet = new HashSet<String>();
		for (DataText dataText : approximationDataText) {
			decisionSet.add(dataText.getDecision());
		}
		List<String> decisionList = new ArrayList<String>(decisionSet);

		for (String tempDecisionValue : decisionList) {
			int i = 1;
			Set<Integer> tempDecisionSet = new HashSet<Integer>();
			for (DataText dataText : approximationDataText) {
				if (dataText.getDecision().equalsIgnoreCase(tempDecisionValue)) {
					tempDecisionSet.add(i);
				}
				i++;
			}
			decisionApproximationSet.put(tempDecisionValue, tempDecisionSet);
		}
		return decisionApproximationSet;
	}

	/**
	 * This functions get the probability value to be compared with alpha value
	 * 
	 * @return probabilityApproximationSet - the set containing the probability
	 *         value for all decision
	 */
	public Map<String, Map<Integer, Float>> getApproximationProbabilityValue() {

		for (Map.Entry<String, Set<Integer>> decisionEntry : decisionApproximationSet.entrySet()) {
			int i = 1;
			Map<Integer, Float> tempProbabilityValueMap = new HashMap<Integer, Float>();
			for (Map.Entry<Integer, Set<Integer>> characteristicSetEntry : characteristicSet.entrySet()) {
				Set<Integer> tempSet1 = new HashSet<Integer>(decisionEntry.getValue());
				Set<Integer> tempSet2 = new HashSet<Integer>(characteristicSetEntry.getValue());
				int characteristicSetSize = characteristicSetEntry.getValue().size();
				tempSet1.retainAll(tempSet2);
				float probability = (float) tempSet1.size() / (float) characteristicSetSize;
				float roundOffprobabilityValue = (float) Math.round(probability * 1000) / 1000;
				tempProbabilityValueMap.put(i, roundOffprobabilityValue);
				i++;
			}
			probabilityApproximationSet.put(decisionEntry.getKey(), tempProbabilityValueMap);
		}
		return probabilityApproximationSet;
	}

	/**
	 * This method gets the probabilistic approximation for all the 3 algorithm
	 * types
	 * 
	 * @param algorithmType
	 *            - The algorithm to execute
	 * @param alphaValue
	 *            - the value of alpha entered by the user
	 * @return approximationSet - the approximation set
	 */
	public Map<String, Set<Integer>> getApproximationData(int algorithmType, float alphaValue) {
		for (Map.Entry<String, Set<Integer>> decisionEntry : decisionApproximationSet.entrySet()) {
			int i = 1;
			Set<Integer> tempApproximationSet = new HashSet<Integer>();
			for (Map.Entry<Integer, Set<Integer>> characteristicSetEntry : characteristicSet.entrySet()) {
				if (algorithmType == 1 && probabilityApproximationSet.get(decisionEntry.getKey()).get(i) >= alphaValue) {
					tempApproximationSet.add(i);
				} else if ((algorithmType == 2 || (algorithmType == 3 && decisionEntry.getValue().contains(i)))
						&& probabilityApproximationSet.get(decisionEntry.getKey()).get(i) >= alphaValue) {
					for (Integer value : characteristicSetEntry.getValue()) {
						tempApproximationSet.add(value);
					}
				}
				i++;
			}
			approximationSet.put(decisionEntry.getKey(), tempApproximationSet);

		}
		return approximationSet;
	}

}
