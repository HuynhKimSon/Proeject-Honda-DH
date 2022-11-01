package vn.com.irtech.irbot.business.type;

public enum DictType {
	BUSINESS_ACCOUNT_HCR_DUC_HIEU_1("business_account_hcr_duc_hieu_1"),
	BUSINESS_ACCOUNT_HPM_DUC_HIEU_1("business_account_hpm_duc_hieu_1"),
	BUSINESS_ACCOUNT_HCR_DUC_HIEU_2("business_account_hcr_duc_hieu_2"),
	BUSINESS_ACCOUNT_HPM_DUC_HIEU_2("business_account_hpm_duc_hieu_2"),
	BUSINESS_UNIT("business_unit");

	private final String value;

	DictType(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public static DictType fromValue(String value) {
		for (DictType e : DictType.values()) {
			if (value.equals(e.value)) {
				return e;
			}
		}
		return null;
	}

}
