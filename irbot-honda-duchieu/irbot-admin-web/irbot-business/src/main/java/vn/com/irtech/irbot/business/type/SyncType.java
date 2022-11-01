package vn.com.irtech.irbot.business.type;

public enum SyncType {
	KTDK(1), KTDKPT(2), PSC(3);

	private final Integer value;

	SyncType(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

	public static SyncType fromValue(Integer value) {
		for (SyncType e : SyncType.values()) {
			if (value == e.value) {
				return e;
			}
		}
		return null;
	}
}
