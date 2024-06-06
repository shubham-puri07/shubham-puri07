package com.genentech.baseclasses;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.genentech.util.ExcelReader;
import com.genentech.util.PageManager;
import com.genentech.util.PageObject;

public class RedirectsBasePage extends PageObject {
	
	public RedirectsBasePage(PageManager pm, ExcelReader xl) {
		super(pm, xl);
	}
	
	private static List<String> expectedSourceURLList = new ArrayList<>();
	private static List<String> expectedDestURLList = new ArrayList<>();
	private static List<String> actualDestURLList = new ArrayList<>();
	private static List<String> actualDestURLStatusCodeList = new ArrayList<>();
	private static List<String> redirectURLCompareResultList = new ArrayList<>();
	SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
	String date = df.format(new Date());
	BasePage basePage = new BasePage(pageManager, excelReader);	
	
	public void readData(){

		String data;
		for(int i=1;i<=excelReader.getRowCount("Sheet1");i++)
		{
			 data = excelReader.getCellData("Sheet1", "Source URL", i).toString();
			 expectedSourceURLList.add(data);
			 data = excelReader.getCellData("Sheet1", "Destination URL", i).toString();
			 expectedDestURLList.add(data);
		}
		//Remove Column Name items from list
		expectedSourceURLList.remove(0);
		expectedDestURLList.remove(0);
	}
	
	public void verifyRedirects() throws IOException, InterruptedException {
		for (int i=0; i<expectedSourceURLList.size();i++) {
			if(expectedSourceURLList.get(i).startsWith("http://") || expectedSourceURLList.get(i).startsWith("https://")) {
				basePage.navigateToURL(expectedSourceURLList.get(i));
			} else {
				String source_url = "https://" + expectedSourceURLList.get(i);
				basePage.navigateToURL(source_url);
			}
			
			System.out.println("Source URL: " + expectedSourceURLList.get(i));
			System.out.println("Destination URL: " + expectedDestURLList.get(i));
			System.out.println("Actual URL: "+pageManager.getDriver().getCurrentUrl());
			actualDestURLList.add(pageManager.getDriver().getCurrentUrl().toString());
			System.out.println(pageManager.getURLResponse(pageManager.getDriver().getCurrentUrl().toString()));
			actualDestURLStatusCodeList.add(pageManager.getURLResponse(pageManager.getDriver().getCurrentUrl().toString()));
			if(expectedDestURLList.get(i).equals(actualDestURLList.get(i))) {
				redirectURLCompareResultList.add("Pass");
			} else {
				redirectURLCompareResultList.add("Fail");
			}
		}
	}
	
	public void createOutputExcelFile() throws IOException {
		// Create new workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet redirect_sheet;
		Row redirectRow;
		// Create a new font and alter it.
		XSSFFont font = workbook.createFont();
		font.setBold(true);

		// Fonts are set into a style so create a new one to use.
		CellStyle style = workbook.createCellStyle();
		style.setFont(font);
		
		redirect_sheet = workbook.createSheet("Redirects_Output");
		redirectRow = redirect_sheet.createRow(0);
		redirectRow.createCell(0).setCellValue("Source URL");
		redirectRow.createCell(1).setCellValue("Destination URL");
		redirectRow.createCell(2).setCellValue("Actual URL");
		redirectRow.createCell(3).setCellValue("Status");
		redirectRow.createCell(4).setCellValue("Result");
		
		for (int i = 0; i < redirectRow.getLastCellNum(); i++) {
			redirectRow.getCell(i).setCellStyle(style);
		}
		
		for(int i=0; i<expectedSourceURLList.size();i++) {
			int rowNum_redirect = redirect_sheet.getLastRowNum();
			redirectRow = redirect_sheet.createRow(rowNum_redirect + 1);
			redirectRow.createCell(0).setCellValue(expectedSourceURLList.get(i));
			redirectRow.createCell(1).setCellValue(expectedDestURLList.get(i));
			redirectRow.createCell(2).setCellValue(actualDestURLList.get(i));
			redirectRow.createCell(3).setCellValue(actualDestURLStatusCodeList.get(i));
			redirectRow.createCell(4).setCellValue(redirectURLCompareResultList.get(i));
		}
		

		try {
			FileOutputStream out = new FileOutputStream(
					 System.getProperty("user.dir")+ "/ExcelOutput/RedirectsVerification/RedirectsTest_" + date + ".xlsx");
			workbook.write(out);
			workbook.close();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Excel Created!");
	}

}
