package vn.com.irtech.irbot.business.dto;

import java.io.Serializable;

public class RobotSyncJobDetailReq implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;

	private String description;

	private String price;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

}
