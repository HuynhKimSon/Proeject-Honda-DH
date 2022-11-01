package vn.com.irtech.irbot.business.domain;

import vn.com.irtech.core.common.domain.BaseEntity;

public class KtdkPtDetail extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private Long id;

	private Long ktdkPtId;

	/** 0-spare 1-job */
	private Integer typeDetail;

	private String code;

	private String quantity;

	private String description;

	private String price;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setKtdkPtId(Long ktdkPtId) {
		this.ktdkPtId = ktdkPtId;
	}

	public Long getKtdkPtId() {
		return ktdkPtId;
	}

	public void setTypeDetail(Integer typeDetail) {
		this.typeDetail = typeDetail;
	}

	public Integer getTypeDetail() {
		return typeDetail;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPrice() {
		return price;
	}
}