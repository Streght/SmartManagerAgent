package com.smartmanageragent.exteriorcomm;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Maxime on 20/06/2017.
 */
// TODO Clément
public interface InterfaceCommApp {

    // communication Application --> Agent
    abstract void createMeeting(String titre, Calendar dateDebut, Calendar dateFin, String participant);

    abstract void deleteMeeting();

    abstract void modifyMeeting();

    abstract void availability();

    ////////////////////////////////////////////////////////////////////////////////

    // communication Application --> serveur
    abstract List<String> getListId();

    abstract void postIp(String id, String password);

    abstract void updateTable();
}
