package vn.com.irtech.irbot.business.service;

import java.util.List;

import vn.com.irtech.irbot.business.domain.PscDetail;

public interface IPscDetailService {

	public PscDetail selectPscDetailById(Long id);

	public List<PscDetail> selectPscDetailList(PscDetail pscDetail);

	public int insertPscDetail(PscDetail pscDetail);

	public int updatePscDetail(PscDetail pscDetail);

	public int deletePscDetailByIds(String ids);

	public int deletePscDetailById(Long id);
}