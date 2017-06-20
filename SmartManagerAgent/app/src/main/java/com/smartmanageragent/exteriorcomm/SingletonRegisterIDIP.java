package com.smartmanageragent.exteriorcomm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


public class SingletonRegisterIDIP {

    private static SingletonRegisterIDIP instance;
    private ConcurrentHashMap<String, String> mapIdIp;

    public static synchronized SingletonRegisterIDIP getInstance()
    {
        if (instance == null)
        { 	instance = new SingletonRegisterIDIP();
        }
        return instance;
    }

    private SingletonRegisterIDIP() {
        mapIdIp = new ConcurrentHashMap<String, String>();
    }

    public String getIp (String id) {
        return mapIdIp.get(id);
    }


    public void updateUser (String username, String ip) {
        mapIdIp.put(username, ip);
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
        Iterator it = mapIdIp.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            listId.add((String)pair.getKey());
        }
        return listId;
    }
}
