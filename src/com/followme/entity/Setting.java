package com.followme.entity;

public class Setting {
	
	private String idSetting;
	private String value;
	
	public Setting(String idSetting, String value) {
		this.idSetting = idSetting;
		this.value = value;
	}
	public String getIdSetting() {
		return idSetting;
	}
	public void setIdSetting(String idSetting) {
		this.idSetting = idSetting;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
