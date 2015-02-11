package network.messages;

public class MotorMessage extends Message {
	private static final long serialVersionUID = -8317383403438281892L;
	//speeds are between -1 (backward) and 1 (forward)
	private double leftMotor;
	private double rightMotor;

	public MotorMessage(double leftMotor, double rightMotor) {
		super();
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}

	public double getLeftMotor() {
		return leftMotor;
	}

	public double getRightMotor() {
		return rightMotor;
	}
}
