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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.genentech.util.ExcelReader;
import com.genentech.util.PageManager;
import com.genentech.util.PageObject;

public class TitleMetaBasePage extends PageObject {
	
	
	public TitleMetaBasePage(PageManager pm, ExcelReader xl) {
		super(pm, xl);
	}
	
	@FindBy(xpath="//meta[@name='description']")
	private WebElement metaDescriptionWebPage;
	
	@FindBy(xpath="//script[@src='//nexus.ensighten.com/gene/dev/Bootstrap.js']")
	private WebElement bootStrapUAT;
	
	@FindBy(xpath="//script[@src='//nexus.ensighten.com/gene/stage/Bootstrap.js']")
	private WebElement bootStrapPreview;
	
	@FindBy(xpath="//script[@src='//nexus.ensighten.com/gene/prod/Bootstrap.js']")
	private WebElement bootStrapLive;
	
	@FindBy(xpath="//script[@data-adobe-client-data-layers = 'xsdidatalayer']")
	private WebElement datalayerWebElement;
	
	private static List<String> urlList = new ArrayList<>();
	private static List<String> expectedTitleList = new ArrayList<>();
	private static List<String> expectedMetaList = new ArrayList<>();
	private static List<String> actualTitleList = new ArrayList<>();
	private static List<String> actualMetaList = new ArrayList<>();
	private static List<String> bootstrapOutputList = new ArrayList<>();
	private static List<String> HTML5OutputList = new ArrayList<>();
	SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
	String date = df.format(new Date());
	
	BasePage bp = new BasePage(pageManager, excelReader);
	
	public void readData(){

		String data;
		for(int i=1;i<=excelReader.getRowCount("Sheet1");i++)
		{
			 data = excelReader.getCellData("Sheet1", "URL", i);
			 urlList.add(data);
			 data = excelReader.getCellData("Sheet1", "Page Title", i);
			 expectedTitleList.add(data);
			 data = excelReader.getCellData("Sheet1", "Meta Description", i);
			 expectedMetaList.add(data);
		}
		//Remove Column Name items from list
		urlList.remove(0);
		expectedTitleList.remove(0);
		expectedMetaList.remove(0);
	}
	
	public void getTitleMetaInformation() throws InterruptedException, NoSuchElementException {
		for(int i=0;i<urlList.size();i++) {
			bp.navigateToURL(urlList.get(i));
			System.out.println(urlList.get(i));

			try { 
				// Get the Actual Title from the site
				System.out.println(pageManager.getDriver().getTitle());
				if(pageManager.getDriver().getTitle()!=null) {
					actualTitleList.add(pageManager.getDriver().getTitle().toString().trim());
				} else {
					actualTitleList.add("N/A");
				}
			} catch (NoSuchElementException e) {
				actualTitleList.add("N/A");
				System.out.println(e);
			}
			
			try{ 
				System.out.println(metaDescriptionWebPage.getAttribute("content"));
				if(!metaDescriptionWebPage.getAttribute("content").isBlank()) {
					actualMetaList.add(metaDescriptionWebPage.getAttribute("content"));
				} else {
					actualMetaList.add("");
				}
			} catch (NoSuchElementException e) {
				actualMetaList.add("N/A");
				System.out.println(e);
			}
			
			try {
				// Get the Bootstrap and HTML5 value from the site
				if (urlList.get(i).contains("uat")) {
					String x = bootStrapUAT.getAttribute("src");
					String y[] = x.split("/");
					System.out.println("BootStrap:" + y[4]);
					bootstrapOutputList.add(y[4]);
	
				} else if (urlList.get(i).contains("preview")) {
					String x = bootStrapPreview.getAttribute("src");
					String y[] = x.split("/");
					System.out.println("BootStrap:" + y[4]);
					bootstrapOutputList.add(y[4]);
				} else {
					String x = bootStrapLive.getAttribute("src");
					String y[] = x.split("/");
					System.out.println("BootStrap:" + y[4]);
					bootstrapOutputList.add(y[4]);
				}
			} catch (NoSuchElementException e) {
				bootstrapOutputList.add("N/A");
				System.out.println(e);
			}
			
			try { 
				if(datalayerWebElement.getAttribute("data-adobe-client-data-layers").contentEquals("")) {
					HTML5OutputList.add("");
				} else {
					HTML5OutputList.add(datalayerWebElement.getAttribute("data-adobe-client-data-layers").toString());
				}
			} catch (NoSuchElementException e) {
				HTML5OutputList.add("N/A");
				System.out.println(e);
			}
		
		}	
	}
	
