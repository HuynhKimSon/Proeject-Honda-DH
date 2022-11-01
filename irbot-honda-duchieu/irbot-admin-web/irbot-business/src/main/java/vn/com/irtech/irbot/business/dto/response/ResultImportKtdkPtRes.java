package vn.com.irtech.irbot.business.dto.response;

import java.io.Serializable;
import java.util.List;
import vn.com.irtech.irbot.business.dto.ImportKtdkPt;

public class ResultImportKtdkPtRes implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ImportKtdkPt> listKtdkPt;

	private List<String> errors;

	public List<ImportKtdkPt> getListKtdkPt() {
		return listKtdkPt;
	}

	public void setListKtdkPt(List<ImportKtdkPt> listKtdkPt) {
		this.listKtdkPt = listKtdkPt;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

}
