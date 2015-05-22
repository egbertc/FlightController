package egbertc.flightplan;

public class SingleEnginePiston extends Airplane {

	public SingleEnginePiston(double pilotWeight, double passengerWeight,double bagWeight, double fuelWeight, String color, String tn)
	{
		super(pilotWeight, passengerWeight, bagWeight, fuelWeight, color, tn);
		typeCode = "PA28";
		basicEmptyWeight = 1500;
		maxTakeOffWeight = 2500;
	}

	@Override
	public void weightAndBalance() {
		takeOffWeight = basicEmptyWeight + pilotWeight + passengerWeight + fuelWeight +bagWeight;
		pilotMoment = pilotWeight*80.5;
		passMoment = passengerWeight*118.1;
		bagMoment = bagWeight*142.8;
		fuelMoment = fuelWeight*95;
		takeOffMoment = 128850 + pilotMoment + passMoment + fuelMoment +bagMoment;
		takeOffArm = takeOffMoment/takeOffWeight;
	}

	@Override
	public void calcAirspeed(double densityAlt) {
		airspeed = 120 + (densityAlt/600);
		if(airspeed>135)
			airspeed = 135;
	}

	@Override
	public void finalizeReport() {
		weightBal.put("inLimits", checkLimits());
		weightBal.put("airspeed", Double.toString(airspeed));
		weightBal.put("moment", Double.toString(takeOffMoment));
		weightBal.put("arm", Double.toString(takeOffArm));
		double fuelGals = fuelWeight/6;
		double fuelTime = fuelGals/9.2;
		weightBal.put("fuelTime", Double.toString(fuelTime));
	}
	
	@Override
	public String checkLimits()
	{
		if(takeOffWeight > maxTakeOffWeight)
			return "weight limit exceeded: " +takeOffWeight;
		if(takeOffArm < 88 || takeOffArm > 93)
			return "arm limit exceeded: " + takeOffArm;
		
		return "clear";
	}

}
