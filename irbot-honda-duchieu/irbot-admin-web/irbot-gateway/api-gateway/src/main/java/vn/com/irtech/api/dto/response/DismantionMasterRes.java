package vn.com.irtech.api.dto.response;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class DismantionMasterRes implements Serializable {

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

	private Long importId;

	private String importNumberId;

	private Long exportId;

	private String exportNumberId;

	private VoucherMasterRes importVoucher;

	private VoucherMasterRes exportVoucher;

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

	public Long getImportId() {
		return importId;
	}

	public void setImportId(Long importId) {
		this.importId = importId;
	}

	public String getImportNumberId() {
		return importNumberId;
	}

	public void setImportNumberId(String importNumberId) {
		this.importNumberId = importNumberId;
	}

	public Long getExportId() {
		return exportId;
	}

	public void setExportId(Long exportId) {
		this.exportId = exportId;
	}

	public String getExportNumberId() {
		return exportNumberId;
	}

	public void setExportNumberId(String exportNumberId) {
		this.exportNumberId = exportNumberId;
	}

	public VoucherMasterRes getImportVoucher() {
		return importVoucher;
	}

	public void setImportVoucher(VoucherMasterRes importVoucher) {
		this.importVoucher = importVoucher;
	}

	public VoucherMasterRes getExportVoucher() {
		return exportVoucher;
	}

	public void setExportVoucher(VoucherMasterRes exportVoucher) {
		this.exportVoucher = exportVoucher;
	}

}
