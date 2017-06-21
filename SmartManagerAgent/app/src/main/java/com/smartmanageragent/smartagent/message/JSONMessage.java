package com.smartmanageragent.smartagent.message;

import org.json.JSONObject;

public class JSONMessage extends Message<String> {

    public static final Object defaultValue = "";
    public static final String localAddressee = "LOCAL";
    private static final String commandField = Fields.COMMAND.toString();

    private JSONObject jsonObj = new JSONObject();

    /**
     * Message fields
     */
    public enum Fields {
        SENDER,
        ADDRESSEES,
        COMMAND,
        TIMETABLE,
        ACTIVITY,
        SLOT,
        PASSWORD,
        ID
    }

    /**
     * Creates an empty JSON message
     */
    public JSONMessage() {
        super("");
        try {
            this.jsonObj = new JSONObject();
            // Initializes all the message fields with default value
            for (Fields field : Fields.values()) {
                jsonObj.put(field.toString(), defaultValue);
            }
            this.content = jsonObj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            this.jsonObj = null;
            this.content = "";
        }
    }

    /**
     * Creates a JSON message from a JSON String
     *
     * @param jsonString
     */
    public JSONMessage(String jsonString) {
        super("");
        try {
            this.jsonObj = new JSONObject(jsonString);
            this.content = this.jsonObj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets a field of the JSON message
     *
     * @param field
     * @param value
     */
    public void setField(Fields field, String value) {
        try {
            this.jsonObj.put(field.toString(), value);
            this.content = jsonObj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the string value associated to a given key
     *
     * @param field
     * @return value
     */
    public String getField(Fields field) {
        try {
            return this.jsonObj.getString(field.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getCommandName() {
        try {
            return this.jsonObj.getString(commandField);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
