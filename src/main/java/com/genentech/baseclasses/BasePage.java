package com.genentech.baseclasses;

import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.genentech.util.ExcelReader;
import com.genentech.util.PageManager;
import com.genentech.util.PageObject;

public class BasePage extends PageObject {
	
	Object[][] testData = null;
	//New Login 
	@FindBy(id = "identifierInput")
	public WebElement signonUsername;
	
	@FindBy(id = "submitBtn")
	public WebElement submitBtn;
	
	//Old Login
	@FindBy(xpath = ".//input[@id='username']")
	public WebElement username;
	
	@FindBy(xpath = ".//input[@id='password']")
	public WebElement password;

	@FindBy(xpath = ".//input[@value='Login']")
	public WebElement submit;
	
	@FindBy(css="div#cta_buttonClose")
	public List<WebElement> chatbotLink;
	
	@FindBy(xpath = "//*[contains(@class, 'cmp-modal__footer')]//a[contains(text(),'OK')]")
	public List<WebElement> hcpPopup;
	
	@FindBy(css = "#onetrust-accept-btn-handler")
    public List<WebElement> ccpaBtn;
	
	public BasePage(PageManager pm, ExcelReader xl) {
		super(pm, xl);
	}
	
	/**
	 * This method to login.
	 */
	public void login(String env) 
	{
		if (env.contains("uat")) {
			pageManager.navigate("https://uat-rituxan-core.gene.com/");
			pageManager.sendKeys(signonUsername, "khergadv");
			pageManager.click(submitBtn);
			
			pageManager.sendKeys(password, "VishTester!1706");
			pageManager.click(submit);
			pageManager.waitForSeconds(15000);
			pageManager.waitForPageLoad();
			pageManager.refresh();
			pageManager.waitForSeconds(5000);
			pageManager.waitForPageLoaded();
			pageManager.waitForPageLoad();
		} else if (env.contains("preview")) {
			pageManager.navigate("https://preview-cotellic.gene.com/");
			//New Login
			pageManager.sendKeys(signonUsername, "khergadv");
			pageManager.click(submitBtn);
			
			pageManager.sendKeys(password, "VishTester!1706");
			pageManager.click(submit);
			pageManager.waitForSeconds(15000);
			pageManager.waitForPageLoad();
			pageManager.refresh();
			pageManager.waitForSeconds(5000);
			pageManager.waitForPageLoaded();
			pageManager.waitForPageLoad();
		} 
	}
	
	public void loadDefaultExcelFile() {
		excelReader.setDefaultExcelSheet();
	}
	
	public void navigateToURL(String url) throws InterruptedException {
		pageManager.navigateWithCacheClear(url);
		pageManager.waitForSeconds(5000);
		pageManager.waitForPageLoaded();
		pageManager.waitForPageLoad();
		
		System.out.println("Current Page URL: "+pageManager.getDriver().getCurrentUrl());
		
		JavascriptExecutor js = (JavascriptExecutor) pageManager.getDriver();
		if(ccpaBtn.size()>0) {
			pageManager.switchToNewWindow();
			js.executeScript("arguments[0].click()", ccpaBtn.get(0));
			pageManager.switchToMainWindow();
			System.out.println("CCPA Cookie 'Accept All' button clicked.");
		} 
		
		// Handling the HCP pop-up
		if(hcpPopup.size()>0 && url.contains("hcp")) {
			pageManager.switchToNewWindow();
			js.executeScript("arguments[0].click()", hcpPopup.get(0));
			pageManager.switchToWindow(0);
			System.out.println("HCP Site - HCP accepted");
		} else if(hcpPopup.size()==0 && url.contains("hcp")) {
			System.out.println("ERROR: HCP Site - HCP Modal not available."); 
		} else if(hcpPopup.size()==0 && !url.contains("hcp")){
			System.out.println("Patient Site - HCP Modal Not Available.");
		}
		
		
		if(chatbotLink.size()>0) {
			pageManager.waitForSeconds(5000);
			pageManager.waitAndClick(chatbotLink.get(0), 10);
			pageManager.clickByJavaScriptExecutor(chatbotLink.get(0));
			pageManager.waitForSeconds(2000);
			System.out.println("ChatBot Closed");
		}
	}
	
}
