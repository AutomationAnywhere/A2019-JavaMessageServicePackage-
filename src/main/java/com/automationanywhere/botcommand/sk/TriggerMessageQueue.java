/*
 * Copyright (c) 2020 Automation Anywhere.
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
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automationanywhere.bot.service.TriggerException;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.StartListen;
import com.automationanywhere.commandsdk.annotations.StopAllTriggers;
import com.automationanywhere.commandsdk.annotations.StopListen;
import com.automationanywhere.commandsdk.annotations.TriggerCallable;
import com.automationanywhere.commandsdk.annotations.TriggerId;
import com.automationanywhere.commandsdk.annotations.TriggerRunnable;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import com.automationanywhere.core.security.SecureString;
import com.automationanywhere.toolchain.runtime.Trigger3;
import com.automationanywhere.toolchain.runtime.Trigger3ListenerContext;
import java.util.function.Consumer;


/**
 * 
 * 
 * @author Stefan Karsten
 *
 */


@BotCommand(commandType = BotCommand.CommandType.Trigger)
@CommandPkg(label = "Queue Trigger", description = "Queue Trigger", icon = "pkg.svg", name = "QueueTrigger" , group_label="Queue" , comment = true ,  text_color = "#dc7ca3" , background_color =  "#dc7ca3",
return_type=DataType.RECORD  ,return_label="Message", return_required=true )
public class TriggerMessageQueue implements MessageListener,Trigger3 {

	// Map storing multiple MessageListenerContainer
	private static final Map<String, MQConnection> taskMap = new ConcurrentHashMap<>();
	

	  private static final Logger logger = LogManager.getLogger(TriggerMessageQueue.class);
		
	
	Message message ;
	String queueName;

	@TriggerId
	private String triggerUid;
	
	@TriggerRunnable
	private Runnable runnable;
	

	private Consumer callable;
		
	

	

	public void startTrigger( @Idx(index = "1", type = TEXT) @Pkg(label = "ActiveMQ Host",  default_value_type = STRING) @NotEmpty String host,
							  @Idx(index = "2", type = AttributeType.CREDENTIAL) @Pkg(label = "User",  default_value_type = DataType.STRING)  SecureString user,
							  @Idx(index = "3", type = AttributeType.CREDENTIAL) @Pkg(label = "Password",  default_value_type = DataType.STRING) SecureString pw,
		    				  @Idx(index = "4", type = TEXT) @Pkg(label = "Queue",  default_value_type = STRING) @NotEmpty String queue) throws Exception {
		
		if (taskMap.get(triggerUid) == null) {
		//	synchronized (this) {
				if (taskMap.get(triggerUid) == null) {
					this.queueName = queue;
					logger.info("Queue "+queue);
			        String username = (user == null) ? "" : user.getInsecureString();
			    	logger.info("User "+username);
			        String password = (pw == null) ? "" : pw.getInsecureString();
			        MQConnection connection = new MQConnection(host,"admin","admin",MQConnection.TYPE_AMQ);
			        connection.createListener(queue, this);
			        logger.info("Stop Create");
					taskMap.put(triggerUid, connection);

				}
			}
		//}
		
	

	}


	/*
	 * Cancel all the task and clear the map.
	 */
	@StopAllTriggers
	public void stopAllTriggers() {
		taskMap.forEach((k, v) -> {
			try {
				v.closeSession();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			taskMap.remove(k);
			
			logger.info("Stop All Trigger ");
		});
		
	

	}

	/*
	 * Cancel the task and remove from map
	 *
	 * @param triggerUid
	 */
	@StopListen
	public void stopListen(String triggerUid) {
		try {
			taskMap.get(triggerUid).closeSession();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			logger.info("Stop Listen : " +e.getMessage());
		}
		taskMap.remove(triggerUid);
		logger.info("	");
	}

	public String getTriggerUid() {
		return triggerUid;
	}

	public void setTriggerUid(String triggerUid) {
		this.triggerUid = triggerUid;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public void setRunnable(Runnable runnable) {
     //   logger.info("CR URL "+this.globalSessionContext.getTriggerUid());
		this.runnable = runnable;
	}

	@Override
	public void onMessage(Message message) {
		TextMessage textmsg = (TextMessage)message;
		
		RecordValue record = new RecordValue();
		try {
			record.set(RecordUtil.getRecord( "message received", triggerUid, TriggerType.QUEUE,queueName,textmsg.getText()));
			logger.info("Record set ");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			logger.info("On Message : "+e.getMessage());
		}
		callable.accept(record);
		//runnable.run();

	}


	@StartListen
	public void startListen(Trigger3ListenerContext context, Map<String, Value> arg1) throws TriggerException {
		// TODO Auto-generated method stub
		logger.info("Start Listen");
	 Consumer callable = context.getEventCallback();
		
	}




}
