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
import vn.com.irtech.irbot.business.domain.KtdkPt;
import vn.com.irtech.irbot.business.domain.KtdkPtDetail;
import vn.com.irtech.irbot.business.domain.Psc;
import vn.com.irtech.irbot.business.domain.PscDetail;
import vn.com.irtech.irbot.business.domain.Robot;
import vn.com.irtech.irbot.business.domain.WorkProcess;
import vn.com.irtech.irbot.business.dto.ImportKtdkPt;
import vn.com.irtech.irbot.business.dto.ProcessConfig;
import vn.com.irtech.irbot.business.dto.RobotSyncJobDetailReq;
import vn.com.irtech.irbot.business.dto.RobotSyncKtdkPtReq;
import vn.com.irtech.irbot.business.dto.RobotSyncSpareDetailReq;
import vn.com.irtech.irbot.business.dto.request.SendRobotDataReq;
import vn.com.irtech.irbot.business.dto.request.UpdateStatusReq;
import vn.com.irtech.irbot.business.dto.response.ResultImportKtdkPtRes;
import vn.com.irtech.irbot.business.mapper.KtdkPtDetailMapper;
import vn.com.irtech.irbot.business.mapper.KtdkPtMapper;
import vn.com.irtech.irbot.business.mapper.PscDetailMapper;
import vn.com.irtech.irbot.business.mapper.PscMapper;
import vn.com.irtech.irbot.business.mapper.RobotMapper;
import vn.com.irtech.irbot.business.mapper.WorkProcessMapper;
import vn.com.irtech.irbot.business.service.IExcelService;
import vn.com.irtech.irbot.business.service.IKtdkPtService;
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
public class KtdkPtServiceImpl implements IKtdkPtService {

	private static final Logger logger = LoggerFactory.getLogger(KtdkPtServiceImpl.class);

	@Autowired
	private KtdkPtMapper ktdkPtMapper;

	@Autowired
	private KtdkPtDetailMapper ktdkPtDetailMapper;

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
	public KtdkPt selectKtdkPtById(Long id) {
		return ktdkPtMapper.selectKtdkPtById(id);
	}

	@Override
	public List<KtdkPt> selectKtdkPtList(KtdkPt ktdkPt) {
		return ktdkPtMapper.selectKtdkPtList(ktdkPt);
	}

	@Override
	public int insertKtdkPt(KtdkPt ktdkPt) {
		ktdkPt.setCreateTime(DateUtils.getNowDate());
		return ktdkPtMapper.insertKtdkPt(ktdkPt);
	}

	@Override
	public int updateKtdkPt(KtdkPt ktdkPt) {
		ktdkPt.setUpdateTime(DateUtils.getNowDate());
		return ktdkPtMapper.updateKtdkPt(ktdkPt);
	}

	@Override
	public int deleteKtdkPtByIds(String ids) throws Exception {
		logger.info("Delete Kiem tra dinh ky co phu tung!");
		Long idArr[] = Convert.toLongArray(ids);
		Integer reusult = 0;
		for (Long id : idArr) {
			// Check status record
			KtdkPt ktdkPtSelect = ktdkPtMapper.selectKtdkPtById(id);
			if (ktdkPtSelect.getStatus() != ProcessStatus.NOTSEND.value()) {
				throw new Exception("Phiếu dữ liệu có số máy " + ktdkPtSelect.getVehicleCode()
						+ " đã được gửi lệnh, vui lòng kiểm tra lại!");
			}

			// Delete detail KtdkPt
			ktdkPtDetailMapper.deleteKtdkPtDetailByKtdkPtId(id);

			// Delete master KtdkPt
			ktdkPtMapper.deleteKtdkPtById(id);

			reusult++;
		}
		return reusult;
	}

	@Override
	public int deleteKtdkPtById(Long id) {
		return ktdkPtMapper.deleteKtdkPtById(id);
	}

