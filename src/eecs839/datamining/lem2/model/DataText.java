package eecs839.datamining.lem2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The model class to store the input file data.
 * 
 * @author Ragaprabha Chinnaswamy - 2830383
 */
public class DataText {

	private List<Map<String, String>> attributeMapList = new ArrayList<Map<String, String>>();
	private String decision;

	public List<Map<String, String>> getAttributeMapList() {
		return attributeMapList;
	}

	public void setAttributeMapList(List<Map<String, String>> attributeMapList) {
		this.attributeMapList = attributeMapList;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

}
