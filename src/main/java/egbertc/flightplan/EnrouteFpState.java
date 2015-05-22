package egbertc.flightplan;

public class EnrouteFpState implements FlightPlanState {

	

	@Override
	public String getFlightData(FlightPlan plan) {
		// TODO Auto-generated method stub
		return "Enroute to : " + plan.getDestinationPort() + "\n"
				+ "Altitude: " + plan.getCruiseAltitude() + "\n"
				+ "Airspeed: " + plan.getCruiseSpeed();
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
		return "Enroute";
	}

	

}
