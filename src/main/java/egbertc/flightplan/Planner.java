package egbertc.flightplan;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Planner {
	private FlightPlan fPlan;
	private Map<String, String> weightBal = new HashMap<String, String>();
	private String planeStatus;
	private String weatherStatus;
	private String fileStatus = "unfiled";
	private int people;
	private String planType;
	private int cruiseAlt;
	private double cruiseSpeed;
	private double densityAlt;
	private int windDir;
	private int windSpeed;
	private double distance;
	private double bearing;
	private double flightTime;
	FlightCenter flightCenter;
	
	private JSONObject planJson;
	
	private CamelContext context = new DefaultCamelContext();
	private ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:62012");
	
	public Planner(FlightCenter flightCenter, Airplane plane, String start, String end, String type, int cruiseAlt, int people)
	{
		context.addComponent("jms",JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
		this.flightCenter = flightCenter;
		String planID = genID(plane);
		fPlan = new FlightPlan(planID);
		this.cruiseAlt = cruiseAlt;
		this.people = people;
		this.planType = type;
		retrieveWeather(start,end);
		
		if(weatherStatus == "clear")
		{
			plane.calcFlightInfo(densityAlt);
		
			weightBal = plane.getWeightBalance();
			setPlaneStatus(weightBal.get("inLimits"));
			
			calculateSpeedAndTime();
			
			if(Double.parseDouble(weightBal.get("fuelTime")) < flightTime + .5)
				setPlaneStatus("Fuel Range");
		}	
		
		if(planeStatus == "clear")
			buildPlan(plane, start, end, cruiseAlt);
		
		if(fileStatus == "send")
		{
			try
			{
				sendFlightPlan();
			} 
			catch(Exception e)
			{
				//e.printStackTrace();
				fileStatus = "failed";
			}
			
			
		}
		
		if(fileStatus == "send")
		{
			lookForConfirmation();
		}
		
		System.out.println("&&&&&&&&&&");
		System.out.println("&&&&&&&&&&");
		System.out.println("File Status: " + fileStatus);
		System.out.println("&&&&&&&&&&");
		System.out.println("&&&&&&&&&&");
			
	}
	
	private void sendFlightPlan() throws Exception
	{
		ProducerTemplate template = context.createProducerTemplate();
		template.sendBody("jms:MPCS_ATC_FltPlnRequest", planJson.toString());		
		// start the route and let it do its work
        context.start();	
	}
	
	private void lookForConfirmation()
	{
		// add our route to the CamelContext
				try
				{
					context.addRoutes(new RouteBuilder() {
					public void configure() {
					    
						from("jms:MPCS_ATC_FltPlnReply").
					    log("RECEIVED: NEW STATION: ${body}").
					    choice().
					    	when(body().startsWith(fPlan.getPlanID())).
					    			process(processReply()).
					    			to("jms:MPCS_51050_FINAL_LOG").
					    	otherwise().
					    		to("jms:MPCS_ATC_FltPlnReply");		  
					    
		   
					    try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					});
				
					if(fileStatus == "ACCEPTED" || fileStatus.startsWith("DENIED"))
						context.stop();
				} catch (Exception e) {
					
					//e.printStackTrace();
				}
	}
	
	private Processor processReply()
	{
		Processor p = new Processor()
		{
        	public void process(Exchange e) throws Exception
        	{
        		String result = e.getIn().getBody(String.class);
        		int breakPoint = result.indexOf('|') + 1;
        		result = result.substring(breakPoint);
        		
        		if(result.startsWith("ACCEPTED"))
        		{
        			fileStatus = "ACCEPTED";        			
        		}
        		else
        		{
        			fileStatus = "FAILED: " + result;
        		}
        	}
        };
        return p;
	}
	
	private void buildPlan(Airplane plane, String start, String end, int cruiseAlt)
	{
		fPlan.setPlanType(planType);
		fPlan.setCruiseAltitude(cruiseAlt);
		fPlan.setCruiseSpeed((int) cruiseSpeed);
		fPlan.setDeparturePort(start);
		fPlan.setDestinationPort(end);
		long departTime = DateUtils.addSeconds(new Date(), 60).getTime();
		//departTime = DateUtils.addSeconds(departTime, 30);//.addHours(departTime, 2);
		fPlan.setDepartTime(departTime);
		fPlan.setTimeEnroute(flightTime);
		fPlan.setFuelTime(Double.parseDouble(weightBal.get("fuelTime")));
		fPlan.setTailNum(plane.tailNum);
		fPlan.setPeopleOnBoard(people);
		fPlan.setAircraftColor("white");
		fPlan.setAlternatePort("none");
		fPlan.setPilotInfo("Johnny Pilot");
		fPlan.setDestinationContactInfo("paradise");
		fPlan.setRoute(null);
		fPlan.setNotes("");
		fPlan.setTypeCode(plane.typeCode);
		planJson = fPlan.retrievePlanJson();
		
		if(planJson != null)
			fileStatus = "send";
	}
	
	private void retrieveWeather(String start, String end)
	{
		JSONObject startWx = flightCenter.getWeather(start);
		JSONObject endWx = flightCenter.getWeather(end);
				//String startWx = flightCenter.getWeather(start);
		if(startWx == null || endWx == null)
		{
			weatherStatus = "faulty";
			return;
		}
		try
		{
			System.out.println("&&&&&&&");
			System.out.println("&&&&&&&");
			System.out.println("get weather object: " + startWx);
			System.out.println("&&&&&&&");
			System.out.println("&&&&&&&");
						
			double startAlt = Double.parseDouble(startWx.getJSONArray("altimeter").get(0).toString());
			double startTemp = Double.parseDouble(startWx.getJSONArray("temp").get(0).toString());
			double endAlt = Double.parseDouble(endWx.getJSONArray("altimeter").get(0).toString());
			double endTemp = Double.parseDouble(endWx.getJSONArray("temp").get(0).toString());
			
			double avgAlt = (startAlt+endAlt)/2;
			double avgTemp = (startTemp+endTemp)/2;
			
			double pressureAlt = (29.92-avgAlt)*1000 + cruiseAlt;
			densityAlt = pressureAlt + (avgTemp - (((cruiseAlt/1000)*2)-15));
						
			distance = calcDistance(startWx, endWx);
			bearing = calcBearing(startWx, endWx);
			windDir = Integer.parseInt(startWx.getJSONArray("windDir").get(0).toString());
			windSpeed = Integer.parseInt(startWx.getJSONArray("windSpeed").get(0).toString());
						
			if(densityAlt != 0)
				weatherStatus = "clear";
			
		}		
		catch (NumberFormatException e)
		{
			weatherStatus = "faulty";
			
			//e.printStackTrace();
		}
		catch (JSONException e)
		{
			weatherStatus = "faulty";
			//e.printStackTrace();
		}
		
	}
	
	private double calcDistance(JSONObject startWx, JSONObject endWx) throws JSONException
	{
		double startLon = Double.parseDouble(startWx.getJSONArray("longitude").get(0).toString());
		double startLat = Double.parseDouble(startWx.getJSONArray("latitude").get(0).toString());
		
		double endLon = Double.parseDouble(endWx.getJSONArray("longitude").get(0).toString());
		double endLat = Double.parseDouble(endWx.getJSONArray("latitude").get(0).toString());
		
		double distLon = toRad(endLon-startLon);
		double distLat = toRad(endLat-startLat);
		
		double a = Math.pow(Math.sin(distLat/2),2) + (Math.cos(toRad(startLat))*Math.cos(toRad(endLat))*Math.pow(Math.sin(distLon/2), 2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double distanceLoc = 3442.01 * c; // 3442.01 is aprox distance around earth in nautical miles
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		System.out.println("Flight Distance: " + distanceLoc);
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		return distanceLoc;
	}
	
	private double calcBearing(JSONObject startWx, JSONObject endWx) throws JSONException
	{
		double startLon = Double.parseDouble(startWx.getJSONArray("longitude").get(0).toString());
		double startLat = Double.parseDouble(startWx.getJSONArray("latitude").get(0).toString());
		
		double endLon = Double.parseDouble(endWx.getJSONArray("longitude").get(0).toString());
		double endLat = Double.parseDouble(endWx.getJSONArray("latitude").get(0).toString());
		
		double distLon = toRad(endLon-startLon);
		
				
		double bearingRadians = Math.atan2(Math.sin(distLon)*Math.cos(endLat),
				(Math.cos(startLat)*Math.sin(endLat))-(Math.sin(startLat)*Math.cos(endLat)*Math.cos(distLon)));
		double bearingLoc = ((bearingRadians * (180/Math.PI)) + 360) % 360;
		
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		System.out.println("Flight Bearing: " + bearingLoc);
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		
		return bearingLoc;
	}
	
	private double toRad(double value)
	{
		return value * Math.PI/180;
	}
	
	private void calculateSpeedAndTime()
	{
		cruiseSpeed = Double.parseDouble(weightBal.get("airspeed"));
		int crossWind =(int) Math.abs(((windDir+180)%360)-bearing);
		double groundSpeed = Math.sqrt(Math.pow(cruiseSpeed, 2) + Math.pow(windSpeed, 2) - (2*cruiseSpeed*windSpeed*Math.cos(crossWind)));
		
		flightTime = distance/groundSpeed;
	}
	
	private void setPlaneStatus(String status)
	{
		planeStatus = status;
	}
	
	public String getPlaneStatus()
	{
		return planeStatus;
	}
	
	private String genID(Airplane p)
	{
		DecimalFormat form = new DecimalFormat("00000");
		int randID = (int) (Math.random()*10000);
		String id = p.getTailNum()+"-"+form.format(randID);
		return id;
	}
	
	public String getPlanStatus()
	{
		return fileStatus;
	}
	
	public String getAllStatus()
	{
		return "Weather Status: " + weatherStatus + "\nPlane Status: " + planeStatus + "\nFile Status: " + fileStatus;
	}
}
