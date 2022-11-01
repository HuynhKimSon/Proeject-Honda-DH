package vn.com.irtech.irbot.business.dto.request;

import java.io.Serializable;

public class ApiGetDataReq implements Serializable {

	private static final long serialVersionUID = 1L;

	private String startDate;

	private String endDate;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
