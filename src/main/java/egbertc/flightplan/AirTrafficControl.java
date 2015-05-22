package egbertc.flightplan;


import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
//import org.hsqldb.lib.Set;
import org.json.JSONException;
import org.json.JSONObject;


public class AirTrafficControl {
	private static AirTrafficControl instance = null;
	private CamelContext context = new DefaultCamelContext();
	private ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:62012");
	private Map<String, FlightPlan> flights = new HashMap<String,FlightPlan>();
	
	private AirTrafficControl()	
	{
		try
		{
			establishConnection();
		}catch(Exception e)
		{
			System.out.println("&&&&&&");
			System.out.println("&&&&&&");
			System.out.println("***ATC connection could not be established!");
		}
		
		Thread flightWatch = new Thread(new FlightWatch(flights));
		flightWatch.start();
	}
	
	private void establishConnection() throws Exception
	{
		context.addComponent("jms",JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
	    
	        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                
            	from("jms:MPCS_ATC_FltPlnRequest").
                log("RECEIVED: Flight Plan Request: ${body}").
                process(processRequest()).
                to("jms:MPCS_51050_FINAL_LOG");
      
                
        
                try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
            }
        });

        // start the route and let it do its work
        context.start();		
	}
	
	private Processor processRequest()
	{
		Processor p = new Processor()
						{
				        	public void process(Exchange e) throws Exception
				        	{
				        		String input = e.getIn().getBody(String.class);
				        		JSONObject newRequest = new JSONObject(input);
				        		
				        		System.out.println("&&&&&&");
				        		System.out.println("&&&&&&");
				        		System.out.println("&&&&&&");
				        		System.out.println("ATC RECIEVED REQUEST: " + newRequest.toString());
				        		System.out.println("&&&&&&");
				        		System.out.println("&&&&&&");
				        		System.out.println("&&&&&&");
				        		
				        		boolean valid = true;
				        		String error = "";
				        		FlightPlan newPlan = null;
				        		try
				        		{
				        			newPlan = getFlightPlan(newRequest);
				        			if(!newPlan.isValid())
									{
										valid = false;
										error = newPlan.getError();
									}
				        		}
				        		catch(JSONException je)
				        		{
				        			valid = false;
				        			error = "creation error";
				        		}
				        		
				        		if(valid)
				        		{
				        			System.out.println("&&&&&&&&&");
				        			System.out.println("&&&&&&&&&");				        			
				        			System.out.println("VALIDATE (addFlightPlan) JSON: " + newPlan.retrievePlanJson().toString());
				        			System.out.println("&&&&&&&&&");
				        			System.out.println("&&&&&&&&&");
				        			addFlightPlan(newPlan);
				        		}
				        		else
				        		{
				        			rejectFlightPlan(newPlan, error);
				        		}
				        		
				        	}
				        };
        return p;
	}
	
	private FlightPlan getFlightPlan(JSONObject request) throws JSONException
	{
		FlightPlan newPlan = new FlightPlan(request);			
		
		
		return newPlan; //flightplan
	}
	
	private void addFlightPlan(FlightPlan plan)
	{
		
		plan.setState(new PreflightFpState());
		String id = plan.getPlanID();
		flights.put(id, plan);
		
		ProducerTemplate template = context.createProducerTemplate();
		template.sendBody("jms:MPCS_ATC_FltPlnReply", plan.getPlanID() + "|ACCEPTED|");
		
	}
	
	private void rejectFlightPlan(FlightPlan plan, String err)
	{
		String id =plan.getPlanID();
		
		ProducerTemplate template = context.createProducerTemplate();
		template.sendBody("jms:MPCS_ATC_FltPlnReply", id + "|DENIED|"+ err);
		
	}

	public static AirTrafficControl getInstance() // Calls the constructor method only once o/w it returns the Bank object
    {
        if (instance == null)
        {                
            instance = new AirTrafficControl();
        }
        return instance;
    }
}

class FlightWatch implements Runnable
{
	
	private Map<String,FlightPlan> flights;
		
	public FlightWatch(Map<String, FlightPlan> flights)
	{
		this.flights = flights;
	}
	
	@Override
	public void run() {
		
		try
		{
			while(true)
			{
				//try
				//{
					Set<String> keys = flights.keySet();
					Iterator<String> i = keys.iterator();
					
					while(i.hasNext())
					{
						Object key = (Object) i.next();
						FlightPlan currentPlan = flights.get(key);
						
						Date currentTime = new Date();
						long departTime = currentPlan.getDepartTime();//new Date(plan.departTime.getTime() + (long)(plan.timeEnroute*3600000));
						String message = "ATC FLIGHT STATUS: " + currentPlan.tailNum + " | " + currentPlan.getFlightData();
						
						if(currentPlan.getState().toString() == "Preflight")
						{						
							long timeRemaining = departTime - currentTime.getTime();
							if(timeRemaining <= 0.0)
								currentPlan.setState(new EnrouteFpState());		
							else
							{
								long minutes = timeRemaining /(long) 60000.0;
								message = message + " | " + "Time Until Departure: " + (long)minutes + " minutes";
							}
								
						}
						else if(currentPlan.getState().toString() == "Enroute")
						{
							Date estArrival = new Date(departTime + (long)(currentPlan.timeEnroute*3600000.0));
							long timeRemainingEnroute = estArrival.getTime() - currentTime.getTime();
							if(timeRemainingEnroute <= 0)
							{
								currentPlan.setState(new PastDueFpState());
								message = "Past Due by: " + (-1*timeRemainingEnroute)/60000.0 + " minutes";
							}								
							else
							{
								message = message + "| Time Remaining in Flight: " + timeRemainingEnroute/60000.0 + " minutes.";
							}
							
						}
						System.out.println("&&&&&&&&&");
						System.out.println("&&&&&&&&&");
						System.out.println("DEPART TIME: " + new Date(departTime) + " | " + message);
						System.out.println("&&&&&&&&&");
						System.out.println("&&&&&&&&&");
					}
					
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
				//}
//				catch(NullPointerException ex)
//				{
//					System.out.println("&&&&&&&&&");
//					System.out.println("&&&&&&&&&");
//					System.out.println("VAR IS LOCKED");
//					System.out.println("&&&&&&&&&");
//					System.out.println("&&&&&&&&&");
//				}
			}
		}
		catch (Exception e)
		{
			//e.printStackTrace();
		}
		
	}
	
}
