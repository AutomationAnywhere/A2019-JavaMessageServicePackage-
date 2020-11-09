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
import com.automationanywhere.core.security.SecureString;




/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg(label = "Start ActiveMQ Session", name = "StartAMQSession", description = "Start ActiveMQ session", 
icon = "pkg.svg", comment = true ,  text_color = "#dc7ca3" , background_color =  "#dc7ca3" ,  group_label="Connect" ,node_label = "Start AMQSession {{sessionName}}|") 
public class StartAMQSession {
 
    @Sessions
    private Map<String, Object> sessions;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
    
	  
	@com.automationanywhere.commandsdk.annotations.GlobalSessionContext
	private GlobalSessionContext globalSessionContext;

	  
	  public void setGlobalSessionContext(GlobalSessionContext globalSessionContext) {
	        this.globalSessionContext = globalSessionContext;
	    }
	  
	  
	private MQConnection connection;
	
    
    @Execute
    public void start(@Idx(index = "1", type = TEXT) @Pkg(label = "Session Name",  default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName,
    				  @Idx(index = "2", type = TEXT) @Pkg(label = "ActiveMQ Host",  default_value_type = STRING) @NotEmpty String host,
    				  @Idx(index = "3", type = AttributeType.CREDENTIAL) @Pkg(label = "User",  default_value_type = DataType.STRING)  SecureString user,
    				  @Idx(index = "4", type = AttributeType.CREDENTIAL) @Pkg(label = "Password",  default_value_type = DataType.STRING) SecureString pw
    		) throws Exception {
 
        // Check for existing session
        if (sessions.containsKey(sessionName))
            throw new BotCommandException(MESSAGES.getString("Session name in use")) ;
        
        // Create a ConnectionFactory
        String username = (user == null) ? "" : user.getInsecureString();
        String password = (pw == null) ? "" : pw.getInsecureString();
        connection = new MQConnection(host,username,password,MQConnection.TYPE_AMQ);
        this.sessions.put(sessionName, this.connection);
        

    }
 
    
    
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    

    
    
 
    
    
}