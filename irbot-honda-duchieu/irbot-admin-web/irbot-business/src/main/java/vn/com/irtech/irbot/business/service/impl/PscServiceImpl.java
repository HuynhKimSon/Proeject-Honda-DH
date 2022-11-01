package vn.com.irtech.irbot.business.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.com.irtech.core.common.utils.DateUtils;
import vn.com.irtech.core.common.utils.bean.BeanUtils;
import vn.com.irtech.core.system.service.ISysConfigService;
import vn.com.irtech.core.system.service.ISysDictDataService;
import vn.com.irtech.irbot.business.domain.Psc;
import vn.com.irtech.irbot.business.domain.PscDetail;
import vn.com.irtech.irbot.business.domain.Robot;
import vn.com.irtech.irbot.business.domain.WorkProcess;
import vn.com.irtech.irbot.business.dto.ImportPsc;
import vn.com.irtech.irbot.business.dto.ProcessConfig;
import vn.com.irtech.irbot.business.dto.RobotSyncJobDetailReq;
import vn.com.irtech.irbot.business.dto.RobotSyncPscReq;
import vn.com.irtech.irbot.business.dto.RobotSyncSpareDetailReq;
import vn.com.irtech.irbot.business.dto.request.SendRobotDataReq;
import vn.com.irtech.irbot.business.dto.request.UpdateStatusReq;
import vn.com.irtech.irbot.business.dto.response.ResultImportPscRes;

import vn.com.irtech.irbot.business.mapper.PscDetailMapper;
import vn.com.irtech.irbot.business.mapper.PscMapper;
import vn.com.irtech.irbot.business.mapper.RobotMapper;
import vn.com.irtech.irbot.business.mapper.WorkProcessMapper;
import vn.com.irtech.irbot.business.service.IExcelService;
import vn.com.irtech.irbot.business.service.IPscService;
import vn.com.irtech.irbot.business.type.DetailType;
import vn.com.irtech.irbot.business.type.ProcessStatus;
import vn.com.irtech.irbot.business.type.RobotServiceType;
import vn.com.irtech.irbot.business.type.RobotStatusType;
import vn.com.irtech.irbot.business.type.UnitCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import vn.com.irtech.core.common.domain.AjaxResult;
import vn.com.irtech.core.common.text.Convert;

@Service
public class PscServiceImpl implements IPscService {

	private static final Logger logger = LoggerFactory.getLogger(PscServiceImpl.class);

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
	public Psc selectPscById(Long id) {
		return pscMapper.selectPscById(id);
	}

	@Override
	public List<Psc> selectPscList(Psc psc) {
		return pscMapper.selectPscList(psc);
	}

	@Override
	public int insertPsc(Psc psc) {
		psc.setCreateTime(DateUtils.getNowDate());
		return pscMapper.insertPsc(psc);
	}

	@Override
	public int updatePsc(Psc psc) {
		psc.setUpdateTime(DateUtils.getNowDate());
		return pscMapper.updatePsc(psc);
	}

	@Override
	public int deletePscByIds(String ids) throws Exception {
		logger.info("Delete Phieu sua chua!");
		Long idArr[] = Convert.toLongArray(ids);
		Integer reusult = 0;
		for (Long id : idArr) {
			// Check status record
			Psc pscSelect = pscMapper.selectPscById(id);
			if (pscSelect.getStatus() != ProcessStatus.NOTSEND.value()) {
				throw new Exception("Phiếu dữ liệu có số máy " + pscSelect.getVehicleCode()
						+ " đã được gửi lệnh, vui lòng kiểm tra lại!");
			}

			// Delete detail Psc
			pscDetailMapper.deletePscDetailByPscId(id);

			// Delete master Psc
			pscMapper.deletePscById(id);

			reusult++;
		}
		return reusult;
	}

	@Override
	public int deletePscById(Long id) {
		return pscMapper.deletePscById(id);
	}