	public void createOutputExcelFile() throws IOException {
		// Create new workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet linkSheet_title, linkSheet_meta, linkSheet_Others;
		Row titleRow,metaRow,otherRow;
		// Create a new font and alter it.
		XSSFFont font = workbook.createFont();
		font.setBold(true);

		// Fonts are set into a style so create a new one to use.
		CellStyle style = workbook.createCellStyle();
		style.setFont(font);
		
		linkSheet_title = workbook.createSheet("Title_Output");
		titleRow = linkSheet_title.createRow(0);
		titleRow.createCell(0).setCellValue("Url");
		titleRow.createCell(1).setCellValue("Expected Title");
		titleRow.createCell(2).setCellValue("Actual Title");
		titleRow.createCell(3).setCellValue("Status");
		
		for (int i = 0; i < titleRow.getLastCellNum(); i++) {
			titleRow.getCell(i).setCellStyle(style);
		}

		linkSheet_meta = workbook.createSheet("Meta_Output");
		metaRow = linkSheet_meta.createRow(0);
		metaRow.createCell(0).setCellValue("Url");
		metaRow.createCell(1).setCellValue("Expected Meta");
		metaRow.createCell(2).setCellValue("Actual Meta");
		metaRow.createCell(3).setCellValue("Status");
		
		for (int i = 0; i < metaRow.getLastCellNum(); i++) {
			metaRow.getCell(i).setCellStyle(style);
		}
		
		linkSheet_Others = workbook.createSheet("Other_Outputs");
		otherRow = linkSheet_Others.createRow(0);
		otherRow.createCell(0).setCellValue("Url");
		otherRow.createCell(1).setCellValue("Bootstrap");
		otherRow.createCell(2).setCellValue("HTML5");
		
		for (int i = 0; i < otherRow.getLastCellNum(); i++) {
			otherRow.getCell(i).setCellStyle(style);
		}
		
		for(int i=0; i<urlList.size();i++) {
			int rowNum_title = linkSheet_title.getLastRowNum();
			titleRow = linkSheet_title.createRow(rowNum_title + 1);
			titleRow.createCell(0).setCellValue(urlList.get(i));
			titleRow.createCell(1).setCellValue(expectedTitleList.get(i));
			titleRow.createCell(2).setCellValue(actualTitleList.get(i));
			if(expectedTitleList.get(i).toString().trim().equals(actualTitleList.get(i).toString().trim())) {
				titleRow.createCell(3).setCellValue("Pass");
			} else { 
				titleRow.createCell(3).setCellValue("Fail");
			}
			
			int rowNum_meta = linkSheet_meta.getLastRowNum();
			metaRow = linkSheet_meta.createRow(rowNum_meta + 1);
			metaRow.createCell(0).setCellValue(urlList.get(i));
			metaRow.createCell(1).setCellValue(expectedMetaList.get(i));
			metaRow.createCell(2).setCellValue(actualMetaList.get(i));
			if(expectedMetaList.get(i).toString().trim().equals(actualMetaList.get(i).toString().trim())) {
				metaRow.createCell(3).setCellValue("Pass");
			} else { 
				metaRow.createCell(3).setCellValue("Fail");
			}
			
			int rowNum_other = linkSheet_Others.getLastRowNum();
			otherRow = linkSheet_Others.createRow(rowNum_other +1);
			otherRow.createCell(0).setCellValue(urlList.get(i));
			otherRow.createCell(1).setCellValue(bootstrapOutputList.get(i));
			otherRow.createCell(2).setCellValue(HTML5OutputList.get(i));
		}
		
		try {
			FileOutputStream out = new FileOutputStream(
					 System.getProperty("user.dir")+ "/ExcelOutput/TitleMetaVerification/TitleMetaTest_" + date + ".xlsx");
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
