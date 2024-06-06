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

public class FSToolv2BasePage extends PageObject {

	public FSToolv2BasePage(PageManager pm, ExcelReader xl) {
		super(pm, xl);
		// TODO Auto-generated constructor stub
	}
	
	private static List<String> urlList = new ArrayList<>();
	private static List<String> expectedIndicationsList = new ArrayList<>();
	private static List<String> expectedPrimaryInsuranceList = new ArrayList<>();
	private static List<String> expectedFinancialAssistanceConfirmationList = new ArrayList<>();
	private static List<String> expectedTypeOfFinancialAssistanceList = new ArrayList<>();
	private static List<String> expectedInsuranceCoverageList = new ArrayList<>();
	private static List<String> expectedIncomeLimitList = new ArrayList<>();
	private static List<String> expectedResultPageLinkList = new ArrayList<>();
	private static List<String> actualResultPageLinkList = new ArrayList<>();
	private static List<String> compareResultPageLinkList = new ArrayList<>();
	String houseHold_actual_Url = "";
	
	BasePage basePage = new BasePage(pageManager, excelReader);	
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
	String date = df.format(new Date());
	
	@FindBy(xpath = ".//select[@name='prescribed-for']")
	public WebElement dropDown;
	
	@FindBy(xpath = ".//input[@name='type-of-insurance'][@value='private-commercial']")
	public WebElement privateRadioBtn;
	
	@FindBy(xpath = ".//input[@name='type-of-insurance'][@value='government']")
	public WebElement pubRadioBtn;
	
	@FindBy(xpath = ".//input[@value='none']")
	public WebElement noneRadioBtn;
	
	@FindBy(xpath = ".//input[@name='other-assistance'][@value='yes']")
	public WebElement yesOARadioBtn;
	
	@FindBy(xpath = ".//input[@name='other-assistance'][@value= 'no']")
	public WebElement noOARadioBtn;
	
	@FindBy(xpath = ".//input[@value='co-pay']")
	public WebElement coPayRadioBtn;
	
	@FindBy(xpath = ".//input[@value='patient-foundation']")
	public WebElement patientFoundationRadioBtn;
	
	@FindBy(xpath = ".//input[@value='independent-other']")
	public WebElement independentRadioBtn;
	
	@FindBy(xpath = ".//input[@name='covered-by-insurance'][@value='yes']")
	public WebElement yesCBIRadioBtn;
	
	@FindBy(xpath = ".//input[@name='covered-by-insurance'][@value='no']")
	public WebElement noCBIRadioBtn;
	
	@FindBy(xpath = ".//input[@name='covered-by-insurance'][@value='unsure']")
	public WebElement unsureCBIRadioBtn;
	
	@FindBy(xpath = "//button[contains(., 'Next')]")
	public WebElement submitBtn;
	
	//*********************Household Income tool***********************//
	@FindBy(xpath = ".//select[@name='number-of-household-members']")
	public WebElement memberDrpDwn;
	
	@FindBy(xpath = ".//select[@name='net-household-income']")
	public WebElement incomeDrpDwn;
	
	@FindBy(xpath = "//button[contains(., 'Confirm')]")
	public WebElement confirmBtn;
	
	
	//*********************FS Tool version 3 extra webElements********************************//
	@FindBy(xpath = ".//input[@name='covered-by-insurance'][@value='dont-have']")
	public WebElement donthaveCBIRadioBtn;
	
