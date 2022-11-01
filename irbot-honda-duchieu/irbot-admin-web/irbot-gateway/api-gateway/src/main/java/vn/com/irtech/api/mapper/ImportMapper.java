package vn.com.irtech.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import vn.com.irtech.api.dto.request.GetVoucherDataReq;
import vn.com.irtech.api.dto.response.VoucherDetailRes;
import vn.com.irtech.api.dto.response.VoucherMasterRes;

@Mapper
public interface ImportMapper {
	
	public List<VoucherMasterRes> getImportMaster(GetVoucherDataReq request);
	
	public List<VoucherDetailRes> getImportDetails(Long importId);
	
}
