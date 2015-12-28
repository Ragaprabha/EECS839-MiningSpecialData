package eecs839.datamining.lem2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import eecs839.datamining.lem2.model.DataText;

/**
 * This class handles the following,
 * <ol>
 * <li>Calculates the attribute value block for the given attributes</li>
 * <li>Process the value key for the attributes to compute Relevance</li>
 * </ol>
 * 
 * @author Ragaprabha Chinnaswamy - 2830383
 */
public class AttributeValueBlock {

	private List<DataText> attributeDecisionDataText;
	private List<String> independentAttributes;
	private List<String> numericAttributes;
	private boolean isNumericDataInAttributes;
	private Map<String, List<Float>> cutPointsMap = new HashMap<String, List<Float>>();
	private Map<String, Map<Integer, List<String>>> hyphenPositionValueMap = new HashMap<String, Map<Integer, List<String>>>();
	private Map<String, Map<String, Set<Integer>>> attributeValueBlockMap = new HashMap<String, Map<String, Set<Integer>>>();

	/**
	 * Constructor for AttributeValueBlock
	 * 
	 * @param attributeDecisionDataText
	 *            - {@link DataText}
	 * @param independentAttributes
	 *            - List of Attributes
	 * @param numericAttributes
	 *            - List of numeric attributes
	 * @param cutPointsMap
	 *            - Cut points for numeric attributes
	 * @param isNumericDataInAttributes
	 *            - defines if the input data has numeric attributes or not
	 */
	public AttributeValueBlock(List<DataText> attributeDecisionDataText, List<String> independentAttributes,
			List<String> numericAttributes, Map<String, List<Float>> cutPointsMap, boolean isNumericDataInAttributes) {
		this.attributeDecisionDataText = attributeDecisionDataText;
		this.independentAttributes = independentAttributes;
		this.numericAttributes = numericAttributes;
		this.isNumericDataInAttributes = isNumericDataInAttributes;
		this.cutPointsMap = cutPointsMap;

	}

