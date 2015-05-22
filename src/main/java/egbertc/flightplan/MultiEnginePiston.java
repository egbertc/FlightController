package egbertc.flightplan;

public class MultiEnginePiston extends Airplane {

	public MultiEnginePiston(double pilotWeight, double passengerWeight,double bagWeight, double fuelWeight, String color, String tn)
	{
		super(pilotWeight, passengerWeight, bagWeight, fuelWeight, color, tn);
		typeCode = "BC48";
		basicEmptyWeight = 3950;
		maxTakeOffWeight = 5500;
	}

	@Override
	public void weightAndBalance() {
		rampWeight = basicEmptyWeight + pilotWeight + passengerWeight + fuelWeight;
		pilotMoment = pilotWeight*78;
		passMoment = passengerWeight*130;
		bagMoment = bagWeight*180;
		fuelMoment = fuelWeight*82.7;
		rampMoment = 310624 + pilotMoment + passMoment + fuelMoment + bagMoment;
		double taxiFuelBurn = 20;
		takeOffMoment = rampMoment - (20*82.7);
		takeOffWeight = rampWeight-taxiFuelBurn;
		takeOffArm = takeOffMoment/takeOffWeight;
	}

	@Override
	public void calcAirspeed(double densityAlt) {
		airspeed = 160 + (densityAlt/400);
		if(airspeed>200)
			airspeed = 200;
	}

	@Override
	public void finalizeReport() {
		weightBal.put("inLimits", checkLimits());
		weightBal.put("airspeed", Double.toString(airspeed));
		weightBal.put("moment", Double.toString(takeOffMoment));
		weightBal.put("arm", Double.toString(takeOffArm));
		double fuelGals = fuelWeight/6;
		double fuelTime = fuelGals/21;
		weightBal.put("fuelTime", Double.toString(fuelTime));
	}
	
	@Override
	public String checkLimits()
	{
		if(takeOffWeight > maxTakeOffWeight)
			return "weight limit: " + takeOffWeight;
		if(takeOffArm < 78 || takeOffArm > 86)
			return "arm limit: " + takeOffArm;
		
		return "clear";
	}

}
