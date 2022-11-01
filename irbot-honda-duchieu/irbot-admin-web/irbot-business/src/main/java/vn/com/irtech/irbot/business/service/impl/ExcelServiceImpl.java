package vn.com.irtech.irbot.business.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import vn.com.irtech.core.common.utils.StringUtils;
import vn.com.irtech.irbot.business.domain.Ktdk;
import vn.com.irtech.irbot.business.domain.KtdkPtDetail;
import vn.com.irtech.irbot.business.domain.PscDetail;
import vn.com.irtech.irbot.business.dto.ImportKtdkPt;
import vn.com.irtech.irbot.business.dto.ImportPsc;
import vn.com.irtech.irbot.business.dto.response.ResultImportKtdkPtRes;
import vn.com.irtech.irbot.business.dto.response.ResultImportKtdkRes;
import vn.com.irtech.irbot.business.dto.response.ResultImportPscRes;
import vn.com.irtech.irbot.business.service.IExcelService;
import vn.com.irtech.irbot.business.type.DetailType;

@Service
public class ExcelServiceImpl implements IExcelService {

	private static final Logger log = LoggerFactory.getLogger(ExcelServiceImpl.class);

	/**
	 * Read file excel import ktdk
	 */
	@Override
	public ResultImportKtdkRes importKtdk(InputStream is) {

		ResultImportKtdkRes result = new ResultImportKtdkRes();
		List<String> errors = new ArrayList<String>();
		Workbook wb = null;
		Sheet sheet = null;

		List<Ktdk> listKtdk = new ArrayList<Ktdk>();

		try {

			wb = WorkbookFactory.create(is);
			sheet = wb.getSheetAt(0);

			if (sheet == null) {
				log.error("sheet not exist");
				return null;
			}

			int rows = sheet.getPhysicalNumberOfRows();

			if (rows <= 1) {
				log.error("Data empty");
				return null;
			}

			String currentVehicleCode = "";
			Ktdk importKtdk = null;

			for (int i = 2; i < rows; i++) {
				// The data is taken from the second row, and the default first row is the
				// header.
				Row row = sheet.getRow(i);
				// Check if the current line is empty
				if (isRowEmpty(row)) {
					continue;
				}

				String vehicleCode = getCellValue(row, 1).toString();
				if (StringUtils.isBlank(vehicleCode)) {
					continue;
				}

				if (!vehicleCode.equals(currentVehicleCode)) {
					importKtdk = new Ktdk();

					importKtdk.setVehicleCode(vehicleCode);

					String km = getCellValue(row, 2).toString();
					importKtdk.setKm(km);

					String vehicleNumber = getCellValue(row, 3).toString();
					if (StringUtils.isBlank(vehicleNumber)) {
						vehicleNumber = "0";
					}
					importKtdk.setVehicleNumber(vehicleNumber);

					String technician = getCellValue(row, 4).toString();
					importKtdk.setTechnician(technician);

					String finalCheck = getCellValue(row, 5).toString();
					importKtdk.setFinalCheck(finalCheck);

					String pi = getCellValue(row, 6).toString();
					if (NumberUtils.isParsable(pi)) {
						importKtdk.setPi(Integer.parseInt(pi));
					}

					// Kiem tra du lieu hop le
					List<String> listError = checkKtdk(i + 1, importKtdk);
					if (CollectionUtils.isEmpty(listError)) {
						listKtdk.add(importKtdk);
					} else {
						errors.addAll(listError);
					}

					currentVehicleCode = vehicleCode;
				}
			}
			result.setErrors(errors);
			result.setListKtdk(listKtdk);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Import data exception: {}", e.getMessage());
		} finally {
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * Read file excel import ktdkPt
	 */
	@Override
	public ResultImportKtdkPtRes importKtdkPt(InputStream is) {

		ResultImportKtdkPtRes result = new ResultImportKtdkPtRes();
		List<String> errors = new ArrayList<String>();
		Workbook wb = null;
		Sheet sheet = null;

		List<ImportKtdkPt> listKtdkPt = new ArrayList<ImportKtdkPt>();

		try {
			wb = WorkbookFactory.create(is);
			sheet = wb.getSheetAt(0);

			if (sheet == null) {
				log.error("sheet not exist");
				return null;
			}

			int rows = sheet.getPhysicalNumberOfRows();

			if (rows <= 1) {
				log.error("Data empty");
				return null;
			}

			String currentVehicleCode = "";
			ImportKtdkPt importKtdkPt = null;
			List<KtdkPtDetail> ktdkPtDetails = null;

			for (int i = 2; i < rows; i++) {
				int rowNum = i + 1;
				// The data is taken from the second row, and the default first row is the
				// header.
				Row row = sheet.getRow(i);
				// Check if the current line is empty
				if (isRowEmpty(row)) {
					continue;
				}

				String vehicleCode = getCellValue(row, 1).toString();

				// Check if row first empty
				if (StringUtils.isBlank(vehicleCode) && i == 2) {
					errors.add("Dòng [" + rowNum + "] : Số máy không hợp lệ!");
					result.setErrors(errors);
					return result;
				}

				// Merge row tiep theo neu so may blank
				Boolean isMerge = false;
				if (StringUtils.isBlank(vehicleCode)) {
					isMerge = true;
				}

				if (!vehicleCode.equals(currentVehicleCode) || currentVehicleCode == "") {

					// Init importKtdkPt neu khong gop row
					if (!isMerge) {
						importKtdkPt = new ImportKtdkPt();

						importKtdkPt.setVehicleCode(vehicleCode);

						String km = getCellValue(row, 2).toString();
						importKtdkPt.setKm(km);

						String vehicleNumber = getCellValue(row, 3).toString();
						if (StringUtils.isBlank(vehicleNumber)) {
							vehicleNumber = "0";
						}
						importKtdkPt.setVehicleNumber(vehicleNumber);

						String technician = getCellValue(row, 9).toString();
						importKtdkPt.setTechnician(technician);

						String finalCheck = getCellValue(row, 10).toString();
						importKtdkPt.setFinalCheck(finalCheck);

						String pi = getCellValue(row, 11).toString();
						if (NumberUtils.isParsable(pi)) {
							importKtdkPt.setPi(Integer.parseInt(pi));
						}

						ktdkPtDetails = new ArrayList<KtdkPtDetail>();
					}

					// Add detail spare
					KtdkPtDetail spareDetail = new KtdkPtDetail();
					spareDetail.setTypeDetail(DetailType.SPARE.value());

					String spareCode = getCellValue(row, 4).toString();

					if (!StringUtils.isBlank(spareCode)) {
						String spareQuantity = getCellValue(row, 5).toString();
						if (StringUtils.isBlank(spareQuantity) || !NumberUtils.isParsable(spareQuantity)
								|| Integer.parseInt(spareQuantity) <= 0) {
							errors.add("Dòng [" + rowNum + "] : Số lượng phụ tùng không hợp lệ! ");
						}
						spareDetail.setCode(spareCode);
						spareDetail.setQuantity(spareQuantity);
						ktdkPtDetails.add(spareDetail);
					}

					// Add detail job
					KtdkPtDetail jobDetail = new KtdkPtDetail();
					jobDetail.setTypeDetail(DetailType.JOB.value());

					String jobCode = getCellValue(row, 6).toString();
					if (!StringUtils.isBlank(jobCode)) {
						String jobDescription = getCellValue(row, 7).toString();
						if (StringUtils.isBlank(jobDescription)) {
							errors.add("Dòng [" + rowNum + "] : Mô tả khác không hợp lệ! ");
						}

						String jobPrice = getCellValue(row, 8).toString();
						if (StringUtils.isBlank(jobPrice)) {
							jobPrice = "0";
						} else if (!NumberUtils.isParsable(jobPrice)) {
							errors.add("Dòng [" + rowNum + "] : Tiền công không hợp lệ! ");
						}

						jobDetail.setCode(jobCode);
						jobDetail.setDescription(jobDescription);
						jobDetail.setPrice(jobPrice);
						ktdkPtDetails.add(jobDetail);
					}

					// Add list details into importKtdkPt
					if (spareDetail != null || jobDetail != null) {
						importKtdkPt.setDetails(ktdkPtDetails);
					}

					// Kiem tra du lieu hop le
					if (!isMerge) {
						List<String> listError = checkKtdkPt(rowNum, importKtdkPt);
						if (CollectionUtils.isEmpty(listError)) {
							listKtdkPt.add(importKtdkPt);
						} else {
							errors.addAll(listError);
						}
					} else {
						// Xoa va them lai record neu gop row
						if (!listKtdkPt.isEmpty()) {
							listKtdkPt.remove(listKtdkPt.size() - 1);
							listKtdkPt.add(importKtdkPt);
						}
					}

					currentVehicleCode = vehicleCode;
				}
			}
			result.setErrors(errors);
			result.setListKtdkPt(listKtdkPt);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Import data exception: {}", e.getMessage());
		} finally {
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * Read file excel import psc
	 */
	@Override
	public ResultImportPscRes importPsc(InputStream is) {

		ResultImportPscRes result = new ResultImportPscRes();
		List<String> errors = new ArrayList<String>();
		Workbook wb = null;
		Sheet sheet = null;

		List<ImportPsc> listPsc = new ArrayList<ImportPsc>();
		try {
			wb = WorkbookFactory.create(is);
			sheet = wb.getSheetAt(0);

			if (sheet == null) {
				log.error("sheet not exist");
				return null;
			}

			int rows = sheet.getPhysicalNumberOfRows();

			if (rows <= 1) {
				log.error("Data empty");
				return null;
			}

			String currentVehicleCode = "";
			ImportPsc importPsc = null;
			List<PscDetail> pscDetails = null;

			for (int i = 2; i < rows; i++) {
				int rowNum = i + 1;
				// The data is taken from the second row, and the default first row is the
				// header.
				Row row = sheet.getRow(i);
				// Check if the current line is empty
				if (isRowEmpty(row)) {
					continue;
				}

				String vehicleCode = getCellValue(row, 1).toString();

				// Check if row first empty
				if (StringUtils.isBlank(vehicleCode) && i == 2) {
					errors.add("Dòng [" + rowNum + "] : Số máy không hợp lệ!");
					result.setErrors(errors);
					return result;
				}

				// Merge row tiep theo neu so may blank
				Boolean isMerge = false;
				if (StringUtils.isBlank(vehicleCode)) {
					isMerge = true;
				}

				if (!vehicleCode.equals(currentVehicleCode) || currentVehicleCode == "") {

					// Init importPsc neu khong gop row
					if (!isMerge) {
						importPsc = new ImportPsc();

						importPsc.setVehicleCode(vehicleCode);

						String km = getCellValue(row, 2).toString();
						importPsc.setKm(km);

						String vehicleNumber = getCellValue(row, 3).toString();
						if (StringUtils.isBlank(vehicleNumber)) {
							vehicleNumber = "0";
						}
						importPsc.setVehicleNumber(vehicleNumber);

						String technician = getCellValue(row, 9).toString();
						importPsc.setTechnician(technician);

						String finalCheck = getCellValue(row, 10).toString();
						importPsc.setFinalCheck(finalCheck);

						String repairType = getCellValue(row, 11).toString();
						importPsc.setRepairType(repairType);

						pscDetails = new ArrayList<PscDetail>();
					}

					// Add detail spare
					PscDetail spareDetail = new PscDetail();
					spareDetail.setTypeDetail(DetailType.SPARE.value());
					String spareCode = getCellValue(row, 4).toString();

					if (!StringUtils.isBlank(spareCode)) {
						String spareQuantity = getCellValue(row, 5).toString();
						if (StringUtils.isBlank(spareQuantity) || !NumberUtils.isParsable(spareQuantity)
								|| Integer.parseInt(spareQuantity) <= 0) {
							errors.add("Dòng [" + rowNum + "] : Số lượng phụ tùng không hợp lệ! ");
						}
						spareDetail.setCode(spareCode);
						spareDetail.setQuantity(spareQuantity);
						pscDetails.add(spareDetail);
					}

					// Add detail job
					PscDetail jobDetail = new PscDetail();
					jobDetail.setTypeDetail(DetailType.JOB.value());
					String jobCode = getCellValue(row, 6).toString();
					if (!StringUtils.isBlank(jobCode)) {
						String jobDescription = getCellValue(row, 7).toString();
						if (StringUtils.isBlank(jobDescription)) {
							errors.add("Dòng [" + rowNum + "] : Mô tả khác không hợp lệ! ");
						}

						String jobPrice = getCellValue(row, 8).toString();
						if (StringUtils.isBlank(jobPrice)) {
							jobPrice = "0";
						} else if (!NumberUtils.isParsable(jobPrice)) {
							errors.add("Dòng [" + rowNum + "] : Tiền công không hợp lệ! ");
						}

						jobDetail.setCode(jobCode);
						jobDetail.setDescription(jobDescription);
						jobDetail.setPrice(jobPrice);
						pscDetails.add(jobDetail);
					}

					// Add list details into importKtdkPt
					if (spareDetail != null || jobDetail != null) {
						importPsc.setDetails(pscDetails);
					}

					// Kiem tra du lieu hop le
					if (!isMerge) {
						List<String> listError = checkPsc(rowNum, importPsc);
						if (CollectionUtils.isEmpty(listError)) {
							listPsc.add(importPsc);
						} else {
							errors.addAll(listError);
						}
					} else {
						// Xoa va them lai record neu gop row
						if (!listPsc.isEmpty()) {
							listPsc.remove(listPsc.size() - 1);
							listPsc.add(importPsc);
						}
					}

					currentVehicleCode = vehicleCode;
				}
			}
			result.setErrors(errors);
			result.setListPsc(listPsc);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Import data exception: {}", e.getMessage());
		} finally {
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * Check row empty
	 * 
	 * @param row
	 * @return
	 */
	private boolean isRowEmpty(Row row) {
		if (row == null) {
			return true;
		}
		for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
			Cell cell = row.getCell(i);
			if (cell != null && cell.getCellType() != CellType.BLANK) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Validate import ktdk
	 * 
	 * @param rowNum
	 * @param ktdk
	 * @return
	 */
	private List<String> checkKtdk(int rowNum, Ktdk ktdk) {
		List<String> errors = new ArrayList<String>();
		String prefix = "Dòng [" + rowNum + "] : ";

		// example : JA39E-2525795
		if (ktdk.getVehicleCode() == null || StringUtils.isBlank(ktdk.getVehicleCode())
				|| ktdk.getVehicleCode().length() != 13 || !ktdk.getVehicleCode().substring(5, 6).equals("-")) {
			errors.add(prefix + "Số máy không hợp lệ!");
		}

		if (ktdk.getKm() == null || StringUtils.isBlank(ktdk.getKm()) || !NumberUtils.isParsable(ktdk.getKm())
				|| Integer.parseInt(ktdk.getKm()) < 0 || Integer.parseInt(ktdk.getKm()) > 30000) {
			errors.add(prefix + "Km không hợp lệ!");
		}

		if (ktdk.getPi() == null || ktdk.getPi() <= 0 || ktdk.getPi() > 6) {
			errors.add(prefix + "Số lần PI không hợp lệ!");
		}

		if (ktdk.getTechnician() == null || StringUtils.isBlank(ktdk.getTechnician())) {
			errors.add(prefix + "Kỹ thuật viên không hợp lệ!");
		}

		if (ktdk.getFinalCheck() == null || StringUtils.isBlank(ktdk.getFinalCheck())) {
			errors.add(prefix + "Kiểm tra cuối không hợp lệ!");
		}
		return errors;
	}

	/**
	 * Validate import ktdkPt
	 * 
	 * @param rowNum
	 * @param importKtdkPt
	 * @return
	 */
	private List<String> checkKtdkPt(int rowNum, ImportKtdkPt importKtdkPt) {
		List<String> errors = new ArrayList<String>();
		String prefix = "Dòng [" + rowNum + "] : ";

		// example : JA39E-2525795
		if (importKtdkPt.getVehicleCode() == null || StringUtils.isBlank(importKtdkPt.getVehicleCode())
				|| importKtdkPt.getVehicleCode().length() != 13
				|| !importKtdkPt.getVehicleCode().substring(5, 6).equals("-")) {
			errors.add(prefix + "Số máy không hợp lệ!");
		}

		if (importKtdkPt.getKm() == null || StringUtils.isBlank(importKtdkPt.getKm())
				|| !NumberUtils.isParsable(importKtdkPt.getKm()) || Integer.parseInt(importKtdkPt.getKm()) < 0
				|| Integer.parseInt(importKtdkPt.getKm()) > 30000) {
			errors.add(prefix + "Km không hợp lệ!");
		}

		if (importKtdkPt.getPi() == null || importKtdkPt.getPi() <= 0 || importKtdkPt.getPi() > 6) {
			errors.add(prefix + "Số lần PI không hợp lệ!");
		}

		if (importKtdkPt.getTechnician() == null || StringUtils.isBlank(importKtdkPt.getTechnician())) {
			errors.add(prefix + "Kỹ thuật viên không hợp lệ!");
		}

		if (importKtdkPt.getFinalCheck() == null || StringUtils.isBlank(importKtdkPt.getFinalCheck())) {
			errors.add(prefix + "Kiểm tra cuối không hợp lệ!");
		}
		return errors;
	}

	/**
	 * Validate import PSC
	 * 
	 * @param rowNum
	 * @param importPsc
	 * @return
	 */
	private List<String> checkPsc(int rowNum, ImportPsc importPsc) {
		List<String> errors = new ArrayList<String>();
		String prefix = "Dòng [" + rowNum + "] : ";

		// example : JA39E-2525795
		if (importPsc.getVehicleCode() == null || StringUtils.isBlank(importPsc.getVehicleCode())
				|| importPsc.getVehicleCode().length() != 13
				|| !importPsc.getVehicleCode().substring(5, 6).equals("-")) {
			errors.add(prefix + "Số máy không hợp lệ!");
		}

		if (importPsc.getKm() == null || StringUtils.isBlank(importPsc.getKm())
				|| !NumberUtils.isParsable(importPsc.getKm()) || Integer.parseInt(importPsc.getKm()) < 0) {
			errors.add(prefix + "Km không hợp lệ!");
		}

		if (importPsc.getRepairType() == null || StringUtils.isBlank(importPsc.getRepairType())) {
			errors.add(prefix + "Loại dịch vụ không hợp lệ!");
		}

		if (importPsc.getTechnician() == null || StringUtils.isBlank(importPsc.getTechnician())) {
			errors.add(prefix + "Kỹ thuật viên không hợp lệ!");
		}

		if (importPsc.getFinalCheck() == null || StringUtils.isBlank(importPsc.getFinalCheck())) {
			errors.add(prefix + "Kiểm tra cuối không hợp lệ!");
		}
		return errors;
	}

	/**
	 * Validate date time
	 * 
	 * @param date
	 * @return
	 */
	private boolean isValidDate(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			dateFormat.parse(date);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Lay du lieu
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public Object getCellValue(Row row, int column) {
		if (row == null) {
			return row;
		}
		Object val = "";
		try {
			Cell cell = row.getCell(column);
			if (StringUtils.isNotNull(cell)) {
				if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
					val = cell.getNumericCellValue();
					if (DateUtil.isCellDateFormatted(cell)) {
						val = DateUtil.getJavaDate((Double) val); // POI Excel 日期格式转换
					} else {
						if ((Double) val % 1 != 0) {
							val = new BigDecimal(val.toString());
						} else {
							val = new DecimalFormat("0").format(val);
						}
					}
				} else if (cell.getCellType() == CellType.STRING) {
					String celVal = cell.getStringCellValue();
					val = (celVal == null) ? null : celVal.trim();
				} else if (cell.getCellType() == CellType.BOOLEAN) {
					val = cell.getBooleanCellValue();
				} else if (cell.getCellType() == CellType.ERROR) {
					val = cell.getErrorCellValue();
				}
			}
		} catch (Exception e) {
			return val;
		}
		return val;
	}
}
