package com.smartmanageragent.exteriorCommunication;

import java.util.HashMap;

/**
 * Created by Maxime on 18/06/2017.
 */

public class RegisterIDIP {

    private HashMap<String, String> mapId2Ip;

    public RegisterIDIP() {
        mapId2Ip = new HashMap<String, String>();
    }

    public String getIp (String id) {
        return mapId2Ip.get(id);
    }
}
