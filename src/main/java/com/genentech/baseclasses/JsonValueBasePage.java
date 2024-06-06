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
import org.json.JSONObject;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.genentech.util.ExcelReader;
import com.genentech.util.PageManager;
import com.genentech.util.PageObject;

public class JsonValueBasePage extends PageObject {
	
	
	public JsonValueBasePage(PageManager pm, ExcelReader xl) {
		super(pm, xl);
		// TODO Auto-generated constructor stub
	}
	
	private static List<String> urlList = new ArrayList<>();
	private static List<String> expectedSiteNameList = new ArrayList<>();
	private static List<String> expectedIndicationList = new ArrayList<>();
	private static List<String> expectedSiteAudienceList = new ArrayList<>();
	private static List<String> expectedTopicList = new ArrayList<>();
	private static List<String> actualSiteNameList = new ArrayList<>();
	private static List<String> actualIndicationList = new ArrayList<>();
	private static List<String> actualSiteAudienceList = new ArrayList<>();
	private static List<String> actualTopicList = new ArrayList<>();
	SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
	String date = df.format(new Date());
	BasePage basePage = new BasePage(pageManager, excelReader);	

	public void readData(){
		
		String data;
		for(int i=1;i<=excelReader.getRowCount("Sheet1");i++)
		{
			 data = excelReader.getCellData("Sheet1", "URL", i);
			 urlList.add(data);
			 data = excelReader.getCellData("Sheet1", "SiteName", i);
			 expectedSiteNameList.add(data);
			 data = excelReader.getCellData("Sheet1", "Indication", i);
			 expectedIndicationList.add(data);
			 data = excelReader.getCellData("Sheet1", "SiteAudience", i);
			 expectedSiteAudienceList.add(data);
			 data = excelReader.getCellData("Sheet1", "Topic", i);
			 expectedTopicList.add(data);
		}
		//Remove Column Name items from list
		urlList.remove(0);
		expectedSiteNameList.remove(0);
		expectedIndicationList.remove(0);
		expectedSiteAudienceList.remove(0);
		expectedTopicList.remove(0);
	}
	
	public void getJsonValues() throws JsonProcessingException, InterruptedException {
		
		for(int i=0;i<urlList.size();i++) {
			basePage.navigateToURL(urlList.get(i));
			try {
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				JavascriptExecutor j = (JavascriptExecutor) pageManager.getDriver();
				Object t = j.executeScript("return dataLayer");
				String json = ow.writeValueAsString(t);
				int j1 = json.indexOf("{");
				json = json.substring(j1);
				System.out.println(json);
				JSONObject jsonobject = new JSONObject(json);
				actualSiteNameList.add(jsonobject.getString("siteName"));
				actualIndicationList.add(jsonobject.getString("indication"));
				actualSiteAudienceList.add(jsonobject.getString("siteAudience"));
				actualTopicList.add(jsonobject.getString("topic"));
			}catch (JavascriptException e) {
				
				actualSiteNameList.add("No dataLayer present");
				actualIndicationList.add("No dataLayer present");
				actualSiteAudienceList.add("No dataLayer present");
				actualTopicList.add("No dataLayer present");
				
				
				System.err.println(e.getMessage());
			}
		
		}
	}
	
	public void createOutputExcelFile() throws IOException {
		// Create new workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet json_sheet;
		Row jsonRow;
		// Create a new font and alter it.
		XSSFFont font = workbook.createFont();
		font.setBold(true);

		// Fonts are set into a style so create a new one to use.
		CellStyle style = workbook.createCellStyle();
		style.setFont(font);
		
		json_sheet = workbook.createSheet("JSON_Output");
		jsonRow = json_sheet.createRow(0);
		jsonRow.createCell(0).setCellValue("Url");
		jsonRow.createCell(1).setCellValue("Expected SiteName");
		jsonRow.createCell(2).setCellValue("Actual SiteName");
		jsonRow.createCell(3).setCellValue("Status SiteName");
		jsonRow.createCell(4).setCellValue("Expected SiteAudience");
		jsonRow.createCell(5).setCellValue("Actual SiteAudience");
		jsonRow.createCell(6).setCellValue("Status SiteAudience");
		jsonRow.createCell(7).setCellValue("Expected Indication");
		jsonRow.createCell(8).setCellValue("Actual Indication");
		jsonRow.createCell(9).setCellValue("Status Indication");
		jsonRow.createCell(10).setCellValue("Expected Topic");
		jsonRow.createCell(11).setCellValue("Actual Topic");
		jsonRow.createCell(12).setCellValue("Status Topic");
		
		for (int i = 0; i < jsonRow.getLastCellNum(); i++) {
			jsonRow.getCell(i).setCellStyle(style);
		}
		
		for(int i=0; i<urlList.size();i++) {
			int rowNum_json = json_sheet.getLastRowNum();
			jsonRow = json_sheet.createRow(rowNum_json + 1);
			jsonRow.createCell(0).setCellValue(urlList.get(i));
			jsonRow.createCell(1).setCellValue(expectedSiteNameList.get(i));
			jsonRow.createCell(2).setCellValue(actualSiteNameList.get(i));
			if(expectedSiteNameList.get(i).toString().trim().equalsIgnoreCase(actualSiteNameList.get(i).toString().trim())) {
				jsonRow.createCell(3).setCellValue("Pass");
			} else { 
				jsonRow.createCell(3).setCellValue("Fail");
			}
			jsonRow.createCell(4).setCellValue(expectedSiteAudienceList.get(i));
			jsonRow.createCell(5).setCellValue(actualSiteAudienceList.get(i));
			if(expectedSiteAudienceList.get(i).toString().trim().equalsIgnoreCase(actualSiteAudienceList.get(i).toString().trim())) {
				jsonRow.createCell(6).setCellValue("Pass");
			} else { 
				jsonRow.createCell(6).setCellValue("Fail");
			}
			jsonRow.createCell(7).setCellValue(expectedIndicationList.get(i));
			jsonRow.createCell(8).setCellValue(actualIndicationList.get(i));
			if(expectedIndicationList.get(i).toString().trim().equalsIgnoreCase(actualIndicationList.get(i).toString().trim())) {
				jsonRow.createCell(9).setCellValue("Pass");
			} else { 
				jsonRow.createCell(9).setCellValue("Fail");
			}
			jsonRow.createCell(10).setCellValue(expectedTopicList.get(i));
			jsonRow.createCell(11).setCellValue(actualTopicList.get(i));
			if(expectedTopicList.get(i).toString().trim().equalsIgnoreCase(actualTopicList.get(i).toString().trim())) {
				jsonRow.createCell(12).setCellValue("Pass");
			} else { 
				jsonRow.createCell(12).setCellValue("Fail");
			}
		}
		
		try {
			FileOutputStream out = new FileOutputStream(
					 System.getProperty("user.dir")+ "/ExcelOutput/JsonValueVerification/JsonValueTest_" + date + ".xlsx");
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
