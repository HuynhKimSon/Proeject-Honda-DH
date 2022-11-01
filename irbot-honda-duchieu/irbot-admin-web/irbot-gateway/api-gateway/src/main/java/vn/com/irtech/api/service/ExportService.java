package vn.com.irtech.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import vn.com.irtech.api.dto.request.GetVoucherDataReq;
import vn.com.irtech.api.dto.response.VoucherMasterRes;
import vn.com.irtech.api.mapper.ExportMapper;

@Service
public class ExportService {

	@Autowired
	private ExportMapper exportMapper;

	public List<VoucherMasterRes> getList(GetVoucherDataReq request) {
		List<VoucherMasterRes> listMaster = exportMapper.getExportMaster(request);
		if (!CollectionUtils.isEmpty(listMaster)) {
			for (VoucherMasterRes master : listMaster) {
				master.setDetails(exportMapper.getExportDetails(master.getVoucherId()));
			}
		}
		return listMaster;
	}
}