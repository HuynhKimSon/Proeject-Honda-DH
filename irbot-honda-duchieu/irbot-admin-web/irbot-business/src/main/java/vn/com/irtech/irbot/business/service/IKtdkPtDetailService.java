package vn.com.irtech.irbot.business.service;

import java.util.List;

import vn.com.irtech.irbot.business.domain.KtdkPtDetail;

public interface IKtdkPtDetailService {

	public KtdkPtDetail selectKtdkPtDetailById(Long id);

	public List<KtdkPtDetail> selectKtdkPtDetailList(KtdkPtDetail ktdkPtDetail);

	public int insertKtdkPtDetail(KtdkPtDetail ktdkPtDetail);

	public int updateKtdkPtDetail(KtdkPtDetail ktdkPtDetail);

	public int deleteKtdkPtDetailByIds(String ids);

	public int deleteKtdkPtDetailById(Long id);
}