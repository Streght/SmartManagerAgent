package com.smartmanageragent.smartagent.commands.list;

import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.Message;

public class RemoveActivity<K, T> extends Command<K, T, String> {

    public RemoveActivity(Message<String> message, Agent<K, T, String> agent) {
        super(message, agent);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute() {
        // TODO Auto-generated method stub
        return false;
    }

}
