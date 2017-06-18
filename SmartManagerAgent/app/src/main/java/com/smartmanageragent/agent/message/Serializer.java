package message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class Serializer {

	/** Serializes an object and transforms it into a String
	 * @param obj
	 * @return strObj
	 */
	public static String serialize(Serializable obj) throws NotSerializableException {
		String strObj = "";
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream so = new ObjectOutputStream(bo);
			so.writeObject(obj);
			so.flush();
			strObj = encode(bo.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strObj;
	}

	/** Deserializes an object from a String
	 * @param strObj
	 * @return obj
	 */
	public static Serializable deserialize(String strObj) {
		Object obj = null;
		try {
			byte b[] = decode(strObj);
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream si = new ObjectInputStream(bi);
			obj = si.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (Serializable) obj;
	}
	
	/** Encodes a byte array to a String, using Base64
	 * @param byteArray
	 * @return encoded
	 */
	private static String encode(byte[] byteArray) {
		String encoded = Base64.getEncoder().encodeToString(byteArray);
		return encoded;
	}
	
	/** Decodes a byte array from a String
	 * @param string
	 * @return decoded
	 */
	private static byte[] decode(String string) {
		byte[] decoded = Base64.getDecoder().decode(string);
		return decoded;
	}
	
}
