package com.smartmanageragent.exteriorcomm;

import com.smartmanageragent.smartagent.commands.list.AddActivity;
import com.smartmanageragent.smartagent.commands.list.RemoveActivity;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;
import com.smartmanageragent.smartagent.timeTable.TimeTableImpl;

import java.io.NotSerializableException;
import java.util.Calendar;
import java.util.List;


public class CommApp {

    public static JSONMessage createMeeting(String name, String titre, Calendar dateDebut, Calendar dateFin, float length, List<String> participant) throws NotSerializableException {
        JSONMessage jsMessage = new JSONMessage();

        Activity<Float> activity = new Activity<>(length, TimeTableImpl.unPriority, titre);

        for (String s: participant
             ) {
            activity.addAttendee(s);
        }

        jsMessage.setField(JSONMessage.Fields.ACTIVITY, Serializer.serialize(activity));
        jsMessage.setField(JSONMessage.Fields.SENDER, name);
        jsMessage.setField(JSONMessage.Fields.ADDRESSEES, participant.toString());
        jsMessage.setField(JSONMessage.Fields.COMMAND, AddActivity.class.getName());
        return jsMessage;
    }

    public static JSONMessage deleteMeeting() {
        JSONMessage jsMessage = new JSONMessage();
        jsMessage.setField(JSONMessage.Fields.SENDER, "LOCAL");
        jsMessage.setField(JSONMessage.Fields.COMMAND, RemoveActivity.class.getName()); // TODO je ne sais pas le nom de la commande
        return jsMessage;
    }

    // Commande pas encore implémentée
    public static JSONMessage modifyMeeting() {
        JSONMessage jsMessage = new JSONMessage();
        jsMessage.setField(JSONMessage.Fields.SENDER, "LOCAL");
        jsMessage.setField(JSONMessage.Fields.COMMAND, "JeSAISpAS"); // TODO je ne sais pas le nom de la commande
        return jsMessage;
    }

    public static JSONMessage availability() {
        JSONMessage jsMessage = new JSONMessage();
        jsMessage.setField(JSONMessage.Fields.SENDER, "LOCAL");
        jsMessage.setField(JSONMessage.Fields.COMMAND, "JeSAISpAS"); // TODO je ne sais pas le nom de la commande
        // TODO : ajouter à la message queue de l'agent
        return jsMessage;
    }

    public static List<String> getListId() {
        return SingletonRegisterIDIP.getInstance().getListId();
    }

    public static JSONMessage postIp(String id, String password) {
        JSONMessage request = new JSONMessage();
        request.setField(JSONMessage.Fields.COMMAND, CommunicationService.postIp);
        request.setField(JSONMessage.Fields.ID, id);
        request.setField(JSONMessage.Fields.PASSWORD, password);
        return request;
    }

    public static JSONMessage updateTable() {
        JSONMessage request = new JSONMessage();
        request.setField(JSONMessage.Fields.COMMAND, CommunicationService.updateMap);
        return request;
    }
}
