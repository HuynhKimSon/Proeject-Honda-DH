package vn.com.irtech.irbot.business.mapper;

import java.util.List;

import vn.com.irtech.irbot.business.domain.Ktdk;

public interface KtdkMapper {

	public Ktdk selectKtdkById(Long id);

	public List<Ktdk> selectKtdkList(Ktdk ktdk);

	public int insertKtdk(Ktdk ktdk);

	public int updateKtdk(Ktdk ktdk);

	public int deleteKtdkById(Long id);

	public int deleteKtdkByIds(String[] ids);

	public List<Ktdk> selectKtdkByVehicleCode(String vehicleCode);
}
