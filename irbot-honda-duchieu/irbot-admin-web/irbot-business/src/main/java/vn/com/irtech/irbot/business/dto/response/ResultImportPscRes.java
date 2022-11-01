package vn.com.irtech.irbot.business.dto.response;

import java.io.Serializable;
import java.util.List;
import vn.com.irtech.irbot.business.dto.ImportPsc;

public class ResultImportPscRes implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ImportPsc> listPsc;

	private List<String> errors;

	public List<ImportPsc> getListPsc() {
		return listPsc;
	}

	public void setListPsc(List<ImportPsc> listPsc) {
		this.listPsc = listPsc;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

}
