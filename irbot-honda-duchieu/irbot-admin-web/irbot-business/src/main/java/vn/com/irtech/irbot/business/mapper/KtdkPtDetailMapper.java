package vn.com.irtech.irbot.business.mapper;

import java.util.List;

import vn.com.irtech.irbot.business.domain.KtdkPtDetail;

public interface KtdkPtDetailMapper {

	public KtdkPtDetail selectKtdkPtDetailById(Long id);

	public List<KtdkPtDetail> selectKtdkPtDetailList(KtdkPtDetail ktdkPtDetail);

	public int insertKtdkPtDetail(KtdkPtDetail ktdkPtDetail);

	public int updateKtdkPtDetail(KtdkPtDetail ktdkPtDetail);

	public int deleteKtdkPtDetailById(Long id);
	
	public int deleteKtdkPtDetailByKtdkPtId(Long id);

	public int deleteKtdkPtDetailByIds(String[] ids);

	public int deleteKtdkPtDetailByKtdkPtIds(String[] ids);
}