	public void readData() {
		String data;
		for(int i=1;i<=excelReader.getRowCount("Sheet1");i++)
		{
			 data = excelReader.getCellData("Sheet1", "URL", i);
			 urlList.add(data);
			 data = excelReader.getCellData("Sheet1", "For which of the following indications?", i);
			 expectedIndicationsList.add(data);
			 data = excelReader.getCellData("Sheet1", "What type of primary insurance?", i);
			 expectedPrimaryInsuranceList.add(data);
			 data = excelReader.getCellData("Sheet1", "Receiving any other form of financial assistance?", i);
			 expectedFinancialAssistanceConfirmationList.add(data);
			 data = excelReader.getCellData("Sheet1", "What type of assistance?", i);
			 expectedTypeOfFinancialAssistanceList.add(data);
			 data = excelReader.getCellData("Sheet1", "Does insurance cover Brand X?", i);
			 expectedInsuranceCoverageList.add(data);
			 data = excelReader.getCellData("Sheet1", "Meets Household Size/Income Limit", i);
			 expectedIncomeLimitList.add(data);
			 data = excelReader.getCellData("Sheet1", "Results Page Link", i);
			 expectedResultPageLinkList.add(data);
		}
		//Remove Column Name items from list
		urlList.remove(0);
		expectedIndicationsList.remove(0);
		expectedPrimaryInsuranceList.remove(0);
		expectedFinancialAssistanceConfirmationList.remove(0);
		expectedTypeOfFinancialAssistanceList.remove(0);
		expectedInsuranceCoverageList.remove(0);
		expectedIncomeLimitList.remove(0);
		expectedResultPageLinkList.remove(0);
	}
	
