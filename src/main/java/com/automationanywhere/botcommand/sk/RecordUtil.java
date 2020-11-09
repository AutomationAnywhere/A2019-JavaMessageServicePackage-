package com.automationanywhere.botcommand.sk;


import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcore.api.dto.AttributeType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class RecordUtil
{
  private static final Logger Logger = LogManager.getLogger(RecordUtil.class);
  
  public static Record getRecord( String eventName, String triggerUid, TriggerType triggerType, String queue, String message) {
    Logger.traceEntry();
    Record record = new Record(getSchema(triggerType), getValues(eventName, triggerType, triggerUid,queue,message));
    return (Record)Logger.traceExit(record);
  }
  
  private static List<Schema> getSchema(TriggerType triggerType) {

    List<Schema> schemas = new ArrayList<Schema>();
    Schema triggerTypeSchema = new Schema("triggerType", AttributeType.STRING);
    Schema eventType = new Schema("eventType", AttributeType.STRING);
    Schema uid = new Schema("triggerUid", AttributeType.STRING);
    Schema timeStamp = new Schema("timeStamp", AttributeType.DATETIME);
    Schema fromQueue = new Schema("fromQueue", AttributeType.STRING);
    Schema messageQueue = new Schema("message", AttributeType.STRING);
    schemas.add(triggerTypeSchema);
    schemas.add(eventType);
    schemas.add(uid);
    schemas.add(timeStamp);
    schemas.add(fromQueue);
    schemas.add(messageQueue);
    
    return schemas; 
    
  } 
  
  
  
  private static List<Value> getValues( String eventName, TriggerType triggerType, String triggerUid, String queue, String message) {

    List<Value> values = new ArrayList<Value>();
    StringValue triggerTypeValue = new StringValue(triggerType.toString());
    StringValue eventType = new StringValue(eventName);
    StringValue uid = new StringValue(triggerUid);
    DateTimeValue timeStamp = new DateTimeValue(Instant.now().toString());
    StringValue fromQueue = new StringValue(queue);
    StringValue messageQueue = new StringValue(message);
    values.add(triggerTypeValue);
    values.add(eventType);
    values.add(uid);
    values.add(timeStamp);
    values.add(fromQueue);
    values.add(messageQueue);
    return values;
  }
}
