package com.smartmanageragent.exteriorcomm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class SingletonMapIDIP {

    private static SingletonMapIDIP instance;
    private ConcurrentHashMap<String, String> mapIdIp;

    public static synchronized SingletonMapIDIP getInstance()
    {
        if (instance == null)
        { 	instance = new SingletonMapIDIP();
        }
        return instance;
    }

    private SingletonMapIDIP() {
        mapIdIp = new ConcurrentHashMap<String, String>();
    }

    String getIp (String id) {
        return mapIdIp.get(id);
    }


    public void updateUser (String username, String ip) {
        mapIdIp.put(username, ip);
    }
    /**
     * Update the whole map
     * @param jsonArray
     */
     void updateAll(JSONArray jsonArray) {
        mapIdIp.clear();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject js = jsonArray.getJSONObject(i);
                //mapIdIp.put((String)js.get("username"), "TestEnAttendant");
                 mapIdIp.put((String)js.get("username"), (String) js.get("ip"));
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
        for (Object o : mapIdIp.entrySet()) {
            HashMap.Entry pair = (HashMap.Entry) o;
            listId.add((String) pair.getKey());
        }
        return listId;
    }
}
