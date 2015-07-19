package com.gbi.commons.util.file;

import org.json.JSONObject;

public class Excel {

	// excel2003扩展名
	public static final String EXCEL03_EXTENSION = ".xls";
	// excel2007扩展名
	public static final String EXCEL07_EXTENSION = ".xlsx";

	/**
	 * 读取Excel文件，可能是03也可能是07版本
	 * 
	 * @param excel03
	 * @param excel07
	 * @param fileName
	 * @throws Exception
	 */
	public static void readExcel(ExcelRowReader reader, String fileName) throws Exception {
		// 处理excel2003文件
/*		if (fileName.endsWith(EXCEL03_EXTENSION)) {
			Excel2003Reader excel = new Excel2003Reader();
			excel.setRowReader(reader);
			excel.process(fileName);
			// 处理excel2007文件
		} */
		if (fileName.endsWith(EXCEL07_EXTENSION)) {
			Excel2007Reader excel = new Excel2007Reader();
			excel.setRowReader(reader);
			excel.processOneSheet(fileName, "Sheet0");
		} else {
			throw new Exception("文件格式错误，扩展名只能是xls或xlsx");
		}
	}

	public static void main(String[] args) throws Exception {
		ExcelRowReader reader = new RowReader();
		readExcel(reader, "E:\\data.xlsx");
	}
}

class RowReader implements ExcelRowReader {
	public void getRows(int rowNumber, JSONObject json) {
		System.out.println(json);
	}
}
