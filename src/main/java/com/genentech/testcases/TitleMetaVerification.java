package com.genentech.testcases;

import java.io.IOException;

import org.testng.annotations.Test;

import com.genentech.util.TestCaseBase;

import com.genentech.baseclasses.BasePage;
import com.genentech.baseclasses.TitleMetaBasePage;

public class TitleMetaVerification extends TestCaseBase {
	
	@Test(priority=0)
	public void getInputExcelSheetData() throws IOException {
		BasePage bp = new BasePage(pageManager, excelReader);
		TitleMetaBasePage tmBase = new TitleMetaBasePage(pageManager,excelReader);

		bp.loadDefaultExcelFile();
		tmBase.readData();
	}
	
	@Test(priority=1)
	public void loginToEnvironment() {
		
		BasePage bp = new BasePage(pageManager, excelReader);
		
		//-----"live" OR "uat" OR "preview"-----//
		bp.login("uat");
	}
	
	@Test(priority=2)
	public void verifyTitleMeta() throws InterruptedException {
		TitleMetaBasePage tmBase = new TitleMetaBasePage(pageManager,excelReader);
		tmBase.getTitleMetaInformation();
	}
	
	@Test(priority=3) 
	public void addDataToOutputExcelFile() throws IOException {
		TitleMetaBasePage tmBase = new TitleMetaBasePage(pageManager,excelReader);
		tmBase.createOutputExcelFile();
	}
}
