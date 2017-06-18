package message;

import org.json.JSONObject;

public class JSONMessage extends Message<String> {

	public static final Object defaultValue = "";
	private JSONObject jsonObj = new JSONObject();
	
	/** Message fields
	 */
	public enum Fields {
		SENDER,
		ADDRESSEES,
		COMMAND,
		TIMETABLE,
		ACTIVITY
	}
	
	/** Creates an empty JSON message
	 */
	public JSONMessage() {
		super("");
		this.jsonObj = new JSONObject();
		// Initializes all the message fields with default value
		for(Fields field : Fields.values()) {
			jsonObj.append(field.toString(), defaultValue);
		}
		this.content = jsonObj.toString();
	}
	
	/** Creates a JSON message from a JSON String
	 * @param jsonString
	 */
	public JSONMessage(String jsonString) {
		super("");
		this.jsonObj = new JSONObject(jsonString);
		this.content = this.jsonObj.toString();
	}
	
	/** Sets a field of the JSON message
	 * @param key
	 * @param value
	 */
	public void setField(Fields field, String value) {
		this.jsonObj.put(field.toString(), value);
		this.content = jsonObj.toString();
	}

	/** Returns the string value associated to a given key
	 * @param key
	 * @return value
	 */
	public String getField(Fields field) {
		return this.jsonObj.getString(field.toString());
	}
	
}
