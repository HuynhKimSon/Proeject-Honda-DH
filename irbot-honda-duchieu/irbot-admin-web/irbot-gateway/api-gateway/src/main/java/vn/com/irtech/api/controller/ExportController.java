package vn.com.irtech.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.com.irtech.api.common.utils.R;
import vn.com.irtech.api.dto.request.GetVoucherDataReq;
import vn.com.irtech.api.service.ExportService;

@Controller
@RequestMapping("/api/export")
public class ExportController {

	@Autowired
	private ExportService exportService;

	@PostMapping("/getData")
	@ResponseBody
	public R getData(@RequestBody GetVoucherDataReq request) {
		// Validate request
		if (request == null || (request.getStartDate() == null && request.getId() == null)) {
			return R.ok();
		}
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("data", exportService.getList(request));
		return R.ok(responseData);
	}
}
