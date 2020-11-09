package com.automationanywhere.botcommand.sk;

public enum TriggerType {
	  QUEUE("queue"), TOPIC("topic");
	  private String value;
	  
	  TriggerType(String value) { this.value = value; }
	}