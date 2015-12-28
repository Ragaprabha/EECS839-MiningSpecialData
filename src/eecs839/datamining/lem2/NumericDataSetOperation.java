package eecs839.datamining.lem2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eecs839.datamining.lem2.model.DataText;

/**
 * This class handles the following,
 * <ol>
 * <li>Checks if the data set is valid and contains numeric values</li>
 * <li>populates the cut points if the data set has numeric values</li>
 * </ol>
 */
public class NumericDataSetOperation {

	private List<DataText> numericDataTexts;
	private List<String> decisionAttributes;
	private List<String> numberAttributes = new ArrayList<String>();
	private Map<String, List<Float>> cutPointsMapList = new HashMap<String, List<Float>>();
	private List<String> specialCharAttributeList = new ArrayList<String>();

	/**
	 * Constructor for NumericDataSetOperation
	 * 
	 * @param numericDataTexts
	 *            - {@link DataText}
	 * @param decisionAttributes
	 *            - List of decision attributes
	 */
	public NumericDataSetOperation(List<DataText> numericDataTexts, List<String> decisionAttributes) {
		this.numericDataTexts = numericDataTexts;
		this.decisionAttributes = decisionAttributes;
	}

	/**
	 * Method to check if the data set in the input file is valid or not
	 */
	public boolean isDataSetValid() {
		for (String attribute : decisionAttributes) {
			boolean isNumber = false, isString = false;
			String specialCharAttribute = null;
			for (DataText dataText : numericDataTexts) {
				List<Map<String, String>> dataTextMapList = dataText.getAttributeMapList();
				for (Map<String, String> dataTextMap : dataTextMapList) {
					if (dataTextMap.containsKey(attribute) && dataTextMap.get(attribute) != null
							&& isAttributeValueFloat(dataTextMap.get(attribute))) {
						isNumber = true;
					} else if (dataTextMap.containsKey(attribute) && dataTextMap.get(attribute) != null
							&& !isAttributeValueFloat(dataTextMap.get(attribute))) {
						if (!dataTextMap.get(attribute).equalsIgnoreCase("?")
								&& !dataTextMap.get(attribute).equalsIgnoreCase("*")
								&& !dataTextMap.get(attribute).equalsIgnoreCase("-")) {
							isString = true;
						}
					}

					if (dataTextMap.containsKey(attribute)
							&& dataTextMap.get(attribute) != null
							&& (dataTextMap.get(attribute).equalsIgnoreCase("?")
									|| dataTextMap.get(attribute).equalsIgnoreCase("*") || dataTextMap.get(attribute)
									.equalsIgnoreCase("-"))) {
						specialCharAttribute = attribute;
					}
				}
			}
			if (isNumber && isString) {
				System.out.println("Invalid " + attribute + " data. Check input file Data");
				System.exit(2);
			} else if (isNumber && !isString) {
				numberAttributes.add(attribute);
			}

			if (specialCharAttribute != null) {
				specialCharAttributeList.add(specialCharAttribute);
			}
		}
		if (numberAttributes.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method get the cut points required for numeric attributes
	 * 
	 * @return cutPointsMapList - List of cut points for each numeric attributes
	 */
	public Map<String, List<Float>> getCutPoints() {

		for (String attribute : numberAttributes) {
			Set<Float> dataPointSet = new HashSet<Float>();
			for (DataText dataText : numericDataTexts) {
				List<Map<String, String>> dataTextMapList = dataText.getAttributeMapList();
				for (Map<String, String> dataTextMap : dataTextMapList) {
					if (dataTextMap.containsKey(attribute) && !dataTextMap.get(attribute).equalsIgnoreCase("?")
							&& !dataTextMap.get(attribute).equalsIgnoreCase("*")
							&& !dataTextMap.get(attribute).equalsIgnoreCase("-")) {
						dataPointSet.add(Float.valueOf(dataTextMap.get(attribute)));
					}
				}
			}
			List<Float> sortedDataPoint = new ArrayList<Float>(dataPointSet);
			Collections.sort(sortedDataPoint);
			Map<Float, List<String>> dataPointDecisionMap = compareDecisionForDataPoints(sortedDataPoint, attribute);
			List<Float> cutPointMedian = new ArrayList<Float>();
			int i = 0;
			float tempDataPoint = 0;
			for (float dataPoint : sortedDataPoint) {
				if (i == 0) {
					tempDataPoint = dataPoint;
					i++;
				} else {
					if ((dataPointDecisionMap.get(tempDataPoint).size() != dataPointDecisionMap.get(dataPoint).size())
							|| (dataPointDecisionMap.get(tempDataPoint).size() == dataPointDecisionMap.get(dataPoint)
									.size() && !compareList(dataPointDecisionMap.get(tempDataPoint),
									dataPointDecisionMap.get(dataPoint)))) {
						float median = (tempDataPoint + dataPoint) / 2;
						float roundOffMedian = (float) Math.round(median * 100) / 100;
						cutPointMedian.add(roundOffMedian);

					}
					tempDataPoint = dataPoint;
				}
			}
			if (cutPointMedian.size() == 0) {
				int j = 0;
				float tempAddAllDataPoint = 0;
				for (float dataPoint : sortedDataPoint) {
					if (j == 0) {
						tempAddAllDataPoint = dataPoint;
						i++;
					} else {
						float median = (tempAddAllDataPoint + dataPoint) / 2;
						float roundOffMedian = (float) Math.round(median * 100) / 100;
						cutPointMedian.add(roundOffMedian);
					}
				}
			}
			cutPointsMapList.put(attribute, cutPointMedian);
		}
		return cutPointsMapList;
	}

	/**
	 * Method to get the decision for each data point in the data set
	 * 
	 * @param sortedDataPoint
	 *            - Sorted data points for each attribute list
	 * @param attribute
	 *            - the attribute name
	 * @return dataPointDecisionMap - List of decisions corresponding to each
	 *         data point in the attribute list
	 */
	public Map<Float, List<String>> compareDecisionForDataPoints(List<Float> sortedDataPoint, String attribute) {
		Map<Float, List<String>> dataPointDecisionMap = new HashMap<Float, List<String>>();
		for (float dataPointValue : sortedDataPoint) {
			Set<String> dataPointDecisionSet = new HashSet<String>();
			for (DataText dataText : numericDataTexts) {
				List<Map<String, String>> dataTextMapList = dataText.getAttributeMapList();
				for (Map<String, String> dataTextMap : dataTextMapList) {
					if (dataTextMap.containsKey(attribute) && !dataTextMap.get(attribute).equalsIgnoreCase("?")
							&& !dataTextMap.get(attribute).equalsIgnoreCase("*")
							&& !dataTextMap.get(attribute).equalsIgnoreCase("-")
							&& Float.valueOf(dataTextMap.get(attribute)) == dataPointValue) {
						dataPointDecisionSet.add(dataText.getDecision());
					}
				}
			}
			List<String> tempDataPointDecisionList = new ArrayList<String>(dataPointDecisionSet);
			dataPointDecisionMap.put(dataPointValue, tempDataPointDecisionList);
		}
		return dataPointDecisionMap;
	}

	/**
	 * Compare the two list values
	 * 
	 * @param firstList
	 *            - List of String
	 * @param secondList
	 *            - List of String
	 * @return - <b>true</b> if the two lists have same values, else
	 *         <b>false</b>
	 */
	public boolean compareList(List<String> firstList, List<String> secondList) {
		boolean isListsEqual = true;
		for (String firstListValue : firstList) {
			if (!secondList.contains(firstListValue)) {
				isListsEqual = false;
			}
		}
		return isListsEqual;
	}

	/**
	 * Method to get the attributes name that has valid numeric values.
	 * 
	 * @return <b> numberAttributes </b> when the the list size is greater than
	 *         zero, else <b> null </b>
	 */
	public List<String> getNumericAttrributeList() {
		if (numberAttributes.size() > 0) {
			return numberAttributes;
		} else {
			System.out.println("There are no numeric attributes in the input file");
			return null;
		}
	}

	/**
	 * Method to get the attributes name that has valid numeric values.
	 * 
	 * @return <b> numberAttributes </b> when the the list size is greater than
	 *         zero, else <b> null </b>
	 */
	public List<String> getSpecialCharAttributList() {
		if (specialCharAttributeList.size() > 0) {
			return specialCharAttributeList;
		} else {
			return null;
		}
	}

	/**
	 * This method checks if the given string is a numeric value
	 * 
	 * @param attributeValue
	 *            - the input data
	 * @return <b>true</b> if the String is a numeric value, <b> false</b>
	 *         otherwise
	 */
	public boolean isAttributeValueFloat(String attributeValue) {
		try {
			Float.parseFloat(attributeValue);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
