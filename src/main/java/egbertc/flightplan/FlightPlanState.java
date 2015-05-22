package egbertc.flightplan;


public interface FlightPlanState {
		
	public String getFlightData(FlightPlan plan);
	public void checkValidity(FlightPlan plan);
	public boolean isValid(FlightPlan plan);
	public String getError(FlightPlan plan);
	public void stateChange();
}
