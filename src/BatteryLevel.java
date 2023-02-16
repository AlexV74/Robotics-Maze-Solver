import lejos.hardware.Battery;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

public class BatteryLevel implements Behavior {
	private Battery battery;
	
	public BatteryLevel() {
		this.battery = new Battery();
	}
	
	public boolean takeControl() {
//		Battery battery = new Battery();
		int voltage = battery.getVoltageMilliVolt();
		if (voltage <= 3000) {
			return true;
		} else {
			return false;
		}
	}
	
	public void action() {
		LCD.drawString("LOW BATTERY", 2, 2);
		while (true) {
			Sound.beep();
			Delay.msDelay(500);
		}
	}
	
	public void suppress() {
		return;
	}
}
