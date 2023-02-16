import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.NXTColorSensor;

public class DetectFinishPoint implements Behavior{
	private boolean notifyAction;
	private static boolean mazeSolved = false;
	private BaseRegulatedMotor mLeft; 
	private BaseRegulatedMotor mRight; 
	private EV3ColorSensor csRGB;
	private SampleProvider sp;
	private float[] samples = new float[4]; 
	
	public DetectFinishPoint(boolean notifyAction, BaseRegulatedMotor mLeft, BaseRegulatedMotor mRight, EV3ColorSensor csRGB) {
		this.notifyAction = false;
		this.mLeft = mLeft;
		this.mRight = mRight;
		this.csRGB = csRGB;
		this.sp =  csRGB.getAmbientMode();
	}
	
	public static boolean getMazeSolved() { return mazeSolved; }
	
	public static void stop(BaseRegulatedMotor mLeft, BaseRegulatedMotor mRight) {
		mLeft.startSynchronization(); 
		mLeft.stop();
		mRight.stop();
		mLeft.endSynchronization();	
	}

	public boolean takeControl() {
		sp.fetchSample(samples, 0); // samples[0] contains the ambient light level.
		sp = csRGB.getRGBMode();
		sp.fetchSample(samples, 1);
		
		int red = (int) samples[1];
		int green = (int) samples[2];
		int blue = (int) samples[3];
	// when sensors detect the colour red return true else return false.
		if (green > blue && green > red){
			return true;
		}
		else {
			return false;
		}
	}
	
	public void action() {
	//when the field equates to true, stop moving and print message : maze solved.
		if (this.notifyAction == true) {
			stop(mLeft, mRight);
			mazeSolved = true;
			Delay.msDelay(1000);
			LCD.drawString("MAZE SOLVED", 2, 2);
			Button.waitForAnyPress();
		}
	}
	
	public void suppress() {
	// when takeControl() is true, set the field to being true
		if (takeControl() == true) {
			this.notifyAction = true;
		}		
	}
}
