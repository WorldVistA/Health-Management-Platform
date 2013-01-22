package EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns;

import java.util.ArrayList;

/**
 * One configuration field.
 * @author vhaislchandj
 *
 */
public class Config {
	public static final String DATA_TYPE_STRING = "STRING";
	public static final String DATA_TYPE_NUMERIC = "NUMERIC";
	public static final String DATA_TYPE_BOOLEAN = "BOOLEAN";
	public static final String DATA_TYPE_MAP = "MAP";
	public static final String DATA_TYPE_LIST = "LIST";
	public static final String DATA_TYPE_RANGE = "RANGE";
	
	private String name;
	private String label;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public ArrayList<? extends Object> getChoiceList() {
		return choiceList;
	}
	public void setChoiceList(ArrayList<? extends Object> choices) {
		choiceList = choices;
	}
	private String dataType;
	private ArrayList<? extends Object> choiceList;
}
