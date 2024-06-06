package com.genentech.testcases;

import java.io.IOException;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.genentech.util.TestCaseBase;

import com.genentech.baseclasses.BasePage;
import com.genentech.baseclasses.JsonValueBasePage;

public class JsonValueVerification extends TestCaseBase {

	@Test(priority=0)
	public void getInputExcelSheetData() throws IOException {
		BasePage bp = new BasePage(pageManager, excelReader);
		JsonValueBasePage jvBase = new JsonValueBasePage(pageManager,excelReader);

		bp.loadDefaultExcelFile();
		jvBase.readData();
	}
	
	@Test(priority=1)
	public void loginToEnvironment() {
		
		BasePage bp = new BasePage(pageManager, excelReader);
		
		//-----"live" OR "uat" OR "preview"-----//
		bp.login("uat");
	}
	
	@Test(priority=2)
	public void verifyJsonValues() throws JsonProcessingException, InterruptedException {
		JsonValueBasePage jvBase = new JsonValueBasePage(pageManager,excelReader);
		jvBase.getJsonValues();
	}
	
	@Test(priority=3) 
	public void addDataToOutputExcelFile() throws IOException {
		JsonValueBasePage jvBase = new JsonValueBasePage(pageManager,excelReader);
		jvBase.createOutputExcelFile();
	}
}
