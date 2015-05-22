package egbertc.flightplan;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;


public class FlightPlan {
	protected String planID;
	protected String planType;
	protected String tailNum;
	protected String departurePort;
	protected String destinationPort;
	protected ArrayList<String> route;
	protected String alternatePort;
	protected int cruiseAltitude;
	protected int cruiseSpeed;
	protected String typeCode;
	protected long departTime;
	protected double timeEnroute;
	protected String notes;
	protected double fuelTime;
	protected String pilotInfo;
	protected int peopleOnBoard;
	protected String aircraftColor;
	protected String destinationContactInfo;
	protected long createDate;
	
	protected JSONObject fPlanJson;
	protected boolean valid;
	protected String error = "unkown";
	
	protected FlightPlanState state;
	
	public FlightPlan(String ID)
	{
		state = new RequestFpState();
		planID = ID;
		createDate = new Date().getTime();
	}
	
	public FlightPlan(JSONObject plan) throws JSONException
	{
		
		state = new ValidateFpState();
		fPlanJson = plan;
		planID = plan.getJSONArray("planID").get(0).toString();
		checkValidity();
		
	}
	
	public void setState(FlightPlanState s)
	{
		System.out.println("&&&&&&&&&");
		System.out.println("&&&&&&&&&");
		System.out.println("STATE CHANGE!");
		System.out.println("&&&&&&&&&");
		System.out.println("&&&&&&&&&");
		state = s;
		state.stateChange();
	}
	
	public String getFlightData()
	{
		return state.getFlightData(this);
	}
	
	public FlightPlanState getState()
	{
		return state;
	}
	
	
	private void checkValidity()
	{
		state.checkValidity(this);
//		DateFormat df = DateFormat.getDateInstance();
//		try
//		{
//			planID = fPlanJson.getJSONArray("planID").get(0).toString();
//			planType = fPlanJson.getJSONArray("planType").get(0).toString();
//			tailNum = fPlanJson.getJSONArray("tailNum").get(0).toString();
//			departurePort = fPlanJson.getJSONArray("departurePort").get(0).toString();
//			destinationPort = fPlanJson.getJSONArray("destinationPort").get(0).toString();
//			route = null;//fPlanJson.getJSONObject("route").get(0).toString();
//			alternatePort = fPlanJson.getJSONArray("alternate").get(0).toString();
//			cruiseAltitude = Integer.parseInt(fPlanJson.getJSONArray("cruiseAlt").get(0).toString());
//			cruiseSpeed = Integer.parseInt(fPlanJson.getJSONArray("cruiseSpeed").get(0).toString());
//			typeCode = fPlanJson.getJSONArray("aircraftType").get(0).toString();
//			departTime = new Date(Long.parseLong(fPlanJson.getJSONArray("departTime").get(0).toString()));//Sun Aug 24 23:06:57 CDT 2014
//			
//			timeEnroute = Double.parseDouble(fPlanJson.getJSONArray("timeEnroute").get(0).toString());
//			notes = fPlanJson.getJSONArray("notes").get(0).toString();
//			fuelTime = Double.parseDouble(fPlanJson.getJSONArray("fuelTime").get(0).toString());
//			pilotInfo = fPlanJson.getJSONArray("pilotInfo").get(0).toString();
//			peopleOnBoard = Integer.parseInt(fPlanJson.getJSONArray("peopleCount").get(0).toString());
//			aircraftColor = fPlanJson.getJSONArray("color").get(0).toString();
//			destinationContactInfo = fPlanJson.getJSONArray("destinationContact").get(0).toString();
//			createDate = df.parse(fPlanJson.getJSONArray("timestamp").get(0).toString());
//		}
//		catch(JSONException e)
//		{
//			valid = false;
//			
//		}
//		catch(ParseException p)
//		{
//			p.printStackTrace();
//			valid = false;
//		}
//		catch(NullPointerException n)
//		{
//			valid = false;
//			error = "Empty Field";
//		}
//		
//		valid = true;
	}
	
	public boolean isValid()
	{
		return state.isValid(this);
	}
	
	public String getError()
	{
		return state.getError(this);
	}
	
