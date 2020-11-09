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

import com.automationanywhere.bot.service.GlobalSessionContext;

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
import com.automationanywhere.commandsdk.model.DataType;




/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg(label = "Create Durable Subscription", name = "CreateDurableSunscription", description = "Create durable subscription for a topic", 
icon = "pkg.svg" , group_label="Topic" , node_label = "Create Durable Subscription {{sessionName}}|", comment = true , text_color = "#dc7ca3" , background_color =  "#dc7ca3" ) 
public class CreateSubscription {
 
    @Sessions
    private Map<String, Object> sessions;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
    
	 
    
    @Execute
    public void action(@Idx(index = "1", type = TEXT) @Pkg(label = "Session Name",  default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName,
    				  @Idx(index = "2", type = TEXT) @Pkg(label = "Topic",  default_value_type = STRING) @NotEmpty String topic,
    				  @Idx(index = "3", type = TEXT) @Pkg(label = "Subscriber Name",  default_value_type = STRING) @NotEmpty String subscriber,
      				  @Idx(index = "4", type = AttributeType.TEXT) @Pkg(label = "JMS Filter",  default_value_type = DataType.STRING , description = "Use JMS filter notation")  String filter

    		) throws Exception {
 

    	MQConnection connection  = (MQConnection) this.sessions.get(sessionName);  
    	
    	filter = (filter == null) ? "" : filter;
    	
    	connection.createSubscriber(topic, subscriber,filter);

    }
 
    
    
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    

    
    
 
    
    
}