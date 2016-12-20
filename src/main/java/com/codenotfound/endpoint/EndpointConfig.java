package com.codenotfound.endpoint;

import java.util.HashMap;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EndpointConfig {

	@Autowired
    private Bus bus;
	
	@Autowired
	private HelloWorldImpl helloWorldImpl;
	
	@Value("${HelloWorldWS.ParamOne}")
	private String paramOne;
	
	@Value("${HelloWorldWS.ParamTwo}")
	private String paramTwo;
	
	@Bean
	public HashMap<String, String> endpointConfigs() {
		
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("HelloWorldWS.ParamOne", paramOne);
    	props.put("HelloWorldWS.ParamTwo", paramTwo);
    	//System.out.println ("p11=" + paramOne);
    	//System.out.println (props);
    	//System.out.println ("Done endpointConfigs");
		return props;
	
	}
	
    @Bean
    public Endpoint endpoint() {
    	
    	//HelloWorldImpl helloWorldImpl = new HelloWorldImpl();
    	helloWorldImpl.setProps(endpointConfigs());
    	EndpointImpl endpoint = new EndpointImpl(bus, helloWorldImpl);
        endpoint.publish("/helloworld");
        
        return endpoint;
    
    }

}