	@Override
	@Transactional
	public AjaxResult importKtdkPt(MultipartFile file, String unitCode, String userName) throws Exception {
		logger.info("Read file excel Kiem tra dinh ky co phu tung!");
		ResultImportKtdkPtRes resultImport = excelService.importKtdkPt(file.getInputStream());
		if (resultImport == null) {
			throw new Exception("File không có dữ liệu hoặc không đúng định dạng, vui lòng kiểm tra lại!");
		}

		AjaxResult ajaxResult = AjaxResult.success();

		// Check return errors
		if (resultImport.getErrors().size() != 0) {
			ajaxResult.put("errors", resultImport.getErrors());
			return ajaxResult;
		}

		// Insert data into table ktdk_pt
		List<ImportKtdkPt> importKtdkPtList = resultImport.getListKtdkPt();
		for (ImportKtdkPt importKtdkPt : importKtdkPtList) {

			Date now = new Date();
			importKtdkPt.setCreateTime(now);

			// Check exist data date now
			List<KtdkPt> selectKtdkPt = ktdkPtMapper.selectKtdkByVehicleCode(importKtdkPt.getVehicleCode());

			if (!CollectionUtils.isEmpty(selectKtdkPt)) {
				KtdkPt ktdkPtExist = selectKtdkPt.get(0);
				String currentDayStr = DateUtils.parseDateToStr("yyyy-MM-dd", ktdkPtExist.getCreateTime());
				String importDateStr = DateUtils.parseDateToStr("yyyy-MM-dd", now);
				if (currentDayStr.equals(importDateStr)) {
					continue;
				}
			}

			// Check unitCode
			if (Integer.parseInt(unitCode) == UnitCode.DUC_HIEU_1.value()) {
				importKtdkPt.setUnitCode(UnitCode.DUC_HIEU_1.value());
			} else {
				importKtdkPt.setUnitCode(UnitCode.DUC_HIEU_2.value());
			}

			importKtdkPt.setStatus(ProcessStatus.NOTSEND.value());
			importKtdkPt.setCreateBy(userName);

			KtdkPt ktdkPtNew = new KtdkPt();
			BeanUtils.copyProperties(importKtdkPt, ktdkPtNew);
			ktdkPtMapper.insertKtdkPt(ktdkPtNew);

			// Insert data into table ktdk_pt_detail
			List<KtdkPtDetail> ktdkPtDetailList = importKtdkPt.getDetails();
			for (KtdkPtDetail detail : ktdkPtDetailList) {
				KtdkPtDetail ktdkPtDetailNew = new KtdkPtDetail();
				ktdkPtDetailNew.setKtdkPtId(ktdkPtNew.getId());
				ktdkPtDetailNew.setCode(detail.getCode());
				ktdkPtDetailNew.setCreateTime(now);
				ktdkPtDetailNew.setCreateBy(userName);
				ktdkPtDetailNew.setTypeDetail(detail.getTypeDetail());
				// 0: spare 1: job
				if (detail.getTypeDetail() == DetailType.SPARE.value()) {
					ktdkPtDetailNew.setQuantity(detail.getQuantity());
				} else {
					ktdkPtDetailNew.setDescription(detail.getDescription());
					ktdkPtDetailNew.setPrice(detail.getPrice());
				}
				ktdkPtDetailMapper.insertKtdkPtDetail(ktdkPtDetailNew);
			}
		}

		return ajaxResult;
	}

