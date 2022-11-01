package vn.com.irtech.irbot.business.type;

public enum DetailType {
	SPARE(0), JOB(1);

	private final Integer value;

	DetailType(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

	public static DetailType fromValue(Integer value) {
		for (DetailType e : DetailType.values()) {
			if (value == e.value) {
				return e;
			}
		}
		return null;
	}
}
