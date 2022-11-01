package vn.com.irtech.irbot.business.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.com.irtech.core.common.utils.DateUtils;
import vn.com.irtech.core.system.service.ISysConfigService;
import vn.com.irtech.core.system.service.ISysDictDataService;
import vn.com.irtech.irbot.business.domain.Ktdk;
import vn.com.irtech.irbot.business.domain.Psc;
import vn.com.irtech.irbot.business.domain.PscDetail;
import vn.com.irtech.irbot.business.domain.Robot;
import vn.com.irtech.irbot.business.domain.WorkProcess;
import vn.com.irtech.irbot.business.dto.ProcessConfig;
import vn.com.irtech.irbot.business.dto.RobotSyncKtdkReq;
import vn.com.irtech.irbot.business.dto.request.SendRobotDataReq;
import vn.com.irtech.irbot.business.dto.request.UpdateStatusReq;
import vn.com.irtech.irbot.business.dto.response.ResultImportKtdkRes;
import vn.com.irtech.irbot.business.mapper.KtdkMapper;
import vn.com.irtech.irbot.business.mapper.PscDetailMapper;
import vn.com.irtech.irbot.business.mapper.PscMapper;
import vn.com.irtech.irbot.business.mapper.RobotMapper;
import vn.com.irtech.irbot.business.mapper.WorkProcessMapper;
import vn.com.irtech.irbot.business.service.IExcelService;
import vn.com.irtech.irbot.business.service.IKtdkService;
import vn.com.irtech.irbot.business.type.DetailType;
import vn.com.irtech.irbot.business.type.ProcessStatus;
import vn.com.irtech.irbot.business.type.RobotServiceType;
import vn.com.irtech.irbot.business.type.RobotStatusType;
import vn.com.irtech.irbot.business.type.UnitCode;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import vn.com.irtech.core.common.domain.AjaxResult;
import vn.com.irtech.core.common.text.Convert;

@Service
public class KtdkServiceImpl implements IKtdkService {

	private static final Logger logger = LoggerFactory.getLogger(KtdkServiceImpl.class);

	@Autowired
	private KtdkMapper ktdkMapper;

	@Autowired
	private PscMapper pscMapper;

	@Autowired
	private PscDetailMapper pscDetailMapper;

	@Autowired
	private IExcelService excelService;

	@Autowired
	private RobotMapper robotMapper;

	@Autowired
	private WorkProcessMapper workProcessMapper;

	@Autowired
	private ISysConfigService configService;

	@Autowired
	private ISysDictDataService sysDictDataService;

	@Override
	public Ktdk selectKtdkById(Long id) {
		return ktdkMapper.selectKtdkById(id);
	}

	@Override
	public List<Ktdk> selectKtdkList(Ktdk ktdk) {
		return ktdkMapper.selectKtdkList(ktdk);
	}

	@Override
	public int insertKtdk(Ktdk ktdk) {
		ktdk.setCreateTime(DateUtils.getNowDate());
		return ktdkMapper.insertKtdk(ktdk);
	}

	@Override
	public int updateKtdk(Ktdk ktdk) {
		ktdk.setUpdateTime(DateUtils.getNowDate());
		return ktdkMapper.updateKtdk(ktdk);
	}

	@Override
	public int deleteKtdkByIds(String ids) throws Exception {
		logger.info("Delete Kiem tra dinh ky!");
		Long idArr[] = Convert.toLongArray(ids);
		Integer reusult = 0;
		for (Long id : idArr) {
			// Check status record
			Ktdk ktdkSelect = ktdkMapper.selectKtdkById(id);

			if (ktdkSelect.getStatus() != ProcessStatus.NOTSEND.value()) {
				throw new Exception("Phiếu dữ liệu có số máy " + ktdkSelect.getVehicleCode()
						+ " đã được gửi lệnh, vui lòng kiểm tra lại!");
			}

			// Delete master Ktdk
			ktdkMapper.deleteKtdkById(id);

			reusult++;
		}
		return reusult;
	}

