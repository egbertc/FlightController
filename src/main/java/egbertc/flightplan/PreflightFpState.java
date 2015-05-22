package egbertc.flightplan;


public class PreflightFpState implements FlightPlanState {

	
	@Override
	public String getFlightData(FlightPlan plan) {
		// TODO Auto-generated method stub
		
		return "On Ground at: " + plan.departurePort + " | ";
	}

	@Override
	public void stateChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkValidity(FlightPlan plan) {
		// TODO Auto-generated method stub
		
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
		return "Preflight";
	}

}
