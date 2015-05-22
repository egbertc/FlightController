package egbertc.flightplan;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.json.JSONObject;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Airport {
	
	private String stationID;
	private CamelContext context = new DefaultCamelContext();
	private ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:62012");
	private String stationDataDir;
	private Thread weatherRetrieval;
	
	Airport(String stationID)
	{
		this.setStationID(stationID);
		stationDataDir = "data/weatherStations/" + getStationID();
		retrieveWeather();
				
		try
		{
			context.addComponent("jms",JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
			attemptRegister();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try
		{
			beginBroadcast();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		
		//beginBroadcast();
	}
	
	private void retrieveWeather()
	{
		if(!new File(stationDataDir).exists())
		{
			if(!new File(stationDataDir).mkdirs())
			{
				System.out.println("***OUTPUT DIRECTORY PROBLEM!!!");
				return;
			}
		}
		weatherRetrieval = new Thread(new WeatherRetriever(stationDataDir, getStationID()));
		weatherRetrieval.start();
	}
	
	public boolean attemptRegister() throws Exception
	{		
				
		
		ProducerTemplate template = context.createProducerTemplate();
		template.sendBody("jms:MPCS_WeatherRegister", getStationID());		
		
        // start the route and let it do its work
        context.start();
        try
        {
        	Thread.sleep(5000);
        }
        catch(InterruptedException e)
        {
        	//e.printStackTrace();
        }
        return true;
	}
	
	private void beginBroadcast()
	{
		try
		{
			context.addRoutes(new RouteBuilder() {
			    public void configure() {
			        
			    	from("file:"+stationDataDir).
			        log("NEW WEATHER DATA: Station:"+getStationID()+" - {body}").
			        process(updateWxReport()).
			        choice().
			        	when(body().startsWith("Blank")).
			        		to("jms:MPCS_51050_FINAL_LOG").
			        	otherwise().
			        		to("jms:topic:MPCS_WX_" + getStationID());               
			
			        try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    }
			});
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}       
	}
	
	private Processor updateWxReport()
	{
		Processor p = new Processor()
		{
        	public void process(Exchange e) throws Exception
        	{
        		String input = e.getIn().getBody(String.class);
        		JSONObject wxReport = new JSONObject();
        		wxReport.append("stationID", getStationID());
        		
        		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        		InputSource inSource = new InputSource(new StringReader(input));
        		
//        		System.out.println("***********************************");
//        		System.out.println("***********************************");
//        		System.out.println("***********************************");
        		
        		Document doc = db.parse(inSource);
        		NodeList nodes = doc.getElementsByTagName("data");
        		Element data = (Element) nodes.item(0);
        		
        		int results = Integer.parseInt(data.getAttribute("num_results"));
        		if(results > 0)
        		{
	        		NodeList metarNodes = data.getElementsByTagName("METAR");
	        		Element wxData = (Element) metarNodes.item(0);//.getChildNodes();
	        		        		
	        		NodeList observationTime = wxData.getElementsByTagName("observation_time");        		
	        			wxReport.append("observationTime", getElementString((Element) observationTime.item(0)));
	        		NodeList lat =  wxData.getElementsByTagName("latitude");
	        			wxReport.append("latitude", getElementString((Element)lat.item(0)));
	        		NodeList lon =  wxData.getElementsByTagName("longitude");
	        			wxReport.append("longitude", getElementString((Element)lon.item(0)));
	        		NodeList temp = wxData.getElementsByTagName("temp_c");
	        			wxReport.append("temp", getElementString((Element) temp.item(0)));
	        		NodeList dewPoint = wxData.getElementsByTagName("dewpoint_c");
	        			wxReport.append("dewPoint", getElementString((Element) dewPoint.item(0)));
	        		NodeList windDir = wxData.getElementsByTagName("wind_dir_degrees");
	        			wxReport.append("windDir", getElementString((Element) windDir.item(0)));
	        		NodeList windSpd = wxData.getElementsByTagName("wind_speed_kt");
	        			wxReport.append("windSpeed", getElementString((Element) windSpd.item(0)));
	        		NodeList vis = wxData.getElementsByTagName("visibility_statute_mi");
	        			wxReport.append("visibility", getElementString((Element) vis.item(0)));
	        		NodeList altim = wxData.getElementsByTagName("altim_in_hg");
	        			wxReport.append("altimeter", getElementString((Element) altim.item(0)));
	        		NodeList elevation = wxData.getElementsByTagName("elevation_m");
	        			wxReport.append("elevation", getElementString((Element) elevation.item(0)));
	        			
	        		e.getIn().setBody(wxReport.toString());
        		}
        		else
        		{
        			e.getIn().setBody("Blank Report: " + getStationID());
        		}
        		System.out.println("***********************************");
        		System.out.println("**REPORT: " + wxReport.toString());
        		System.out.println("***********************************");
        		
        	}
        };
        return p;
	}

	private String getElementString(Element e)
	{
		Node data = e.getFirstChild();
		if(data instanceof CharacterData)
		{
			CharacterData text = (CharacterData) data;
			return text.getData();
		}
		return "";
	}
	
	public String getStationID() {
		return stationID;
	}

	public void setStationID(String stationID) {
		this.stationID = stationID;
	}
}

class WeatherRetriever implements Runnable
{
	
	private String outDir;
	private String stationID;
	
	public WeatherRetriever(String outDir, String stationID)
	{
		this.outDir = outDir;
		this.stationID = stationID;
	}
	
	@Override
	public void run() {
		URL wxUrl;
		try {
			System.out.println("&&&&&&&");
			System.out.println("&&&&&&&");
			System.out.println("GETTING WEATHER FROM WEB -Station: " +stationID);
			System.out.println("&&&&&&&");
			System.out.println("&&&&&&&");
			
			wxUrl = new URL("http://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&stationString="
					+stationID+"&mostRecent=true&hoursBeforeNow=3");
			URLConnection urlConnection = wxUrl.openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			
			Date date = new Date();
			DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
			
			FileOutputStream outStream = new FileOutputStream(outDir+"/WX_REPORT_"+dFormat.format(date));
			
			byte[] buffer = new byte[4096];
			int bytesRead = -1;			
			
			while((bytesRead = in.read(buffer)) != -1)
			{
				outStream.write(buffer,0,bytesRead);				
			}
			outStream.close();
			
			Thread.sleep(1000);
		}
		catch (MalformedURLException e)
		{
			//e.printStackTrace();
		}
		catch(IOException e)
		{
			//e.printStackTrace();
		}
		catch(InterruptedException e)
		{
			//e.printStackTrace();
		}
	}
	
}
