package com.codenotfound.endpoint;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codenotfound.kafka.KafkaSender;
import com.codenotfound.services.helloworld.HelloWorldPortType;
import com.codenotfound.types.helloworld.Greeting;
import com.codenotfound.types.helloworld.ObjectFactory;
import com.codenotfound.types.helloworld.Person;


@Component
public class HelloWorldImpl implements HelloWorldPortType {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldImpl.class);
    
    @Autowired
    private KafkaSender kafkaSender;
    
    private HashMap<String, String> serviceProps;
    
    public void setProps(HashMap<String, String> props) {
    	this.serviceProps = props;
    	//System.out.println ("Props set");
    }

    
    @Override
    public Greeting sayHello(Person request) {
    	
    	//System.out.println (serviceProps.get("HelloWorldWS.ParamOne"));
    	long tid = Thread.currentThread().getId();
    	
    	if (tid %2 == 0) { 
    		try { 
    			Thread.sleep(30*1000);
    		} catch (Exception e) {
    			
    		}
    	}
    	
    	String paramOne = serviceProps.get("HelloWorldWS.ParamOne");
    	    	
        LOGGER.info(tid + "::Endpoint received person=[firstName:{},lastName:{}]", request.getFirstName(), request.getLastName());

        String greeting = "Hello " + request.getFirstName() + " " + request.getLastName() + " from " + paramOne + "!!!";

        ObjectFactory factory = new ObjectFactory();
        Greeting response = factory.createGreeting();
        response.setGreeting(greeting);

        kafkaSender.sendMessageSync("T1", greeting);
        LOGGER.info(tid + "::Endpoint " + paramOne + " sending greeting=[{}] ", response.getGreeting());
        return response;
    
    }

}