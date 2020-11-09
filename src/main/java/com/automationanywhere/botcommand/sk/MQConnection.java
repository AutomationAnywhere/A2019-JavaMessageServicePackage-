package com.automationanywhere.botcommand.sk;


import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.utils.UUIDGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.jms.JmsConstants;
import com.ibm.msg.client.wmq.WMQConstants;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;

public class MQConnection {
	


	   private Session session;
	   private Connection connection;
	   private TopicSubscriber subscriber;
	   private String subscriberName;
	   private RequestReply replyDetails;
	   
	   public static String TYPE_AMQ = "TYPE_AMQ";
	   public static  String TYPE_WMQ = "TYPE_WMQ";
	   public static  String NOMESSAGE = "#NoM#";


	  
	   
	   public  MQConnection(String host,String username, String password , String type) throws Exception {

		   
		   if (type == TYPE_AMQ) {
			   ActiveMQConnection(host,username, password,Session.AUTO_ACKNOWLEDGE);
		   }

	   }
	   
	   public  MQConnection(String host,String username, String password , String type, int ackmode) throws Exception {

		   
		   if (type == TYPE_AMQ) {
			   ActiveMQConnection(host,username, password,ackmode);
		   }

	   }
	   
	   public  MQConnection(String host,String channel,Integer port,String qMgr,String username,String password,String type) throws Exception {

		   if (type == TYPE_WMQ) {
			   WebsphereMQConnection(host,channel,port,qMgr,username,password,Session.AUTO_ACKNOWLEDGE);
		   }
		   

	   }
	   
	   public  MQConnection(String host,String channel,Integer port,String qMgr,String username,String password,String type,int ackmode) throws Exception {

		   if (type == TYPE_WMQ) {
			   WebsphereMQConnection(host,channel,port,qMgr,username,password,ackmode);
		   }
		   

	   }
	   
	 
	   
	   
	   
	   
	   private void WebsphereMQConnection(String host,String channel,Integer port,String qMgr,String username,String password, int ackmode) throws Exception {

           MQConnectionFactory mqConnectionFactory = new MQConnectionFactory();
           mqConnectionFactory.setHostName(host);
           mqConnectionFactory.setChannel(channel);
           mqConnectionFactory.setPort(port);
       //    mqConnectionFactory.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME,"rpabot");
           mqConnectionFactory.setQueueManager(qMgr);//service provider 
           mqConnectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
           mqConnectionFactory.setBooleanProperty(JmsConstants.USER_AUTHENTICATION_MQCSP ,true);
           mqConnectionFactory.setStringProperty(WMQConstants.USERID, username);
           mqConnectionFactory.setStringProperty(WMQConstants.PASSWORD, password);

           this.connection = mqConnectionFactory.createConnection();
           this.connection.setClientID(UUIDGenerator.getInstance().generateStringUUID());
		   this.session = connection.createSession(false, ackmode);
	   }
	   
	   
	   private void ActiveMQConnection(String host,String username, String password, int ackmode) throws Exception {

			  ConnectionFactory cf = ActiveMQJMSClient.createConnectionFactory(host, UUIDGenerator.getInstance().generateStringUUID());

			   if (username != "" && password != "") {
					  
				   this.connection = cf.createConnection(username,password);
				   
			   }
			   else {
					  
				   this.connection = cf.createConnection();
			   }
	           this.connection.setClientID(UUIDGenerator.getInstance().generateStringUUID());
				  
			   this.session = connection.createSession(false, ackmode);
	   }
	   
	   
	   public void closeSession() throws JMSException {
		   this.connection.close();
	   }
	   
	   public String getNoMessagesCode() {
		   return this.NOMESSAGE;	   }
	   
	   public Session  getSession() {
		   return session;
	   }
	   
	   public Connection  getConnection() {
		   return connection;
	   }
	   
	   
	   public TopicSubscriber getSubscriber() {
		   return this.subscriber;
	   }
	   
	   
	   public String getJMSClientId() throws Exception {
		   return this.connection.getClientID();
	   }
	   
	   public void createSubscriber(String topicname, String subscribername,String filter) throws Exception {
		   Topic topic = session.createTopic(topicname);
		   this.subscriberName = subscribername;
		   if (filter == "" || filter == null) {
			   this.subscriber = session.createDurableSubscriber(topic,this.subscriberName);
		   }
		   else {
			   this.subscriber = session.createDurableSubscriber(topic,this.subscriberName,filter, false);
		   }
	   }
	   
	   
	   
	   public void createListener(String queue,MessageListener listener) throws Exception {
		   Destination destination = session.createQueue(queue);
		   MessageConsumer consumer;
		   consumer = session.createConsumer(destination);
		   consumer.setMessageListener(listener);

		   connection.start();
	   }
	   
	
	   public String consumeMessageQueue(String queue,Integer timeout,String filter) throws Exception {
		   Destination destination = session.createQueue(queue);
		   MessageConsumer consumer;
		   if (filter == "" || filter == null) {
			   consumer = session.createConsumer(destination);
		   }
		   else {
			   consumer = session.createConsumer(destination,filter);
		   }
		   connection.start();
		   TextMessage message = (TextMessage)consumer.receive(timeout);
		   String text = (message == null)  ? NOMESSAGE : message.getText();
		   consumer.close();
		   return text;
	   }
	   
	   
	   
		
	   public boolean browseQueue(String queuename ,String filter) throws Exception {
		   
		   Queue queue = session.createQueue(queuename);
		   QueueBrowser browser = session.createBrowser(queue);
		   return browser.getEnumeration().hasMoreElements();
		   
	   }
	   
	   
	   
