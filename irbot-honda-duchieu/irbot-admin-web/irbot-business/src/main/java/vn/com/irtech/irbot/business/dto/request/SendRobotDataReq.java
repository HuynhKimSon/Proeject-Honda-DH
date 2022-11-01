package vn.com.irtech.irbot.business.dto.request;

import java.io.Serializable;

public class SendRobotDataReq implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String ids;

	private String accountHcr;

	private String accountHpm;
	
	private String userName;

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getAccountHcr() {
		return accountHcr;
	}

	public void setAccountHcr(String accountHcr) {
		this.accountHcr = accountHcr;
	}

	public String getAccountHpm() {
		return accountHpm;
	}

	public void setAccountHpm(String accountHpm) {
		this.accountHpm = accountHpm;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
