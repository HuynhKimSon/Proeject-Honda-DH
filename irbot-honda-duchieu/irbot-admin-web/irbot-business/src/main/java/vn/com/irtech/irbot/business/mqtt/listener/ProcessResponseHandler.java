package vn.com.irtech.irbot.business.mqtt.listener;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import vn.com.irtech.core.common.utils.DateUtils;
import vn.com.irtech.irbot.business.domain.Ktdk;
import vn.com.irtech.irbot.business.domain.KtdkPt;
import vn.com.irtech.irbot.business.domain.Psc;
import vn.com.irtech.irbot.business.domain.Robot;
import vn.com.irtech.irbot.business.domain.WorkProcess;
import vn.com.irtech.irbot.business.dto.response.RobotProcessRes;
import vn.com.irtech.irbot.business.mapper.KtdkMapper;
import vn.com.irtech.irbot.business.mapper.KtdkPtMapper;
import vn.com.irtech.irbot.business.mapper.PscMapper;
import vn.com.irtech.irbot.business.mapper.RobotMapper;
import vn.com.irtech.irbot.business.mapper.WorkProcessMapper;
import vn.com.irtech.irbot.business.type.ProcessStatus;
import vn.com.irtech.irbot.business.type.RobotServiceType;
import vn.com.irtech.irbot.business.type.RobotStatusType;

@Component
public class ProcessResponseHandler implements IMqttMessageListener {

	private static final Logger logger = LoggerFactory.getLogger(ProcessResponseHandler.class);

	@Autowired
	@Qualifier("threadPoolTaskExecutor")
	private TaskExecutor executor;

	@Autowired
	private WorkProcessMapper workProcessMapper;

	@Autowired
	private KtdkMapper ktdkMapper;

	@Autowired
	private KtdkPtMapper ktdkPtMapper;

	@Autowired
	private PscMapper pscMapper;
	
