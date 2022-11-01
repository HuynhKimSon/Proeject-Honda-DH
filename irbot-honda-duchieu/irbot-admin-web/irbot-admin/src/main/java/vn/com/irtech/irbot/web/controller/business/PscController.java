package vn.com.irtech.irbot.web.controller.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import vn.com.irtech.core.common.controller.BaseController;
import vn.com.irtech.core.common.domain.AjaxResult;
import vn.com.irtech.core.common.domain.LoginUser;
import vn.com.irtech.core.common.domain.entity.SysDictData;
import vn.com.irtech.core.common.page.TableDataInfo;
import vn.com.irtech.core.framework.util.ShiroUtils;
import vn.com.irtech.irbot.business.domain.Psc;
import vn.com.irtech.irbot.business.domain.PscDetail;
import vn.com.irtech.irbot.business.domain.WorkProcess;
import vn.com.irtech.irbot.business.dto.request.SendRobotDataReq;
import vn.com.irtech.irbot.business.dto.request.UpdateStatusReq;
import vn.com.irtech.irbot.business.service.IPscService;
import vn.com.irtech.irbot.business.service.IPscDetailService;
import vn.com.irtech.irbot.business.service.IWorkProcessService;
import vn.com.irtech.irbot.business.type.DetailType;
import vn.com.irtech.irbot.business.type.DictType;
import vn.com.irtech.irbot.business.type.UnitCode;
import vn.com.irtech.core.system.service.ISysDictDataService;

@Controller
@RequestMapping("/business/psc")
public class PscController extends BaseController {

	private String prefix = "business/psc";

	@Autowired
	private IPscService pscService;

	@Autowired
	private IPscDetailService pscDetailService;

	@Autowired
	private ISysDictDataService sysDictDataService;

	@Autowired
	private IWorkProcessService workProcessService;

	@GetMapping()
	public String processPsc(ModelMap mmap) {
		Map<String, String> unitCodeOptions = new HashMap<String, String>();
		LoginUser loginUser = ShiroUtils.getLoginUser();
		SysDictData sysDictDataSelectUnit = new SysDictData();
		sysDictDataSelectUnit.setDictType(DictType.BUSINESS_UNIT.value());
		List<SysDictData> unitList = sysDictDataService.selectDictDataList(sysDictDataSelectUnit);

		if (loginUser.getDeptId() == UnitCode.ADMIN.value().longValue()) {
			for (SysDictData item : unitList) {
				unitCodeOptions.put(item.getDictLabel(), item.getDictValue());
			}
		} else {
			for (SysDictData item : unitList) {
				if (loginUser.getDeptId().toString().equals(item.getDictLabel())) {
					unitCodeOptions.put(item.getDictLabel(), item.getDictValue());
				}
			}
		}

		mmap.put("unitCodeOptions", unitCodeOptions);
		return prefix + "/psc";
	}

	@PostMapping("/list")
	@ResponseBody
	public TableDataInfo list(Psc psc) {
		startPage();
		List<Psc> list = pscService.selectPscList(psc);
		return getDataTable(list);
	}

	@GetMapping("/add")
	public String add() {
		return prefix + "/add";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Long id, ModelMap mmap) {
		mmap.put("entity", "");
		return prefix + "/edit";
	}

	@PostMapping("/remove")
	@ResponseBody
	public AjaxResult remove(String ids) {
		try {
			pscService.deletePscByIds(ids);
			AjaxResult ajaxResult = AjaxResult.success();
			return ajaxResult;
		} catch (Exception e) {
			logger.error(">>>>>> Error: " + e.getMessage());
			return AjaxResult.error(e.getMessage());
		}
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") Long id, ModelMap mmap) {
		Psc psc = pscService.selectPscById(id);
		if (psc == null) {
			psc = new Psc();
		}
		mmap.put("psc", psc);

		PscDetail pscDetail = new PscDetail();
		// Get detail spare
		pscDetail.setPscId(id);
		pscDetail.setTypeDetail(DetailType.SPARE.value());
		List<PscDetail> spareDetails = pscDetailService.selectPscDetailList(pscDetail);
		mmap.put("spareDetails", spareDetails);

		// Get detail job
		pscDetail.setTypeDetail(DetailType.JOB.value());
		List<PscDetail> jobDetails = pscDetailService.selectPscDetailList(pscDetail);
		mmap.put("jobDetails", jobDetails);

		return prefix + "/detail";
	}

	@GetMapping("/history/{id}")
	public String history(@PathVariable("id") Long id, ModelMap mmap) {
		Psc psc = pscService.selectPscById(id);
		WorkProcess process = new WorkProcess();
		if (psc != null && psc.getProcessId() != null) {
			process = workProcessService.selectWorkProcessById(psc.getProcessId());
			if (process == null) {
				process = new WorkProcess();
			}
		}
		mmap.put("process", process);
		return prefix + "/history";
	}

	@GetMapping("/update-status")
	public String updateStatus() {
		return prefix + "/update-status";
	}

	@PostMapping("/update-status")
	@ResponseBody
	public AjaxResult updateStatus(@RequestBody UpdateStatusReq request) {
		try {
			pscService.updateStatus(request);
			AjaxResult ajaxResult = AjaxResult.success();
			return ajaxResult;
		} catch (Exception e) {
			logger.error(">>>>>> Error: " + e.getMessage());
			return AjaxResult.error(e.getMessage());
		}
	}