	@Override
	public int deleteKtdkById(Long id) {
		return ktdkMapper.deleteKtdkById(id);
	}

	@Override
	@Transactional
	public AjaxResult importKtdk(MultipartFile file, String unitCode, String userName) throws Exception {
		logger.info("Read file excel Kiem tra dinh ky!");
		ResultImportKtdkRes resultImport = excelService.importKtdk(file.getInputStream());

		if (resultImport == null) {
			throw new Exception("File không có dữ liệu hoặc không đúng định dạng, vui lòng kiểm tra lại!");
		}

		AjaxResult ajaxResult = AjaxResult.success();

		// Check return errors
		if (resultImport.getErrors().size() != 0) {
			ajaxResult.put("errors", resultImport.getErrors());
			return ajaxResult;
		}

		// Insert vao table ktdk
		List<Ktdk> importKtdkList = resultImport.getListKtdk();
		for (Ktdk importKtdk : importKtdkList) {

			Date now = new Date();
			importKtdk.setCreateTime(now);

			// Check exist data date now
			List<Ktdk> selectKtdk = ktdkMapper.selectKtdkByVehicleCode(importKtdk.getVehicleCode());

			if (!CollectionUtils.isEmpty(selectKtdk)) {
				Ktdk ktdkExist = selectKtdk.get(0);
				String currentDayStr = DateUtils.parseDateToStr("yyyy-MM-dd", ktdkExist.getCreateTime());
				String importDateStr = DateUtils.parseDateToStr("yyyy-MM-dd", now);
				if (currentDayStr.equals(importDateStr)) {
					continue;
				}
			}

			// Check unitCode
			if (Integer.parseInt(unitCode) == UnitCode.DUC_HIEU_1.value()) {
				importKtdk.setUnitCode(UnitCode.DUC_HIEU_1.value());
			} else {
				importKtdk.setUnitCode(UnitCode.DUC_HIEU_2.value());
			}

			importKtdk.setStatus(ProcessStatus.NOTSEND.value());
			importKtdk.setCreateBy(userName);

			ktdkMapper.insertKtdk(importKtdk);
		}

		return ajaxResult;
	}

	@Override
	public void updateStatus(UpdateStatusReq request) {
		logger.info("Reset Status Kiem tra dinh ky!");
		Long idArr[] = Convert.toLongArray(request.getIds());
		for (Long id : idArr) {
			// Update status Ktdk
			Ktdk ktdkExist = ktdkMapper.selectKtdkById(id);
			if (ktdkExist != null) {
				Ktdk ktdkUpdate = new Ktdk();
				ktdkUpdate.setId(id);
				ktdkUpdate.setStatus(request.getStatus());
				ktdkMapper.updateKtdk(ktdkUpdate);
			}

			// Update status WorkProcess
			WorkProcess workProcessSelect = new WorkProcess();
			workProcessSelect.setSyncId(id);
			workProcessSelect.setServiceId(RobotServiceType.KTDK.value());

			List<WorkProcess> workProcessExistList = workProcessMapper.selectWorkProcessList(workProcessSelect);
			if (!CollectionUtils.isEmpty(workProcessExistList)) {
				WorkProcess workProcessExist = workProcessExistList.get(0);
				if (workProcessExist != null) {
					// Delete record if status equals NOTSEND
					if (request.getStatus() == ProcessStatus.NOTSEND.value()) {
						workProcessMapper.deleteWorkProcessById(workProcessExist.getId());
					} else {
						WorkProcess workProcessUpdate = new WorkProcess();
						workProcessUpdate.setId(workProcessExist.getId());
						workProcessUpdate.setStatus(request.getStatus());
						workProcessMapper.updateWorkProcess(workProcessUpdate);
					}
				}
			}
		}
	}