	/**
	 * This method computes the relevance for the input attributes [(a,v)].
	 * 
	 * @return attributeValueBlockMap - the attribute value block Map
	 */
	public Map<String, Map<String, Set<Integer>>> computeRelevance() {

		getHyphenValuePositionList();
		for (String attribute : independentAttributes) {
			List<String> attributValuesList = getAttributeValues(attribute);
			Map<String, Set<Integer>> attributeValuePositionMap = new HashMap<String, Set<Integer>>();
			if ((!isNumericDataInAttributes) || (isNumericDataInAttributes && !numericAttributes.contains(attribute))) {
				for (String attributeValue : attributValuesList) {
					Set<Integer> attributeValuePosition = new HashSet<Integer>();
					int i = 1;
					for (DataText dataText : attributeDecisionDataText) {
						List<Map<String, String>> dataTextMapList = dataText.getAttributeMapList();
						for (Map<String, String> dataTextMap : dataTextMapList) {
							if (dataTextMap.containsKey(attribute)
									&& (dataTextMap.get(attribute).equalsIgnoreCase(attributeValue) || dataTextMap.get(
											attribute).equalsIgnoreCase("*"))
									&& !dataTextMap.get(attribute).equalsIgnoreCase("?")
									&& !dataTextMap.get(attribute).equalsIgnoreCase("-")) {
								attributeValuePosition.add(i);
							}
						}
						i++;
					}
					attributeValuePositionMap.put(attributeValue, attributeValuePosition);
				}
				if (hyphenPositionValueMap.containsKey(attribute)) {
					Map<Integer, List<String>> tempHyphenPositionnMap = hyphenPositionValueMap.get(attribute);
					for (Map.Entry<Integer, List<String>> entry : tempHyphenPositionnMap.entrySet()) {
						for (String tempValue : entry.getValue()) {
							if (attributeValuePositionMap.containsKey(tempValue)) {
								attributeValuePositionMap.get(tempValue).add(entry.getKey());
							}
						}
					}
				}

			} else {
				for (String attributeValue : attributValuesList) {
					String[] tempAttributeValue = null;
					tempAttributeValue = attributeValue.split(Pattern.quote(".."));
					Set<Integer> attributeValuePosition = new HashSet<Integer>();
					int i = 1;
					for (DataText dataText : attributeDecisionDataText) {
						List<Map<String, String>> dataTextMapList = dataText.getAttributeMapList();
						for (Map<String, String> dataTextMap : dataTextMapList) {
							if (dataTextMap.containsKey(attribute) && dataTextMap.get(attribute).equalsIgnoreCase("*")) {
								attributeValuePosition.add(i);
							} else if (dataTextMap.containsKey(attribute)
									&& !dataTextMap.get(attribute).equalsIgnoreCase("?")
									&& !dataTextMap.get(attribute).equalsIgnoreCase("-")) {
								if (Float.valueOf(dataTextMap.get(attribute)) >= Float.valueOf(tempAttributeValue[0])
										&& Float.valueOf(dataTextMap.get(attribute)) <= Float
												.valueOf(tempAttributeValue[1])) {
									attributeValuePosition.add(i);
								}
							}
						}
						i++;
					}
					attributeValuePositionMap.put(attributeValue, attributeValuePosition);
				}
				if (hyphenPositionValueMap.containsKey(attribute)) {
					String[] tempAttributeValue = null;
					Map<Integer, List<String>> tempHyphenPositionnMap = hyphenPositionValueMap.get(attribute);
					for (Map.Entry<Integer, List<String>> entry : tempHyphenPositionnMap.entrySet()) {
						for (String tempValue : entry.getValue()) {
							for (Map.Entry<String, Set<Integer>> tempSetValue : attributeValuePositionMap.entrySet()) {
								tempAttributeValue = tempSetValue.getKey().split(Pattern.quote(".."));
								if (Float.valueOf(tempValue) >= Float.valueOf(tempAttributeValue[0])
										&& Float.valueOf(tempValue) <= Float.valueOf(tempAttributeValue[1])) {
									attributeValuePositionMap.get(tempSetValue.getKey()).add(entry.getKey());
								}
							}
						}
					}
				}
			}
			attributeValueBlockMap.put(attribute, attributeValuePositionMap);
		}
		return attributeValueBlockMap;
	}

	/**
	 * This method get the list of values for the given attribute
	 * 
	 * @param requiredAttribute
	 *            - attribute for which the values are required
	 * @return attributValues - observations of the attribute
	 */
	public List<String> getAttributeValues(String requiredAttribute) {
		String lowestValue = null, highestValue = null, tempString1 = null, tempString2 = null;
		Set<String> attributValuesSet = new HashSet<String>();
		if ((!isNumericDataInAttributes)
				|| (isNumericDataInAttributes && !numericAttributes.contains(requiredAttribute))) {
			for (DataText dataText : attributeDecisionDataText) {
				List<Map<String, String>> dataTextMapList = dataText.getAttributeMapList();
				for (Map<String, String> dataTextMap : dataTextMapList) {
					if (dataTextMap.containsKey(requiredAttribute)
							&& !dataTextMap.get(requiredAttribute).equalsIgnoreCase("?")
							&& !dataTextMap.get(requiredAttribute).equalsIgnoreCase("*")
							&& !dataTextMap.get(requiredAttribute).equalsIgnoreCase("-")) {
						attributValuesSet.add(dataTextMap.get(requiredAttribute));
					}
				}
			}
		} else {
			Set<Float> tempValuesSet = new HashSet<Float>();
			for (DataText dataText : attributeDecisionDataText) {
				List<Map<String, String>> dataTextMapList = dataText.getAttributeMapList();
				for (Map<String, String> dataTextMap : dataTextMapList) {
					if (dataTextMap.containsKey(requiredAttribute)
							&& !dataTextMap.get(requiredAttribute).equalsIgnoreCase("?")
							&& !dataTextMap.get(requiredAttribute).equalsIgnoreCase("*")
							&& !dataTextMap.get(requiredAttribute).equalsIgnoreCase("-")) {
						tempValuesSet.add(Float.valueOf(dataTextMap.get(requiredAttribute)));
					}
				}
			}

			List<Float> tempValuesList = new ArrayList<Float>(tempValuesSet);
			Collections.sort(tempValuesList);
			int tempValuesListSize = tempValuesList.size();
			lowestValue = Float.toString(tempValuesList.get(0));
			highestValue = Float.toString(tempValuesList.get(tempValuesListSize - 1));
			Collections.sort(cutPointsMap.get(requiredAttribute));
			for (float val : cutPointsMap.get(requiredAttribute)) {
				tempString1 = lowestValue + ".." + Float.toString(val);
				tempString2 = Float.toString(val) + ".." + highestValue;
				attributValuesSet.add(tempString1);
				attributValuesSet.add(tempString2);
			}
		}
		List<String> attributValuesList = new ArrayList<String>(attributValuesSet);
		return attributValuesList;
	}

