package com.kalinya.javafx.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RowData {
	private final StringProperty propertyColumn1;
	private final StringProperty propertyColumn2;
	
	public RowData(String columnValue1, String columnValue2) {
		this.propertyColumn1 = new SimpleStringProperty(columnValue1);
		this.propertyColumn2 = new SimpleStringProperty(columnValue2);		
	}
	
	public String getPropertyColumn1() {
		return propertyColumn1.get();
	}
	
	public void setPropertyColumn1(String value) {
		propertyColumn1.set(value);
	}
	
	public String getPropertyColumn2() {
		return propertyColumn2.get();
	}
	
	public void setPropertyColumn2(String value) {
		propertyColumn2.set(value);
	}
}
