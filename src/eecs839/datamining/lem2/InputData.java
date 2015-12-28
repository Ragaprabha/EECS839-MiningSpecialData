package eecs839.datamining.lem2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import eecs839.datamining.lem2.model.DataText;
import eecs839.datamining.lem2.model.DecisionRuleSet;

/**
 * This class handles the following,
 * <ol>
 * <li>Gets the type of algorithm to be executed from the user</li>
 * <li>Gets the name of the input file from the user</li>
 * <li>Reads the input data and executes the algorithm</li>
 * </ol>
 * 
 * @author Ragaprabha Chinnaswamy - 2830383
 */
public class InputData {

	private static String fileName;
	private static String decision;
	private static List<DataText> dataTexts = new ArrayList<DataText>();
	private static List<String> attributeName = new ArrayList<String>();
	private static int noOfAttributes, noOfDecision, algorithmType;
	private static float alphaValue;
	private static boolean isNumericaData = false;
	private static Map<String, List<Float>> cutPointsMapList = new HashMap<String, List<Float>>();
	private static List<String> numericAttributes = new ArrayList<String>();
	private static List<String> specialCharAttributeList = new ArrayList<String>();
	private static AttributeValueBlock attributeValueBlock;
	private static AttributeValueBlock attributeValueBlockForCharacteristicSet;
	private static CharacteristicSet characteristicSet;
	private static AlgorithmExecution algorithmExecution;
	private static MLem2AlgorithmExecution lem2AlgorithmExecution;
	private static RuleSetMinimization ruleSetMinimization;
	private static Map<String, Map<String, Set<Integer>>> attributeValueBlockMap = new HashMap<String, Map<String, Set<Integer>>>();
	private static Map<Integer, Set<Integer>> characteristicSetMap = new HashMap<Integer, Set<Integer>>();
	private static Map<String, Set<Integer>> approximationSet = new HashMap<String, Set<Integer>>();
	private static List<Map<String, List<String>>> minimalDecisionRuleListMap = new ArrayList<Map<String, List<String>>>();
	private static List<DecisionRuleSet> decisionRuleSetList = new ArrayList<DecisionRuleSet>();

	/**
	 * Main function.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		boolean flag = false;
		System.out.println("\nEnter the Algorithm to Execute:\n1. Singleton\n2. Subset\n3. Concept");

		while (!flag) {
			Scanner algorithmScanner = new Scanner(System.in);
			try {
				algorithmType = algorithmScanner.nextInt();
				if (algorithmType >= 1 && algorithmType <= 3) {
					flag = true;
				} else {
					System.out.println("Enter an number between 1 and 3: ");
				}
			} catch (Exception e) {
				System.out.println("Invalid input.. Enter an Integer: ");
			}
		}
		flag = false;
		System.out.println("\nEnter Alpha Value (Between 0 & 1): ");
		while (!flag) {
			Scanner alphaValueScanner = new Scanner(System.in);
			try {
				alphaValue = alphaValueScanner.nextFloat();
				if (alphaValue >= 0 && alphaValue <= 1) {
					flag = true;
				} else {
					System.out.println("Alpha value must be between 0 & 1: ");
				}
			} catch (Exception e) {
				System.out.println("Invalid input.. Enter a numeric value between 0 & 1 for Alpha: ");
			}
		}

		fileName = getFileName();
		fileValidator();
		readFile();
		getAlgorithmtoExecute();
	}

	/**
	 * Method to execute the algorithm selected by the user.
	 */
	private static void getAlgorithmtoExecute() {

		NumericDataSetOperation numericDataSetOperation = new NumericDataSetOperation(dataTexts, attributeName);
		isNumericaData = numericDataSetOperation.isDataSetValid();
		specialCharAttributeList = numericDataSetOperation.getSpecialCharAttributList();
		if (isNumericaData) {
			cutPointsMapList = numericDataSetOperation.getCutPoints();
			numericAttributes = numericDataSetOperation.getNumericAttrributeList();
			attributeValueBlock = new AttributeValueBlock(dataTexts, attributeName, numericAttributes,
					cutPointsMapList, isNumericaData);
		} else {
			attributeValueBlock = new AttributeValueBlock(dataTexts, attributeName, null, null, isNumericaData);
		}
		attributeValueBlockMap = attributeValueBlock.computeRelevance();

		attributeValueBlockForCharacteristicSet = new AttributeValueBlock(dataTexts, attributeName, null, null, false);

		characteristicSet = new CharacteristicSet(dataTexts, numericAttributes, false,
				attributeValueBlockForCharacteristicSet.computeRelevance(),
				attributeValueBlockForCharacteristicSet.getHyphenPositionValueMap());
		characteristicSetMap = characteristicSet.getCharacteristicSet();

		algorithmExecution = new AlgorithmExecution(dataTexts, characteristicSetMap, algorithmType, alphaValue);
		algorithmExecution.getApproximationProbabilityValue();
		approximationSet = algorithmExecution.getApproximationData(algorithmType, alphaValue);

		lem2AlgorithmExecution = new MLem2AlgorithmExecution(attributeName, attributeValueBlockMap, approximationSet,
				numericAttributes, isNumericaData, specialCharAttributeList);
		decisionRuleSetList = lem2AlgorithmExecution.execute();

		ruleSetMinimization = new RuleSetMinimization(decisionRuleSetList, approximationSet);
		minimalDecisionRuleListMap = ruleSetMinimization.getMinimalRuleset();
		printFinalRule();
	}

