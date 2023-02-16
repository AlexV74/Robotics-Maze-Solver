import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.NXTColorSensor;

public class DetectDeadEnd implements Behavior{
	private boolean notifyAction;
	private BaseRegulatedMotor mLeft;
	private BaseRegulatedMotor mRight;
	private EV3ColorSensor csRGB;
	private SampleProvider sp;
	private float[] samples = new float[4];
	private EV3ColorSensor csm;
	private SampleProvider cm;
	
	public DetectDeadEnd(boolean notifyAction, BaseRegulatedMotor mLeft, BaseRegulatedMotor mRight, EV3ColorSensor csm, EV3ColorSensor csRGB) {
		this.notifyAction = false;
		this.mLeft = mLeft;
		this.mRight = mRight;
		this.cm = csm.getRedMode();
		this.csRGB = csRGB;
		this.sp = csRGB.getAmbientMode();
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

	public boolean takeControl() {
		sp.fetchSample(samples, 0); // samples[0] contains the ambient light level.
		sp = csRGB.getRGBMode();
		sp.fetchSample(samples, 1);
		
		int red = (int) samples[1];
		int green = (int) samples[2];
		int blue = (int) samples[3];
	// when sensors detect the colour red return true else return false.
		if (red > blue && red > green){
			return true;
		}
		else {
			return false;
		}
	}
	
	public void action() {
	//when the field equates to true, stop moving and turn 180 degrees.
		if (this.notifyAction == true) {
			float[] level = {1};
			rotateRight(mLeft, mRight, 0);
			Delay.msDelay(400);
			while (level[0] > 0.29) { cm.fetchSample(level, 0); }
			stop(mLeft, mRight);
		}
	}
	
	public void suppress() {
	// when takeControl() is true, set the field to being true
		if (takeControl() == true) {
			this.notifyAction = true;
		}		
	}
}




	
