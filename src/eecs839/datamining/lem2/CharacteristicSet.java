package eecs839.datamining.lem2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import eecs839.datamining.lem2.model.DataText;

/**
 * This class is used to find the characteristic set using the attribute value
 * block
 * 
 * @author Ragaprabha Chinnaswamy - 2830383
 */
public class CharacteristicSet {

	private List<DataText> characteristicDataText;
	private List<String> numericAttributes;
	private boolean isNumericDataInAttributes;
	private Map<String, Map<String, Set<Integer>>> attributeValueBlockMap = new HashMap<String, Map<String, Set<Integer>>>();
	private Map<String, Map<Integer, List<String>>> hyphenPositionValueMap = new HashMap<String, Map<Integer, List<String>>>();
	private Map<Integer, Set<Integer>> characteristicSet = new HashMap<Integer, Set<Integer>>();

	/**
	 * Constructor for CharacteristicSet
	 * 
	 * @param characteristicDataText
	 *            - {@link DataText}
	 * @param numericAttributes
	 *            - List of numeric attributes
	 * @param isNumericDataInAttributes
	 *            - defines if the input data has numeric attributes or not
	 * @param attributeValueBlockMap
	 *            - the attribute value block required to find the
	 *            characteristic set
	 * @param hyphenPositionValueMap
	 *            - the hyphen related position and corresponding values
	 */
	public CharacteristicSet(List<DataText> characteristicDataText, List<String> numericAttributes,
			boolean isNumericDataInAttributes, Map<String, Map<String, Set<Integer>>> attributeValueBlockMap,
			Map<String, Map<Integer, List<String>>> hyphenPositionValueMap) {
		this.characteristicDataText = characteristicDataText;
		this.numericAttributes = numericAttributes;
		this.isNumericDataInAttributes = isNumericDataInAttributes;
		this.attributeValueBlockMap = attributeValueBlockMap;
		this.hyphenPositionValueMap = hyphenPositionValueMap;
	}

	/**
	 * Method to get the characteristic set for the input data text
	 * 
	 * @return characteristicSet - the characteristic set
	 */
	public Map<Integer, Set<Integer>> getCharacteristicSet() {

		int i = 1;
		for (DataText dataText : characteristicDataText) {
			List<Map<String, String>> dataTextMapList = dataText.getAttributeMapList();
			Set<Integer> tempSet1 = null;
			Set<Integer> tempSet2 = null;
			Set<Integer> intersectionSet = new HashSet<Integer>();
			for (Map<String, String> dataTextMap : dataTextMapList) {
				for (Map.Entry<String, String> entry : dataTextMap.entrySet()) {
					if (((!isNumericDataInAttributes) || (isNumericDataInAttributes && !numericAttributes
							.contains(entry.getKey())))
							&& !entry.getValue().equalsIgnoreCase("*")
							&& !entry.getValue().equalsIgnoreCase("?") && !entry.getValue().equalsIgnoreCase("-")) {
						if (tempSet1 == null) {
							tempSet1 = new HashSet<Integer>(attributeValueBlockMap.get(entry.getKey()).get(
									entry.getValue()));
						} else {
							tempSet2 = new HashSet<Integer>(attributeValueBlockMap.get(entry.getKey()).get(
									entry.getValue()));
							tempSet1.retainAll(tempSet2);
						}

					} else if (((!isNumericDataInAttributes) || (isNumericDataInAttributes && !numericAttributes
							.contains(entry.getKey())))
							&& entry.getValue().equalsIgnoreCase("-")
							&& hyphenPositionValueMap.containsKey(entry.getKey())
							&& hyphenPositionValueMap.get(entry.getKey()).containsKey(i)
							&& !hyphenPositionValueMap.get(entry.getKey()).get(i).isEmpty()) {
						Set<Integer> unionSet = new HashSet<Integer>();
						for (String tempString : hyphenPositionValueMap.get(entry.getKey()).get(i)) {
							Set<Integer> tempUnionSet = new HashSet<Integer>(attributeValueBlockMap.get(entry.getKey())
									.get(tempString));
							unionSet.addAll(tempUnionSet);
						}
						if (tempSet1 == null) {
							tempSet1 = new HashSet<Integer>(unionSet);
						} else {
							tempSet2 = new HashSet<Integer>(unionSet);
							tempSet1.retainAll(tempSet2);
						}
					} else if (isNumericDataInAttributes && numericAttributes.contains(entry.getKey())
							&& entry.getValue().equalsIgnoreCase("-")
							&& hyphenPositionValueMap.containsKey(entry.getKey())
							&& hyphenPositionValueMap.get(entry.getKey()).containsKey(i)
							&& !hyphenPositionValueMap.get(entry.getKey()).get(i).isEmpty()) {
						Set<Integer> unionSet = new HashSet<Integer>();
						for (String tempString : hyphenPositionValueMap.get(entry.getKey()).get(i)) {
							Set<Integer> tempUnionSet = new HashSet<Integer>(getNumericValueSet(entry.getKey(),
									tempString));
							unionSet.addAll(tempUnionSet);
						}
						if (tempSet1 == null) {
							tempSet1 = new HashSet<Integer>(unionSet);
						} else {
							tempSet2 = new HashSet<Integer>(unionSet);
							tempSet1.retainAll(tempSet2);
						}
					} else if (isNumericDataInAttributes && numericAttributes.contains(entry.getKey())
							&& !entry.getValue().equalsIgnoreCase("*") && !entry.getValue().equalsIgnoreCase("?")
							&& !entry.getValue().equalsIgnoreCase("-")) {
						if (tempSet1 == null) {
							tempSet1 = new HashSet<Integer>(getNumericValueSet(entry.getKey(), entry.getValue()));
						} else {
							tempSet2 = new HashSet<Integer>(getNumericValueSet(entry.getKey(), entry.getValue()));
							tempSet1.retainAll(tempSet2);
						}
					}
				}

			}
			intersectionSet.addAll(tempSet1);
			characteristicSet.put(i, intersectionSet);
			i++;
		}
		return characteristicSet;
	}

	/**
	 * This method gets the union of all numeric attribute value
	 * 
	 * @param attribute
	 *            - the attribute name
	 * @param numericValueString
	 *            - the corresponding attribute value
	 * @return unionSet - the union set
	 */
	public Set<Integer> getNumericValueSet(String attribute, String numericValueString) {
		String[] tempAttributeValue = null;
		Set<Integer> unionSet = new HashSet<Integer>();
		for (Map.Entry<String, Set<Integer>> entry : attributeValueBlockMap.get(attribute).entrySet()) {
			tempAttributeValue = entry.getKey().split(Pattern.quote(".."));
			if (Float.valueOf(numericValueString) >= Float.valueOf(tempAttributeValue[0])
					&& Float.valueOf(numericValueString) <= Float.valueOf(tempAttributeValue[1])) {
				Set<Integer> tempUnionSet = new HashSet<Integer>(attributeValueBlockMap.get(attribute).get(
						entry.getKey()));
				unionSet.addAll(tempUnionSet);

			}
		}
		return unionSet;
	}
}
