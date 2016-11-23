import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3GyroSensor;

public class Robot {
	private static float GRIPPER_SPEED = 90f; 
	private static float GRIPPER_GEAR_RATIO = 3f;
	
	private static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S2);
	private static EV3UltrasonicSensor sonicSensor = new EV3UltrasonicSensor(SensorPort.S3);
	private static EV3GyroSensor gyroSensor = new EV3GyroSensor(SensorPort.S4);  
	// public static float[] COLOUR_VALUES = { 0.102f, 0.160f, 0.312f, 0.507f, 0.582f }; 
	public static Position position = new Position(0, 0); 
	public static int ticksSinceLastObstacle = 0;
	
	public static float color, sonic, gyro; 
								
	public static void drive(float l, float r) {
		// B-> to left C-> to right
		Motor.B.setSpeed(Math.abs(l));
		Motor.C.setSpeed(Math.abs(r));
		if (l > 0) {
			Motor.B.forward();
		} else if (l < 0) {
			Motor.B.backward();
		} else {
			Motor.B.stop(true);
		}

		if (r > 0) {
			Motor.C.forward();
		} else if (r < 0) {
			Motor.C.backward();
		} else {
			Motor.C.stop(true);
		}
	}
	
	/* Makes the ultrasonic sensor look in a given direction */ 
	public static void look(int deg) {
		// always start sensor pointing straight forward. The allowed motion is then [-90,90]
		scaledDeg = ultrasonicGearRatio*deg;
		Motor.D.rotate(scaledDeg);	
	}
	
	/* 
	 * Uses the gripper to grab the object. Note: this function is blocking 
	 * */ 
	public static void grab() {
    	float GRIPPER_CLOSED_POSITION = 90; // angle that gripper should be rotated to
    	dist_goal = 1.5;
    
    	look(0); 
    	float dist = pollSonic(true); // from here  
    	while (dist > dist_goal) {
    		float dist = pollSonic(true);
    		Motor.A.forward(); 
    	} // to here should be removed 
    	Motor.A.setSpeed(GRIPPER_SPEED);
    	Motor.A.rotate(GRIPPER_CLOSED_POSITION * GRIPPER_GEAR_RATIO);
	}

	public static void drop(){
    	float GRIPPER_OPEN_POSITION = -90;
    
    	Motor.A.setSpeed(GRIPPER_SPEED);
    	Motor.A.rotate(GRIPPER_OPEN_POSITION * GRIPPER_GEAR_RATIO);
	}
	/* 
	public static void rotate(float s, int l, int r) {
		// B-> to left C-> to right
		// use s as a base speed for motor B arbitrarily 
		Motor.B.setSpeed(Math.abs(s));
		Motor.C.setSpeed(Math.abs(s));
		Motor.B.rotate(l,true);
		Motor.C.rotate(r);
	} */ 
	
	/* 
	public static void arc(float s, int l, int r) {
		// B-> to left C-> to right
		// use s as a base speed for motor B arbitrarily 
		float speedC = s * r / l; 
		
		Motor.B.setSpeed(Math.abs(s));
		Motor.C.setSpeed(Math.abs(speedC));
		Motor.B.rotate(l,true);
		Motor.C.rotate(r);
	} */ 
	
	public static float pollColor(boolean log) {
		int sampleSize = colorSensor.sampleSize();
		float[] redsample = new float[sampleSize];
		colorSensor.getRedMode().fetchSample(redsample, 0);
		if (log) {
			System.out.print("colorSensor: ");
			System.out.println(redsample[0]);
		}
		return redsample[0];
	}
	
	public static float pollSonic(boolean log) {
		int sampleSize = sonicSensor.sampleSize();
		float[] sample = new float[sampleSize];
		sonicSensor.fetchSample(sample, 0);
		if (log) {
			System.out.print("sonicSensor: ");
			System.out.println(sample[0]*100);
		}
		return sample[0]*100;
	}

	private static float pollDist(boolean log) {
		float convS = 360f/16.8f;
		float _dist = (Motor.C.getTachoCount()+Motor.B.getTachoCount())/2.0f/convS; 
		if (log) {
			System.out.print("dist: ");
			System.out.println(_dist);
		}
		return _dist;
	}
	private static float pollGyro(boolean log) {
		float[] sample = new float[gyroSensor.sampleSize()];
		gyroSensor.getAngleMode().fetchSample(sample, 0); 
		if (log) {
			System.out.println("gyroSensor: " + sample[0]);
		}
		return sample[0]; 
	}
	private static void tachoReset() { 
		Motor.B.resetTachoCount(); 
		Motor.C.resetTachoCount(); 
	}
	
	public static Position updateState() {
		// save sensor readings 
		dist = pollDist(false);
		gyro = Math.toRadians(pollGyro(false));
		color = pollColor(false); 
		
		Robot.position.increment(dist * Math.cos(angle), dist * Math.sin(angle));
		Robot.tachoReset();
		return Robot.position; 
	}
}
