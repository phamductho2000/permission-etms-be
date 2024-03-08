package com.fis.invoice.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class Excel {
	private XSSFWorkbook wb;
	private XSSFCreationHelper ch;
	private XSSFCellStyle cs_double;
	private XSSFCellStyle cs_long;
	private XSSFCellStyle cs_left;
	private XSSFCellStyle cs_center;

	private XSSFCellStyle createCellStyle(HorizontalAlignment alignment, XSSFFont font, short format) {
		XSSFCellStyle cs = wb.createCellStyle();
		cs.setFont(font);
		cs.setAlignment(alignment);
		cs.setVerticalAlignment(VerticalAlignment.CENTER);
		if (format != 0)
			cs.setDataFormat(format);
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		return cs;
	}

	private XSSFCell createCell(XSSFRow row, int columnIndex, XSSFCellStyle cellStyle, String content) {
		XSSFCell cell = row.createCell(columnIndex);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(content);
		return cell;
	}

	private XSSFCell createCell(XSSFRow row, int columnIndex, XSSFCellStyle cellStyle, double content) {
		XSSFCell cell = row.createCell(columnIndex);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(content);
		return cell;
	}

	private Map<String, String> arr2map(String[] arr) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < arr.length; i++) {
			map.put("{" + i + "}", arr[i]);
		}
		return map;
	}

	private Map<Integer, Integer> get_maptype(ResultSetMetaData resultSetMetaData, int len) throws Exception {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 1; i <= len; i++) {
			int type = resultSetMetaData.getColumnType(i);
			if (type >= 2 && type <= 8)
				map.put(i, null);
		}
		return map;
	}

	private void dtl(XSSFSheet sheet, ResultSet rs, int rf) throws Exception {
		ResultSetMetaData resultSetMetaData = rs.getMetaData();
		int len = resultSetMetaData.getColumnCount();
		Map<Integer, Integer> mapType = get_maptype(resultSetMetaData, len);
		int stt = 0;
		while (rs.next()) {
			XSSFRow row = sheet.createRow(rf++);
			createCell(row, 0, cs_center, ++stt);
			for (int i = 1; i <= len; i++) {
				if (mapType.containsKey(i)) {
					double d = rs.getDouble(i);
					if (rs.wasNull())
						createCell(row, i, cs_long, "");
					else
						createCell(row, i, (d % 1) == 0 ? cs_long : cs_double, d);
				} else {
					createCell(row, i, cs_left, rs.getString(i));
				}
			}
		}
	}

	private void hdr(Map<String, String> map, XSSFSheet sheet) {
		Iterator<Row> iterRow = sheet.rowIterator();
		while (iterRow.hasNext()) {
			XSSFRow row = (XSSFRow) iterRow.next();
			Iterator<Cell> iterCell = row.cellIterator();
			while (iterCell.hasNext()) {
				XSSFCell cell = (XSSFCell) iterCell.next();
				if (cell.getCellType() == CellType.STRING) {
					String cv = cell.getRichStringCellValue().getString();
					boolean b = false;
					for (Map.Entry<String, String> entry : map.entrySet()) {
						String key = entry.getKey();
						if (cv.indexOf(key) > -1) {
							b = true;
							cv = cv.replace(key, entry.getValue());
						}
					}
					if (b)
						cell.setCellValue(ch.createRichTextString(cv));
				}
			}
		}
	}

	public byte[] createWorkbook(String report, String[] arr, ResultSet rs, int rf) throws Exception {
		String fileName = "template/" + report + ".xlsx";
		ClassLoader classLoader = getClass().getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(fileName);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			wb = new XSSFWorkbook(is);
			XSSFDataFormat df = wb.createDataFormat();
			XSSFFont font = wb.createFont();
			font.setFontName("Times New Roman");
			font.setFontHeightInPoints((short) 11);
			this.cs_left = createCellStyle(HorizontalAlignment.LEFT, font, (short) 0);
			this.cs_center = createCellStyle(HorizontalAlignment.CENTER, font, (short) 0);
			this.cs_long = createCellStyle(HorizontalAlignment.RIGHT, font, df.getFormat("#,##0"));
			this.cs_double = createCellStyle(HorizontalAlignment.RIGHT, font, df.getFormat("#,##0.00"));
			this.ch = wb.getCreationHelper();
			XSSFSheet sheet = wb.getSheetAt(0);
			hdr(arr2map(arr), sheet);
			dtl(sheet, rs, rf);
			wb.write(baos);
			return baos.toByteArray();
		}
	}
}