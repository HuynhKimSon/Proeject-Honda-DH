package vn.com.irtech.irbot.business.mapper;

import java.util.List;

import vn.com.irtech.irbot.business.domain.PscDetail;

public interface PscDetailMapper {

	public PscDetail selectPscDetailById(Long id);

	public List<PscDetail> selectPscDetailList(PscDetail pscDetail);

	public int insertPscDetail(PscDetail pscDetail);

	public int updatePscDetail(PscDetail pscDetail);

	public int deletePscDetailById(Long id);
	
	public int deletePscDetailByPscId(Long id);

	public int deletePscDetailByIds(String[] ids);
	
	public int deletePscDetailByPscIds(String[] ids);
}