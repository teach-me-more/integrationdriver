package com.rbasystems.api.validator;

import java.io.Serializable;

public class FieldTypeEntity implements Serializable{
private static final long serialVersionUID = 1L;
private String name;
private String dataType;
private String description;
private String scoped;
private String parentObject;

public FieldTypeEntity(String name) {
	super();
	this.name = name;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getDataType() {
	return dataType;
}
public void setDataType(String dataType) {
	this.dataType = dataType;
}
public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}
public String getScoped() {
	return scoped;
}
public void setScoped(String scoped) {
	this.scoped = scoped;
}
public String getParentObject() {
	return parentObject;
}
public void setParentObject(String parentObject) {
	this.parentObject = parentObject;
}


}
