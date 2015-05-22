package egbertc.flightplan;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.json.JSONObject;

public class FlightCenter {
	
	private CamelContext context = new DefaultCamelContext();
	private ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:62012");
	private Map<String, JSONObject> stations = new HashMap<String, JSONObject>();
	
	public FlightCenter()
	{
		setupStationWatch();
	}
	
	public JSONObject getWeather(String sID)
	{
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		System.out.println("Getting Weather: " + sID);
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		
		
		/*
		 * 
		 * Sloppy work around to get key.
		 * just using the sID as the key returns null
		 */
		Set<String> keys = stations.keySet();
		Iterator<String> i = keys.iterator();
		Object key = null;
		while(i.hasNext())
		{
			Object k = i.next();
			if(k.toString().contains(sID))
			{
				key = k;
			}
		}
				
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		System.out.println("JSON : " + stations.get(key));
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		
		JSONObject wxReturn = stations.get(key);
				
		return wxReturn;//stations.get(sID);
	}

	private void setupStationWatch()
	{
		context.addComponent("jms",JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
	    
        // add our route to the CamelContext
		try {
			context.addRoutes(new RouteBuilder() {
			public void configure() {
			    
				from("jms:MPCS_WeatherRegister").
			    log("RECEIVED: NEW STATION: ${body}").
			    process(processRequest()).
			    to("jms:MPCS_51050_FINAL_LOG");
  
			    
   
			    try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			});
		} catch (Exception e) {
			
			//e.printStackTrace();
		}

	    // start the route and let it do its work
	    try
	    {
			context.start();
		}
	    catch (Exception e)
	    {
			//e.printStackTrace();
		}	
	}
	
	private Processor processRequest()
	{
		Processor p = new Processor()
		{
        	public void process(Exchange e) throws Exception
        	{
        		String newStationID = e.getIn().getBody(String.class);
        		if(newStationID != null && newStationID.length() == 4)
        		{
        			addStation(newStationID);
        			
        		}
        	}
        };
        return p;
	}
	
	private void addStation(final String stationID)
	{
		
		try {
			context.addRoutes(new RouteBuilder() {
			public void configure() {
			    
				from("jms:topic:MPCS_WX_"+stationID).
			    log("RECEIVED: WEATHER DATA FROM " +stationID+ ": ${body}").
			    process(updateWeather()).
			    to("jms:MPCS_51050_FINAL_LOG");
  
			    
   
			    try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			});
			
			System.out.println("&&&&&&&");
			System.out.println("&&&&&&&");
			System.out.println("Flight Center: Station Added: "+stationID);
			System.out.println("&&&&&&&");
			System.out.println("&&&&&&&");
		} catch (Exception e) {
			
			//e.printStackTrace();
		}
	}
	
	private Processor updateWeather()
	{
		Processor p = new Processor()
		{
        	public void process(Exchange e) throws Exception
        	{
        		JSONObject wxUpdate = new JSONObject(e.getIn().getBody(String.class));
        		//String wxUpdate = e.getIn().getBody(String.class);
        		String sID = wxUpdate.getString("stationID");
        		if(sID != null)
        		{
        			stations.put(sID, wxUpdate);
        			System.out.println("&&&&&&&");
        			System.out.println("&&&&&&&");
        			System.out.println("Flight Center: Station Updated: "+sID);
        			System.out.println("&&&&&&&");
        			System.out.println("&&&&&&&");
        		}
        		
        	}
        };
        return p;
	}
}
