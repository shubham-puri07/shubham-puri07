package com.genentech.testcases;

import java.io.IOException;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.genentech.baseclasses.BasePage;
import com.genentech.baseclasses.HouseHoldIncomeBasePage;
import com.genentech.util.TestCaseBase;

public class HouseHoldIncomeVerification extends TestCaseBase {
	
	@Test(priority=0)
	public void getInputExcelSheetData() throws IOException {
		BasePage bp = new BasePage(pageManager, excelReader);
		HouseHoldIncomeBasePage hhiBase = new HouseHoldIncomeBasePage(pageManager,excelReader);

		bp.loadDefaultExcelFile();
		hhiBase.readData();
	}
	
	@Test(priority=1)
	public void loginToEnvironment() {
		
		BasePage bp = new BasePage(pageManager, excelReader);
		
		//-----"live" OR "uat" OR "preview"-----//
		bp.login("uat");
	}
	
	@Test(priority=2)
	public void verifyJsonValues() throws JsonProcessingException, InterruptedException {
		HouseHoldIncomeBasePage hhiBase = new HouseHoldIncomeBasePage(pageManager,excelReader);
		hhiBase.verifyHouseHoldIncome();
	}
	
	@Test(priority=3) 
	public void addDataToOutputExcelFile() throws IOException {
		HouseHoldIncomeBasePage hhiBase = new HouseHoldIncomeBasePage(pageManager,excelReader);
		hhiBase.createOutputExcelFile();
	}

}
