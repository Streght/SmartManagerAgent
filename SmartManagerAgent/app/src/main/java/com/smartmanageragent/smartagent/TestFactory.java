package com.smartmanageragent.smartagent;

import java.util.Date;

import commands.Command;
import commands.CommandFactory;
import commands.addActivity.AcceptSlot;
import message.JSONMessage;
import message.JSONMessage.Fields;

public class TestFactory {

	public static void main(String[] args) {
		CommandFactory<Date, Float, String> factory = new CommandFactory<>();
		JSONMessage mess = new JSONMessage();
		mess.setField(Fields.COMMAND, AcceptSlot.class.getName());
		Command<Date, Float, String> cmd = factory.createCommand(mess, null);
		// Exception caused because message not correctly initialized, normal behaviour...
		System.out.println(cmd);
	}
	
}
