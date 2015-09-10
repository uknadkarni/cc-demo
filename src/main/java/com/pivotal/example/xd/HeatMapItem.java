package com.pivotal.example.xd;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class HeatMapItem implements Serializable, Comparable{
	private static Logger logger = Logger.getLogger(HeatMapItem.class);
	private String state;
	private int value;
	private String heatMapColor;
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getHeatMapColor() {
		return heatMapColor;
	}
	public void setHeatMapColor(String heatMapColor) {
		this.heatMapColor = heatMapColor;
	}
	
	public int compareTo(Object o) {
		if (!(o instanceof HeatMapItem)) throw new RuntimeException("Trying to compare HeatMapIteam and another obj");
		int compare = Integer.valueOf(getValue()).compareTo(((HeatMapItem)o).getValue());
		if (compare==0) return 1;
		return compare;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	
}
