package com.genentech.testcases;


import java.io.IOException;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.genentech.baseclasses.BasePage;
import com.genentech.baseclasses.FSToolv3BasePage;
import com.genentech.util.TestCaseBase;

public class FSToolv3Verification extends TestCaseBase {

	@Test(priority=0)
	public void getInputExcelSheetData() throws IOException {
		BasePage bp = new BasePage(pageManager, excelReader);
		FSToolv3BasePage fstv3Base = new FSToolv3BasePage(pageManager,excelReader);

		bp.loadDefaultExcelFile();
		fstv3Base.readData();
	}
	
	@Test(priority=1)
	public void loginToEnvironment() {
		
		BasePage bp = new BasePage(pageManager, excelReader);
		
		//-----"live" OR "uat" OR "preview"-----//
		bp.login("preview");
	}
	
	@Test(priority=2)
	public void verifyJsonValues() throws JsonProcessingException, InterruptedException {
		FSToolv3BasePage fstv3Base = new FSToolv3BasePage(pageManager,excelReader);
		fstv3Base.verifyFSToolv3();
	}
	
	@Test(priority=3) 
	public void addDataToOutputExcelFile() throws IOException {
		FSToolv3BasePage fstv3Base = new FSToolv3BasePage(pageManager,excelReader);
		fstv3Base.createOutputExcelFile();
	}

}