	   public void sendMessageQueue(String queue,String text,Map<String,StringValue> properties) throws Exception {

		   MessageProducer producer = session.createProducer(null);
		   TextMessage message =session.createTextMessage(text);
		   if (properties != null) {
			   for (Map.Entry<String, StringValue> property : properties.entrySet()) {
				   message.setStringProperty(property.getKey(),property.getValue().toString());
			   }
		   }
		   producer.setDeliveryMode( DeliveryMode.PERSISTENT);
		   Destination destination = session.createQueue(queue);
		   producer.send(destination,message);
		   producer.close();
	   }
	   
	   
	   public String sendRequestQueue(String requestqueue , String replyqueue, String request, Integer timeout,Map<String,StringValue> properties) throws Exception {

		   MessageProducer producer = session.createProducer(null);
		   TextMessage message =session.createTextMessage(request);
		   if (properties != null) {
			   for (Map.Entry<String, StringValue> property : properties.entrySet()) {
				   message.setStringProperty(property.getKey(),property.getValue().toString());
			   }
		   }
		   producer.setDeliveryMode( DeliveryMode.PERSISTENT);
		   Destination destination = session.createQueue(requestqueue);
		   Destination replyDestination = session.createQueue(replyqueue);
		   message.setJMSReplyTo(replyDestination);
		   String correlatioID = Long.toHexString(new Random(System.currentTimeMillis()).nextLong());
		   message.setJMSCorrelationID(correlatioID);
		   producer.send(destination,message);
		   producer.close();
		   String filter = "JMSCorrelationID = '" + correlatioID   + "'";
		   MessageConsumer consumer = session.createConsumer(replyDestination, filter);
		   connection.start();
	       TextMessage replyMessage = (TextMessage)consumer.receive(timeout);
		   String text = (replyMessage == null)  ? NOMESSAGE : replyMessage.getText();
		   return text;
		   
	   }
	   
	   
	   public String getRequestQueue(String requestqueue ,  Integer timeout, String filter) throws Exception {

		   Destination destination = session.createQueue(requestqueue);
		   MessageConsumer consumer;
		   if (filter == "" || filter == null) {
			   consumer = session.createConsumer(destination);
		   }
		   else {
			   consumer = session.createConsumer(destination,filter);
		   }
		   connection.start();
		   TextMessage requestmessage = (TextMessage)consumer.receive(timeout);
		   String text = (requestmessage == null)  ? NOMESSAGE : requestmessage.getText();
		   if (!text.equals(NOMESSAGE)) {
			   this.replyDetails = new RequestReply(requestmessage.getJMSCorrelationID(),requestmessage.getJMSReplyTo());
			   
		   }
		   consumer.close();
		   
		   return text;

	   }
	   
	   public void replyQueue(String reply) throws Exception {
		   if (this.replyDetails != null) {
			   TextMessage replymessage =session.createTextMessage(reply);
			   replymessage.setJMSDestination(this.replyDetails.getReplyQueue());
			   replymessage.setJMSCorrelationID(this.replyDetails.getCorrelationID());
			   MessageProducer producer = session.createProducer(this.replyDetails.getReplyQueue());
			   producer.send(replymessage);
			   producer.close();
			   this.replyDetails = null;
		   }
	   }

		 	   
	   
	   public void sendMessageTopic(String topicname,String text, Map<String,StringValue> properties) throws Exception {

		   Topic topic = session.createTopic(topicname);
		   MessageProducer producer = session.createProducer(topic);
		   producer.setDeliveryMode( DeliveryMode.PERSISTENT);
		   TextMessage message =session.createTextMessage(text);
		   if (properties != null) {
			   for (Map.Entry<String, StringValue> property : properties.entrySet()) {
				   message.setStringProperty(property.getKey(),property.getValue().toString());
			   }
		   }
		   producer.send(message);
		   producer.close();
	   }
	   
	   public String consumeMessageTopic(Integer timeout) throws Exception  {

		   String text="";
		   
		   if ( this.subscriber != null) {
		   		connection.start();
		   		TextMessage message = (TextMessage)this.subscriber.receive(timeout);
		   		text = (message == null)  ? NOMESSAGE : message.getText();
		   }
		   
		   	return text;
	   }
	   
	   
	   public void unsubscribeTopic() throws Exception {
		   if (this.subscriberName != "" && this.subscriberName != null) {
		   	 subscriber.close();
		     this.session.unsubscribe(this.subscriberName);
		   }
	   }
	
	   
	   public Integer purgeQueue(String queue) throws Exception {
		   
		   boolean purged = false;
		   Integer counter =0;

		   while (!purged) {
			   if (consumeMessageQueue(queue,100,"").equals(NOMESSAGE) ) {
				   purged = true;
			   }
			   else {
				   counter++;
			   }
		   }
		   
		   return counter;
	   }
	  


	
	
	private class RequestReply {
		private String correlationID;
		private Destination replyqueue;
		
		public RequestReply(String ID, Destination queue) {
			this.correlationID = ID;
			this.replyqueue = queue;
		}
		
		public String getCorrelationID() {
			return correlationID;
		}
		
		public Destination getReplyQueue() {
			return replyqueue;
		}
	}
	
}
