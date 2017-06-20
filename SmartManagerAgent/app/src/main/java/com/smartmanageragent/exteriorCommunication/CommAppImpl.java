package com.smartmanageragent.exteriorCommunication;

import com.smartmanageragent.smartagent.message.JSONMessage;

import org.json.JSONException;

import java.util.Calendar;
import java.util.List;


// TODO: Vérifier avec Clément. Appeler la messageQueue
public class CommAppImpl implements InterfaceCommApp {


    @Override
    public void createMeeting(String titre, Calendar dateDebut, Calendar dateFin, String participant) {
        JSONMessage jsMessage = new JSONMessage();

        try {
            jsMessage.setField(JSONMessage.Fields.SENDER, "LOCAL");
            jsMessage.setField(JSONMessage.Fields.ADDRESSEES, participant);
            jsMessage.setField(JSONMessage.Fields.COMMAND, "JeSAISpAS"); // TODO je ne sais pas le nom de la commande
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMeeting() {

    }

    @Override
    public void modifyMeeting() {

    }

    @Override
    public void availability() {

    }

    @Override
    public List<String> getListId() {
        return SingletonRegisterIDIP.getInstance().getListId();
    }

    @Override
    public void postIp(String id, String password) {
        JSONMessage request = new JSONMessage();
        try {
            request.setField(JSONMessage.Fields.ACTIVITY, "POSTIP");
            request.setField(JSONMessage.Fields.ID, id);
            request.setField(JSONMessage.Fields.PASSWORD, password);
            // TODO : send to message QUEUE
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTable() {
        JSONMessage request = new JSONMessage();
        try {
            request.setField(JSONMessage.Fields.ACTIVITY, "UPDATETABLE");
            // TODO : send to message QUEUE
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
