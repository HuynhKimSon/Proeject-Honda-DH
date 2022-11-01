package vn.com.irtech.api.dto.response;

import java.io.Serializable;

public class VoucherDetailRes implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long detailId;

	private String productId;

	private String productName;

	private String stockId;

	private String unitId;

	private Double akRate;

	private Double wtpRate;

	private Double convertQuantity;

	private Double realQuantity;

	public Long getDetailId() {
		return detailId;
	}

	public void setDetailId(Long detailId) {
		this.detailId = detailId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public Double getAkRate() {
		return akRate;
	}

	public void setAkRate(Double akRate) {
		this.akRate = akRate;
	}

	public Double getWtpRate() {
		return wtpRate;
	}

	public void setWtpRate(Double wtpRate) {
		this.wtpRate = wtpRate;
	}

	public Double getConvertQuantity() {
		return convertQuantity;
	}

	public void setConvertQuantity(Double convertQuantity) {
		this.convertQuantity = convertQuantity;
	}

	public Double getRealQuantity() {
		return realQuantity;
	}

	public void setRealQuantity(Double realQuantity) {
		this.realQuantity = realQuantity;
	}

}
