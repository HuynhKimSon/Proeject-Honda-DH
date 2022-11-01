package vn.com.irtech.api.dto.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class VoucherMasterRes implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long voucherId;

	private String voucherNo;

	private String voucherIndex;

	private String voucherType;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date voucherDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date voucherModifyDate;

	private String productGroup;

	private String providerId;

	private String providerName;

	private String stockId;

	private String reason;

	private String description;

	private List<VoucherDetailRes> details;

	public Long getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(Long voucherId) {
		this.voucherId = voucherId;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public String getVoucherIndex() {
		return voucherIndex;
	}

	public void setVoucherIndex(String voucherIndex) {
		this.voucherIndex = voucherIndex;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public Date getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(Date voucherDate) {
		this.voucherDate = voucherDate;
	}

	public Date getVoucherModifyDate() {
		return voucherModifyDate;
	}

	public void setVoucherModifyDate(Date voucherModifyDate) {
		this.voucherModifyDate = voucherModifyDate;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<VoucherDetailRes> getDetails() {
		return details;
	}

	public void setDetails(List<VoucherDetailRes> details) {
		this.details = details;
	}

}
