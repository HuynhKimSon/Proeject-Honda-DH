package vn.com.irtech.irbot.business.service.impl;

import java.util.List;

import vn.com.irtech.core.common.utils.DateUtils;
import vn.com.irtech.irbot.business.domain.PscDetail;
import vn.com.irtech.irbot.business.mapper.PscDetailMapper;
import vn.com.irtech.irbot.business.service.IPscDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.com.irtech.core.common.text.Convert;

@Service
public class PscDetailServiceImpl implements IPscDetailService {
	@Autowired
	private PscDetailMapper pscDetailMapper;

	@Override
	public PscDetail selectPscDetailById(Long id) {
		return pscDetailMapper.selectPscDetailById(id);
	}

	@Override
	public List<PscDetail> selectPscDetailList(PscDetail pscDetail) {
		return pscDetailMapper.selectPscDetailList(pscDetail);
	}

	@Override
	public int insertPscDetail(PscDetail pscDetail) {
		pscDetail.setCreateTime(DateUtils.getNowDate());
		return pscDetailMapper.insertPscDetail(pscDetail);
	}

	@Override
	public int updatePscDetail(PscDetail pscDetail) {
		pscDetail.setUpdateTime(DateUtils.getNowDate());
		return pscDetailMapper.updatePscDetail(pscDetail);
	}

	@Override
	public int deletePscDetailByIds(String ids) {
		return pscDetailMapper.deletePscDetailByIds(Convert.toStrArray(ids));
	}

	@Override
	public int deletePscDetailById(Long id) {
		return pscDetailMapper.deletePscDetailById(id);
	}
}