package vn.com.irtech.irbot.business.type;

public enum RobotServiceType {
	KTDK(100), KTDKPT(200), PSC(300);

	private final Integer value;

	RobotServiceType(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

	public static RobotServiceType fromValue(Integer value) {
		for (RobotServiceType e : RobotServiceType.values()) {
			if (value == e.value) {
				return e;
			}
		}
		return null;
	}
}
