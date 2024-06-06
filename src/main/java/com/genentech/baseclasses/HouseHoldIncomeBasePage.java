package com.genentech.baseclasses;

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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import com.genentech.util.ExcelReader;
import com.genentech.util.PageManager;
import com.genentech.util.PageObject;

public class HouseHoldIncomeBasePage extends PageObject{

	private static List<String> urlList = new ArrayList<>();
	private static List<String> updatedURLList = new ArrayList<>();
	private static List<String> memberCountList = new ArrayList<>();
	private static List<String> incomeCountList = new ArrayList<>();
	private static List<String> actualURLList = new ArrayList<>();
	SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
	String date = df.format(new Date());
	
	String houseHold_actual_Url = "";
	BasePage basePage = new BasePage(pageManager, excelReader);	
	
	public HouseHoldIncomeBasePage(PageManager pm, ExcelReader xl) {
		super(pm, xl);
		// TODO Auto-generated constructor stub
	}
	
	//*********************Household Income tool***********************//
	@FindBy(xpath = ".//select[@name='number-of-household-members']")
	public WebElement memberDrpDwn;
	
	@FindBy(xpath = ".//select[@name='net-household-income']")
	public WebElement incomeDrpDwn;
	
	@FindBy(xpath = "//button[contains(., 'Confirm')]")
	public WebElement confirmBtn;
		
	
	public void readData() {
		String data;
		for(int i=1;i<=excelReader.getRowCount("Sheet1");i++)
		{
			 data = excelReader.getCellData("Sheet1", "URL", i);
			 urlList.add(data);
			
		}
		//Remove Column Name items from list
		urlList.remove(0);
	}
	
	public void verifyHouseHoldIncome() throws InterruptedException {
		for (int j=0; j<urlList.size();j++) {
			JavascriptExecutor js = (JavascriptExecutor) pageManager.getDriver();
			String url = urlList.get(j).replace(".html", "/gpf-financial-eligibility.html");
			
			basePage.navigateToURL(url);
			
			Select meetHouseholdDrpDwn = new Select(memberDrpDwn);
			List<WebElement> memberCount = meetHouseholdDrpDwn.getOptions();

			Select meetIncomedDrpDwn = new Select(incomeDrpDwn);
			List<WebElement> incomeCount = meetIncomedDrpDwn.getOptions();
			
			for (int i = 1; i < memberCount.size(); i++) {
				for (int k = 1; k < incomeCount.size(); k++) {
	
					// *********************Output*******************//
					updatedURLList.add(urlList.get(j).replace(".html", "/gpf-financial-eligibility.html"));
					
					meetHouseholdDrpDwn = new Select(memberDrpDwn);
					memberCount = meetHouseholdDrpDwn.getOptions();
					meetHouseholdDrpDwn.selectByIndex(i);
					memberCountList.add(memberCount.get(i).getText());
	
					pageManager.waitForSeconds(1000);
					meetIncomedDrpDwn = new Select(incomeDrpDwn);
					incomeCount = meetIncomedDrpDwn.getOptions();
					meetIncomedDrpDwn.selectByIndex(k);
					incomeCountList.add(incomeCount.get(k).getText());
	
					js.executeScript("arguments[0].click()", confirmBtn);
					actualURLList.add(pageManager.getDriver().getCurrentUrl());
	
					pageManager.navigateWithCacheClear(url);
				}
			}
		}
	}
	
	public void createOutputExcelFile() throws IOException {
		// Create new workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet linkSheet;
		Row linkRow;
		
		// Create a new font and alter it.
		XSSFFont font = workbook.createFont();
		font.setBold(true);

		// Fonts are set into a style so create a new one to use.
		//CellStyle style = workbook.createCellStyle();
		//style.setFont(font);
		
		// Set column names for output file
		linkSheet = workbook.createSheet("House Hold Income");
		linkRow = linkSheet.createRow(0);
		linkRow.createCell(0).setCellValue("URL");
		linkRow.createCell(1).setCellValue("Household Members");
		linkRow.createCell(2).setCellValue("Income");
		linkRow.createCell(3).setCellValue("Actual URL");
		
		/*for (int i = 0; i < 10; i++) {
			linkRow.getCell(i).setCellStyle(style);
			}*/
		
		for(int i=0; i<updatedURLList.size();i++) {
			int rowNum = linkSheet.getLastRowNum();
			linkRow = linkSheet.createRow(rowNum+ 1);
			linkRow.createCell(0).setCellValue(updatedURLList.get(i));
			linkRow.createCell(1).setCellValue(memberCountList.get(i));
			linkRow.createCell(2).setCellValue(incomeCountList.get(i));
			linkRow.createCell(3).setCellValue(actualURLList.get(i));
		}
		// Write the workbook in file system
		FileOutputStream out = new FileOutputStream(
				System.getProperty("user.dir") + "/ExcelOutput/HouseHoldIncomeVerification/HouseholdIncomeVerification_" + date + ".xlsx");
		workbook.write(out);
		workbook.close();
		out.close();
		System.out.println("Excel Created!");
	}

}