	/**
	 * This method is used to get all the attribute concept values. (eg.,
	 * V[2,Humidity])
	 */
	public void getHyphenValuePositionList() {
		Map<Integer, String> hyphenDecisionMap = new HashMap<Integer, String>();
		for (String attribute : independentAttributes) {
			hyphenDecisionMap = getHyphenDecision(attribute);
			if (hyphenDecisionMap.size() > 0) {
				Map<Integer, List<String>> tempEntryMap = new HashMap<Integer, List<String>>();
				for (Map.Entry<Integer, String> entry : hyphenDecisionMap.entrySet()) {
					Set<String> tempEnteySet = new HashSet<String>();
					for (DataText dataText : attributeDecisionDataText) {
						List<Map<String, String>> dataTextMapList = dataText.getAttributeMapList();
						for (Map<String, String> dataTextMap : dataTextMapList) {
							if (dataTextMap.containsKey(attribute)
									&& dataText.getDecision().equalsIgnoreCase(entry.getValue())
									&& !dataTextMap.get(attribute).equalsIgnoreCase("?")
									&& !dataTextMap.get(attribute).equalsIgnoreCase("*")
									&& !dataTextMap.get(attribute).equalsIgnoreCase("-")) {
								tempEnteySet.add(dataTextMap.get(attribute));
							}
						}
					}
					tempEntryMap.put(entry.getKey(), new ArrayList<String>(tempEnteySet));
				}
				hyphenPositionValueMap.put(attribute, tempEntryMap);
			}
		}
	}

	/**
	 * This Method get the position and corresponding decision for the Hyphen
	 * (<b>-</b>) values
	 * 
	 * @param attribute
	 *            - the attribute for which its required.
	 * @return hyphenDecisionMap - the hash map with position and decision
	 */
	public Map<Integer, String> getHyphenDecision(String attribute) {
		int i = 1;
		Map<Integer, String> hyphenDecisionMap = new HashMap<Integer, String>();
		for (DataText dataText : attributeDecisionDataText) {
			List<Map<String, String>> dataTextMapList = dataText.getAttributeMapList();
			for (Map<String, String> dataTextMap : dataTextMapList) {
				if (dataTextMap.containsKey(attribute) && dataTextMap.get(attribute).equalsIgnoreCase("-")) {
					hyphenDecisionMap.put(i, dataText.getDecision());
				}
			}
			i++;
		}
		return hyphenDecisionMap;
	}

	/**
	 * This method returns the hyphen position in the data list
	 * 
	 * @return hyphenPositionValueMap - the hyphen position map
	 */
	public Map<String, Map<Integer, List<String>>> getHyphenPositionValueMap() {
		return hyphenPositionValueMap;
	}
}
