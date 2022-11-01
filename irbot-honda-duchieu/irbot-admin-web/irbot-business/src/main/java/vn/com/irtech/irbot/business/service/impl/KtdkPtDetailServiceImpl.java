package vn.com.irtech.irbot.business.service.impl;

import java.util.List;

import vn.com.irtech.core.common.utils.DateUtils;
import vn.com.irtech.irbot.business.domain.KtdkPtDetail;
import vn.com.irtech.irbot.business.mapper.KtdkPtDetailMapper;
import vn.com.irtech.irbot.business.service.IKtdkPtDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.com.irtech.core.common.text.Convert;

@Service
public class KtdkPtDetailServiceImpl implements IKtdkPtDetailService {
	@Autowired
	private KtdkPtDetailMapper ktdkPtDetailMapper;

	@Override
	public KtdkPtDetail selectKtdkPtDetailById(Long id) {
		return ktdkPtDetailMapper.selectKtdkPtDetailById(id);
	}

	@Override
	public List<KtdkPtDetail> selectKtdkPtDetailList(KtdkPtDetail ktdkPtDetail) {
		return ktdkPtDetailMapper.selectKtdkPtDetailList(ktdkPtDetail);
	}

	@Override
	public int insertKtdkPtDetail(KtdkPtDetail ktdkPtDetail) {
		ktdkPtDetail.setCreateTime(DateUtils.getNowDate());
		return ktdkPtDetailMapper.insertKtdkPtDetail(ktdkPtDetail);
	}

	@Override
	public int updateKtdkPtDetail(KtdkPtDetail ktdkPtDetail) {
		ktdkPtDetail.setUpdateTime(DateUtils.getNowDate());
		return ktdkPtDetailMapper.updateKtdkPtDetail(ktdkPtDetail);
	}

	@Override
	public int deleteKtdkPtDetailByIds(String ids) {
		return ktdkPtDetailMapper.deleteKtdkPtDetailByIds(Convert.toStrArray(ids));
	}

	@Override
	public int deleteKtdkPtDetailById(Long id) {
		return ktdkPtDetailMapper.deleteKtdkPtDetailById(id);
	}
}