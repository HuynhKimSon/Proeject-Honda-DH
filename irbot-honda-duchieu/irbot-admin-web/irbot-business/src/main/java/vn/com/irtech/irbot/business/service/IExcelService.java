package vn.com.irtech.irbot.business.service;

import java.io.InputStream;

import vn.com.irtech.irbot.business.dto.response.ResultImportKtdkPtRes;
import vn.com.irtech.irbot.business.dto.response.ResultImportKtdkRes;
import vn.com.irtech.irbot.business.dto.response.ResultImportPscRes;

public interface IExcelService {
	
	public ResultImportKtdkRes importKtdk(InputStream is);
	
	public ResultImportKtdkPtRes importKtdkPt(InputStream is);
	
	public ResultImportPscRes importPsc(InputStream is);

}
