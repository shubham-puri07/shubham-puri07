package com.genentech.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverListener; //This is the interface we can use in selenium 4

import java.lang.reflect.Method;
import java.time.Duration;

public class LogWebdriverEventListener implements  WebDriverListener{
	private Logger log = LogManager.getLogger(this.getClass());
	private By lastFindBy;
	private String originalValue;

	public void beforeNavigateTo(String url, WebDriver selenium) {
		log.info("WebDriver navigating to:'" + url + "'");
	}

	public void beforeChangeValueOf(WebElement element, WebDriver selenium) {
		originalValue = element.getAttribute("value");
	}

	public void afterChangeValueOf(WebElement element, WebDriver selenium) {
		log.info("WebDriver changing value in element found " + lastFindBy + " from '" + originalValue + "' to '"
				+ element.getAttribute("value") + "'");
	}

	public void beforeFindBy(By by, WebElement element, WebDriver selenium) {
		lastFindBy = by;
	}

	public void onException(Throwable error, WebDriver selenium) {
		if (error.getClass().equals(NoSuchElementException.class)) {
			log.error("WebDriver error: Element not found " + lastFindBy);
		} else {
			log.error("WebDriver error:", error);
		}
	}

	public void beforeNavigateBack(WebDriver selenium) {
	}

	public void beforeNavigateForward(WebDriver selenium) {
	}

	public void beforeClickOn(WebElement element, WebDriver selenium) {
		log.info("WebDriver clicking on" + element.toString());
	}

	public void beforeScript(String script, WebDriver selenium) {
	}

	public void afterClickOn(WebElement element, WebDriver selenium) {
		log.info("WebDriver clicked on" + element.toString());
		selenium.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
	}

	public void afterFindBy(By by, WebElement element, WebDriver selenium) {
		log.info("WebDriver found " + by);
	}

	public void afterNavigateBack(WebDriver selenium) {
	}

	public void afterNavigateForward(WebDriver selenium) {
	}

	public void afterNavigateTo(String url, WebDriver selenium) {
		selenium.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
	}

	public void afterScript(String script, WebDriver selenium) {
	}

	public void afterAlertAccept(WebDriver arg0) {
		// TODO Auto-generated method stub
		
	}

	public void afterAlertDismiss(WebDriver arg0) {
		// TODO Auto-generated method stub
		
	}

	public void afterChangeValueOf(WebElement arg0, WebDriver arg1,	CharSequence[] arg2) {
		// TODO Auto-generated method stub
		
	}

	public void afterNavigateRefresh(WebDriver arg0) {
		// TODO Auto-generated method stub
		
	}

	public void afterSwitchToWindow(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

	public void beforeAlertAccept(WebDriver arg0) {
		// TODO Auto-generated method stub
		
	}

	public void beforeAlertDismiss(WebDriver arg0) {
		// TODO Auto-generated method stub
		
	}

	public void beforeChangeValueOf(WebElement arg0, WebDriver arg1,
			CharSequence[] arg2) {
		// TODO Auto-generated method stub
		
	}

	public void beforeNavigateRefresh(WebDriver arg0) {
		// TODO Auto-generated method stub
		
	}

	public void beforeSwitchToWindow(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

	public <X> void beforeGetScreenshotAs(OutputType<X> target) {
		// TODO Auto-generated method stub
		
	}

	public <X> void afterGetScreenshotAs(OutputType<X> target, X screenshot) {
		// TODO Auto-generated method stub
		
	}

	public void beforeGetText(WebElement element, WebDriver driver) {
		// TODO Auto-generated method stub
		
	}

	public void afterGetText(WebElement element, WebDriver driver, String text) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void beforeAnyWebDriverCall(WebDriver driver, Method method, Object[] args) {
		
    }

    @Override
    public void afterGet(WebDriver driver, String url) {
       
    }
	 
}