	public void verifyFSToolv2() throws InterruptedException {
		for (int j = 0; j < urlList.size(); j++) {
			JavascriptExecutor js = (JavascriptExecutor) pageManager.getDriver();
			System.out.println("Scenario " + (j+1)+": ");
			
			basePage.navigateToURL(urlList.get(j));
			
			// ******************************************** Question No.1
			// *****************************************//
			Select indicationDrpDwn;
			indicationDrpDwn = new Select(dropDown);
			if (expectedIndicationsList.get(j).equals("Approved FDA Indication")) {
				indicationDrpDwn.selectByIndex(1);
			} else if (expectedIndicationsList.get(j).equals("None of the above")) {
				int selectOptions = indicationDrpDwn.getOptions().size();
				indicationDrpDwn.selectByIndex(selectOptions - 1);
			}
			pageManager.waitForSeconds(3000);

			// ******************************************** Question No.2
			// ******************************************//
			if (expectedPrimaryInsuranceList.get(j).equals("Private / Commercial")) {
				js.executeScript("arguments[0].click()", privateRadioBtn);
			} else if (expectedPrimaryInsuranceList.get(j).equals("Public (Government)")) {
				js.executeScript("arguments[0].click()", pubRadioBtn);
			} else {
				js.executeScript("arguments[0].click()", noneRadioBtn);
			}
			pageManager.waitForSeconds(1000);

			// ******************************************** Question No.3
			// ******************************************//
			if (expectedFinancialAssistanceConfirmationList.get(j).equals("Y")) {
				js.executeScript("arguments[0].click()", yesOARadioBtn);
			} else {
				js.executeScript("arguments[0].click()", noOARadioBtn);
			}
			pageManager.waitForSeconds(1000);

			// ****************************************** Question No.4
			// ******************************************//
			if (expectedTypeOfFinancialAssistanceList.get(j).equals("Co-pay Assistance Program")) {
				js.executeScript("arguments[0].click()", coPayRadioBtn);
			} else if (expectedTypeOfFinancialAssistanceList.get(j).equals("Genentech Patient Foundation")) {
				js.executeScript("arguments[0].click()", patientFoundationRadioBtn);

			} else if (expectedTypeOfFinancialAssistanceList.get(j).equals("Assistance from any other charitable organization")) {
				js.executeScript("arguments[0].click()", independentRadioBtn);
			} else if (expectedTypeOfFinancialAssistanceList.get(j).equals("N/A")) {
				
			}
			pageManager.waitForSeconds(1000);
			// ****************************************** Question No.5
			// ******************************************//
			if (expectedInsuranceCoverageList.get(j).equals("Y")) {
				js.executeScript("arguments[0].click()", yesCBIRadioBtn);
			} else if (expectedInsuranceCoverageList.get(j).equals("N")) {
				js.executeScript("arguments[0].click()", noCBIRadioBtn);
			} else if (expectedInsuranceCoverageList.get(j).equals("Not sure")) {
				js.executeScript("arguments[0].click()", unsureCBIRadioBtn);
			} else {
				
			}
			pageManager.waitForSeconds(1000);
			
			// ****************************************** Submit
			// ******************************************//
			js.executeScript("arguments[0].scrollIntoView();", submitBtn);
			submitBtn.click();
			String actual_Url = pageManager.getDriver().getCurrentUrl();
			System.out.println(actual_Url);
			pageManager.waitForSeconds(5000);
			
			// ****************************************** Household Income tool
			// ******************************************//
			if (actual_Url.contains("gpf-financial-eligibility.html")) {

				Select meetHouseholdDrpDwn = new Select(memberDrpDwn);
				Select meetIncomedDrpDwn = new Select(incomeDrpDwn);
				if (expectedIncomeLimitList.get(j).equals("Y")) {
					meetHouseholdDrpDwn.selectByIndex(1);
					meetIncomedDrpDwn.selectByIndex(1);
				} else if (expectedIncomeLimitList.get(j).equals("N")) {
					meetHouseholdDrpDwn.selectByIndex(1);
					meetIncomedDrpDwn.selectByIndex(5);
				}

				js.executeScript("arguments[0].click()", confirmBtn);
				pageManager.waitForSeconds(3000);
				houseHold_actual_Url = pageManager.getDriver().getCurrentUrl();
				System.out.println(houseHold_actual_Url);
				actualResultPageLinkList.add(houseHold_actual_Url);
			} else {
				actualResultPageLinkList.add(actual_Url);
			}
			
			if (actualResultPageLinkList.get(j).contains(expectedResultPageLinkList.get(j))) {
				compareResultPageLinkList.add("Pass");
			} else {
				compareResultPageLinkList.add("Fail");
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
		CellStyle style = workbook.createCellStyle();
		style.setFont(font);
		
		// Set column names for output file
		linkSheet = workbook.createSheet("FS Tool Output");
		linkRow = linkSheet.createRow(0);
		linkRow.createCell(0).setCellValue("URL");
		linkRow.createCell(1).setCellValue("For which of the following indications?");
		linkRow.createCell(2).setCellValue("What type of primary insurance?");
		linkRow.createCell(3).setCellValue("Receiving any other form of financial assistance?");
		linkRow.createCell(4).setCellValue("What type of assistance?");
		linkRow.createCell(5).setCellValue("Does insurance cover Brand X?");
		linkRow.createCell(6).setCellValue("Meets Household Size/Income Limit");
		linkRow.createCell(7).setCellValue("Expected Landing URL");
		linkRow.createCell(8).setCellValue("Actual Landing URL");
		linkRow.createCell(9).setCellValue("Status");
		
		for (int i = 0; i < 10; i++) {
			linkRow.getCell(i).setCellStyle(style);
			}
		
		for(int i=0; i<urlList.size();i++) {
			int rowNum = linkSheet.getLastRowNum();
			linkRow = linkSheet.createRow(rowNum+ 1);
			linkRow.createCell(0).setCellValue(urlList.get(i));
			linkRow.createCell(1).setCellValue(expectedIndicationsList.get(i));
			linkRow.createCell(2).setCellValue(expectedPrimaryInsuranceList.get(i));
			linkRow.createCell(3).setCellValue(expectedFinancialAssistanceConfirmationList.get(i));
			linkRow.createCell(4).setCellValue(expectedTypeOfFinancialAssistanceList.get(i));
			linkRow.createCell(5).setCellValue(expectedInsuranceCoverageList.get(i));
			linkRow.createCell(6).setCellValue(expectedIncomeLimitList.get(i));
			linkRow.createCell(7).setCellValue(expectedResultPageLinkList.get(i));
			linkRow.createCell(8).setCellValue(actualResultPageLinkList.get(i));
			linkRow.createCell(9).setCellValue(compareResultPageLinkList.get(i));
		}
		// Write the workbook in file system
		FileOutputStream out = new FileOutputStream(
				System.getProperty("user.dir") + "/ExcelOutput/FSToolVerification/FSToolv2/FSToolTest_" + date + ".xlsx");
		workbook.write(out);
		workbook.close();
		out.close();
		System.out.println("Excel Created!");
	}
}

