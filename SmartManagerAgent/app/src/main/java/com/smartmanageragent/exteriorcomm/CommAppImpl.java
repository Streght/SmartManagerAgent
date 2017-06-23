package com.smartmanageragent.exteriorcomm;

import com.smartmanageragent.smartagent.commands.list.AddActivity;
import com.smartmanageragent.smartagent.commands.list.RemoveActivity;
import com.smartmanageragent.smartagent.message.JSONMessage;

import java.util.Calendar;
import java.util.List;



public class CommAppImpl implements InterfaceCommApp {


    @Override
    public JSONMessage createMeeting(String titre, Calendar dateDebut, Calendar dateFin, String participant) {
        JSONMessage jsMessage = new JSONMessage();
        jsMessage.setField(JSONMessage.Fields.SENDER, "LOCAL");
        jsMessage.setField(JSONMessage.Fields.ADDRESSEES, participant);
        jsMessage.setField(JSONMessage.Fields.COMMAND, AddActivity.class.getName()); // TODO je ne sais pas le nom de la commande
        return jsMessage;
    }

    @Override
    public JSONMessage deleteMeeting() {
        JSONMessage jsMessage = new JSONMessage();
        jsMessage.setField(JSONMessage.Fields.SENDER, "LOCAL");
        jsMessage.setField(JSONMessage.Fields.COMMAND, RemoveActivity.class.getName()); // TODO je ne sais pas le nom de la commande
        return jsMessage;
    }

    // Commande pas encore implémentée
    @Override
    public JSONMessage modifyMeeting() {
        JSONMessage jsMessage = new JSONMessage();
        jsMessage.setField(JSONMessage.Fields.SENDER, "LOCAL");
        jsMessage.setField(JSONMessage.Fields.COMMAND, "JeSAISpAS"); // TODO je ne sais pas le nom de la commande
        return jsMessage;
    }

    @Override
    public JSONMessage availability() {
        JSONMessage jsMessage = new JSONMessage();
        jsMessage.setField(JSONMessage.Fields.SENDER, "LOCAL");
        jsMessage.setField(JSONMessage.Fields.COMMAND, "JeSAISpAS"); // TODO je ne sais pas le nom de la commande
        // TODO : ajouter à la message queue de l'agent
        return jsMessage;
    }

    @Override
    public List<String> getListId() {
        return SingletonRegisterIDIP.getInstance().getListId();
    }

    @Override
    public JSONMessage postIp(String id, String password) {
        JSONMessage request = new JSONMessage();
        request.setField(JSONMessage.Fields.COMMAND, MyService.postIp);
        request.setField(JSONMessage.Fields.ID, id);
        request.setField(JSONMessage.Fields.PASSWORD, password);
        return request;
    }

    @Override
    public JSONMessage updateTable() {
        JSONMessage request = new JSONMessage();
        request.setField(JSONMessage.Fields.COMMAND, MyService.updateMap);
        return request;
    }
}
