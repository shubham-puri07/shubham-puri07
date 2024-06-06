package com.genentech.testcases;

import java.io.IOException;

import org.testng.annotations.Test;

import com.genentech.baseclasses.BasePage;
import com.genentech.baseclasses.WebScraperBasePage;
import com.genentech.util.TestCaseBase;

public class WebScraperVerification extends TestCaseBase {
	
	@Test(priority=0)
	public void getInputExcelSheetData() throws IOException {
		BasePage bp = new BasePage(pageManager, excelReader);
		WebScraperBasePage wsp = new WebScraperBasePage(pageManager, excelReader);

		bp.loadDefaultExcelFile();
		wsp.readData();
	}
	
	@Test(priority=1)
	public void loginToEnvironment() {
		
		BasePage bp = new BasePage(pageManager, excelReader);
		
		//-----"live" OR "uat" OR "preview"-----//
		bp.login("uat");
	}
	
	@Test(priority=2)
	public void verifyWebscraper() throws IOException, Exception {
		WebScraperBasePage wsp = new WebScraperBasePage(pageManager, excelReader);
		wsp.brokenLinksVerification();
	}
	
	@Test(priority=3) 
	public void addDataToOutputExcelFile() throws IOException {
		WebScraperBasePage wsp = new WebScraperBasePage(pageManager, excelReader);
		wsp.createOutputExcelFile();
	}

}
