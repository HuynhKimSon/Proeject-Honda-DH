package vn.com.irtech.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import vn.com.irtech.api.dto.request.GetVoucherDataReq;
import vn.com.irtech.api.dto.response.VoucherMasterRes;
import vn.com.irtech.api.mapper.ImportMapper;

@Service
public class ImportService {

	@Autowired
	private ImportMapper importMapper;

	public List<VoucherMasterRes> getList(GetVoucherDataReq request) {
		List<VoucherMasterRes> listMaster = importMapper.getImportMaster(request);
		if (!CollectionUtils.isEmpty(listMaster)) {
			for (VoucherMasterRes master : listMaster) {
				master.setDetails(importMapper.getImportDetails(master.getVoucherId()));
			}
		}
		return listMaster;
	}
}
