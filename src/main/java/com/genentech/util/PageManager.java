package com.genentech.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

/**
 * Updated PageManager Class with some additional Methods
 *
 * */
public class PageManager {
	protected WebDriver driver;
	protected String browserFlag;
	protected ExtentTest test;
	protected Logger log = LogManager.getLogger(this.getClass());
	protected String env;

	public PageManager(WebDriver driverRemote, String browser, String environment, ExtentTest extentTest) {
		driver = driverRemote;
		browserFlag = browser;
		env = environment;
		test = extentTest;
		PageFactory.initElements(driver, this);
	}

	/**
	 * This Method Switches the focus to default window.
	 */
	public void switchToDefaultContent() {
		try {
			driver.switchTo().defaultContent();
			test.log(Status.INFO, "Switched to default Window.");
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
	}

	/**
	 * This methods haults the application for the given time period.
	 * 
	 * @throws InterruptedException
	 * 
	 */
	public void sleep(long milliSeconds) throws InterruptedException {
		Thread.sleep(milliSeconds);
	}

	/**
	 * Dismiss the popup alert
	 */
	public void alertDismiss() {
		try {
			Alert alert = driver.switchTo().alert();
			String AlertMessage = alert.getText();
			log.info("Alert message is: " + AlertMessage);
			alert.dismiss();
			test.log(Status.PASS, "Alert Dismiss");
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
	}

	/**
	 * This method selects option from drop down by using index of data.
	 * 
	 * @param element
	 * @param index
	 */
	public void dropDownHandlingByIndex(WebElement element, int index) {
		try {
			Select drop = new Select(element);
			drop.selectByIndex(index);
			test.log(Status.PASS, "Input in DropDown is Selected.");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * Click on the element using JavaScriptExecutor
	 * 
	 * @param element
	 */
	public void clickByJavaScriptExecutor(WebElement element) {
		try {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
			test.log(Status.PASS, "Click on: " + element);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * This Method scrolls down the webpage to a particular element.
	 * 
	 * @param element
	 */
	public void ScrollWebPage(WebElement element) {
		try {
			/*
			 * Change to accomodate NTZ (DCG)
			 */
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
			// ((JavascriptExecutor)getDriver()).executeScript("window.scrollTo(0," +
			// (element.getLocation().y - 100) + ")");
			test.log(Status.INFO, "Webpage is scrolled to element: " + element);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * This Method Switches the focus to Window number passed as parameter.
	 * 
	 * @param i
	 */
	public void switchpreviousWindow(int i) {
		try {
			ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(tabs.get(i));
			test.log(Status.INFO, "Focused is switched to window number: " + i);
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
	}

	/**
	 * This Method closes the focus Tab number passed as parameter.
	 * 
	 * @param i
	 */
	public void closeCurrentTab(int i) {
		try {
			ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(tabs.get(i));
			driver.close();
			test.log(Status.INFO, "Focused Tab is Closed ");
		} catch (Exception exception) {
			test.log(Status.INFO, "Focused Tab is Not Closed ");
			exceptionPrintError(exception, null);
		}
	}

	/**
	 * This Method selects option form drop down with respect to the Visible text of
	 * drop down.
	 * 
	 * @param element
	 * @param value
	 */
	public void dropDownHandlingByText(WebElement element, String value) {
		try {
			Select drop = new Select(element);
			drop.selectByVisibleText(value);
			test.log(Status.PASS, "Input in drop down is selected.");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * This Method selects option form drop down with respect to the value of drop
	 * down.
	 * 
	 * @param element
	 * @param value
	 */
	public void dropDownHandlingByValue(WebElement element, String value) {
		try {
			Select drop = new Select(element);
			drop.selectByValue(value);
			test.log(Status.PASS, "Input in drop down is selected.");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * Will click on the element with the index i in the list of the element
	 * 
	 * @param element
	 * @param elements
	 * @param i
	 */
	public void clickByActionSubMenu(WebElement element, List<WebElement> elements, int i) {
		try {
			until(element, 100);
			Actions actions = new Actions(driver);
			actions.moveToElement(element);
			actions.moveToElement(elements.get(i)).click().build().perform();
			test.log(Status.PASS, "Click on the Element in list");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
			exceptionPrintError(exception, elements.get(i));
			elementPrintError(exception, elements);
		}
	}

	/**
	 * This Method returns the object of WebDriver Class.
	 * 
	 * @return
	 */
	public WebDriver getDriver() {
		test.log(Status.INFO, "GET WebDriver instance");
		return this.driver;
	}

	/**
	 * This Method returns the Browser Flag.
	 * 
	 * @return
	 */
	public String getBrowserFlag() {
		String str = null;
		try {
			test.log(Status.INFO, "Browser Flag is returned");
			str = browserFlag;
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
		return str;
	}

	/**
	 * This Method navigates to a Webpage pointed by given URL.
	 * 
	 * @param url
	 */
	public void navigate(String url) {
		try {
			driver.navigate().to(url);
			//waitForPageLoaded();
			test.log(Status.INFO, "Page navigates to " + url);
			addBrowserCookies();
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
	}
	
	public void get(String url) {
		try {
			driver.get(url);
			waitForPageLoaded();
			test.log(Status.INFO, "Page navigates to " + url);
			addBrowserCookies();
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
	}

	/**
	 * This method is used to add multiple browser cookies provide by the user as a
	 * parameter "-DbrowserCookies" Example:- "-DbrowserCookies =
	 * cookieName1:value1,cookiename2:value2"
	 */
	public void addBrowserCookies() {
		String inputCookies = System.getProperty("browserCookies");
		if (inputCookies != null) {
			String[] cookies = inputCookies.split(",");
			for (String cookie : cookies) {
				String[] cookiedetails = cookie.split(":");
				driver.manage().addCookie(new Cookie(cookiedetails[0], cookiedetails[1]));
			}
		} else {
			log.debug("Cookies not provided by the user");
		}
		driver.navigate().refresh();
		waitForPageLoaded();
	}

	/**
	 * This Method returns title of a WebPage.
	 * 
	 * @return
	 */
	public String getTitle() {
		String title = null;
		try {
			title = driver.getTitle();
			test.log(Status.INFO, "Page Title is " + title);
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
		return title;
	}

	/**
	 * This Method switches focus of the Web Driver to an iframe with help of iframe
	 * name.
	 * 
	 * @param frame
	 */
	public void switchToFrameByName(String frame) {
		try {
			driver.switchTo().frame(frame);
			test.log(Status.INFO, "Switch to Frame " + frame.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
	}

	/**
	 * This Method switches focus of the Web Driver to an iframe.
	 * 
	 * @param frame
	 */
	public void switchToFrame(WebElement frame) {
		try {
			driver.switchTo().frame(frame);
			test.log(Status.INFO, "Switch to Frame " + frame.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
	}

	/**
	 * This Method Switches the focus of Web Driver to new Window.
	 * 
	 * @throws InterruptedException
	 */
	public void switchToNewWindow() throws InterruptedException {
		try {
			for (String winHandle : driver.getWindowHandles()) {
				driver.switchTo().window(winHandle);
			}
			Thread.sleep(4000);
			test.log(Status.INFO, "Switch to a new window.");
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
	}

	/**
	 * This Method Clicks on a Web element.
	 * 
	 * @param element
	 */
	public void click(WebElement element) {
		try {
			element.click();
			test.log(Status.PASS, "Click " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * This Method Enters value in a text box Field.
	 * 
	 * @param element
	 * @param keys
	 */
	public void sendKeys(WebElement element, String keys) {
		try {
			element.clear();
			element.sendKeys(keys);

			test.log(Status.PASS, "Send " + keys + " to " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * This Method waits for the element to be clickable and then enters the text in
	 * it.
	 * 
	 * @param element
	 * @param keys
	 * @param numberOfSeconds
	 */
	public void waitAndSendkeys(WebElement element, String keys, Duration numberOfSeconds) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, numberOfSeconds);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			element.sendKeys(keys);
			test.log(Status.INFO, "Send " + keys + " to " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * This Method returns the link text and then clicks on that element.
	 * 
	 * @param element
	 * @param name
	 * @return
	 */
	public String verifyElementTextAndClick(WebElement element, String name) {
		String text = null;
		try {
			text = element.getText();
			log.info("Element text is " + text.toString());
			if (text.equalsIgnoreCase(name)) {
				element.click();
			}
			test.log(Status.PASS, "Get Text " + text + " of " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
		return text;
	}

	/**
	 * This Method returns the link text of an element.
	 * 
	 * @param element
	 * @return
	 */
	public String getText(WebElement element) {
		String text = null;
		try {
			text = element.getText();
			test.log(Status.INFO, "Get Text " + text + " of " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
		return text;
	}

	/**
	 * This Method returns the data present in attribute 'Value' of an element.
	 * 
	 * @param element
	 * @return
	 */
	public String getTextValue(WebElement element) {
		String textValue = null;
		try {
			textValue = element.getAttribute("Value");
			test.log(Status.INFO, "Get Text " + textValue + " of " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
		return textValue;
	}

	/**
	 * This Method handles Alert pop up and selects accept option.
	 * 
	 * @param element
	 */
	public void alertHandleOnWebelement(WebElement element) {
		try {
			waitAndClick(element, 100);
			Alert alert = driver.switchTo().alert();
			alert.accept();
			test.log(Status.PASS, "Alert Handled: " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	/**
	 * This Method Maximizes the size of browser window.
	 */
	public void maximizeBrowser() {
		try {
			driver.manage().window().maximize();
			test.log(Status.INFO, "Browser Window Maximized.");
		} catch (Exception exception) {
			exceptionPrintError(exception, null);

		}
	}

	

	/**
	 * This Methods Drag and Drops that element from source element to destination
	 * element.
	 * 
	 * @param sourceElement
	 * @param destinationElement
	 */
	public void dragAndDrop(WebElement sourceElement, WebElement destinationElement) {
		try {
			Actions action = new Actions(driver);
			action.dragAndDrop(sourceElement, destinationElement).build().perform();
			test.log(Status.PASS, "Drag: " + sourceElement.toString() + " to: " + destinationElement.toString());
		} catch (StaleElementReferenceException exception) {

		} catch (Exception exception) {
			exceptionPrintError(exception, sourceElement);
			exceptionPrintError(exception, destinationElement);
		}
	}

	/**
	 * This method enters data in element from backend.
	 * 
	 * @param element
	 * @param data
	 */
	public void insertDataFromBackend(WebElement element, String data) {
		try {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].setAttribute('value', '" + data + "')", element);
			test.log(Status.INFO, "Send Data to input field: " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	/**
	 * This Method selects option from right click menu by its index.
	 * 
	 * @param element
	 * @param i
	 */
	public void rightClickAndSelectOption(WebElement element, int i) {
		try {
			Actions builder = new Actions(driver);
			while (i > 0) {
				builder.contextClick(element).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).perform();
				i--;
			}
			test.log(Status.PASS, "Select " + i + " option from right click menu.");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	/**
	 * This Method scrolls to an element and highlights it.
	 * 
	 * @param element
	 */
	public void scrollAndHighlightElement(WebElement element) {

		try {
			Coordinates coordinate = ((Locatable) element).getCoordinates();
			coordinate.onPage();
			coordinate.inViewPort();

			for (int i = 0; i < 4; i++) {
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element,
						"color: yellow; border: 4px solid blue,;");
				js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element);
			}
			test.log(Status.INFO, "Scroll and Highlight: " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	/**
	 * handle element which is not available in one time , (solve the stale element
	 * reference exception problem) it will search 4 times same element still it is
	 * available
	 * 
	 * @param element
	 * @return
	 */
	public boolean retryingForSameElement(WebElement element) {
		boolean result = false;
		try {
			int attempts = 0;
			while (attempts < 4) {
				try {
					element.click();
					result = true;
					break;
				} catch (StaleElementReferenceException exception) {
					exception.printStackTrace();
				}
				attempts++;
			}
			test.log(Status.PASS, "Retrying for Same Element: " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
		return result;
	}

	/**
	 * Wait until the timeout given
	 * 
	 * @param element
	 * @param timeout
	 */
	public void until(WebElement element, int timeout) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.visibilityOf(element));
			test.log(Status.INFO, "Wait until " + element.toString() + "is visible.");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	/**
	 * wait until the list of Element is visible
	 * 
	 * @param element
	 */
	public void untilAvailable(List<WebElement> element) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
			wait.until(ExpectedConditions.visibilityOfAllElements(element));
			test.log(Status.INFO, "Wait until " + element.toString() + "is visible.");
		} catch (Exception exception) {
			elementPrintError(exception, element);

		}

	}

	/**
	 * Switch to new Url.
	 * 
	 * @param i
	 * @param url
	 * @throws InterruptedException
	 * @throws AWTException
	 */
	public void switchToNewURL(int i, String url) throws InterruptedException, AWTException {
		try {
			Thread.sleep(6000);
			Robot r = new Robot();
			r.keyPress(KeyEvent.VK_CONTROL);
			r.keyPress(KeyEvent.VK_T);
			r.keyRelease(KeyEvent.VK_CONTROL);
			r.keyRelease(KeyEvent.VK_T);
			Thread.sleep(1000);
			ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
			// switches to new tab
			driver.switchTo().window(tabs.get(i));
			driver.get(url);
			test.log(Status.INFO, "Navigated " + url.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, null);

		}
	}

	/**
	 * Wait until the element is clickable
	 * 
	 * @param element
	 * @param timeout
	 */
	public void untilClickable(WebElement element, int timeout) {
		try {

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.elementToBeClickable(element)).click();
			test.log(Status.INFO, "Wait until " + element.toString() + "is clickable.");

		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	/**
	 * Wait until the element is clickable and then click on that element
	 * 
	 * @param element
	 * @param numberOfSeconds
	 */
	public void waitAndClick(WebElement element, int numberOfSeconds) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(numberOfSeconds));
			wait.until(ExpectedConditions.elementToBeClickable(element)).click();
			test.log(Status.INFO, "Wait until " + element.toString() + "is clickable");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}

	}

	/**
	 * Click on list of element with the index 'i'
	 * 
	 * @param element
	 * @param i
	 */
	public void clickOnElementList(List<WebElement> element, int i) {
		WebElement element1 = null;
		try {
			List<WebElement> eleList = element;
			element1 = eleList.get(i);
			element1.click();
			test.log(Status.PASS, "Click " + element.toString());
		} catch (Exception exception) {
			elementPrintError(exception, element);

		}
	}

	/**
	 * Click on element after mouse hover
	 * 
	 * @param element
	 * @param element1
	 * @throws InterruptedException
	 */
	public void menuSelection(WebElement element, WebElement element1) throws InterruptedException {
		try {
			Actions actions = new Actions(driver);
			actions.moveToElement(element);
			actions.moveToElement(element1).click().build().perform();
			test.log(Status.PASS, "Menu selection " + element1.toString() + "is Selected");
			Thread.sleep(1000);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}

	}

	/**
	 * This method check for the element till its visible
	 * 
	 * @param element
	 * @return
	 * @throws IOException Comment - Updated the method to work for both Strings
	 *                     Message and Stepdata
	 */
	public boolean isElementVisible(WebElement element, String stepData) throws IOException {
		try {
			element.isDisplayed();
			String message = (stepData == null) ? element.toString() : stepData;
			test.log(Status.PASS, message + " is visible.");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
		return element.isDisplayed();
	}

	/**
	 * Method to capture ScreenShot
	 * 
	 * @param drivername
	 * @return
	 */
	public String snapshot(TakesScreenshot drivername) {
		String currentPath = "./test-output/errorImages";
		String returnPath = "./errorImages";
		File scrFile = drivername.getScreenshotAs(OutputType.FILE);
		try {
			log.info("save snapshot path is:" + currentPath + "/" + getDatetime() + ".png");
			FileUtils.copyFile(scrFile, new File(currentPath + "\\" + getDatetime() + ".png"));
			FileUtils.copyFile(scrFile, new File(returnPath + "\\" + getDatetime() + ".png"));
		} catch (IOException exception) {
			log.error("Can't save screenshot");
			return "";
		}
		return returnPath + "/" + getDatetime() + ".png";
	}

	/**
	 * Method returns the current date
	 * 
	 * @return
	 */
	public String getDatetime() {

		SimpleDateFormat date = new SimpleDateFormat("yyyymmdd_hhmmss");
		return date.format(new Date());

	}

	/**
	 * This method print the exceptions message according as per the condition
	 * satisfies.
	 * 
	 * @param e
	 * @param element
	 */
	public void exceptionPrintError(Exception exception, WebElement element) {
		String path = snapshot((TakesScreenshot) driver);
		try {
			if (exception.toString().contains("StaleElementReferenceException") && element != null) {
				test.log(Status.FAIL, ErrorType.ELEMENT_STALE + element.toString() + exception.toString()
						+ "\nScreencast below: " + test.addScreenCaptureFromPath(path));
			} else if (exception.toString().contains("StaleElementReferenceException") && element == null) {
				test.log(Status.FAIL, ErrorType.ELEMENT_STALE + exception.toString() + "\nScreencast below: "
						+ test.addScreenCaptureFromPath(path));
			} else if (exception.toString().contains("NoSuchElementException") && element != null) {
				test.log(Status.FAIL, ErrorType.ELEMENT_NOTFOUND + element.toString() + exception.toString()
						+ "\nScreencast below: " + test.addScreenCaptureFromPath(path));
			} else if (exception.toString().contains("NoSuchElementException") && element == null) {
				test.log(Status.FAIL, ErrorType.ELEMENT_NOTFOUND + exception.toString() + "\nScreencast below: "
						+ test.addScreenCaptureFromPath(path));
			} else if (exception.toString().contains("WebDriverException") && element != null) {
				test.log(Status.FAIL, element.toString() + exception.toString() + "\nScreencast below: "
						+ test.addScreenCaptureFromPath(path));
			} else if (exception.toString().contains("WebDriverException") && element == null) {
				test.log(Status.FAIL,
						exception.toString() + "\nScreencast below: " + test.addScreenCaptureFromPath(path));
			} else if (exception.toString().contains("ElementNotInteractableException")) {
				test.log(Status.WARNING,
						element.toString() + exception.toString() + "\nCould not interact with Element");
			}
		} catch (Exception ioException) {
			ioException.printStackTrace();
		}
	}

	/**
	 * Method to Identify the type of the error occured.
	 */
	public enum ErrorType {
		ELEMENT_NOTFOUND("Element was not found, "),
		ELEMENT_STALE("Element was no longer located in the DOM and has become stale, "),
		WAIT_TIMEOUT("Wait timeout occured, ");

		private ErrorType(String errorMsg) {
		}
	}

	/**
	 * Close the current window in focus
	 * 
	 * @throws AWTException
	 */
	public void closeCurrrentWindow() throws AWTException {
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_W);
			test.log(Status.INFO, "Current Window is closed");
		} catch (Exception exception) {
			exceptionPrintError(exception, null);

		}

	}

	/**
	 * Find the Broken images links in the page.
	 * 
	 * @param element
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void findBrokenImages(WebElement element) throws ClientProtocolException, IOException {
		int counter = 0;
		List<WebElement> allImg = element.findElements(By.tagName("img"));
		log.info("Number of image tags - " + allImg.size());

		for (int i = 0; i < allImg.size(); i++) {
			allImg = element.findElements(By.tagName("img"));
			try {
				int responseCode = Request.Get(allImg.get(i).getAttribute("src")).execute().returnResponse()
						.getStatusLine().getStatusCode();
				String code = String.valueOf(responseCode);
				String txt = allImg.get(i).getText();
				String imgLink = allImg.get(i).getAttribute("src");

				// Writing in the excel file.
				log.info(i + ". Text " + txt);
				log.info(i + ". Link " + imgLink);
				log.info(i + ". Status " + code);
				log.info(" ");

				if (code == "404") {
					counter += 1;
					test.log(Status.FAIL, "Image Link " + element.toString() + "is Broken");
				}
			} catch (Exception exception) {
				exceptionPrintError(exception, allImg.get(i));
				exceptionPrintError(exception, null);
				continue;

			}
		}
		log.info("Total Number of Broken Images - " + counter);
	}

	/**
	 * Method to print the error message when exception occurs in list of elements
	 * and capture the screenshots.
	 * 
	 * @param e
	 * @param elements
	 */
	public void elementPrintError(Object e, List<WebElement> elements) {
		String path = snapshot((TakesScreenshot) driver);
		try {
			test.log(Status.FAIL, ErrorType.ELEMENT_NOTFOUND + elements.toString() + "\n" + e
					+ "\nScreencast below: " + test.addScreenCaptureFromPath(path));
			} catch (Exception ioException) {
				ioException.printStackTrace();
			}
		} 

	/**
	 * Find the Broken links in the page Supply the parent tag of the broken links
	 * as parameter.
	 * 
	 * @param element
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void findBrokenLinks(WebElement element) throws IOException {
		int counter = 0;
		List<WebElement> allLinks = element.findElements(By.tagName("a"));

		int responseCode = 0;
		for (WebElement webElement : allLinks) {

			allLinks = element.findElements(By.tagName("a"));
			try {
				String href = webElement.getAttribute("href");
				if (href.equals("#"))
					continue;
				if (href.startsWith("\\/")) {
					String BaseUrl = new URL(driver.getCurrentUrl()).getHost();
					href = BaseUrl + href;
				}

				responseCode = Request.Get(webElement.getAttribute("href")).execute().returnResponse().getStatusLine()
						.getStatusCode();
				String code = String.valueOf(responseCode);
				String txt = webElement.getText();
				String link = webElement.getAttribute("href");

				// Writing in the excel file.
				log.info(counter + ". Text " + txt);
				log.info(counter + ". Link " + link);
				TestData.set("Broken link" + counter, link);
				log.info(counter + ". Status " + code);
				log.info(" ");

				if (responseCode == 404) {
					counter += 1;
					test.log(Status.FAIL, " Link " + element.toString() + "is Broken");
				}
			} catch (Exception exception) {
				exceptionPrintError(exception, allLinks.get(counter));
				exceptionPrintError(exception, null);
				continue;
			}
		}
		log.info("Total Number of Broken Items - " + counter);
	}
	
	/**
	 * Find the Broken links and images in the page and log them in an excel file
	 * 
	 */
	public void findBrokenLinksAndImages(String path) throws Exception, IOException {
		String projectPath = System.getProperty("user.dir");

		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
		String date = df.format(new Date());

		// Create new workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet linkSheet;
		Row linkRow;

		try {
			// Existing workbook
			FileInputStream fileInput = new FileInputStream(projectPath + path);
			XSSFWorkbook inputWorkbook = new XSSFWorkbook(fileInput);
			XSSFSheet inputSheet = inputWorkbook.getSheetAt(0);

			int totalRows = inputSheet.getLastRowNum();
			if ((totalRows > 0) || (inputSheet.getPhysicalNumberOfRows() > 0)) {
				totalRows++;
			}
			System.out.println("Rows count: " + totalRows);

			// Create a new font and alter it.
			XSSFFont font = workbook.createFont();
			font.setBold(true);

			// Fonts are set into a style so create a new one to use.
			CellStyle style = workbook.createCellStyle();
			style.setFont(font);

			linkSheet = workbook.createSheet("Details");
			linkRow = linkSheet.createRow(0);
			linkRow.createCell(0).setCellValue("Site");
			linkRow.createCell(1).setCellValue("Url");
			linkRow.createCell(2).setCellValue("Link_Text");
			linkRow.createCell(3).setCellValue("Tag_Name");
			linkRow.createCell(4).setCellValue("Status");
			linkRow.createCell(5).setCellValue("Message");

			for (int i = 0; i < 6; i++) {
				linkRow.getCell(i).setCellStyle(style);
			}

			for (int j = 0; j < totalRows; j++) {
				String site = inputSheet.getRow(j).getCell(0).getStringCellValue();
				System.out.println("url: " + site);
				navigate(site);
				waitForSeconds(10000);
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

				// Get the list of all links and images
				List<WebElement> linksList = driver.findElements(By.tagName("a"));
				linksList.addAll(driver.findElements(By.tagName("img")));

				System.out.println("Count of links and images: " + linksList.size());

				// iterate linkslist
				for (int i = 0; i < linksList.size(); i++) {
					int rowNum = linkSheet.getLastRowNum();

					System.out.println(rowNum + 1 + ". " + linksList.get(i).getAttribute("href"));
					if (linksList.get(i).getAttribute("href") != null
							&& (!linksList.get(i).getAttribute("href").contains("javascript"))
							&& (!linksList.get(i).getAttribute("href").contains("tel"))) {
						String url = linksList.get(i).getAttribute("href");
						String linktext = linksList.get(i).getText();
						String tagname = linksList.get(i).getTagName();
						
						Set<Cookie> cookieSet = getDriver().manage().getCookies();
						String cookie = cookieSet.toString().replace("[", "");
						// System.out.println("Cookie: "+cookie);

						HttpURLConnection connection = (HttpURLConnection) new URL(
								linksList.get(i).getAttribute("href")).openConnection();
						connection.setRequestProperty("Cookie", cookie);
						connection.connect();
						int status = connection.getResponseCode();
						String message = connection.getResponseMessage();
						connection.disconnect();

						linkRow = linkSheet.createRow(rowNum + 1);
						linkRow.createCell(0).setCellValue(site);
						linkRow.createCell(1).setCellValue(url);
						linkRow.createCell(2).setCellValue(linktext);
						linkRow.createCell(3).setCellValue(tagname);
						linkRow.createCell(4).setCellValue(status);
						linkRow.createCell(5).setCellValue(message);
					} else {
						String url = linksList.get(i).getAttribute("href");
						String tagname = linksList.get(i).getTagName();
						if (url != null) {
							linkRow = linkSheet.createRow(rowNum + 1);
							linkRow.createCell(0).setCellValue(site);
							linkRow.createCell(1).setCellValue(url);
							linkRow.createCell(3).setCellValue(tagname);
						} else {
							linkRow = linkSheet.createRow(rowNum + 1);
							linkRow.createCell(0).setCellValue(site);
							linkRow.createCell(1).setCellValue("null");
							linkRow.createCell(3).setCellValue(tagname);
						}
					}
				}
			}
			inputWorkbook.close();
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		} finally {
			// Write the workbook in file system
			FileOutputStream out = new FileOutputStream(
					projectPath + "/test-output/webscraper/WebScraper_" + date + ".xlsx");
			workbook.write(out);
			workbook.close();
			
			out.close();
			System.out.println("Excel Created!");
		}
	}

	/**
	 * Uploading File using Send Keys method of WebDriver
	 * 
	 * @param element
	 * @param path
	 */
	public void uploadFileUsingSendkeys(WebElement element, String path) {
		try {
			try {

				Thread.sleep(4000);
			} catch (InterruptedException ieException) {
				ieException.printStackTrace();
			}
			element.sendKeys(path);
			test.log(Status.PASS, " File in  " + element.toString() + "is Uploaded");
		} catch (Exception exception) {

			exceptionPrintError(exception, element);
		}
	}

	/**
	 * Uploading file using the Robot Class
	 * 
	 * @param element
	 * @param path
	 * @throws AWTException
	 */
	public void uploadFileUsingRobotClass(WebElement element, String path) throws AWTException {
		try {
			element.click();
			StringSelection selection = new StringSelection(path);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, null);
			Robot robot;
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ENTER);
			test.log(Status.PASS, " File in  " + element.toString() + "is Uploaded");
		} catch (Exception exception) {

			exceptionPrintError(exception, element);
		}
	}

	/**
	 * Uploading File using Java Script Executor.
	 * 
	 * @param element
	 * @param path
	 */
	public void uploadFileUsingJavaScriptExecutor(WebElement element, String path) {
		try {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].setAttribute('value', '" + path + "')", element);
			test.log(Status.PASS, " File in  " + element.toString() + "is Uploaded");
		} catch (Exception exception) {

			exceptionPrintError(exception, element);
		}

	}

	/**
	 * 
	 * @param strElement
	 * @return
	 * @throws Exception
	 */
	public By getLocator(String strElement) throws Exception {
		String locator = TestData.get(strElement);

		String locatorType = locator.substring(0, locator.indexOf(":"));
		String locatorValue = locator.substring(locator.indexOf(":") + 1);

		if (locatorType.toLowerCase().equals("id"))
			return By.id(locatorValue);
		else if (locatorType.toLowerCase().equals("name"))
			return By.name(locatorValue);
		else if ((locatorType.toLowerCase().equals("classname")) || (locatorType.toLowerCase().equals("class")))
			return By.className(locatorValue);
		else if ((locatorType.toLowerCase().equals("tagname")) || (locatorType.toLowerCase().equals("tag")))
			return By.className(locatorValue);
		else if ((locatorType.toLowerCase().equals("linktext")) || (locatorType.toLowerCase().equals("link")))
			return By.linkText(locatorValue);
		else if (locatorType.toLowerCase().equals("partiallinktext"))
			return By.partialLinkText(locatorValue);
		else if ((locatorType.toLowerCase().equals("cssselector")) || (locatorType.toLowerCase().equals("css")))
			return By.cssSelector(locatorValue);
		else if (locatorType.toLowerCase().equals("xpath"))
			return By.xpath(locatorValue);
		else
			throw new Exception("Unknown locator type '" + locatorType + "'");
	}

	/**
	 * This method identifies and gets the element.
	 * 
	 * @param strElement
	 * @return
	 * @throws Exception
	 */
	public WebElement getElement(String strElement) throws Exception {
		By by = getLocator(strElement);

		if (driver.findElements(by).size() <= 0) {
			Assert.fail(String.format("%s element is not found on the page", strElement));
		}

		return driver.findElement(by);
	}

	/**
	 * Wait till the page loads completely.
	 */
	public void waitForPageLoaded() {

		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		try {
			Thread.sleep(1000);
						
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
			wait.until(expectation);
		} catch (Throwable error) {
			log.info("Timeout waiting for Page Load Request to complete.");
		}
	}

	/**
	 * Scrolls page till element gets visible.
	 * 
	 * @param element
	 */
	public void scrollToVisible(WebElement element) {
		((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0," + (element.getLocation().y - 100) + ")");

	}

	/**
	 * Scrolls page till element gets visible and click on it.
	 * 
	 * @param locator
	 */
	public void scrollToVisibleAndClick(final By locator) {
		WebElement element = getDriver().findElement(locator);
		((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0," + (element.getLocation().y - 100) + ")");
		waitForSeconds(5000);
		click(element);
	}

	/**
	 * Scrolls to top of the page.
	 */
	public void scrollToTop() {
		((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0,0)");
	}

	/**
	 * Drag and drop the element.
	 * 
	 * @param locator
	 */
	public void clickHoldAndRelease(final By scrLocator,final By desLocator) {
		WebElement scrElement = driver.findElement(scrLocator);
		WebElement desElement = driver.findElement(desLocator);

		// Using Action Class
		Actions action = new Actions(driver);
		Action clickAndMove = action.clickAndHold(scrElement).release(desElement).build();
		clickAndMove.perform();
		waitForPageLoaded();
	}

	/**
	 * Hover on the element.
	 * 
	 * @param locator
	 */
	public void hoverElement(final By locator) {
		Actions action = new Actions(driver);
		WebElement element = driver.findElement(locator);
		action.moveToElement(element).build().perform();
	}

	/**
	 * Switch to main window.
	 */
	public void switchToMainWindow() {
		Set<String> existHandlers = driver.getWindowHandles();
		String expectedhandler = null;
		for (String handler : existHandlers) {
			test.info("existHandler..." + handler);
			expectedhandler = handler;
		}
		driver.switchTo().window(expectedhandler);
	}

	/**
	 * Wait for seconds.
	 * 
	 * @param wait
	 */
	public void waitForSeconds(long wait) {
		try {
			Thread.sleep(wait);
		} catch (InterruptedException e) {
			test.info(e.getMessage());
		}
	}

	/**
	 * This method check for the element and returns its status
	 * 
	 * @param locator
	 */
	public boolean isElementVisible(final By locator) {
		waitForPageLoaded();// WebElement element = driver.findElement(locator);
		try {
			WebElement Element = (new WebDriverWait(driver, Duration.ofSeconds(50))).until(new ExpectedCondition<WebElement>() {
				public WebElement apply(WebDriver d) {
					test.info("method[isElementVisible] try to find element in HTML " + locator);
					return d.findElement(locator);
				}
			});

			if (Element.isDisplayed()) {
				return true;
			}

		} catch (Exception e) {
			test.info("Error: element not found for " + locator);
			test.info(this.getClass().getName() + e.getMessage());
		}
		return false;
	}

	/**
	 * This Method Clicks on a Web element.
	 * 
	 * @param locator
	 */
	public void click(final By locator) {
		WebElement element = null;
		try {
			element = driver.findElement(locator);
			element.click();
			test.log(Status.PASS, "Click " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * This Method navigates to a Webpage pointed by given URL with clear cache.
	 * 
	 * @param url
	 */
	public void navigateWithCacheClear(String url) {
		try {
			driver.manage().deleteAllCookies();
			test.info("All Cookies have been cleaned....");
			driver.navigate().to(url);
			waitForPageLoaded();
			test.log(Status.INFO, "Page navigates to " + url);
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
	}

	/**
	 * This Method clicks on the element using JavascriptExecutor.
	 * 
	 * @param locator
	 */
	public void clickByJavaScriptExecutor(final By locator) {
		try {
			WebElement element = driver.findElement(locator);
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
			test.log(Status.PASS, "Click on: " + element);
		} catch (Exception exception) {
			test.info("Method[clickByJavaScriptExecutor] cannot find this element " + exception.getMessage());
		}
	}

	/**
	 * This Method Enters value in a text box Field.
	 * 
	 * @param locator
	 * @param keys
	 */
	public void sendKeys(final By locator, String keys) {
		WebElement element = null;
		try {
			element = driver.findElement(locator);
			element.clear();
			element.sendKeys(keys);

			test.log(Status.INFO, "Send " + keys + " to " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * This Method clicks on Element by using Actions Class.
	 *
	 * @param locator
	 */
	public void clickByAction(final By locator) {
		WebElement element = null;
		try {
			element = getDriver().findElement(locator);
			Actions actions = new Actions(driver);
			actions.click(element).build().perform();
			test.log(Status.PASS, "clickByAction " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	
	public String getEnvironment() {
		String environment = null;
		try {
			test.log(Status.INFO, "Environment is returned");
			environment = env;
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
		return environment;
	}

	public void untilUnavailable(WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
			wait.until(ExpectedConditions.invisibilityOf(element));

			test.log(Status.INFO, "Wait until " + element.toString() + "is invisible.");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}

	}

	/**
	 * This method check for the element till its Selected
	 * 
	 * @param element
	 * @return
	 * @throws IOException
	 */
	public boolean isElementSelected(WebElement element, String message) {
		try {

			element.isDisplayed();
			element.isSelected();
			test.log(Status.INFO, message + " is Selected.");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
		return element.isSelected();
	}

	/**
	 * This methods refresh the application
	 * 
	 */

	public void refresh() {
		try {
			driver.navigate().refresh();
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
			test.log(Status.INFO, "Refreshed the Window.");

		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}

	}

	/**
	 * Wait till element is clickable
	 * 
	 * @param element
	 * @param timeout
	 */

	public void untilClickable(WebElement element, int timeout, String message) {
		try {

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.elementToBeClickable(element)).click();
			test.log(Status.INFO, "Wait until " + message + "is clickable.");

		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	/**
	 * This Method Enters value in a text box Field.
	 * 
	 * @param element
	 * @param keys
	 */
	public void sendKeys(WebElement element, String keys, String message) {
		try {
			element.clear();
			element.sendKeys(keys);

			test.log(Status.INFO, "Send " + keys + " to " + message);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * Wait until the element is visible
	 * 
	 * @param element
	 * @return
	 */
	public void untilAvalible(WebElement element, String message) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
			wait.until(ExpectedConditions.visibilityOf(element));

			test.log(Status.INFO, "Wait until " + message + "is visible.");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}

	}

	/**
	 * This method check if the element is enabled
	 * 
	 * @param element
	 * @return
	 * @throws IOException
	 */
	public boolean isElementEnabled(WebElement element, String message) throws IOException {

		boolean enabled = false;
		try {
			element.isEnabled();
			enabled = true;
			log.info("Element is Enabled...." + enabled);
			test.log(Status.INFO, message + " is enabled.");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
			log.error("Element is not Enabled..." + enabled);

		}
		return enabled;
	}

	/**
	 * This Method Clicks on a Web element.
	 * 
	 * @param element
	 */
	public void click(WebElement element, String message) {
		try {
			element.click();
			test.log(Status.PASS, "Click... " + message);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * This Method returns the link text of an element.
	 * 
	 * @param element
	 * @return
	 */
	public String getText(WebElement element, String message) {
		String t = null;
		try {
			t = element.getText();
			log.info("Element is displayed......." + t);
			test.log(Status.INFO, "Get Text " + t + " of " + message);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
		return t;
	}

	/**
	 * Wait until the element is clickable and then click on that element
	 * 
	 * @param ele
	 * @param time
	 */
	public void waitAndClick(WebElement element, int time, String message) {
		try {
			WebDriverWait wait4 = new WebDriverWait(driver, Duration.ofSeconds(time));
			wait4.until(ExpectedConditions.elementToBeClickable(element)).click();
			test.log(Status.PASS, "Wait until " + message + "is clickable");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}

	}

	/**
	 * This Method navigates webpage to the previuos page.
	 * 
	 */
	public void navigateback() {
		try {
			driver.navigate().back();
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
			test.log(Status.INFO, "Page navigates to ");
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
	}

	/**
	 * This Method switches focus of the Web Driver to an iframe.
	 * 
	 * @param frame
	 */
	public void switchToFrame(WebElement frame, String message) {
		try {
			driver.switchTo().frame(frame);
			test.log(Status.INFO, "Switch to Frame " + message);
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
	}

	/**
	 * This Method scrolls to an element and highlights it.
	 * 
	 * @param element
	 */
	public void scrollAndHighlightElement(WebElement element, String message) {

		try {

			for (int i = 0; i < 4; i++) {
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element,
						"color: yellow; border: 4px solid blue,;");
				js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element);
			}
			test.log(Status.INFO, "Scroll and Highlight: " + message);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	/**
	 * This method verifies the status code of the URL.
	 * 
	 * @param elements
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getURLResponse(String url) throws IOException {
		String code = null;
		try {
			int responseCode = Request.Get(url).execute().returnResponse().getStatusLine().getStatusCode();
			code = String.valueOf(responseCode);
			test.log(Status.PASS, "URL Response is 200");
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
		return code;
	}

	
	/**
	 * This Method returns url of a WebPage.
	 * 
	 * @return
	 */
	public String getUrl() {
		String currentURL = null;
		try {
			currentURL = driver.getCurrentUrl();
			test.log(Status.INFO, "Page Url is " + currentURL);
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
		return currentURL;
	}
	
	
	/**
	 * Wait until the timeout given
	 * 
	 * @param element
	 * @param timeout
	 */
	public void until(WebElement element, int timeout, String message) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait.until(ExpectedConditions.visibilityOf(element));
			test.log(Status.INFO, "Wait until " + message + "is visible.");
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	/**
	 * This Method waits for the element to be clickable and then enters the text in
	 * it.
	 * 
	 * @param ele
	 * @param keys
	 * @param i
	 */
	public void waitAndSendkeys(WebElement element, String keys, int timeout, String message) {
		try {
			WebDriverWait wait4 = new WebDriverWait(driver, Duration.ofSeconds(timeout));
			wait4.until(ExpectedConditions.elementToBeClickable(element));
			element.clear();
			element.sendKeys(keys);
			test.log(Status.INFO, "Send " + keys + " to " + message);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * This Method returns the data present in attribute 'Value' of an element.
	 * 
	 * @param element
	 * @return
	 */
	public String getTextValue(WebElement element, String message) {
		String t = null;
		try {
			t = element.getAttribute("Value");
			log.info("Get Text value is" + t);
			test.log(Status.INFO, "Get Text " + t + " of " + message);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
		return t;
	}

	/**
	 * This Method set the size of browser window.
	 */
	public void setBrowsersize() {
		try {
			Dimension d = new Dimension(450, 800);
			// Resize current window to the set dimension
			driver.manage().window().setSize(d);
			test.log(Status.INFO, "Browser Window Maximized.");
		} catch (Exception exception) {
			exceptionPrintError(exception, null);

		}
	}

	/**
	 * This Method is used to add cookie.
	 * 
	 * @param name, value
	 */
	public void addCookie(String name, String value) {
		try {
			driver.manage().addCookie(new Cookie(name, value));
			driver.navigate().refresh();
			test.log(Status.INFO, name + "Cookie have been added");
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
			test.log(Status.INFO, name + "Cookie have not been added");
		}
	}

	public void scrollToTopOfThePage() {

		Actions actions = new Actions(driver);
		actions.keyDown(Keys.CONTROL).sendKeys(Keys.HOME).keyUp(Keys.CONTROL).build().perform();
		test.log(Status.INFO, "Webpage is scrolled to top of page");
	}

	public void scrollWaitAndClick(WebElement element) {
		ScrollWebPage(element);
		waitAndClick(element, 6);
	}

	public void scrollWebPageToElementOnBottomOfScreen(WebElement element) {
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", element);
			test.log(Status.INFO, "Webpage is scrolled to element: " + element);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	public void scrollToSpecificPixels(int x, int y) {
		try {
			sleep(3000);
			((JavascriptExecutor) driver).executeScript("window.scrollBy(" + x + ", " + y + ")");
		} catch (Exception exception) {
			log.error(exception.getMessage());
		}
	}

	public void scrollPageUntilElementIsClickable(WebElement element) {
		try {
			String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
					+ "var elementTop = arguments[0].getBoundingClientRect().top;"
					+ "window.scrollBy(0, elementTop-(viewPortHeight/2));";

			((JavascriptExecutor) driver).executeScript(scrollElementIntoMiddle, element);

			test.log(Status.INFO, "Webpage is scrolled to element: " + element);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * This Method waits for page to be loaded.
	 */
	public void waitForPageLoad() {
		try {
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(50));
			sleep(1000);
		} catch (Exception e) {
			exceptionPrintError(e, null);
		}
	}

	/**
	 * This method is used to write data to HTML Extent Report File.
	 * 
	 * @param string
	 */
	public void commentInfo(String string) {
		log.info(string);
		test.info(string);
	}

	/**
	 * Click on the element using JavaScriptExecutor and pass link name message.
	 * 
	 * @param element
	 */
	public void clickByJavaScriptExecutor(WebElement element, String message) {
		try {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
			test.pass("Click on link: " + message);

		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	/**
	 * Scroll to Element with height value in string.
	 * 
	 * @param string
	 */
	public void scrollByJavaScriptExecutor(String string) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0," + string + ")", "");
	}

	
	/**
	 * This Method clicks on Element by using Actions Class.
	 * 
	 * @param element
	 */
	public void clickByAction(WebElement element) {
		try {
			Actions actions = new Actions(driver);
			actions.click(element).build().perform();
			test.log(Status.PASS, "clickByAction " + element.toString());
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	
	/**
	 * This Method perform Right clicks on Element by using Actions Class.
	 * 
	 * @param element
	 */
	public void rightClickByAction(WebElement element, String message) {
		try {
			Actions actions = new Actions(driver);
			actions.contextClick(element).build().perform();
			test.pass("Right Click on link: " + message);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}
	
	/**This Method perform double clicks on Element by using Actions Class.
	 * @param element
	 */
	public void doubleClickByAction(WebElement element, String message) {
		try {
			Actions actions = new Actions(driver);
			actions.doubleClick(element).build().perform();
			test.pass("Right Click on link: " + message);
		} catch (Exception exception) {
			exceptionPrintError(exception, element);

		}
	}

	/**This method is used to hold the element and then release
	 * @param element
	 * @throws Exception
	 */
	public void clickHoldAndRelease(WebElement sourceElement, WebElement destinationElement) throws Exception {
		// Using Action Class
		Actions action = new Actions(driver);
		action.clickAndHold(sourceElement).release(destinationElement).build().perform();
		waitForPageLoaded();
	}
	

	/**
	 * This method is used to hover over Element,
	 * @param element
	 * @throws Exception
	 */
	public void hoverElement(WebElement element) throws Exception {
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();
	}

	/**
	 * This Method opens any element in a new tab
	 * @param element
	 * @author devesh.sarda
	 */
	public void openInNewTab(WebElement element) {
		try {
			element.sendKeys(Keys.CONTROL, Keys.ENTER);
			test.log(Status.INFO, "Element " + element.toString() + " opened in new tab ");
		} catch (Exception exception) {
			test.log(Status.FAIL, "Element " + element.toString() + " cannot be opened in new tab ");
			exceptionPrintError(exception, element);
		}
	}

	/**
	 * This method is used to check the url response code without actually clicking
	 * the links
	 * 
	 * @param url - Takes "href" tags to get the response code
	 * @throws Exception
	 */
	public void urlStatus(String url) throws Exception {
		int resCode = 0;
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		String error = "We're Sorry | File Not Found";
		try {
			HttpResponse response = client.execute(request);
			resCode = response.getStatusLine().getStatusCode();
			log.info(resCode);
			if ((resCode) == 404 || (resCode) == 500 || getDriver().getTitle().toString().contains(error)) {
				test.log(Status.FAIL, "The url " + url + " has the response code " + resCode);
				log.info("The url " + url + " has the response code " + resCode);
			} else {
				test.log(Status.PASS, "The url " + url + " has the response code " + resCode);
				log.info("The url " + url + " has the response code " + resCode);
			}
		} catch (Exception exception) {
			// exceptionPrintError(exception,null);
		}
	}

	/**
	 * This Method Switches the focus to Window number passed as parameter.
	 * 
	 * @param i
	 */
	public void switchToWindow(int i) {
		try {
			ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(tabs.get(i));
			test.log(Status.INFO, "Focused is switched to window number: " + i);
		} catch (Exception exception) {
			exceptionPrintError(exception, null);
		}
	}

		
	 /**
  	 *This Method click on imitate Below CTA (Available in Selenium4)
	 * @param actualBelowElement - The element which is below
	 * @param aboveElement - The element which is above
	 * @author zainab.firdos
	 */
	public void belowElementClick(By actualBelowElement,By aboveElement) {
		try {
			By relativeloc = RelativeLocator.with(actualBelowElement).below(aboveElement);
			driver.findElement(relativeloc).click();
			//System.out.println(driver.findElement(relativeloc).getText());
						
		} catch (Exception exception) {
			exception.getMessage();
		}
	}
	
	/**This Method click on imitate Above CTA (Available in Selenium4)
	 * @param actualAboveElement - The element which is above
	 * @param belowElement - The element which is below
	 * @author zainab.firdos
	 */
	
	public void aboveElementClick(By actualAboveElement,By belowElement) {
		try {
			By relativeloc = RelativeLocator.with(actualAboveElement).above(belowElement);
			driver.findElement(relativeloc).click();
			//System.out.println(driver.findElement(relativeloc).getText());
			
		} catch (Exception exception) {
			exception.getMessage();
		}
	}
	
	
	/**This Method click on imitate Right CTA (Available in Selenium4)
	 * @param actualRightElement - The element which is Right to
	 * @param leftElement - The element which is left
	 * @author zainab.firdos
	 */
	
	public void rightElementClick(By actualRightElement,By leftElement) {
		try {
			By relativeloc = RelativeLocator.with(actualRightElement).toRightOf(leftElement);
			driver.findElement(relativeloc).click();
			//System.out.println(driver.findElement(relativeloc).getText());
						
		} catch (Exception exception) {
			exception.getMessage();
		}
	}
	
	
	/**This Method click on imitate Left CTA (Available in Selenium4)
	 * @param actaulLeftElement - The element which is left of
	 * @param rightElement - The element which is right
	 * @author zainab.firdos
	 */
	public void leftElementClick(By actaulLeftElement,By rightElement) {
		try {
			By relativeloc = RelativeLocator.with(actaulLeftElement).toLeftOf(rightElement);
			driver.findElement(relativeloc).click();
			//System.out.println(driver.findElement(relativeloc).getText());
						
		} catch (Exception exception) {
			exception.getMessage();
		}
	}
	
	
	/**This Method waits for 2 secs (Available in Selenium4)
	 * 
	 * @author zainab.firdos
	 */
	public void implicitWait() {
		driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2000));
		
	}

}
