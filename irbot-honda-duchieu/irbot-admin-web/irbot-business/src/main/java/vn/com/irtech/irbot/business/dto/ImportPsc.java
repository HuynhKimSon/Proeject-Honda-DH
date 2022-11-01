package vn.com.irtech.irbot.business.dto;

import java.util.List;

import vn.com.irtech.irbot.business.domain.Psc;
import vn.com.irtech.irbot.business.domain.PscDetail;

public class ImportPsc extends Psc {

	private static final long serialVersionUID = 1L;

	private List<PscDetail> details;

	public List<PscDetail> getDetails() {
		return details;
	}

	public void setDetails(List<PscDetail> details) {
		this.details = details;
	}

}