	/**
	 * Method to check the file exists or not.
	 */
	private static void fileValidator() {
		try {
			File inputFile = new File(fileName);
			Scanner fileNameValidator = new Scanner(inputFile);
		} catch (Exception exception) {
			System.out.println("File not found");
			System.exit(1);
		}
	}

	/**
	 * Method to read the data from the input file
	 */
	private static void readFile() {
		try {
			File inputFile = new File(fileName);
			Scanner fileNameValidatorScanner = new Scanner(inputFile);
			int tempCount = 0;
			String attributStart = null;

			while (fileNameValidatorScanner.hasNext()) {
				String token = fileNameValidatorScanner.next();

				if (!token.equals(">")) {
					if (token.equalsIgnoreCase("a")) {
						noOfAttributes++;
					} else if (token.equalsIgnoreCase("d")) {
						noOfDecision++;
					}
				} else {
					break;
				}
			}

			while (fileNameValidatorScanner.hasNext()) {
				String token = fileNameValidatorScanner.next();

				if (token.equals("[")) {
					attributStart = token;
				}

				if (attributStart.equals("[") && !token.equals("[") && !token.equals("]")) {
					if (tempCount < noOfAttributes) {
						attributeName.add(token);
						tempCount++;
					} else if (tempCount == noOfAttributes) {
						decision = token;
					}
				} else if (token.equals("]")) {
					break;
				}
			}

			while (fileNameValidatorScanner.hasNext()) {
				tempCount = 0;
				DataText dataText = new DataText();
				List<Map<String, String>> tempAttributeMapList = new ArrayList<Map<String, String>>();
				for (int i = 0; i < (noOfAttributes + noOfDecision); i++) {
					if (i < noOfAttributes) {
						Map<String, String> tempAttributeValue = new HashMap<String, String>();
						tempAttributeValue.put(attributeName.get(tempCount), fileNameValidatorScanner.next());
						tempCount++;
						tempAttributeMapList.add(tempAttributeValue);
					} else {
						dataText.setDecision(fileNameValidatorScanner.next());
					}
				}
				dataText.setAttributeMapList(tempAttributeMapList);
				dataTexts.add(dataText);
			}

		} catch (Exception exception) {
			System.out.println("Invalid File Data. Check input file Data");
			System.exit(2);
		}
	}

	/**
	 * Method to get the input file name from the user.
	 * 
	 * @return userDefinedFileName - name of the input file
	 */
	private static String getFileName() {
		System.out.println("Enter the input file name: ");
		String userDefinedFileName = null;

		try {
			Scanner fileInputNameScanner = new Scanner(System.in);
			userDefinedFileName = fileInputNameScanner.next();
		} finally {
			// fileInputNameScanner.close();
		}
		return userDefinedFileName;
	}

