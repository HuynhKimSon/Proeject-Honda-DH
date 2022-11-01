package vn.com.irtech.irbot.business.dto;

import java.util.List;

import vn.com.irtech.irbot.business.domain.KtdkPt;
import vn.com.irtech.irbot.business.domain.KtdkPtDetail;

public class ImportKtdkPt extends KtdkPt  {

	private static final long serialVersionUID = 1L;

	private List<KtdkPtDetail> details;

	public List<KtdkPtDetail> getDetails() {
		return details;
	}

	public void setDetails(List<KtdkPtDetail> details) {
		this.details = details;
	}
	
}
