import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class TurnAtJunction implements Behavior {
	private BaseRegulatedMotor mLeft;
	private BaseRegulatedMotor mRight;
	private EV3ColorSensor csm;
	private EV3ColorSensor csl;
	private EV3ColorSensor csr;
	private SampleProvider cm;
	private SampleProvider cl;
	private SampleProvider cr;
	private static float[] level = new float[3];
	private float LIGHT_AVERAGE;
	private float LIGHT_DIFFERENCE;
	private FollowLineForward fLF;
	private SolveMaze solveMaze;
	private final float LIGHT_MULTIPLIER = 50;
	private final float START_SPEED = 150;
	private final float LINE_COLOUR_LEVEL = (float) 0.37;
	
	public TurnAtJunction(EV3ColorSensor csm, EV3ColorSensor csl, EV3ColorSensor csr, BaseRegulatedMotor mLeft, BaseRegulatedMotor mRight, FollowLineForward fLF, SolveMaze solveMaze, float LIGHT_AVERAGE, float LIGHT_DIFFERENCE) {
		this.csm = csm;
		this.csl = csl;
		this.csr = csr;
		this.mLeft = mLeft;
		this.mRight = mRight;
		this.cm = csm.getRedMode();
		this.cl = csl.getRedMode();
		this.cr = csr.getRedMode();
		this.fLF = fLF;
		this.solveMaze = solveMaze;
		this.LIGHT_AVERAGE = LIGHT_AVERAGE;
		this.LIGHT_DIFFERENCE = LIGHT_DIFFERENCE;
	}
	
	public static float[] getReading(SampleProvider cm, SampleProvider cl, SampleProvider cr, float[] minArray, float[] level, int step) {
		//get all readings
		cm.fetchSample(level, 0);
		cl.fetchSample(level, 1);
		cr.fetchSample(level, 2);
		
		for (int j = 0; j < 3; j++) {
			//find the minimum value, as any sample could be on the line, we have to account for a margin of error where the robot may not be
			//perfectly on the tape (usually due to imperfectly drawn mazes, needs to be exactly 90 degree for no error, or the friction of 
			//the surface which may not read the tape colour the first reading.
			if (minArray[j] > level[j]) { minArray[j] = level[j]; }
		}
		//stop for a bit to get multiple readings per "wiggle" of the robot
		Delay.msDelay(100);
		//recursive call to get multiple readings to avoid clogging action() method with lots of repeated code
		if (step != 0) { getReading(cm, cl, cr, minArray, level, step - 1); }
		return minArray;
	}
	
	public static void goForward(BaseRegulatedMotor mLeft, BaseRegulatedMotor mRight, long delay) {
		mLeft.startSynchronization();
		mLeft.forward();
		mRight.forward();
		mLeft.endSynchronization();
		Delay.msDelay(delay);
	}
	
	public static void goBack(BaseRegulatedMotor mLeft, BaseRegulatedMotor mRight, long delay) {
		mLeft.startSynchronization();
		mLeft.backward();
		mRight.backward();
		mLeft.endSynchronization();
		Delay.msDelay(delay);
	}
	
	public static void stop(BaseRegulatedMotor mLeft, BaseRegulatedMotor mRight) {
		mLeft.startSynchronization(); 
		mLeft.stop();
		mRight.stop();
		mLeft.endSynchronization();	
	}
	
	public static void rotateRight(BaseRegulatedMotor mLeft, BaseRegulatedMotor mRight, long delay) {
		mLeft.startSynchronization();
		mLeft.forward();
		mRight.backward();
		mLeft.endSynchronization();
		Delay.msDelay(delay);
	}
	
	public static void rotateLeft(BaseRegulatedMotor mLeft, BaseRegulatedMotor mRight, long delay) {
		mLeft.startSynchronization();
		mLeft.backward();
		mRight.forward();
		mLeft.endSynchronization();
		Delay.msDelay(delay);
	}

	public static void turnLeft(BaseRegulatedMotor mRight, long delay) {
		mRight.forward();
		Delay.msDelay(delay);
	}
	
	public static void turnRight(BaseRegulatedMotor mLeft, long delay) {
		mLeft.forward();
		Delay.msDelay(delay);
	}

	@Override
	public boolean takeControl() {
		cl.fetchSample(level, 1);
		cr.fetchSample(level, 2);
		return (level[1] < LINE_COLOUR_LEVEL || level[2] < LINE_COLOUR_LEVEL);
	}

	@Override
	public void action() {
//		get speeds from thread to keep movement consistent, then inch ahead of the junction to get ready to detect splits
		float rSpeed = fLF.getRSpeed();
		float lSpeed = fLF.getLSpeed();
		stop(mLeft, mRight);
		goForward(mLeft, mRight, 250);
		stop(mLeft, mRight);
		float[] minArray = {1,1,1};
//		constant speeds for "wiggling" to detect splits, so no margin of error
		mLeft.setSpeed(150);
		mRight.setSpeed(150);
//		movement to "wiggle" to detect splits using recursive getReading() method	
		rotateLeft(mLeft, mRight, 0);
		minArray = getReading(cm, cl, cr, minArray, level, 2);  
		stop(mLeft, mRight);
		Delay.msDelay(1000);
		rotateRight(mLeft, mRight, 0);
		minArray = getReading(cm, cl, cr, minArray, level, 5);
		stop(mLeft, mRight);
		Delay.msDelay(1000);
		rotateLeft(mLeft, mRight, 0);
		minArray = getReading(cm, cl, cr, minArray, level, 2);
		stop(mLeft, mRight);
		Delay.msDelay(1000);
		rotateRight(mLeft, mRight, 65);
		stop(mLeft, mRight);
		Delay.msDelay(1000);
//		display verbose output of readings to the user
		LCD.drawString("CL:", 0, 5);
		LCD.drawString("CM:", 5, 5);
		LCD.drawString("CR:", 10, 5);
		LCD.drawString(String.valueOf(minArray[1]), 0, 7);
		LCD.drawString(String.valueOf(minArray[0]), 5, 7);
		LCD.drawString(String.valueOf(minArray[2]), 10, 7);
//		determine what type of junction this is, and then output it to the user for clarity
		String junction = null;
		if (minArray[0] < LINE_COLOUR_LEVEL && minArray[1] < LINE_COLOUR_LEVEL && minArray[2] < LINE_COLOUR_LEVEL) { junction = ("3"); LCD.drawString("3 SPLIT", 2, 6); }
		else if (minArray[1] < LINE_COLOUR_LEVEL && minArray[2] < LINE_COLOUR_LEVEL) { junction = ("T"); LCD.drawString("T SPLIT", 2, 6); }
		else if (minArray[0] < LINE_COLOUR_LEVEL && minArray[2] < LINE_COLOUR_LEVEL) { junction = ("UR"); LCD.drawString("UP RIGHT", 2, 6); }
		else if (minArray[0] < LINE_COLOUR_LEVEL && minArray[1] < LINE_COLOUR_LEVEL) { junction = ("UL"); LCD.drawString("UP LEFT", 2, 6); }
		else if (minArray[1] < LINE_COLOUR_LEVEL) { junction = ("L"); LCD.drawString("LEFT", 2, 6); }
		else if (minArray[2] < LINE_COLOUR_LEVEL) { junction = ("R"); LCD.drawString("RIGHT", 2, 6); }
//		set speeds back to constant for consistency
		mRight.setSpeed(200);
		mLeft.setSpeed(200);
//		go back to realign with the tape, then go forwards to make a turn
		goBack(mLeft, mRight, 1000);
		goForward(mLeft, mRight, 0);
		level[1] = 1;
		level[2] = 1;
//		go forward along the line until at the junction to make a turn
		while (level[2] > LINE_COLOUR_LEVEL && level[1] > LINE_COLOUR_LEVEL) {
			cm.fetchSample(level, 0);
			rSpeed = START_SPEED + LIGHT_MULTIPLIER * ((LIGHT_AVERAGE - level[0]) / LIGHT_DIFFERENCE);
			mRight.setSpeed(rSpeed);
			lSpeed = START_SPEED - LIGHT_MULTIPLIER * ((LIGHT_AVERAGE - level[0]) / LIGHT_DIFFERENCE);
			mLeft.setSpeed(lSpeed);
			cl.fetchSample(level, 1);
			cr.fetchSample(level, 2);
		}
//		stop at the junction and make a decision by calling the atjunction() method in the solveMaze class using Tremaux algorithm
		stop(mLeft, mRight);
		int decision = 5;
		if (junction.equals("3")) { decision = solveMaze.atjunction(true, true, true); }
		else if (junction.equals("T")) { decision = solveMaze.atjunction(true, false, true); }
		else if (junction.equals("UR")) { decision = solveMaze.atjunction(false, true, true); }
		else if (junction.equals("UL")) { decision = solveMaze.atjunction(true, true, false); }
		else if (junction.equals("L")) { decision = solveMaze.atjunction(true, false, false); }
		else if (junction.equals("R")) { decision = solveMaze.atjunction(false, false, true); }
//		constant speeds for consistent turning
		mLeft.setSpeed(150);
		mRight.setSpeed(150);
//		make a turn based on the return value of atjunction()
		if (decision == 0) {} //dead end code, do nothing.
		else if (decision == 1) { Delay.msDelay(5000); } //Ensure thread catches up
		else if (decision == 2) { 
			level[0] = 1;
			turnRight(mLeft, 600);
			while (level[0] > LINE_COLOUR_LEVEL) { cm.fetchSample(level, 0); }
			stop(mLeft, mRight);
		}
		else if (decision == 3) {
			level[0] = 1;
			rotateRight(mLeft, mRight, 500);
			while (level[0] > LINE_COLOUR_LEVEL) { cm.fetchSample(level, 0); }
			Delay.msDelay(80);
			stop(mLeft, mRight);
		}
		else if (decision == 4) {
			level[0] = 1;
			turnLeft(mRight, 800);
			while (level[0] > LINE_COLOUR_LEVEL) { cm.fetchSample(level, 0); }
			Delay.msDelay(130);
			stop(mLeft, mRight);
		}
		level[0] = 1;
		level[1] = 1;
		level[2] = 1;
		LCD.clear();
		Delay.msDelay(1000);
	}
	
	@Override
	public void suppress() {}
//	action will always exit gracefully, no need for a suppress method.
}
