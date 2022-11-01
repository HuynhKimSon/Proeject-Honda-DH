package vn.com.irtech.irbot.business.type;

public enum UnitCode {
	ADMIN(100), DUC_HIEU_1(200), DUC_HIEU_2(210);

	private final Integer value;

	UnitCode(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

	public static UnitCode fromValue(Integer value) {
		for (UnitCode e : UnitCode.values()) {
			if (value == e.value) {
				return e;
			}
		}
		return null;
	}
}
