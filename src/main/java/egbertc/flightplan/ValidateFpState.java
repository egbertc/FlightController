package egbertc.flightplan;

import org.json.JSONException;



public class ValidateFpState implements FlightPlanState {

	

	@Override
	public String getFlightData(FlightPlan plan) {
		// TODO Auto-generated method stub
		return "Validating";
	}

	@Override
	public void stateChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkValidity(FlightPlan plan) {
		//DateFormat df = DateFormat.getDateInstance();
				
		try
		{
			plan.planID = plan.fPlanJson.getJSONArray("planID").get(0).toString();
			plan.setPlanType(plan.fPlanJson.getJSONArray("planType").get(0).toString());
			plan.tailNum = plan.fPlanJson.getJSONArray("tailNum").get(0).toString();
			plan.departurePort = plan.fPlanJson.getJSONArray("departurePort").get(0).toString();
			plan.destinationPort = plan.fPlanJson.getJSONArray("destinationPort").get(0).toString();
			plan.route = null;//plan.fPlanJson.getJSONObject("route").get(0).toString();
			plan.alternatePort = plan.fPlanJson.getJSONArray("alternate").get(0).toString();
			plan.cruiseAltitude = Integer.parseInt(plan.fPlanJson.getJSONArray("cruiseAlt").get(0).toString());
			plan.cruiseSpeed = Integer.parseInt(plan.fPlanJson.getJSONArray("cruiseSpd").get(0).toString());
			plan.typeCode = plan.fPlanJson.getJSONArray("aircraftType").get(0).toString();
			plan.departTime = Long.parseLong(plan.fPlanJson.getJSONArray("departTime").get(0).toString());//Sun Aug 24 23:06:57 CDT 2014			
			plan.timeEnroute = Double.parseDouble(plan.fPlanJson.getJSONArray("timeEnroute").get(0).toString());
			plan.notes = plan.fPlanJson.getJSONArray("notes").get(0).toString();
			plan.fuelTime = Double.parseDouble(plan.fPlanJson.getJSONArray("fuelTime").get(0).toString());
			plan.setPilotInfo(plan.fPlanJson.getJSONArray("pilotInfo").get(0).toString());
			plan.peopleOnBoard = Integer.parseInt(plan.fPlanJson.getJSONArray("peopleCount").get(0).toString());
			plan.aircraftColor = plan.fPlanJson.getJSONArray("color").get(0).toString();
			plan.destinationContactInfo = plan.fPlanJson.getJSONArray("destinationContact").get(0).toString();
			plan.createDate = Long.parseLong(plan.fPlanJson.getJSONArray("timestamp").get(0).toString());
		}
		catch(JSONException e)
		{
			plan.valid = false;
			System.out.println("&&&&&&&&&");
			System.out.println("&&&&&&&&&");
			System.out.println("JSON EXCEPTION");
			System.out.println("&&&&&&&&&");
			System.out.println("&&&&&&&&&");
			e.printStackTrace();
			//System.exit(0);
		}		
		catch(NullPointerException n)
		{
			plan.valid = false;
			plan.error = "Empty Field";
			
			System.out.println("&&&&&&&&&");
			System.out.println("&&&&&&&&&");
			System.out.println("NULL POINTER EXCEPTION");
			System.out.println("&&&&&&&&&");
			System.out.println("&&&&&&&&&");
			//System.exit(0);
		}
		
		plan.valid = true;
		//plan.setState(new PreflightFpState());
	}

	@Override
	public boolean isValid(FlightPlan plan) {
		// TODO Auto-generated method stub
		return plan.valid;
	}

	@Override
	public String getError(FlightPlan plan) {
		// TODO Auto-generated method stub
		return plan.error;
	}

	@Override
	public String toString()
	{
		return "Validation";
	}
	

}
