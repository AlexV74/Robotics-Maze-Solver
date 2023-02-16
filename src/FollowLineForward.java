import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class FollowLineForward implements Runnable {
	private BaseRegulatedMotor mLeft;
	private BaseRegulatedMotor mRight;
	private EV3ColorSensor csm;
	private EV3ColorSensor csl;
	private EV3ColorSensor csr;
	private SampleProvider cm;
	private SampleProvider cl;
	private SampleProvider cr;
	private float rSpeed;
	private float lSpeed;
	private float[] level = new float[7];
	private float LIGHT_AVERAGE;
	private float LIGHT_DIFFERENCE;
	private final float LIGHT_MULTIPLIER = 50;
	private final float START_SPEED = 150;
	private final float LINE_COLOUR_LEVEL = (float) 0.35;
	
	public FollowLineForward(EV3ColorSensor csm, EV3ColorSensor csl, EV3ColorSensor csr, BaseRegulatedMotor mLeft, BaseRegulatedMotor mRight, float LIGHT_AVERAGE, float LIGHT_DIFFERENCE) { //EV3ColorSensor csRGB
		this.csm = csm;
		this.csl = csl;
		this.csr = csr;
		this.mLeft = mLeft;
		this.mRight = mRight;
		this.cm = csm.getRedMode();
		this.cl = csl.getRedMode();
		this.cr = csr.getRedMode();
		this.LIGHT_AVERAGE = LIGHT_AVERAGE;
		this.LIGHT_DIFFERENCE = LIGHT_DIFFERENCE;
	}
	
	public static void goForward(BaseRegulatedMotor mLeft, BaseRegulatedMotor mRight, long delay) {
		mLeft.startSynchronization();
		mLeft.forward();
		mRight.forward();
		mLeft.endSynchronization();
		Delay.msDelay(delay);
	}
	
	public static void stop(BaseRegulatedMotor mLeft, BaseRegulatedMotor mRight) {
		mLeft.startSynchronization(); 
		mLeft.stop();
		mRight.stop();
		mLeft.endSynchronization();	
	}
		
	public float getRSpeed() { return this.rSpeed; }
	
	public float getLSpeed() { return this.lSpeed; }
	
	@Override
	public void run() {
//		initialise variables and set speeds to constant
		cm.fetchSample(level, 0);
		mLeft.setSpeed(200);
		mRight.setSpeed(200);
		mLeft.synchronizeWith(new BaseRegulatedMotor[] {mRight});
		goForward(mLeft, mRight, 0);
//		continue going forward until maze is solved
		while (DetectFinishPoint.getMazeSolved() != true) {
//			constantly change speeds to keep on the tape using the sensor to adjust these speeds
			cm.fetchSample(level, 0);
			this.rSpeed = START_SPEED + LIGHT_MULTIPLIER * ((LIGHT_AVERAGE - level[0]) /  LIGHT_DIFFERENCE);
			mRight.setSpeed(rSpeed);
			this.lSpeed = START_SPEED - LIGHT_MULTIPLIER * ((LIGHT_AVERAGE - level[0]) /  LIGHT_DIFFERENCE);
			mLeft.setSpeed(lSpeed);
//			check for a junction
			cl.fetchSample(level, 1);
			cr.fetchSample(level, 2);
//			check to exit gracefully
			if (Button.waitForAnyPress(10) == 2) { System.exit(0); }
			if (level[1] < LINE_COLOUR_LEVEL || level[2] < LINE_COLOUR_LEVEL) {
				try { 
//					give control to TurnAtJunction method
					Thread.sleep(11000);
					goForward(mLeft, mRight, 0);
					level[1] = 1;
					level[2] = 1;
					Delay.msDelay(1000);
				} catch (InterruptedException e) { System.err.println("Interrupted, the behaviors are out of sync."); }
			}
		}
		return;
	}
}