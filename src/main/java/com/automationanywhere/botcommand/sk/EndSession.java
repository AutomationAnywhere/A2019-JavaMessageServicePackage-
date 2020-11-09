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
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.Sessions;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;

/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg( label = "End Session", name = "EndJMSSession", description = "End Session", icon = "pkg.svg", group_label="Connect" ,node_label = "End Session {{sessionName}}" , comment = true , text_color = "#dc7ca3" , background_color =  "#dc7ca3" )
public class EndSession {


    @Sessions
    private Map<String, Object> sessions;
 
    @Execute
    public void end(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session Name", default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName) throws Exception {
    	
    	MQConnection connection  = (MQConnection) this.sessions.get(sessionName);  
    	connection.closeSession();
        sessions.remove(sessionName);
         
    }
     
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
}