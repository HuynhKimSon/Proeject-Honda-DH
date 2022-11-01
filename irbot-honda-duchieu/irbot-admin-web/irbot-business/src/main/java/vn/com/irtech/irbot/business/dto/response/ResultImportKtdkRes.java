package vn.com.irtech.irbot.business.dto.response;

import java.io.Serializable;
import java.util.List;

import vn.com.irtech.irbot.business.domain.Ktdk;

public class ResultImportKtdkRes implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Ktdk> listKtdk;

	private List<String> errors;

	public List<Ktdk> getListKtdk() {
		return listKtdk;
	}

	public void setListKtdk(List<Ktdk> listKtdk) {
		this.listKtdk = listKtdk;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

}
