package egbertc.flightplan;

public class CompletedFpState implements FlightPlanState {

	

	@Override
	public String getFlightData(FlightPlan plan) {
		// TODO Auto-generated method stub
		return "Landed at: " + plan.getDestinationPort();
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
		return "Completed";
	}

	
}