	@Override
	@Transactional
	public int retry(SendRobotDataReq request) throws Exception {
		int result = 0;
		try {
			// Get a robot available, busy
			List<Robot> robots = robotMapper.selectRobotByServices(new Integer[] { RobotServiceType.KTDK.value() },
					new String[] { RobotStatusType.AVAILABLE.value(), RobotStatusType.BUSY.value() });

			// Case if have not any robot available
			if (CollectionUtils.isEmpty(robots)) {
				throw new Exception("Không tìm thấy robot khả dụng để làm lệnh!");
			}

			Map<RobotSyncKtdkReq, Robot> robotRequestMap = new HashMap<RobotSyncKtdkReq, Robot>();
			Long[] idArr = Convert.toLongArray(request.getIds());
			Date now = new Date();

			for (Long id : idArr) {

				Ktdk ktdkSelect = ktdkMapper.selectKtdkById(id);
				// Check Status
				if (ktdkSelect.getStatus() == ProcessStatus.SUCCESS.value()) {
					throw new Exception("Tồn tại Phiếu đã đồng bộ thành công!");
				}

				WorkProcess workProcessNew = new WorkProcess();
				workProcessNew.setServiceId(RobotServiceType.KTDK.value());
				workProcessNew.setSyncId(ktdkSelect.getId());
				workProcessNew.setPriority(RandomUtils.nextInt(0, 9));
				workProcessNew.setStatus(ProcessStatus.WAIT.value());
				workProcessNew.setStartDate(now);
				workProcessNew.setCreateBy(request.getUserName());

				// Add data request
				RobotSyncKtdkReq dataRequest = new RobotSyncKtdkReq();
				dataRequest.setConfig(getProcessConfig(request.getAccountHcr()));
				dataRequest.setServiceId(RobotServiceType.KTDK.value());
				dataRequest.setUnitCode(ktdkSelect.getUnitCode());
				dataRequest.setVehicleCode(ktdkSelect.getVehicleCode());
				dataRequest.setVehicleNumber(ktdkSelect.getVehicleNumber());
				dataRequest.setKm(ktdkSelect.getKm());
				dataRequest.setPi(ktdkSelect.getPi().toString());
				dataRequest.setTechnician(ktdkSelect.getTechnician());
				dataRequest.setFinalCheck(ktdkSelect.getFinalCheck());
				dataRequest.setCreateTime(
						DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, ktdkSelect.getCreateTime()));

				// Delete record exits table WorkProcess
				WorkProcess workProcessSelect = new WorkProcess();
				workProcessSelect.setSyncId(id);
				workProcessSelect.setServiceId(RobotServiceType.KTDK.value());

				List<WorkProcess> workProcessExistList = workProcessMapper.selectWorkProcessList(workProcessSelect);
				if (!CollectionUtils.isEmpty(workProcessExistList)) {
					workProcessMapper.deleteWorkProcessById(workProcessExistList.get(0).getId());
				}

				// Insert record into table WorkProcess
				workProcessMapper.insertWorkProcess(workProcessNew);

				// Update Ktdk
				ktdkSelect.setStatus(ProcessStatus.WAIT.value());
				ktdkSelect.setProcessId(workProcessNew.getId());
				ktdkSelect.setStep(null);
				ktdkSelect.setUpdateTime(now);
				ktdkMapper.updateKtdk(ktdkSelect);

				// Set processId Json (data_request) after insert success
				dataRequest.setProcessId(workProcessNew.getId());

				// Insert record (data_request) into robotRequestMap
				robotRequestMap.put(dataRequest, robots.get(0));

				result++;
				// Loop insert/update dataRequest into table workProcess
				if (robotRequestMap.size() > 0) {
					for (Map.Entry<RobotSyncKtdkReq, Robot> entry : robotRequestMap.entrySet()) {
						requestRobot(entry.getValue(), entry.getKey());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(">>>>>> Error: " + e.getMessage());
			throw e;
		}
		return result;
	}

	/**
	 * 
	 * @param userNameHcr
	 * @return ProcessConfig
	 */
	private ProcessConfig getProcessConfig(String userNameHcr) {
		ProcessConfig processConfig = new ProcessConfig();
		processConfig.setUrl(configService.selectConfigByKey("business.web.hms.url"));
		processConfig.setUserNameHcr(userNameHcr);
		processConfig.setPassWordHcr(sysDictDataService.selectDictValue(userNameHcr));
		return processConfig;
	}

	/**
	 * 
	 * @param robot
	 * @param process
	 */
	private void requestRobot(Robot robot, RobotSyncKtdkReq process) {

		String message = new Gson().toJson(process);

		WorkProcess processUpdate = new WorkProcess();
		processUpdate.setId(process.getProcessId());
		processUpdate.setDataRequest(message);
		processUpdate.setRobotUuid(robot.getUuid());
		workProcessMapper.updateWorkProcess(processUpdate);
	}

	@Override
	@Transactional
	public void changeRepair(String ids) throws Exception {
		logger.info("Change Repair Kiem tra dinh ky co phu tung!");
		Long[] idArr = Convert.toLongArray(ids);
		Date now = new Date();
		for (Long id : idArr) {
			Ktdk ktdkSelect = ktdkMapper.selectKtdkById(id);
			if (ktdkSelect == null) {
				throw new Exception("Không tìm thấy phiếu cần chuyển đổi! ");
			}
			if (ktdkSelect.getStatus() == ProcessStatus.SUCCESS.value()) {
				throw new Exception("Phiếu chuyển đổi đã đồng bộ thành công! ");
			}
			if (ktdkSelect.getStatus() == ProcessStatus.WAIT.value()
					|| ktdkSelect.getStatus() == ProcessStatus.SENT.value()
					|| ktdkSelect.getStatus() == ProcessStatus.PROCESSING.value()) {
				throw new Exception("Phiếu chuyển đổi đã được gửi cho robot! ");
			}

			// Check Exist
			Psc pscSelect = new Psc();
			pscSelect.setVehicleCode(ktdkSelect.getVehicleCode());
			pscSelect.setCreateTime(now);
			List<Psc> pscExistList = pscMapper.selectPscList(pscSelect);
			if (!CollectionUtils.isEmpty(pscExistList)) {
				Psc pscExist = pscExistList.get(pscExistList.size() - 1);
				String currentDayStr = DateUtils.parseDateToStr("yyyy-MM-dd", pscExist.getCreateTime());
				String convertDateStr = DateUtils.parseDateToStr("yyyy-MM-dd", now);
				if (currentDayStr.equals(convertDateStr)) {
					throw new Exception(
							"Phiếu sửa chữa có số máy " + ktdkSelect.getVehicleCode() + " đã tồn tại!");
				}
			}

			// Insert Ktdk into Psc
			Psc pscInsert = new Psc();
			pscInsert.setUnitCode(ktdkSelect.getUnitCode());
			pscInsert.setVehicleCode(ktdkSelect.getVehicleCode());
			pscInsert.setVehicleNumber(ktdkSelect.getVehicleNumber());
			pscInsert.setKm(ktdkSelect.getKm());
			pscInsert.setRepairType("RP");
			pscInsert.setTechnician(ktdkSelect.getTechnician());
			pscInsert.setFinalCheck(ktdkSelect.getFinalCheck());
			pscInsert.setStatus(ProcessStatus.NOTSEND.value());
			pscInsert.setCreateBy(ktdkSelect.getCreateBy());
			pscInsert.setCreateTime(now);
			pscMapper.insertPsc(pscInsert);

			// Insert Ktdk detail into Psc Detail
			PscDetail pscDetailInsert = new PscDetail();
			pscDetailInsert.setPscId(pscInsert.getId());
			pscDetailInsert.setCode("GR005");
			pscDetailInsert.setCreateTime(now);
			pscDetailInsert.setCreateBy(ktdkSelect.getCreateBy());
			pscDetailInsert.setTypeDetail(DetailType.JOB.value());
			// Add detail job, set default
			pscDetailInsert.setDescription("SCTT");
			pscDetailInsert.setPrice("100000");
			pscDetailMapper.insertPscDetail(pscDetailInsert);

			// Delete record in table WorkProcess
			workProcessMapper.deleteWorkProcessById(ktdkSelect.getProcessId());

			// Delete record in table Ktdk
			ktdkMapper.deleteKtdkById(id);
		}
	}

}
