package vn.com.irtech.irbot.business.mapper;

import java.util.List;

import vn.com.irtech.irbot.business.domain.KtdkPt;

public interface KtdkPtMapper {

	public KtdkPt selectKtdkPtById(Long id);

	public List<KtdkPt> selectKtdkPtList(KtdkPt ktdkPt);

	public int insertKtdkPt(KtdkPt ktdkPt);

	public int updateKtdkPt(KtdkPt ktdkPt);

	public int deleteKtdkPtById(Long id);

	public int deleteKtdkPtByIds(String[] ids);

	public List<KtdkPt> selectKtdkByVehicleCode(String vehicleCode);
}