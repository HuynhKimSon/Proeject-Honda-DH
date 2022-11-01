package vn.com.irtech.irbot.business.dto;

import java.io.Serializable;

public class RobotSyncSpareDetailReq implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;

	private String quantity;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

}
