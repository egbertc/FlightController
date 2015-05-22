package egbertc.flightplan;

import java.util.Date;

public class PastDueFpState implements FlightPlanState {

	

	@Override
	public String getFlightData(FlightPlan plan) {
		Date currentTime = new Date();
		Date estArrival = new Date(plan.departTime + (long)(plan.timeEnroute*3600000.0));
		long overtime = currentTime.getTime() - estArrival.getTime();
		return "Past Due by: " + overtime/3600000.0 + " hours";
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
		return "Past Due";
	}

}
