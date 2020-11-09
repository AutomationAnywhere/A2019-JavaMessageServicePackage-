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
import static com.automationanywhere.commandsdk.model.DataType.DICTIONARY;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import java.util.HashMap;
import java.util.Map;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.Sessions;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.commandsdk.model.AttributeType;




/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg(label = "Send Message Topic", name = "SendMessageTopic", description = "Sends a message to topic", 
icon = "pkg.svg" , group_label="Topic" , node_label = "Send Message Topic {{sessionName}}|", comment = true , text_color = "#dc7ca3" , background_color =  "#dc7ca3" ) 
public class SendMessageTopic {
 
    @Sessions
    private Map<String, Object> sessions;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
    
	 
    
    @Execute
    public void action(@Idx(index = "1", type = TEXT) @Pkg(label = "Session Name",  default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName,
    				  @Idx(index = "2", type = TEXT) @Pkg(label = "Topic",  default_value_type = STRING) @NotEmpty String topic,
    				  @Idx(index = "3", type = TEXT) @Pkg(label = "Message",  default_value_type = STRING) @NotEmpty String message,
    				  @Idx(index = "4", type = AttributeType.DICTIONARY) @Pkg(label = "JMS Properties",  default_value_type = DICTIONARY  )  Map<String, StringValue> properties

    		) throws Exception {
 

    	MQConnection connection  = (MQConnection) this.sessions.get(sessionName);  
    	
    	properties = (properties == null) ? new HashMap<String,StringValue>() : properties ;
    	
    	connection.sendMessageTopic(topic, message,properties);

    }
 
    
    
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    

    
    
 
    
    
}