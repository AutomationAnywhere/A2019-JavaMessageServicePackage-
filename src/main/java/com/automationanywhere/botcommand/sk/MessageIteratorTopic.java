/*
 * Copyright (c) 2019 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */
/**
 * 
 */
package com.automationanywhere.botcommand.sk;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand.CommandType;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.HasNext;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Inject;
import com.automationanywhere.commandsdk.annotations.Next;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.Sessions;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;




/**
 * @author Stefan Karsten
 *
 */

@BotCommand(commandType=CommandType.Iterator)
@CommandPkg(return_label = "Received message" , return_required=true , node_label = "Read All Topic Messages", 
label = "Topic Message Reader", description = "Reads all messages from a topic", name = "iteratormessagetopic", return_type = DataType.STRING, 
icon = "pkg.svg" , group_label="Queue" , comment = true ,  text_color = "#dc7ca3" , background_color =  "#dc7ca3" )
public class MessageIteratorTopic {
	@Idx(index = "1", type = TEXT) @Pkg(label = "Session Name",  default_value_type = STRING, default_value = "Default") @Inject @NotEmpty 
	private String sessionName;

	  private static final Logger logger = LogManager.getLogger(MessageIteratorTopic.class);
    
    @Sessions
	private Map<String, Object> sessionMap;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
    
    private String message = "";
    
    private MQConnection connection;
    
    @HasNext
    public boolean hasNext() throws Exception {
    	if (this.connection == null) {
        	this.connection  = (MQConnection) this.sessionMap.get(sessionName);  
    	}
    	this.message = this.connection.consumeMessageTopic(1000);
		return !this.message.equals(MQConnection.NOMESSAGE);
    }

    @Next
    public StringValue next() throws Exception{
        return new StringValue(this.message);
    }

    public void setSessionName(String sessionname) {
    	this.sessionName = sessionname;
    }
    
	 
	 

    
	// Ensure that a public setter exists.
	public void setSessionMap(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}
    

    
    
 
    
    
}