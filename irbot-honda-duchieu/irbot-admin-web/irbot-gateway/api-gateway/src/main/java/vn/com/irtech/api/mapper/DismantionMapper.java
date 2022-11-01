package vn.com.irtech.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import vn.com.irtech.api.dto.request.GetVoucherDataReq;
import vn.com.irtech.api.dto.response.DismantionMasterRes;
import vn.com.irtech.api.dto.response.VoucherDetailRes;
import vn.com.irtech.api.dto.response.VoucherMasterRes;

@Mapper
public interface DismantionMapper {
	
	public List<DismantionMasterRes> getDismantionMaster(GetVoucherDataReq request);
	
}
