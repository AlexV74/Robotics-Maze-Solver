import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class MazeSolver {
	public static void main(String[] args) {
		EV3ColorSensor csm = new EV3ColorSensor(SensorPort.S2); //0
		EV3ColorSensor csl = new EV3ColorSensor(SensorPort.S1); //1
		EV3ColorSensor csr = new EV3ColorSensor(SensorPort.S3); //2
		EV3ColorSensor csRGB = new EV3ColorSensor(SensorPort.S4); //rgb colours
		SampleProvider cm = csm.getRedMode();
		float[] level = new float[3];
		BaseRegulatedMotor mLeft = new EV3LargeRegulatedMotor(MotorPort.A);
		BaseRegulatedMotor mRight = new EV3LargeRegulatedMotor(MotorPort.B);
//		welcome screen
		LCD.drawString("Maze Solver", 0, 0);
		LCD.drawString("Alex Verboom", 0, 1);
		LCD.drawString("Cameron Frisby", 0, 2);
		LCD.drawString("Ben Warne", 0, 3);
		LCD.drawString("Karen Bola", 0, 4);
		LCD.drawString("v4.0.0 Final", 6, 7);
		Button.waitForAnyPress();
		LCD.clear();
//		calibrate sensors and assign them to average and range values for motor speed calculation
		float[] LIGHT_ARRAY = Calibrate(cm);
		float LIGHT_AVERAGE = LIGHT_ARRAY[0];
		float LIGHT_DIFFERENCE = LIGHT_ARRAY[1];
//		instantiate and start thread and maze solving algorithm objects
		FollowLineForward fLF = new FollowLineForward(csm, csl, csr, mLeft, mRight, LIGHT_AVERAGE, LIGHT_DIFFERENCE);
		Thread forwardThread = new Thread(fLF);
		SolveMaze solveMaze = new SolveMaze();
		forwardThread.start();
//		instantiate behaviour objects
		Behavior TurnAtJunction = new TurnAtJunction(csm, csl, csr, mLeft, mRight, fLF, solveMaze, LIGHT_AVERAGE, LIGHT_DIFFERENCE);
		Behavior DetectDeadEnd = new DetectDeadEnd(false, mLeft, mRight, csm, csRGB);
		Behavior DetectFinishPoint = new DetectFinishPoint(false, mLeft, mRight, csRGB);
		Behavior BatteryLevel = new BatteryLevel();
//		instantiate and run arbitrator
		Arbitrator ab = new Arbitrator(new Behavior[] {TurnAtJunction, DetectDeadEnd, DetectFinishPoint, BatteryLevel});
		ab.go();
	}
	
	public static float[] Calibrate(SampleProvider colour) {
		float[] level = new float[1];
		float maxValue = 0;
		float minValue = 1000;
		while (true) {
			//calibrate sensors based on lowest and highest readings and show output to user.	
			colour.fetchSample(level, 0);
			if (level[0] > maxValue) { maxValue = level[0]; }
			if (level[0] < minValue) { minValue = level[0]; }
			String sample = String.valueOf(level[0]);
		    String maxValueString = String.valueOf(maxValue);
		    String minValueString = String.valueOf(minValue);
			LCD.drawString(sample, 2, 2);
			LCD.drawString("Min: " + minValueString, 2, 3);
			LCD.drawString("Max: " + maxValueString, 2, 4);
			if (Button.waitForAnyPress(2000) == 2) {
				LCD.clear();
				float[] returnArray = {((minValue + maxValue) / 2), maxValue - minValue};
				return returnArray;
			}
		}
	}
}
