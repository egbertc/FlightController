package egbertc.flightplan;

public class RequestFpState implements FlightPlanState {

	
	@Override
	public String getFlightData(FlightPlan plan) {
		// TODO Auto-generated method stub
		return "none";
	}

	@Override
	public void stateChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkValidity(FlightPlan plan) {
		System.out.println("&&&&&&&&&");
		System.out.println("&&&&&&&&&");
		System.out.println("REQUEST VALIDATE IS WRONG: ");
		System.out.println("&&&&&&&&&");
		System.out.println("&&&&&&&&&");		
	}

	@Override
	public boolean isValid(FlightPlan plan) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getError(FlightPlan plan) {
		// TODO Auto-generated method stub
		return null;
	}
	


	@Override
	public String toString()
	{
		return "Request";
	}

	

}