	@Override
	public void updateStatus(UpdateStatusReq request) {
		logger.info("Reset Status Kiem tra dinh ky co phu tung!");
		Long idArr[] = Convert.toLongArray(request.getIds());
		for (Long id : idArr) {
			// Update status KtdkPt
			KtdkPt ktdkPtExist = ktdkPtMapper.selectKtdkPtById(id);
			if (ktdkPtExist != null) {
				KtdkPt ktdkPtUpdate = new KtdkPt();
				ktdkPtUpdate.setId(id);
				ktdkPtUpdate.setStatus(request.getStatus());
				ktdkPtMapper.updateKtdkPt(ktdkPtUpdate);
			}

			// Update status WorkProcess
			WorkProcess workProcessSelect = new WorkProcess();
			workProcessSelect.setSyncId(id);
			workProcessSelect.setServiceId(RobotServiceType.KTDKPT.value());

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
			List<Robot> robots = robotMapper.selectRobotByServices(new Integer[] { RobotServiceType.KTDKPT.value() },
					new String[] { RobotStatusType.AVAILABLE.value(), RobotStatusType.BUSY.value() });

			// Case if have not any robot available
			if (CollectionUtils.isEmpty(robots)) {
				throw new Exception("Không tìm thấy robot khả dụng để làm lệnh!");
			}

			Map<RobotSyncKtdkPtReq, Robot> robotRequestMap = new HashMap<RobotSyncKtdkPtReq, Robot>();
			Long[] idArr = Convert.toLongArray(request.getIds());
			Date now = new Date();

			for (Long id : idArr) {

				KtdkPt ktdkPtSelect = ktdkPtMapper.selectKtdkPtById(id);
				// Check Status
				if (ktdkPtSelect.getStatus() == ProcessStatus.SUCCESS.value()) {
					throw new Exception("Tồn tại Phiếu đã đồng bộ thành công!");
				}

				WorkProcess workProcessNew = new WorkProcess();
				workProcessNew.setServiceId(RobotServiceType.KTDKPT.value());
				workProcessNew.setSyncId(ktdkPtSelect.getId());
				workProcessNew.setPriority(RandomUtils.nextInt(0, 9));
				workProcessNew.setStatus(ProcessStatus.WAIT.value());
				workProcessNew.setStartDate(now);
				workProcessNew.setCreateBy(request.getUserName());

				// Add data request
				RobotSyncKtdkPtReq dataRequest = new RobotSyncKtdkPtReq();
				dataRequest.setConfig(getProcessConfig(request.getAccountHcr(), request.getAccountHpm()));
				dataRequest.setServiceId(RobotServiceType.KTDKPT.value());
				dataRequest.setVehicleCode(ktdkPtSelect.getVehicleCode());
				dataRequest.setVehicleCode(ktdkPtSelect.getVehicleCode());
				dataRequest.setVehicleNumber(ktdkPtSelect.getVehicleNumber());
				dataRequest.setKm(ktdkPtSelect.getKm());
				dataRequest.setPi(ktdkPtSelect.getPi().toString());
				dataRequest.setTechnician(ktdkPtSelect.getTechnician());
				dataRequest.setFinalCheck(ktdkPtSelect.getFinalCheck());
				dataRequest.setCreateTime(
						DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, ktdkPtSelect.getCreateTime()));

				KtdkPtDetail ktdkPtDetail = new KtdkPtDetail();
				ktdkPtDetail.setKtdkPtId(id);

				// Add spare details
				ktdkPtDetail.setTypeDetail(DetailType.SPARE.value());
				List<KtdkPtDetail> spareDetailList = ktdkPtDetailMapper.selectKtdkPtDetailList(ktdkPtDetail);

				List<RobotSyncSpareDetailReq> spares = new ArrayList<RobotSyncSpareDetailReq>();
				for (KtdkPtDetail detail : spareDetailList) {
					RobotSyncSpareDetailReq detailReq = new RobotSyncSpareDetailReq();
					detailReq.setCode(detail.getCode());
					detailReq.setQuantity(detail.getQuantity());
					spares.add(detailReq);
				}

				dataRequest.setSpares(spares);
				dataRequest.setNumberSpare(spares.size());

				// Add job details
				ktdkPtDetail.setTypeDetail(DetailType.JOB.value());
				List<KtdkPtDetail> jobDetailList = ktdkPtDetailMapper.selectKtdkPtDetailList(ktdkPtDetail);

				List<RobotSyncJobDetailReq> jobs = new ArrayList<RobotSyncJobDetailReq>();
				for (KtdkPtDetail detail : jobDetailList) {
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
				workProcessSelect.setServiceId(RobotServiceType.KTDKPT.value());

				List<WorkProcess> workProcessExistList = workProcessMapper.selectWorkProcessList(workProcessSelect);
				if (!CollectionUtils.isEmpty(workProcessExistList)) {
					workProcessMapper.deleteWorkProcessById(workProcessExistList.get(0).getId());
				}

				// Insert record into table WorkProcess
				workProcessMapper.insertWorkProcess(workProcessNew);

				// Update KtdkPt
				ktdkPtSelect.setStatus(ProcessStatus.WAIT.value());
				ktdkPtSelect.setProcessId(workProcessNew.getId());
				ktdkPtSelect.setStep(null);
				ktdkPtSelect.setUpdateTime(now);
				ktdkPtMapper.updateKtdkPt(ktdkPtSelect);

				// Set processId Json (data_request) after insert success
				dataRequest.setProcessId(workProcessNew.getId());

				// Insert record (data_request) into robotRequestMap
				robotRequestMap.put(dataRequest, robots.get(0));

				result++;
				// Loop insert/update dataRequest into table workProcess
				if (robotRequestMap.size() > 0) {
					for (Map.Entry<RobotSyncKtdkPtReq, Robot> entry : robotRequestMap.entrySet()) {
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
	private void requestRobot(Robot robot, RobotSyncKtdkPtReq process) {

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
			KtdkPt ktdkPtSelect = ktdkPtMapper.selectKtdkPtById(id);
			if (ktdkPtSelect == null) {
				throw new Exception("Không tìm thấy phiếu cần chuyển đổi! ");
			}
			if (ktdkPtSelect.getStatus() == ProcessStatus.SUCCESS.value()) {
				throw new Exception("Phiếu chuyển đổi đã đồng bộ thành công! ");
			}
			if (ktdkPtSelect.getStatus() == ProcessStatus.WAIT.value()
					|| ktdkPtSelect.getStatus() == ProcessStatus.SENT.value()
					|| ktdkPtSelect.getStatus() == ProcessStatus.PROCESSING.value()) {
				throw new Exception("Phiếu chuyển đổi đã được gửi cho robot! ");
			}

			// Check Exist
			Psc pscSelect = new Psc();
			pscSelect.setVehicleCode(ktdkPtSelect.getVehicleCode());
			pscSelect.setCreateTime(now);
			List<Psc> pscExistList = pscMapper.selectPscList(pscSelect);
			if (!CollectionUtils.isEmpty(pscExistList)) {
				Psc pscExist = pscExistList.get(pscExistList.size() - 1);
				String currentDayStr = DateUtils.parseDateToStr("yyyy-MM-dd", pscExist.getCreateTime());
				String convertDateStr = DateUtils.parseDateToStr("yyyy-MM-dd", now);
				if (currentDayStr.equals(convertDateStr)) {
					throw new Exception(
							"Phiếu sửa chữa có số máy " + ktdkPtSelect.getVehicleCode() + " đã tồn tại!");
				}
			}

			// Insert KtdkPt into Psc
			Psc pscInsert = new Psc();
			pscInsert.setUnitCode(ktdkPtSelect.getUnitCode());
			pscInsert.setVehicleCode(ktdkPtSelect.getVehicleCode());
			pscInsert.setVehicleNumber(ktdkPtSelect.getVehicleNumber());
			pscInsert.setKm(ktdkPtSelect.getKm());
			pscInsert.setRepairType("RP");
			pscInsert.setTechnician(ktdkPtSelect.getTechnician());
			pscInsert.setFinalCheck(ktdkPtSelect.getFinalCheck());
			pscInsert.setStatus(ProcessStatus.NOTSEND.value());
			pscInsert.setCreateBy(ktdkPtSelect.getCreateBy());
			pscInsert.setCreateTime(now);
			pscMapper.insertPsc(pscInsert);

			// Insert KtdkPt detail into Psc Detail
			KtdkPtDetail ktdkPtDetailSelect = new KtdkPtDetail();
			ktdkPtDetailSelect.setKtdkPtId(ktdkPtSelect.getId());

			List<KtdkPtDetail> detailList = ktdkPtDetailMapper.selectKtdkPtDetailList(ktdkPtDetailSelect);
			if (!CollectionUtils.isEmpty(detailList)) {
				for (KtdkPtDetail detail : detailList) {
					PscDetail pscDetailInsert = new PscDetail();
					pscDetailInsert.setPscId(pscInsert.getId());
					pscDetailInsert.setCode(detail.getCode());
					pscDetailInsert.setCreateTime(now);
					pscDetailInsert.setCreateBy(detail.getCreateBy());
					pscDetailInsert.setTypeDetail(detail.getTypeDetail());
					// 0: spare 1: job
					if (detail.getTypeDetail() == DetailType.SPARE.value()) {
						pscDetailInsert.setQuantity(detail.getQuantity());
					} else {
						pscDetailInsert.setDescription(detail.getDescription());
						pscDetailInsert.setPrice(detail.getPrice());
					}
					pscDetailMapper.insertPscDetail(pscDetailInsert);
				}
			}

			// Delete record in table WorkProcess
			workProcessMapper.deleteWorkProcessById(ktdkPtSelect.getProcessId());

			// Delete record in table KtdkPt detail
			ktdkPtDetailMapper.deleteKtdkPtDetailByKtdkPtId(id);

			// Delete record in table KtdkPt
			ktdkPtMapper.deleteKtdkPtById(id);
		}
	}
}
