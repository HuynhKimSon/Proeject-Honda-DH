package vn.com.irtech.irbot.web.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vn.com.irtech.core.common.utils.DateUtils;
import vn.com.irtech.irbot.business.domain.Robot;
import vn.com.irtech.irbot.business.domain.WorkProcess;
import vn.com.irtech.irbot.business.mapper.RobotMapper;
import vn.com.irtech.irbot.business.mapper.WorkProcessMapper;
import vn.com.irtech.irbot.business.service.IRobotService;
import vn.com.irtech.irbot.business.type.ProcessStatus;
import vn.com.irtech.irbot.business.type.RobotServiceType;
import vn.com.irtech.irbot.business.type.RobotStatusType;

@Component("sendRobotTask")
public class SendRobotTask {

	private static final Logger logger = LoggerFactory.getLogger(SendRobotTask.class);

	@Autowired
	private RobotMapper robotMapper;

	@Autowired
	private WorkProcessMapper workProcessMapper;

	@Autowired
	private IRobotService robotService;

	public void executeTask() {
		logger.info(">>>>>>>>>>>>>> SEND ROBOT TASK - START!");
		try {
			sendRobot(RobotServiceType.KTDK.value(), RobotServiceType.KTDK.toString());
			sendRobot(RobotServiceType.KTDKPT.value(), RobotServiceType.KTDKPT.toString());
			sendRobot(RobotServiceType.PSC.value(), RobotServiceType.PSC.toString());

		} catch (Exception e) {

			logger.error(">>>>>> Error: " + e.getMessage());
		}

		logger.info(">>>>>>>>>>>>>> SEND ROBOT TASK - FINISH!");

	}

	private void sendRobot(Integer robotServiceType, String robotServiceName) {
		Date now = new Date();
		WorkProcess workProcessSelect = new WorkProcess();
		workProcessSelect.setServiceId(robotServiceType);
		workProcessSelect.setStatus(ProcessStatus.WAIT.value());
		workProcessSelect.getParams().put("createTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, now));
		List<WorkProcess> listWorkProcessExist = workProcessMapper.selectWorkProcessList(workProcessSelect);

		if (CollectionUtils.isEmpty(listWorkProcessExist)) {
			logger.info(">>>> SEND ROBOT TASK - KHONG CO LENH " + robotServiceName);
			return;
		}

		List<Robot> robots = robotMapper.selectRobotByService(robotServiceType, RobotStatusType.AVAILABLE.value());

		// Case if have not any robot available
		if (CollectionUtils.isEmpty(robots)) {
			logger.info(">>> SEND ROBOT TASK - KHONG TIM THAY ROBOT KHA DUNG LAM LENH!");
			return;
		}

		Map<Robot, WorkProcess> requestMap = new HashMap<Robot, WorkProcess>();
		for (WorkProcess workProcess : listWorkProcessExist) {
			if (CollectionUtils.isEmpty(robots)) {
				break;
			}

			requestMap.put(robots.get(0), workProcess);

			robots.remove(0);
		}

		try {
			robotService.sendRobot(requestMap);
		} catch (Exception e) {
			logger.warn(">>> ERROR SEND ROBOT " + robotServiceName + " - " + e.getMessage());
		}
	}

}
