package vn.com.irtech.irbot.business.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import vn.com.irtech.core.common.domain.AjaxResult;
import vn.com.irtech.irbot.business.domain.Psc;
import vn.com.irtech.irbot.business.dto.request.SendRobotDataReq;
import vn.com.irtech.irbot.business.dto.request.UpdateStatusReq;

public interface IPscService {

	public Psc selectPscById(Long id);

	public List<Psc> selectPscList(Psc psc);

	public int insertPsc(Psc psc);

	public int updatePsc(Psc psc);

	public int deletePscByIds(String ids) throws Exception;

	public int deletePscById(Long id);

	public AjaxResult importPsc(MultipartFile file, String unitCode, String userName) throws Exception;

	public void updateStatus(UpdateStatusReq request);

	public int retry(SendRobotDataReq request) throws Exception;

}