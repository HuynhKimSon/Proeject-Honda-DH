package vn.com.irtech.irbot.business.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import vn.com.irtech.core.common.domain.AjaxResult;
import vn.com.irtech.irbot.business.domain.KtdkPt;
import vn.com.irtech.irbot.business.dto.request.SendRobotDataReq;
import vn.com.irtech.irbot.business.dto.request.UpdateStatusReq;

public interface IKtdkPtService {

	public KtdkPt selectKtdkPtById(Long id);

	public List<KtdkPt> selectKtdkPtList(KtdkPt ktdkPt);

	public int insertKtdkPt(KtdkPt ktdkPt);

	public int updateKtdkPt(KtdkPt ktdkPt);

	public int deleteKtdkPtByIds(String ids) throws Exception;

	public int deleteKtdkPtById(Long id);

	public AjaxResult importKtdkPt(MultipartFile file, String unitCode, String userName) throws Exception;

	public void updateStatus(UpdateStatusReq request);

	public int retry(SendRobotDataReq request) throws Exception;
	
	public void changeRepair(String ids) throws Exception;

}