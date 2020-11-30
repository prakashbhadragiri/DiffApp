package xyz.foobar;

public class Differences {

    private String fieldName;
    private Object value;
    private boolean rootElem;
    private int level;
    private String type;
    private Object prevValue;
    private String action;

    public Differences(String action, String fieldName, String type, int level, boolean rootElem) {
        this.rootElem = rootElem;
        this.level = level;
        this.type = type;
        this.action = action;
        this.fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1);
    }

    public Differences(String action, String fieldName, String type, Object prevValue, Object value, int level, boolean rootElem) {
        this.rootElem = rootElem;
        this.level = level;
        this.value = value;
        this.type = type;
        this.prevValue = prevValue;
        this.action = action;
        this.fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1);
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isRootElem() {
        return rootElem;
    }

    public void setRootElem(boolean rootElem) {
        this.rootElem = rootElem;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getPrevValue() {
        return prevValue;
    }

    public void setPrevValue(Object prevValue) {
        this.prevValue = prevValue;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
