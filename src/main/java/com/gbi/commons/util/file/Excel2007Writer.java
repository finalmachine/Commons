package com.gbi.commons.util.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.lang3.RandomUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class Excel2007Writer {
	public static void main(String[] args) throws IOException {
		Workbook book = new SXSSFWorkbook(100);
		Sheet sheet = book.createSheet("表单1");
		
		CellStyle dateStyle = book.createCellStyle();
		DataFormat dateFormat = book.createDataFormat();
		System.out.println(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
	//	dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
		dateStyle.setDataFormat(dateFormat.getFormat("2001/3/7"));

		for (int rownum = 0; rownum < 1000; ++rownum) {
			Row row = sheet.createRow(rownum);
			for (int cellnum = 0; cellnum < 20; cellnum++) {
				Cell cell = row.createCell(cellnum);
				if (cellnum % 2 == 0) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(RandomUtils.nextInt(0, 1000));
				} else if (cellnum % 3 == 0) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(dateStyle);
					cell.setCellValue(Calendar.getInstance().getTime());
				} else {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue("abcd");
				}
			}
		}
		FileOutputStream out = new FileOutputStream("D:\\test2.xlsx");
		book.write(out);
		out.close();
		book.close();
	}
}