	public JSONObject retrievePlanJson()
	{
		fPlanJson = new JSONObject();
		try
		{
			fPlanJson.append("planID", planID);
			fPlanJson.append("planType", planType);
			fPlanJson.append("tailNum", tailNum);
			fPlanJson.append("departurePort", departurePort);
			fPlanJson.append("destinationPort", destinationPort);
			JSONObject routeJson = new JSONObject();
			int routeCount = 0;
			if( route != null && !route.isEmpty())
			{
				for(String apt : route)
				{			
					routeJson.append(Integer.toString(routeCount), apt);
					routeCount++;
				}
				fPlanJson.append("route", routeJson);
			}else
				fPlanJson.append("route", "");	
			
			fPlanJson.append("alternate", alternatePort);
			fPlanJson.append("cruiseAlt", cruiseAltitude);
			fPlanJson.append("cruiseSpd", cruiseSpeed);
			fPlanJson.append("aircraftType", typeCode);
			fPlanJson.append("departTime", departTime);
			fPlanJson.append("timeEnroute", timeEnroute);
			fPlanJson.append("notes", notes);
			fPlanJson.append("fuelTime", fuelTime);
			fPlanJson.append("pilotInfo", pilotInfo);
			fPlanJson.append("peopleCount", peopleOnBoard);
			fPlanJson.append("color",aircraftColor);
			fPlanJson.append("destinationContact",destinationContactInfo);
			fPlanJson.append("timestamp", createDate);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return fPlanJson;
	}
	
	public String getPlanID() {
		return planID;
	}


	public void setPlanID(String planID) {
		this.planID = planID;
	}


	public String getPlanType() {
		return planType;
	}


	public void setPlanType(String planType) {
		this.planType = planType;
	}


	public String getTailNum() {
		return tailNum;
	}


	public void setTailNum(String tailNum) {
		this.tailNum = tailNum;
	}


	public String getDeparturePort() {
		return departurePort;
	}


	public void setDeparturePort(String departurePort) {
		this.departurePort = departurePort;
	}


	public String getDestinationPort() {
		return destinationPort;
	}


	public void setDestinationPort(String destinationPort) {
		this.destinationPort = destinationPort;
	}


	public ArrayList<String> getRoute() {
		return route;
	}


	public void setRoute(ArrayList<String> route) {
		this.route = route;
	}


	public String getAlternatePort() {
		return alternatePort;
	}


	public void setAlternatePort(String alternatePort) {
		this.alternatePort = alternatePort;
	}


	public int getCruiseAltitude() {
		return cruiseAltitude;
	}


	public void setCruiseAltitude(int cruiseAltitude) {
		this.cruiseAltitude = cruiseAltitude;
	}


	public int getCruiseSpeed() {
		return cruiseSpeed;
	}


	public void setCruiseSpeed(int cruiseSpeed) {
		this.cruiseSpeed = cruiseSpeed;
	}


	public String getTypeCode() {
		return typeCode;
	}


	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}


	public long getDepartTime() {
		return departTime;
	}


	public void setDepartTime(long departTime) {		
		this.departTime = departTime;
		
	}


	public double getTimeEnroute() {
		return timeEnroute;
	}


	public void setTimeEnroute(double timeEnroute) {
		this.timeEnroute = timeEnroute;
	}


	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}


	public double getFuelTime() {
		return fuelTime;
	}


	public void setFuelTime(double fuelTime) {
		this.fuelTime = fuelTime;
	}


	public String getPilotInfo() {
		return pilotInfo;
	}


	public void setPilotInfo(String pilotInfo) {
		this.pilotInfo = pilotInfo;
	}


	public int getPeopleOnBoard() {
		return peopleOnBoard;
	}


	public void setPeopleOnBoard(int peopleOnBoard) {
		this.peopleOnBoard = peopleOnBoard;
	}


	public String getAircraftColor() {
		return aircraftColor;
	}


	public void setAircraftColor(String aircraftColor) {
		this.aircraftColor = aircraftColor;
	}


	public String getDestinationContactInfo() {
		return destinationContactInfo;
	}


	public void setDestinationContactInfo(String destinationContactInfo) {
		this.destinationContactInfo = destinationContactInfo;
	}	
	
}