	@Autowired
	private RobotMapper robotMapper;

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					processMessage(topic, message);
				} catch (Exception e) {
					logger.error("Error while process mq message", e);
					e.printStackTrace();
				}
			}
		});
	}

	@Transactional
	private void processMessage(String topic, MqttMessage message) throws Exception {
		String messageContent = new String(message.getPayload(), StandardCharsets.UTF_8);
		logger.info(">>>> Receive message topic: {}, content {}", topic, messageContent);
		RobotProcessRes response = null;
		try {
			response = new Gson().fromJson(messageContent, RobotProcessRes.class);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		String robotUuid = topic.split("/")[3];
		Long processId = response.getProcessId();
		Integer serviceId = response.getServiceId();
		Integer unitCode = response.getUnitCode();
		String result = response.getResult();
		String errMsg = response.getErrorMsg();
		String kmHms = "";
		String jobCard = response.getJobCard();
		String step = response.getStep();
		// Get km hms if not meet the conditions of the number of kilometers and the
		// time
		if ((serviceId.equals(RobotServiceType.KTDKPT.value()) || serviceId.equals(RobotServiceType.KTDK.value()))
				&& result.toUpperCase().equals("FAIL")) {
			kmHms = response.getKmHms();
		}

		updateProcessStatus(robotUuid, messageContent, processId, serviceId, unitCode, result, errMsg, kmHms, jobCard,
				step);
		return;
	}

	private void updateProcessStatus(String robotUuid, String messageContent, Long processId, Integer serviceId,
			Integer unitCode, String result, String errorMsg, String kmHms, String jobCard, String step) {
		// Get process by Id
		WorkProcess workProcess = workProcessMapper.selectWorkProcessById(processId);
		if (workProcess == null) {
			logger.warn("Work Process not exist: {}", processId);
			return;
		}
		String errMsg = null;
		ProcessStatus processStatus = null;
		switch (result.toUpperCase()) {
		case "SUCCESS":
			processStatus = ProcessStatus.SUCCESS;
			break;
		case "PROCESSING":
			processStatus = ProcessStatus.PROCESSING;
			break;
		case "FAIL":
			processStatus = ProcessStatus.FAIL;
			errMsg = errorMsg;
			break;
		default:
			return;
		}

		// Update info WorkProcess
		WorkProcess processUpdate = new WorkProcess();
		processUpdate.setId(processId);
		processUpdate.setDataResponse(messageContent);
		processUpdate.setRobotUuid(robotUuid);
		if (processStatus != ProcessStatus.PROCESSING) {
			processUpdate.setEndDate(new Date());
		}
		processUpdate.setStatus(processStatus.value());
		processUpdate.setError(errMsg);
		workProcessMapper.updateWorkProcess(processUpdate);

		// Update status KTDK
		if (serviceId.equals(RobotServiceType.KTDK.value())) {
			Ktdk ktdkUpdate = new Ktdk();
			ktdkUpdate.setId(workProcess.getSyncId());

			// Update KM
			if (!StringUtils.isBlank(kmHms) && NumberUtils.isParsable(kmHms) && processStatus == ProcessStatus.FAIL) {
				Ktdk ktdkSelect = ktdkMapper.selectKtdkById(workProcess.getSyncId());
				if (ktdkSelect != null) {
					String kmSelect = ktdkSelect.getKm();
					// Set kmKtdk = kmHms + 1
					if (Integer.parseInt(kmHms) >= Integer.parseInt(kmSelect)) {
						Integer km = Integer.parseInt(kmHms) + 1;
						ktdkUpdate.setKm(km.toString());
					}
				}
			}
			ktdkUpdate.setStatus(processStatus.value());
			ktdkUpdate.setJobCard(jobCard);
			ktdkUpdate.setStep(step);
			ktdkMapper.updateKtdk(ktdkUpdate);
		}

		// Update status KTDKPT
		if (serviceId.equals(RobotServiceType.KTDKPT.value())) {
			KtdkPt ktdkPtUpdate = new KtdkPt();
			ktdkPtUpdate.setId(workProcess.getSyncId());

			// Update KM
			if (!StringUtils.isBlank(kmHms) && NumberUtils.isParsable(kmHms) && processStatus == ProcessStatus.FAIL) {
				KtdkPt ktdkPtSelect = ktdkPtMapper.selectKtdkPtById(workProcess.getSyncId());
				if (ktdkPtSelect != null) {
					String kmSelect = ktdkPtSelect.getKm();
					// Set kmKtdkPt = kmHms + 1
					if (Integer.parseInt(kmHms) >= Integer.parseInt(kmSelect)) {
						Integer km = Integer.parseInt(kmHms) + 1;
						ktdkPtUpdate.setKm(km.toString());
					}
				}
			}
			ktdkPtUpdate.setStatus(processStatus.value());
			ktdkPtUpdate.setJobCard(jobCard);
			ktdkPtUpdate.setStep(step);
			ktdkPtMapper.updateKtdkPt(ktdkPtUpdate);
		}

		// Update status PSC
		if (serviceId.equals(RobotServiceType.PSC.value())) {
			Psc pscUpdate = new Psc();
			pscUpdate.setId(workProcess.getSyncId());
			pscUpdate.setStatus(processStatus.value());
			pscUpdate.setJobCard(jobCard);
			pscUpdate.setStep(step);
			pscMapper.updatePsc(pscUpdate);
		}

		// Update status FAIL all record if exits > 1 record "step" = STOP
		if (step.equals("STOP")) {
			// Update status busy Robot
			Robot robotSelect = new Robot();
			robotSelect.setUuid(robotUuid);
			List<Robot> robotExits = robotMapper.selectRobotList(robotSelect);
			if (CollectionUtils.isNotEmpty(robotExits)) {
				robotExits.get(0).setStatus(RobotStatusType.BUSY.value());
				robotMapper.updateRobot(robotExits.get(0));
			}
			
			Date now = new Date();
			// Check KTDK
			if (serviceId.equals(RobotServiceType.KTDK.value())) {
				Ktdk ktdkSelect = new Ktdk();
				ktdkSelect.setUnitCode(unitCode);
				ktdkSelect.setStep(step);
				ktdkSelect.setStatus(ProcessStatus.FAIL.value());
				ktdkSelect.setCreateTime(DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd", now)));
				List<Ktdk> ktdkExists = ktdkMapper.selectKtdkList(ktdkSelect);

				if (CollectionUtils.isNotEmpty(ktdkExists)) {
					if (ktdkExists.size() > 1) {
						ktdkSelect.setStep("");
						ktdkSelect.setStatus(ProcessStatus.WAIT.value());
						ktdkExists = ktdkMapper.selectKtdkList(ktdkSelect);

						for (Ktdk ktdk : ktdkExists) {
							WorkProcess workProcessSelect = workProcessMapper
									.selectWorkProcessById(ktdk.getProcessId());
							// Update info WorkProcess
							processUpdate = new WorkProcess();
							processUpdate.setId(workProcessSelect.getId());
							processUpdate.setStatus(ProcessStatus.FAIL.value());
							processUpdate.setError(errorMsg);
							workProcessMapper.updateWorkProcess(processUpdate);
							// Update info KTDK
							ktdk.setStatus(ProcessStatus.FAIL.value());
							ktdk.setStep(step);
							ktdkMapper.updateKtdk(ktdk);
						}
					}
				}
			}
			// Check KTDKPT
			if (serviceId.equals(RobotServiceType.KTDKPT.value())) {
				KtdkPt ktdPtSelect = new KtdkPt();
				ktdPtSelect.setUnitCode(unitCode);
				ktdPtSelect.setStep(step);
				ktdPtSelect.setStatus(ProcessStatus.FAIL.value());
				ktdPtSelect.setCreateTime(DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd", now)));
				List<KtdkPt> ktdkPtExists = ktdkPtMapper.selectKtdkPtList(ktdPtSelect);

				if (CollectionUtils.isNotEmpty(ktdkPtExists)) {
					if (ktdkPtExists.size() > 1) {
						ktdPtSelect.setStep("");
						ktdPtSelect.setStatus(ProcessStatus.WAIT.value());
						ktdkPtExists = ktdkPtMapper.selectKtdkPtList(ktdPtSelect);

						for (KtdkPt ktdkPt : ktdkPtExists) {
							WorkProcess workProcessSelect = workProcessMapper
									.selectWorkProcessById(ktdkPt.getProcessId());
							// Update info WorkProcess
							processUpdate = new WorkProcess();
							processUpdate.setId(workProcessSelect.getId());
							processUpdate.setStatus(ProcessStatus.FAIL.value());
							processUpdate.setError(errorMsg);
							workProcessMapper.updateWorkProcess(processUpdate);
							// Update info KTDKPT
							ktdkPt.setStatus(ProcessStatus.FAIL.value());
							ktdkPt.setStep(step);
							ktdkPtMapper.updateKtdkPt(ktdkPt);
						}
					}
				}
			}
			// Check PSC
			if (serviceId.equals(RobotServiceType.PSC.value())) {
				Psc pscSelect = new Psc();
				pscSelect.setUnitCode(unitCode);
				pscSelect.setStep(step);
				pscSelect.setStatus(ProcessStatus.FAIL.value());
				pscSelect.setCreateTime(DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd", now)));
				List<Psc> pscExists = pscMapper.selectPscList(pscSelect);

				if (CollectionUtils.isNotEmpty(pscExists)) {
					if (pscExists.size() > 1) {
						pscSelect.setStep("");
						pscSelect.setStatus(ProcessStatus.WAIT.value());
						pscExists = pscMapper.selectPscList(pscSelect);

						for (Psc psc : pscExists) {
							WorkProcess workProcessSelect = workProcessMapper.selectWorkProcessById(psc.getProcessId());
							// Update info WorkProcess
							processUpdate = new WorkProcess();
							processUpdate.setId(workProcessSelect.getId());
							processUpdate.setStatus(ProcessStatus.FAIL.value());
							processUpdate.setError(errorMsg);
							workProcessMapper.updateWorkProcess(processUpdate);
							// Update info PSC
							psc.setStatus(ProcessStatus.FAIL.value());
							psc.setStep(step);
							pscMapper.updatePsc(psc);
						}
					}
				}
			}
			// Update status available Robot
			if (CollectionUtils.isNotEmpty(robotExits)) {
				robotExits.get(0).setStatus(RobotStatusType.AVAILABLE.value());
				robotMapper.updateRobot(robotExits.get(0));
			}
		}
	}
}