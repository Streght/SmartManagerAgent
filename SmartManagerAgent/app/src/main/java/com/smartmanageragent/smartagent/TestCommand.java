package com.smartmanageragent.smartagent;

import java.util.Date;

import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.commands.CommandFactory;
import com.smartmanageragent.smartagent.commands.list.NotifyAccept;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;

public class TestCommand {

	public static void main(String[] args) {
		CommandFactory<Date, Float, String> factory = new CommandFactory<>();
		JSONMessage mess = new JSONMessage();
		mess.setField(Fields.COMMAND, NotifyAccept.class.getName());
		Command<Date, Float, String> cmd = factory.createCommand(mess, null);
		// Exception caused because message not correctly initialized, not this test purpose
		System.out.println(cmd);
	}
	
}
