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
import com.automationanywhere.commandsdk.model.DataType;




/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg(label = "Consume Message Queue", name = "ConsumeMessageQueue", description = "Consumes a message from a queue", 
icon = "pkg.svg" , group_label="Queue" , node_label = "Consume Message Queue {{sessionName}}|", comment = true ,  text_color = "#dc7ca3" , background_color =  "#dc7ca3" ,
return_type=DataType.STRING  , return_label="Message", return_required=true)

public class ConsumeMessageQueue {
 
    @Sessions
    private Map<String, Object> sessions;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
    
	 
    
    @Execute
    public StringValue action(@Idx(index = "1", type = TEXT) @Pkg(label = "Session Name",  default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName,
    				  @Idx(index = "2", type = TEXT) @Pkg(label = "Queue",  default_value_type = STRING) @NotEmpty String queue,
    				  @Idx(index = "3", type = AttributeType.NUMBER) @Pkg(label = "Time out (ms)",  default_value_type = DataType.NUMBER , description = " A timeout of zero never expires, and the action blocks") @NotEmpty  Number timeout,
    				  @Idx(index = "4", type = AttributeType.TEXT) @Pkg(label = "JMS Filter",  default_value_type = DataType.STRING , description = "Use JMS filter notation")  String filter
    		) throws Exception {
 

    	MQConnection connection  = (MQConnection) this.sessions.get(sessionName);  
    	
    	filter = (filter == null) ? "" : filter;
    	
    	String message = connection.consumeMessageQueue(queue, timeout.intValue(),filter);
    	
    	return new StringValue(message);

    }
 
    
    
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    

    
    
 
    
    
}