	@Override
	@Transactional
	public AjaxResult importPsc(MultipartFile file, String unitCode, String userName) throws Exception {
		logger.info("Read file excel Phieu sua chua!");
		ResultImportPscRes resultImport = excelService.importPsc(file.getInputStream());
		if (resultImport == null) {
			throw new Exception("File không có dữ liệu hoặc không đúng định dạng, vui lòng kiểm tra lại!");
		}

		AjaxResult ajaxResult = AjaxResult.success();

		// Check return errors
		if (resultImport.getErrors().size() != 0) {
			ajaxResult.put("errors", resultImport.getErrors());
			return ajaxResult;
		}

		// Insert data into table psc
		List<ImportPsc> importPscList = resultImport.getListPsc();
		for (ImportPsc importPsc : importPscList) {

			Date now = new Date();
			importPsc.setCreateTime(now);

			// Check exist data date now
			List<Psc> selectPsc = pscMapper.selectPscByVehicleCode(importPsc.getVehicleCode());

			if (!CollectionUtils.isEmpty(selectPsc)) {
				Psc pscExist = selectPsc.get(0);
				String currentDayStr = DateUtils.parseDateToStr("yyyy-MM-dd", pscExist.getCreateTime());
				String importDateStr = DateUtils.parseDateToStr("yyyy-MM-dd", now);
				if (currentDayStr.equals(importDateStr)) {
					continue;
				}
			}

			// Check unitCode
			if (Integer.parseInt(unitCode) == UnitCode.DUC_HIEU_1.value()) {
				importPsc.setUnitCode(UnitCode.DUC_HIEU_1.value());
			} else {
				importPsc.setUnitCode(UnitCode.DUC_HIEU_2.value());
			}

			importPsc.setStatus(ProcessStatus.NOTSEND.value());
			importPsc.setCreateBy(userName);

			Psc pscNew = new Psc();
			BeanUtils.copyProperties(importPsc, pscNew);
			pscMapper.insertPsc(pscNew);

			// Insert data into table psc_detail
			List<PscDetail> pscDetailList = importPsc.getDetails();
			for (PscDetail detail : pscDetailList) {
				PscDetail pscDetailNew = new PscDetail();
				pscDetailNew.setPscId(pscNew.getId());
				pscDetailNew.setCode(detail.getCode());
				pscDetailNew.setCreateTime(now);
				pscDetailNew.setCreateBy(userName);
				pscDetailNew.setTypeDetail(detail.getTypeDetail());
				// 0: spare 1: job
				if (detail.getTypeDetail() == DetailType.SPARE.value()) {
					pscDetailNew.setQuantity(detail.getQuantity());
				} else {
					pscDetailNew.setDescription(detail.getDescription());
					pscDetailNew.setPrice(detail.getPrice());
				}
				pscDetailMapper.insertPscDetail(pscDetailNew);
			}
		}

		return ajaxResult;
	}

