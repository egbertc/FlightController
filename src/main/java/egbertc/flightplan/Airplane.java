package egbertc.flightplan;

import java.util.HashMap;
import java.util.Map;

abstract public class Airplane {
	protected String typeCode;
	protected double basicEmptyWeight;
	protected double maxRampWeight;
	protected double maxTakeOffWeight;
	protected double pilotWeight;
	protected double passengerWeight;
	protected double bagWeight;
	protected double fuelWeight;
	protected String color;
	protected String tailNum;
	protected Map<String,String> weightBal = new HashMap<String,String>();
	
	protected double takeOffWeight ;
	protected double rampWeight;
	protected double pilotMoment;
	protected double passMoment;
	protected double fuelMoment;
	protected double bagMoment;
	protected double takeOffMoment;
	protected double rampMoment;
	protected double takeOffArm;
	protected double airspeed;
	protected double altitude;
	
	public Airplane(double pilotWeight, double passengerWeight, double bagWeight, double fuelWeight, String color, String tn)
	{		
		this.pilotWeight = pilotWeight;
		this.passengerWeight = passengerWeight;
		this.bagWeight = bagWeight;
		this.fuelWeight = fuelWeight;
		this.color = color;
		this.tailNum = tn;
	}
	
	public void calcFlightInfo(double densityAlt)
	{
		weightAndBalance();
		calcAirspeed(densityAlt);
		finalizeReport();
	}
	
	public String getTailNum()
	{
		return tailNum;
	}
	
	public Map<String,String> getWeightBalance()
	{
		return weightBal;
	}
	
	public abstract void weightAndBalance();
	public abstract void calcAirspeed(double densityAlt);
	public abstract void finalizeReport();
	public abstract String checkLimits();
	
}
