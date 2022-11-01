package vn.com.irtech.irbot.business.dto;

import java.io.Serializable;

public class RobotSyncKtdkReq implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long processId;

	private Integer syncId;

	private Integer serviceId;

	private Integer unitCode;

	private String vehicleCode;

	private String vehicleNumber;

	private String km;

	private String pi;

	private String technician;

	private String finalCheck;

	private String createTime;

	private ProcessConfig config;

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

	public String getVehicleCode() {
		return vehicleCode;
	}

	public void setVehicleCode(String vehicleCode) {
		this.vehicleCode = vehicleCode;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getKm() {
		return km;
	}

	public void setKm(String km) {
		this.km = km;
	}

	public String getPi() {
		return pi;
	}

	public void setPi(String pi) {
		this.pi = pi;
	}

	public String getTechnician() {
		return technician;
	}

	public void setTechnician(String technician) {
		this.technician = technician;
	}

	public String getFinalCheck() {
		return finalCheck;
	}

	public void setFinalCheck(String finalCheck) {
		this.finalCheck = finalCheck;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public ProcessConfig getConfig() {
		return config;
	}

	public void setConfig(ProcessConfig config) {
		this.config = config;
	}
}
