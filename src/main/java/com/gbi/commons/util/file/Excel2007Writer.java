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
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;

public class Excel2007Writer {
	public static void main(String[] args) throws IOException {
		SXSSFWorkbook book = new SXSSFWorkbook(100);
		SXSSFSheet sheet = (SXSSFSheet) book.createSheet("表单1");
		
		
		XSSFDataFormat dateFormat = (XSSFDataFormat) book.createDataFormat();
		System.out.println(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
	//	dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
	//	dateStyle.setDataFormat((short)31);//dateFormat.getFormat("2001/3/7")

		for (int rownum = 0; rownum < 60; ++rownum) {
			SXSSFRow row = (SXSSFRow) sheet.createRow(rownum);
				SXSSFCell cell = (SXSSFCell) row.createCell(0);
				/*if (cellnum % 2 == 0) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(RandomUtils.nextInt(0, 1000));
				} else if (cellnum % 3 == 0) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(dateStyle);
					cell.setCellValue(Calendar.getInstance().getTime());
				} else {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue("abcd");
				}*/
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				XSSFCellStyle dateStyle = (XSSFCellStyle) book.createCellStyle();
				dateStyle.setDataFormat((short)rownum);
				cell.setCellStyle(dateStyle);
				cell.setCellValue(Calendar.getInstance().getTime());
		}
		FileOutputStream out = new FileOutputStream("D:\\test3.xlsx");
		book.write(out);
		out.close();
		book.close();
	}
}