	@GetMapping("/import")
	public String importExcel(ModelMap mmap) {
		Map<String, String> unitCodeImport = new HashMap<String, String>();
		LoginUser loginUser = ShiroUtils.getLoginUser();
		SysDictData sysDictDataSelectUnit = new SysDictData();
		sysDictDataSelectUnit.setDictType(DictType.BUSINESS_UNIT.value());
		List<SysDictData> unitList = sysDictDataService.selectDictDataList(sysDictDataSelectUnit);

		if (loginUser.getDeptId() == UnitCode.ADMIN.value().longValue()) {
			for (SysDictData item : unitList) {
				unitCodeImport.put(item.getDictLabel(), item.getDictValue());
			}
		} else {
			for (SysDictData item : unitList) {
				if (loginUser.getDeptId().toString().equals(item.getDictLabel())) {
					unitCodeImport.put(item.getDictLabel(), item.getDictValue());
				}
			}
		}

		mmap.put("unitCodeImport", unitCodeImport);
		return prefix + "/import";
	}

	@PostMapping("/import")
	@ResponseBody
	public AjaxResult importExcel(@RequestParam("file") MultipartFile file, @RequestParam("unitCode") String unitCode) {
		try {
			String userName = ShiroUtils.getLoginName();
			AjaxResult ajaxResult = pscService.importPsc(file, unitCode, userName);
			return ajaxResult;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(">>>>>> Error: " + e.getMessage());
			return AjaxResult.error(e.getMessage());
		}
	}

	@GetMapping("/retry")
	public String retry(ModelMap mmap) {
		Map<String, String> accountHeadHcr = new HashMap<String, String>();
		Map<String, String> accountHeadHpm = new HashMap<String, String>();
		LoginUser loginUser = ShiroUtils.getLoginUser();

		SysDictData sysDictDataSelectHcr1 = new SysDictData();
		sysDictDataSelectHcr1.setDictType(DictType.BUSINESS_ACCOUNT_HCR_DUC_HIEU_1.value());
		List<SysDictData> accountHcrList1 = sysDictDataService.selectDictDataList(sysDictDataSelectHcr1);

		SysDictData sysDictDataSelectHcr2 = new SysDictData();
		sysDictDataSelectHcr2.setDictType(DictType.BUSINESS_ACCOUNT_HCR_DUC_HIEU_2.value());
		List<SysDictData> accountHcrList2 = sysDictDataService.selectDictDataList(sysDictDataSelectHcr2);

		SysDictData sysDictDataSelectHpm1 = new SysDictData();
		sysDictDataSelectHpm1.setDictType(DictType.BUSINESS_ACCOUNT_HPM_DUC_HIEU_1.value());
		List<SysDictData> accountHpmList1 = sysDictDataService.selectDictDataList(sysDictDataSelectHpm1);

		SysDictData sysDictDataSelectHpm2 = new SysDictData();
		sysDictDataSelectHpm2.setDictType(DictType.BUSINESS_ACCOUNT_HPM_DUC_HIEU_2.value());
		List<SysDictData> accountHpmList2 = sysDictDataService.selectDictDataList(sysDictDataSelectHpm2);

		if (loginUser.getDeptId() == UnitCode.DUC_HIEU_1.value().longValue()) {
			for (SysDictData item : accountHcrList1) {
				accountHeadHcr.put(item.getDictLabel(), item.getDictLabel());
			}
			for (SysDictData item : accountHpmList1) {
				accountHeadHpm.put(item.getDictLabel(), item.getDictLabel());
			}
		} else if (loginUser.getDeptId() == UnitCode.DUC_HIEU_2.value().longValue()) {
			for (SysDictData item : accountHcrList2) {
				accountHeadHcr.put(item.getDictLabel(), item.getDictLabel());
			}
			for (SysDictData item : accountHpmList2) {
				accountHeadHpm.put(item.getDictLabel(), item.getDictLabel());
			}
		} else {
			for (SysDictData item : accountHcrList1) {
				accountHeadHcr.put(item.getDictLabel(), item.getDictLabel());
			}

			for (SysDictData item : accountHcrList2) {
				accountHeadHcr.put(item.getDictLabel(), item.getDictLabel());
			}

			for (SysDictData item : accountHpmList1) {
				accountHeadHpm.put(item.getDictLabel(), item.getDictLabel());
			}

			for (SysDictData item : accountHpmList2) {
				accountHeadHpm.put(item.getDictLabel(), item.getDictLabel());
			}
		}
		mmap.put("accountHeadHcr", accountHeadHcr);
		mmap.put("accountHeadHpm", accountHeadHpm);
		return prefix + "/retry";
	}

	@PostMapping("/retry")
	@ResponseBody
	public AjaxResult retry(@RequestBody SendRobotDataReq request) {
		try {
			LoginUser loginUser = ShiroUtils.getLoginUser();
			request.setUserName(loginUser.getLoginName());
			pscService.retry(request);
			AjaxResult ajaxResult = AjaxResult.success();
			return ajaxResult;
		} catch (Exception e) {
			logger.error(">>>>>> Error: " + e.getMessage());
			return AjaxResult.error(e.getMessage());
		}
	}
}
