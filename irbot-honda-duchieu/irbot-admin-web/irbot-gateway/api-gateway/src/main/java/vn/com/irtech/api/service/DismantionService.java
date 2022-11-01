package vn.com.irtech.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import vn.com.irtech.api.dto.request.GetVoucherDataReq;
import vn.com.irtech.api.dto.response.DismantionMasterRes;
import vn.com.irtech.api.dto.response.VoucherMasterRes;
import vn.com.irtech.api.mapper.DismantionMapper;
import vn.com.irtech.api.mapper.ExportMapper;
import vn.com.irtech.api.mapper.ImportMapper;

@Service
public class DismantionService {
	
	@Autowired
	private DismantionMapper dismantionMapper;
	
	@Autowired
	private ImportMapper importMapper;
	
	@Autowired
	private ExportMapper exportMapper;
	
	public List<DismantionMasterRes> getList(GetVoucherDataReq request){
		List<DismantionMasterRes> listMaster = dismantionMapper.getDismantionMaster(request);
		if (!CollectionUtils.isEmpty(listMaster)) {
			for (DismantionMasterRes master : listMaster) {
				
				if (master.getImportId() != null) {
					GetVoucherDataReq importRequest = new GetVoucherDataReq();
					importRequest.setId(master.getImportId());
					List<VoucherMasterRes> importMasterList = importMapper.getImportMaster(importRequest);
					
					if (!CollectionUtils.isEmpty(importMasterList)) {
						VoucherMasterRes voucherImport = importMasterList.get(0);
						voucherImport.setDetails(importMapper.getImportDetails(voucherImport.getVoucherId()));
						master.setImportVoucher(voucherImport);
					}
				}
				
				if (master.getExportId() != null) {
					GetVoucherDataReq exportRequest = new GetVoucherDataReq();
					exportRequest.setId(master.getExportId());
					List<VoucherMasterRes> exportMasterList = exportMapper.getExportMaster(exportRequest);
					
					if (!CollectionUtils.isEmpty(exportMasterList)) {
						VoucherMasterRes voucherExport = exportMasterList.get(0);
						voucherExport.setDetails(exportMapper.getExportDetails(voucherExport.getVoucherId()));
						master.setExportVoucher(voucherExport);
					}
				}
			}
		}
		return listMaster;
	}
	
}