	@Override
	public void updateStatus(UpdateStatusReq request) {
		logger.info("Reset Status Phieu sua chua!");
		Long idArr[] = Convert.toLongArray(request.getIds());
		for (Long id : idArr) {
			// Update status Psc
			Psc pscExist = pscMapper.selectPscById(id);
			if (pscExist != null) {
				Psc pscUpdate = new Psc();
				pscUpdate.setId(id);
				pscUpdate.setStatus(request.getStatus());
				pscMapper.updatePsc(pscUpdate);
			}

			// Update status WorkProcess
			WorkProcess workProcessSelect = new WorkProcess();
			workProcessSelect.setSyncId(id);
			workProcessSelect.setServiceId(RobotServiceType.PSC.value());

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
			List<Robot> robots = robotMapper.selectRobotByServices(new Integer[] { RobotServiceType.PSC.value() },
					new String[] { RobotStatusType.AVAILABLE.value(), RobotStatusType.BUSY.value() });

			// Case if have not any robot available
			if (CollectionUtils.isEmpty(robots)) {
				throw new Exception("Không tìm thấy robot khả dụng để làm lệnh!");
			}

			Map<RobotSyncPscReq, Robot> robotRequestMap = new HashMap<RobotSyncPscReq, Robot>();
			Long[] idArr = Convert.toLongArray(request.getIds());
			Date now = new Date();

			for (Long id : idArr) {

				Psc pscSelect = pscMapper.selectPscById(id);
				// Check Status
				if (pscSelect.getStatus() == ProcessStatus.SUCCESS.value()) {
					throw new Exception("Tồn tại Phiếu đã đồng bộ thành công!");
				}

				WorkProcess workProcessNew = new WorkProcess();
				workProcessNew.setServiceId(RobotServiceType.PSC.value());
				workProcessNew.setSyncId(pscSelect.getId());
				workProcessNew.setPriority(RandomUtils.nextInt(0, 9));
				workProcessNew.setStatus(ProcessStatus.WAIT.value());
				workProcessNew.setStartDate(now);
				workProcessNew.setCreateBy(request.getUserName());

				// Add data request
				RobotSyncPscReq dataRequest = new RobotSyncPscReq();
				dataRequest.setConfig(getProcessConfig(request.getAccountHcr(), request.getAccountHpm()));
				dataRequest.setServiceId(RobotServiceType.PSC.value());
				dataRequest.setUnitCode(pscSelect.getUnitCode());
				dataRequest.setVehicleCode(pscSelect.getVehicleCode());
				dataRequest.setVehicleNumber(pscSelect.getVehicleNumber());
				dataRequest.setKm(pscSelect.getKm());
				dataRequest.setRepairType(pscSelect.getRepairType().toString());
				dataRequest.setTechnician(pscSelect.getTechnician());
				dataRequest.setFinalCheck(pscSelect.getFinalCheck());
				dataRequest.setCreateTime(
						DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, pscSelect.getCreateTime()));

				PscDetail pscDetail = new PscDetail();
				pscDetail.setPscId(id);

				// Add spare details
				pscDetail.setTypeDetail(DetailType.SPARE.value());
				List<PscDetail> spareDetailList = pscDetailMapper.selectPscDetailList(pscDetail);

				List<RobotSyncSpareDetailReq> spares = new ArrayList<RobotSyncSpareDetailReq>();
				for (PscDetail detail : spareDetailList) {
					RobotSyncSpareDetailReq detailReq = new RobotSyncSpareDetailReq();
					detailReq.setCode(detail.getCode());
					detailReq.setQuantity(detail.getQuantity());
					spares.add(detailReq);
				}

				dataRequest.setSpares(spares);
				dataRequest.setNumberSpare(spares.size());

				// Add job details
				pscDetail.setTypeDetail(DetailType.JOB.value());
				List<PscDetail> jobDetailList = pscDetailMapper.selectPscDetailList(pscDetail);

				List<RobotSyncJobDetailReq> jobs = new ArrayList<RobotSyncJobDetailReq>();
				for (PscDetail detail : jobDetailList) {
					RobotSyncJobDetailReq detailReq = new RobotSyncJobDetailReq();
					detailReq.setCode(detail.getCode());
					detailReq.setDescription(detail.getDescription());
					detailReq.setPrice(detail.getPrice());
					jobs.add(detailReq);
				}

				dataRequest.setJobs(jobs);
				dataRequest.setNumberJob(jobs.size());

				// Delete record exits table WorkProcess
				WorkProcess workProcessSelect = new WorkProcess();
				workProcessSelect.setSyncId(id);
				workProcessSelect.setServiceId(RobotServiceType.PSC.value());

				List<WorkProcess> workProcessExistList = workProcessMapper.selectWorkProcessList(workProcessSelect);
				if (!CollectionUtils.isEmpty(workProcessExistList)) {
					workProcessMapper.deleteWorkProcessById(workProcessExistList.get(0).getId());
				}

				// Insert record into table WorkProcess
				workProcessMapper.insertWorkProcess(workProcessNew);

				// Update Psc
				pscSelect.setStatus(ProcessStatus.WAIT.value());
				pscSelect.setProcessId(workProcessNew.getId());
				pscSelect.setStep(null);
				pscSelect.setUpdateTime(now);
				pscMapper.updatePsc(pscSelect);

				// Set processId Json (data_request) after insert success
				dataRequest.setProcessId(workProcessNew.getId());

				// Insert record (data_request) into robotRequestMap
				robotRequestMap.put(dataRequest, robots.get(0));

				result++;
				// Loop insert/update dataRequest into table workProcess
				if (robotRequestMap.size() > 0) {
					for (Map.Entry<RobotSyncPscReq, Robot> entry : robotRequestMap.entrySet()) {
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
	 * @param userNameHcr, userNameHpm
	 * @return ProcessConfig
	 */
	private ProcessConfig getProcessConfig(String userNameHcr, String userNameHpm) {
		ProcessConfig processConfig = new ProcessConfig();
		processConfig.setUrl(configService.selectConfigByKey("business.web.hms.url"));
		processConfig.setUserNameHcr(userNameHcr);
		processConfig.setPassWordHcr(sysDictDataService.selectDictValue(userNameHcr));
		processConfig.setUserNameHpm(userNameHpm);
		processConfig.setPassWordHpm(sysDictDataService.selectDictValue(userNameHpm));
		return processConfig;
	}

	/**
	 * 
	 * @param robot
	 * @param process
	 */
	private void requestRobot(Robot robot, RobotSyncPscReq process) {

		String message = new Gson().toJson(process);

		WorkProcess processUpdate = new WorkProcess();
		processUpdate.setId(process.getProcessId());
		processUpdate.setDataRequest(message);
		processUpdate.setRobotUuid(robot.getUuid());
		workProcessMapper.updateWorkProcess(processUpdate);
	}

}
