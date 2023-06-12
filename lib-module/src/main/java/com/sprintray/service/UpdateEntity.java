package com.sprintray.service;

public class UpdateEntity {

    private String key;

    private Object value;

    private Object subValue;

    public UpdateEntity(String key) {
        this.key = key;
    }

    public UpdateEntity(String key, Object value) {
        this.key = key;
        this.value = value;
    }


    public UpdateEntity(String key, Object value, Object subValue) {
        this.key = key;
        this.value = value;
        this.subValue = subValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getSubValue() {
        return subValue;
    }

    public void setSubValue(Object subValue) {
        this.subValue = subValue;
    }
}