	/**
	 * Print Final rule for all the decisions in the output file (
	 */
	public static void printFinalRule() {

		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(fileName.replaceAll(".d", ".data")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.setOut(out);
		System.out.println("Input data file Name: " + fileName);
		System.out.println("No of Attributes :  " + noOfAttributes);
		System.out.println("No of Decision :  " + noOfDecision);
		System.out.println("Total No.of Rows: " + dataTexts.size() + "\n");
		String algorithmName = null;
		if (algorithmType == 1) {
			algorithmName = "SINGLETON";
		} else if (algorithmType == 2) {
			algorithmName = "SUBSET";
		} else if (algorithmType == 3) {
			algorithmName = "CONCEPT";
		}

		System.out.println("The Algorithm type used: " + algorithmName);
		System.out.println("Alpha Value is: " + alphaValue);
		System.out.println("\nThe Final rule sets are:");
		for (DecisionRuleSet decisionRuleSetData : decisionRuleSetList) {
			int i = 0;
			for (Map.Entry<String, Set<Integer>> decisionRuleSetEntry : decisionRuleSetData.getRuleSetMap().entrySet()) {
				if (i == 0) {
					System.out.print("(" + decisionRuleSetEntry.getKey() + ")");
					i++;
				} else {
					System.out.print(" & ");
					System.out.print("(" + decisionRuleSetEntry.getKey() + ")");
				}
			}
			System.out.println(" --> (" + decision + "," + decisionRuleSetData.getDecision() + ")");
		}

		System.out.println("\nThe Minimal Rule sets are:");
		for (Map<String, List<String>> minimalRuleMap : minimalDecisionRuleListMap) {
			int i = 0;
			for (Map.Entry<String, List<String>> minimalRule : minimalRuleMap.entrySet()) {
				List<Integer> minialRuleValueList = new ArrayList<Integer>(getMinimalRuleValue(minimalRule.getValue(),
						minimalRule.getKey()));
				for (String tempString : minimalRule.getValue()) {

					if (i == 0) {
						System.out.println("(" + minimalRule.getValue().size() + ", " + minialRuleValueList.get(0)
								+ ", " + minialRuleValueList.get(1) + ")");
						System.out.print("(" + tempString + ")");
						i++;
					} else {
						System.out.print(" & ");
						System.out.print("(" + tempString + ")");
					}
				}
				System.out.println(" --> (" + decision + "," + minimalRule.getKey() + ")");
				System.out.println();
			}
		}
	}

	/**
	 * Method to get the Minimal rule set values
	 * 
	 * @param minimalRuleList
	 *            - Minimal rule list
	 * @param decision
	 *            - the decision the rule is covered
	 * @return tempMinimalRuleValue - the minimal rule value
	 */
	public static List<Integer> getMinimalRuleValue(List<String> minimalRuleList, String decision) {
		List<String> tempMinimalRuleValue = new ArrayList<String>(minimalRuleList);
		int secondVal = 0, thirdVal = 0;
		for (DataText dataText : dataTexts) {
			List<Map<String, String>> dataTextMapList = dataText.getAttributeMapList();
			boolean isMinimalRuleAvailable = true;
			for (Map<String, String> dataTextMap : dataTextMapList) {
				for (String temRuleName : tempMinimalRuleValue) {
					String[] tempSplitName = temRuleName.split(Pattern.quote(","));
					if (!numericAttributes.contains(tempSplitName[0])
							&& dataTextMap.containsKey(tempSplitName[0].trim())
							&& !dataTextMap.get(tempSplitName[0].trim()).equalsIgnoreCase(tempSplitName[1])) {
						isMinimalRuleAvailable = false;
					} else if (numericAttributes.contains(tempSplitName[0])) {
						String[] tempnumericValueSplit = tempSplitName[1].split(Pattern.quote(".."));
						if (dataTextMap.containsKey(tempSplitName[0])
								&& (dataTextMap.get(tempSplitName[0]).equalsIgnoreCase("?")
										|| dataTextMap.get(tempSplitName[0]).equalsIgnoreCase("*") || dataTextMap.get(
										tempSplitName[0]).equalsIgnoreCase("-"))) {
							isMinimalRuleAvailable = false;
						} else if (dataTextMap.containsKey(tempSplitName[0])
								&& (Float.valueOf(dataTextMap.get(tempSplitName[0])) < Float
										.valueOf(tempnumericValueSplit[0]) || Float.valueOf(dataTextMap
										.get(tempSplitName[0])) > Float.valueOf(tempnumericValueSplit[1]))) {
							isMinimalRuleAvailable = false;
						}
					}
				}
			}
			if (isMinimalRuleAvailable) {
				thirdVal++;
			}
			if (dataText.getDecision().equalsIgnoreCase(decision) && isMinimalRuleAvailable) {
				secondVal++;
			}
		}
		List<Integer> minialRuleValueList = new ArrayList<Integer>();
		minialRuleValueList.add(secondVal);
		minialRuleValueList.add(thirdVal);
		return minialRuleValueList;

	}
}
