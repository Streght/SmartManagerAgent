package com.smartmanageragent.exteriorCommunication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class SingletonRegisterIDIP {

    private static SingletonRegisterIDIP instance;
    private HashMap<String, String> mapIdIp;

    public static synchronized SingletonRegisterIDIP getInstance()
    {
        if (instance == null)
        { 	instance = new SingletonRegisterIDIP();
        }
        return instance;
    }

    private SingletonRegisterIDIP() {
        mapIdIp = new HashMap<String, String>();
    }

    public String getIp (String id) {
        return mapIdIp.get(id);
    }

    /**
     * Update the whole map
     * @param jsonArray
     */
    public void updateAll(JSONArray jsonArray) {
        mapIdIp.clear();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject js = jsonArray.getJSONObject(i);
                mapIdIp.put((String)js.get("username"), "TestEnAttendant");
                // mapIdIp.put((String)js.get("username"), (String) js.get("ip"))
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return List of all users Id currently registered
     */
    public ArrayList<String> getListId () {
        final ArrayList<String> listId = new ArrayList<String>();
        Iterator it = mapIdIp.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            listId.add((String)pair.getKey());
        }
        return listId;
    }
}
