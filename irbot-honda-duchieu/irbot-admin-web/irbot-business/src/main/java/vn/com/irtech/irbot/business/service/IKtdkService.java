package vn.com.irtech.irbot.business.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import vn.com.irtech.core.common.domain.AjaxResult;
import vn.com.irtech.irbot.business.domain.Ktdk;
import vn.com.irtech.irbot.business.dto.request.SendRobotDataReq;
import vn.com.irtech.irbot.business.dto.request.UpdateStatusReq;

public interface IKtdkService {

	public Ktdk selectKtdkById(Long id);

	public List<Ktdk> selectKtdkList(Ktdk ktdk);

	public int insertKtdk(Ktdk ktdk);

	public int updateKtdk(Ktdk ktdk);

	public int deleteKtdkByIds(String ids) throws Exception;

	public int deleteKtdkById(Long id);

	public AjaxResult importKtdk(MultipartFile file, String unitCode, String userName) throws Exception;

	public void updateStatus(UpdateStatusReq request);

	public int retry(SendRobotDataReq request) throws Exception;
	
	public void changeRepair(String ids) throws Exception;

}