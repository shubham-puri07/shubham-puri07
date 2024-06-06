package com.genentech.testcases;

import java.io.IOException;

import org.testng.annotations.Test;

import com.genentech.util.TestCaseBase;

import com.genentech.baseclasses.AltTagsBasePage;
import com.genentech.baseclasses.BasePage;

public class AltTagsVerification extends TestCaseBase {
	
	@Test(priority=0)
	public void getInputExcelSheetData() throws IOException {
		BasePage bp = new BasePage(pageManager, excelReader);
		AltTagsBasePage atBase = new AltTagsBasePage(pageManager, excelReader);
		bp.loadDefaultExcelFile();
		atBase.readData();
	}
	
	@Test(priority=1)
	public void loginToEnvironment() {
		
		BasePage bp = new BasePage(pageManager, excelReader);
		
		//-----"live" OR "uat" OR "preview"-----//
		bp.login("preview");
	}
	
	@Test(priority=2)
	public void getAltTagsAndImages() throws IOException, InterruptedException {
		AltTagsBasePage atBase = new AltTagsBasePage(pageManager, excelReader);
		atBase.getAltTags();
	}
	
	@Test(priority=3) 
	public void addDataToOutputExcelFile() throws IOException {
		AltTagsBasePage atBase = new AltTagsBasePage(pageManager, excelReader);
		atBase.createOutputExcelFile();
	}

}
