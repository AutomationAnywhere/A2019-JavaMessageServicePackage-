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

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
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
@CommandPkg(label = "Send Request", name = "SendRequestQueue", description = "Sends request to queue", 
icon = "pkg.svg" , group_label="Request/Reply",   node_label = "Send Request {{sessionName}}|", comment = true , text_color = "#dc7ca3" , background_color =  "#dc7ca3" ,
return_type=DataType.STRING  , return_label="Reply", return_required=true) 

public class SendRequestMessageQueue {
 
    @Sessions
    private Map<String, Object> sessions;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
    
	 
    
    @Execute
    public StringValue action(@Idx(index = "1", type = TEXT) @Pkg(label = "Session Name",  default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName,
    				  @Idx(index = "2", type = TEXT) @Pkg(label = "Request Queue",  default_value_type = STRING) @NotEmpty String requestqueue,
    				  @Idx(index = "3", type = TEXT) @Pkg(label = "Reply Queue",  default_value_type = STRING) @NotEmpty String replyqueue,
    				  @Idx(index = "4", type = TEXT) @Pkg(label = "Request",  default_value_type = STRING) @NotEmpty String message,
    				  @Idx(index = "5", type = AttributeType.NUMBER) @Pkg(label = "Time out (ms)",  default_value_type = DataType.NUMBER , description = " A timeout of zero never expires, and the action blocks") @NotEmpty  Number timeout,
       				  @Idx(index = "6", type = AttributeType.DICTIONARY) @Pkg(label = "JMS Properties",  default_value_type = DICTIONARY  )  Map<String, StringValue> properties

    		) throws Exception {
 

    	MQConnection connection  = (MQConnection) this.sessions.get(sessionName);  
    	
    	properties = (properties == null) ? new HashMap<String,StringValue>() : properties ;
    	
    	String reply = connection.sendRequestQueue(requestqueue, replyqueue, message, timeout.intValue(),properties);
    	
    	return (new StringValue(reply));

    }
 
    
    
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    

    
    
 
    
    
}