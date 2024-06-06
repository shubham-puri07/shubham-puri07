package com.genentech.testcases;

import java.io.IOException;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.genentech.baseclasses.BasePage;
import com.genentech.baseclasses.FSToolv2BasePage;
import com.genentech.util.TestCaseBase;

public class FSToolv2Verification extends TestCaseBase {
	
	@Test(priority=0)
	public void getInputExcelSheetData() throws IOException {
		BasePage bp = new BasePage(pageManager, excelReader);
		FSToolv2BasePage fstv2Base = new FSToolv2BasePage(pageManager,excelReader);

		bp.loadDefaultExcelFile();
		fstv2Base.readData();
	}
	
	@Test(priority=1)
	public void loginToEnvironment() {
		
		BasePage bp = new BasePage(pageManager, excelReader);
		
		//-----"live" OR "uat" OR "preview"-----//
		bp.login("live");
	}
	
	@Test(priority=2)
	public void verifyJsonValues() throws JsonProcessingException, InterruptedException {
		FSToolv2BasePage fstv2Base = new FSToolv2BasePage(pageManager,excelReader);
		fstv2Base.verifyFSToolv2();
	}
	
	@Test(priority=3) 
	public void addDataToOutputExcelFile() throws IOException {
		FSToolv2BasePage fstv2Base = new FSToolv2BasePage(pageManager,excelReader);
		fstv2Base.createOutputExcelFile();
	}

}
