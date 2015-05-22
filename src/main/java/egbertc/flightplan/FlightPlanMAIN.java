package egbertc.flightplan;

public class FlightPlanMAIN {

	public static void main(String[] args) {
				
		FlightCenter flightCenter = new FlightCenter();
		
		AirTrafficControl atc = AirTrafficControl.getInstance();
		
		try
		{
			Thread.sleep(5000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		Airport oun = new Airport("KMDW");		
		Airport dfw = new Airport("KSTL");
		Airport ord = new Airport("KORD");
		Airport rhi = new Airport("KRHI");
		
		Airplane warrior = new SingleEnginePiston(300,0,150,150, "white", "N10001");		
		Airplane baron = new MultiEnginePiston(320,0,150,300, "grey", "N20002");
		
		try
		{
			Thread.sleep(10000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		System.out.println("Creating Planner For Baron: ");
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		
		Planner flightPlannerBaron = new Planner(flightCenter, baron, "KMDW", "KSTL", "VFR", 10000, 2);
		
		try
		{
			Thread.sleep(5000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		System.out.println("Creating Planner For Warrior: ");
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		
		Planner flightPlannerWarrior = new Planner(flightCenter, warrior, "KORD", "KRHI", "VFR", 6000, 1);
		
		try
		{
			Thread.sleep(5000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		System.out.println("Baron:\n" + flightPlannerBaron.getAllStatus());
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		System.out.println("Warrior: \n" + flightPlannerWarrior.getAllStatus());
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");

		try
		{
			Thread.sleep(5000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		System.out.println("Baron: \n" + flightPlannerBaron.getAllStatus());
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		System.out.println("Warrior: \n" + flightPlannerWarrior.getAllStatus());
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");

		try
		{
			Thread.sleep(5000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		System.out.println("Baron: \n" + flightPlannerBaron.getAllStatus());
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");
		System.out.println("Warrior: \n" + flightPlannerWarrior.getAllStatus());
		System.out.println("&&&&&&&");
		System.out.println("&&&&&&&");

		
	}

}
