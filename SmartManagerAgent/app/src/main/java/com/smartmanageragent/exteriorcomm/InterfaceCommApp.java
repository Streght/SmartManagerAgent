package com.smartmanageragent.exteriorcomm;

import com.smartmanageragent.smartagent.message.JSONMessage;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Maxime on 20/06/2017.
 */
// TODO Clément
public interface InterfaceCommApp {

    // communication Application --> Agent

    // Renvoie le JSONMessage a placer dans la MessageQueue receive de l'agent pour creer un meeting
    abstract JSONMessage createMeeting(String titre, Calendar dateDebut, Calendar dateFin, String participant);

    // Renvoie le JSONMessage a placer dans la MessageQueue receive de l'agent pour supprimer un meeting
    abstract JSONMessage deleteMeeting();

    // Renvoie le JSONMessage a placer dans la MessageQueue receive de l'agent pour modifier un meeting
    abstract JSONMessage modifyMeeting();

    // Renvoie le JSONMessage a placer dans la MessageQueue receive de l'agent pour signaler sa disponibilite
    abstract JSONMessage availability();

    ////////////////////////////////////////////////////////////////////////////////

    // communication Application --> serveur
    // Recupère la liste des utilisateurs enregistres sur le serveur internet
    abstract List<String> getListId();

    // demande au client de s'enregistrer auprès du serveur
    abstract JSONMessage postIp(String id, String password);

    abstract JSONMessage updateTable();
}
