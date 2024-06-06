package com.genentech.testcases;

import java.io.IOException;

import org.testng.annotations.Test;

import com.genentech.util.TestCaseBase;

import com.genentech.baseclasses.BasePage;
import com.genentech.baseclasses.RedirectsBasePage;

public class RedirectsVerification extends TestCaseBase {
	
	@Test(priority=0)
	public void getInputExcelSheetData() throws IOException {
		BasePage bp = new BasePage(pageManager, excelReader);
		RedirectsBasePage rBase = new RedirectsBasePage(pageManager, excelReader);
		bp.loadDefaultExcelFile();
		rBase.readData();
	}
	
	@Test(priority=1)
	public void loginToEnvironment() {
		
		BasePage bp = new BasePage(pageManager, excelReader);
		
		//-----"live" OR "uat" OR "preview"-----//
		bp.login("live");
	}
	
	@Test(priority=2)
	public void verifyRedirects() throws IOException, InterruptedException {
		RedirectsBasePage rBase = new RedirectsBasePage(pageManager, excelReader);
		rBase.verifyRedirects();
	}
	
	@Test(priority=3) 
	public void addDataToOutputExcelFile() throws IOException {
		RedirectsBasePage rBase = new RedirectsBasePage(pageManager, excelReader);
		rBase.createOutputExcelFile();
	}

}
