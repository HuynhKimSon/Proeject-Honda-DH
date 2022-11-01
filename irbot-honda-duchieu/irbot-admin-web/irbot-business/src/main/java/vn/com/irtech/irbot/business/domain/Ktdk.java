package vn.com.irtech.irbot.business.domain;

import vn.com.irtech.core.common.domain.BaseEntity;

public class Ktdk extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private Long id;

	/** Cua hang */
	private Integer unitCode;

	private String vehicleCode;

	private String vehicleNumber;

	private String km;

	private Integer pi;

	private String technician;

	private String finalCheck;
	
	private String jobCard;
	
	private String step;

	/** id work process */
	private Long processId;

	/** -- 0-Chua lam 1-Đa gui 2-Cho thuc hien 3-Dang lam 4-That bai 5-Thanh cong */
	private Integer status;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setUnitCode(Integer unitCode) {
		this.unitCode = unitCode;
	}

	public Integer getUnitCode() {
		return unitCode;
	}

	public void setVehicleCode(String vehicleCode) {
		this.vehicleCode = vehicleCode;
	}

	public String getVehicleCode() {
		return vehicleCode;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setKm(String km) {
		this.km = km;
	}

	public String getKm() {
		return km;
	}

	public void setPi(Integer pi) {
		this.pi = pi;
	}

	public Integer getPi() {
		return pi;
	}

	public void setTechnician(String technician) {
		this.technician = technician;
	}

	public String getTechnician() {
		return technician;
	}

	public String getFinalCheck() {
		return finalCheck;
	}

	public void setFinalCheck(String finalCheck) {
		this.finalCheck = finalCheck;
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

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}
}
