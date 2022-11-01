package vn.com.irtech.irbot.business.dto;

import java.io.Serializable;

public class ProcessConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	private String url;

	private String userNameHcr;

	private String passWordHcr;

	private String userNameHpm;

	private String passWordHpm;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserNameHcr() {
		return userNameHcr;
	}

	public void setUserNameHcr(String userNameHcr) {
		this.userNameHcr = userNameHcr;
	}

	public String getPassWordHcr() {
		return passWordHcr;
	}

	public void setPassWordHcr(String passWordHcr) {
		this.passWordHcr = passWordHcr;
	}

	public String getUserNameHpm() {
		return userNameHpm;
	}

	public void setUserNameHpm(String userNameHpm) {
		this.userNameHpm = userNameHpm;
	}

	public String getPassWordHpm() {
		return passWordHpm;
	}

	public void setPassWordHpm(String passWordHpm) {
		this.passWordHpm = passWordHpm;
	}

}
