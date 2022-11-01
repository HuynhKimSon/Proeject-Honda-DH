package vn.com.irtech.irbot.business.dto.response;

import java.io.Serializable;

public class RobotProcessRes implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long processId;

	private Integer syncId;

	private Integer serviceId;

	private Integer unitCode;

	private String result;

	private String errorMsg;

	private String kmHms;

	private String jobCard;

	private String step;

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public Integer getSyncId() {
		return syncId;
	}

	public void setSyncId(Integer syncId) {
		this.syncId = syncId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(Integer unitCode) {
		this.unitCode = unitCode;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getKmHms() {
		return kmHms;
	}

	public void setKmHms(String kmHms) {
		this.kmHms = kmHms;
	}

	public String getJobCard() {
		return jobCard;
	}

	public void setJobCard(String jobCard) {
		this.jobCard = jobCard;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}
}
