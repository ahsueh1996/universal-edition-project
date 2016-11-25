import lejos.hardware.Button; 

public class Main {
	
	public static void main(String[] args) {
		Behavior[] behaviours = {
			// insert behavior here
//		  new Avoider(30), // avoider arg is "too close"  
//		  new Pathfinder(),
// 		  new Delivery(-3,0.312f)
		}; 
		
		// I am now in delivery branch!!

		System.out.println("STARTING MAIN");
		Button.waitForAnyPress();
		Robot.gyroReset();
		Robot.updateState(); 
		while (Button.ESCAPE.isUp()) {
			for(int i=0; i<behaviours.length; i++){
				if(behaviours[i].checkActive()) {
					behaviours[i].act(-1);
					break;
				}
			}
			Robot.updateState(); 
			// System.out.println(Robot.position);
			
			if (Button.UP.isDown()) 
				System.out.println(Robot.position);
		}
		System.out.println(Robot.position); 
	}
	
}
