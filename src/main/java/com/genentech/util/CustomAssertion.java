package com.genentech.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.ScreenCapture;

public class CustomAssertion {
	private Logger log = LogManager.getLogger(this.getClass());
	private WebDriver driver;
	private ExtentTest test;
	private SoftAssert softAssert;
	String currentPath = ".\\test-output\\errorImages";
	String returnPath = ".\\errorImages";

	public CustomAssertion(WebDriver d, ExtentTest extentTest, SoftAssert softAssert) {
		driver = d;
		test = extentTest;
		this.softAssert = softAssert;
	}

	public enum ErrorType {
		ELEMENT_NOTFOUND("Element was not found, "), ELEMENT_STALE(
				"Element was no longer located in the DOM and has become stale, "), WAIT_TIMEOUT(
						"Wait timeout occured, "), ASSERTED("Assertion failed, ");

		public String errorMsg;

		private ErrorType(String errorMsg) {
			this.errorMsg = errorMsg;
		}
	}

	
	public boolean assertEquals(String actual, String expected, String message) {
		try {
			Assert.assertEquals(actual, expected, message);
			test.log(Status.PASS, message + " actual: " + actual + " expected: " + expected + "...are equal");
			return true;
		} catch (AssertionError e) {
			test.log(Status.FAIL, "actual: " + actual + " expected: " + expected + "...are not equal");
			String path = snapshot((TakesScreenshot) driver);
			printError(e, message, path);}
			softAssert.assertEquals(actual, expected, message);
			return false;
	}

	public boolean assertEquals(String actual, String expected) {
		try {
			Assert.assertEquals(actual, expected);
			test.log(Status.PASS, "actual: " + actual + " expected: " + expected);
			return true;
		} catch (AssertionError e) {
			test.log(Status.FAIL, "actual: " + actual + " expected: " + expected + "...are not equal");
			String path = snapshot((TakesScreenshot) driver);
			printError(e, path);}
			softAssert.assertEquals(actual, expected);
			return false;
	}

	public boolean assertNotNull(Object obj) {
		try {
			Assert.assertNotNull(obj);
			test.log(Status.PASS,  "Object " + obj.toString() + " is not null.");
			return true;
		} catch (AssertionError e) {
			test.log(Status.FAIL, "Object " + obj.toString() + " is null.");
			String path = snapshot((TakesScreenshot) driver);
			printError(e, path);
			}
			softAssert.assertNotNull(obj);
			return false;
	}
	
	public boolean assertNotNull(Object obj, String message) {
		try {
			Assert.assertNotNull(obj, message);
			test.log(Status.PASS, message + "Object " + obj.toString() + " is not null.");
			return true;
		} catch (AssertionError e) {
			test.log(Status.FAIL, "Object " + obj.toString() + " is null.");
			String path = snapshot((TakesScreenshot) driver);
			printError(e, message, path);
			}
			softAssert.assertNotNull(obj, message);
			return false;
	}


	public boolean assertTrue(boolean expression, String message) {
		try {
			Assert.assertTrue(expression, message);
			test.log(Status.PASS, message + " Expression " + expression + " is true.");
			return true;
		} catch (AssertionError e) {
			test.log(Status.FAIL, "Assertion failed : " + message);
			String path = snapshot((TakesScreenshot) driver);
			printError(e, message, path);
			softAssert.assertTrue(expression, message);
			return false;
		}
	}

	public boolean assertTrue(boolean expression,String PassingMsg, String FailingMsg) {
		try {
			Assert.assertTrue(expression, FailingMsg);
			test.log(Status.PASS, PassingMsg);
			return true;
		} catch (AssertionError e) {
			test.log(Status.FAIL, FailingMsg);
			String path = snapshot((TakesScreenshot) driver);
			printError(e, FailingMsg, path);}
			softAssert.assertTrue(expression, FailingMsg);
			return false;
	}

	public boolean assertFalse(boolean expression) {
		try {
			Assert.assertFalse(expression);
			test.log(Status.PASS, "Expression " + expression + " is false.");
			return true;
		} catch (AssertionError e) {
			test.log(Status.FAIL, "Assertion failed");
			String path = snapshot((TakesScreenshot) driver);
			printError(e, path);}
			softAssert.assertTrue(expression);
			return false;
	}

	public boolean assertFalse(boolean expression, String message) {
		try {
			Assert.assertFalse(expression, message);
			test.log(Status.PASS, message + "Expression " + expression + " is false.");
			return true;
		} catch (AssertionError e) {
			test.log(Status.FAIL, message);
			String path = snapshot((TakesScreenshot) driver);
			printError(e, message, path);}
			softAssert.assertTrue(expression, message);
			return false;
	}
	
	
	public boolean assertNotEquals(String actual, String unexpected, String PassingMsg, String FailingMsg) {
		try {
			Assert.assertNotEquals(actual, unexpected);
			test.log(Status.PASS, "actual: " + actual + " Unexpected: " + unexpected + PassingMsg);
			return true;
		} catch (AssertionError e) {
			test.log(Status.FAIL, "actual: " + actual + " Unexpected: " + unexpected + FailingMsg);
			String path = snapshot((TakesScreenshot) driver);
			printError(e, path, FailingMsg);}
			softAssert.assertNotEquals(actual, unexpected, FailingMsg);
			return false;
	}
	
	public boolean assertContains(String actual, String expected) {
		try {			
			log.info(actual);
			log.info(expected);
			Assert.assertTrue(actual.contains(expected));
			test.log(Status.PASS, "actual: " + actual + " expected: " + expected );
			log.info("Pass");
			return true;
			}
		catch (AssertionError e) {
			test.log(Status.FAIL, "actual: " + actual + " expected: " + expected);
			String path = snapshot((TakesScreenshot) driver);
			printError(e, path);}
			return false;
	}
	
	
	public SoftAssert getSoftAssert() {
		return softAssert;
	}
	
	
	public String snapshot(TakesScreenshot drivername) {
		File scrFile = drivername.getScreenshotAs(OutputType.FILE);
		String dt = getDatetime();
		try {
			log.info("save snapshot path is:" + currentPath + "\\" + dt + ".png");
			FileUtils.copyFile(scrFile, new File(currentPath + "\\" + dt + ".png"));
			FileUtils.copyFile(scrFile, new File(returnPath + "\\" + dt + ".png"));
		} catch (IOException e) {
			log.error("Can't save screenshot");
			return "";
		} finally {
			log.info("screen shot finished, it's in " + currentPath + " folder");
			return returnPath + "\\" + dt + ".png";
		}
	}	

	public String getDatetime() {
		SimpleDateFormat date = new SimpleDateFormat("yyyymmdd_hhmmss");
		return date.format(new Date());
	}

	public void printError(AssertionError e, String message, String path) {
		log.info(path);
		String[] paths = path.split("/");
		String imageName = paths[paths.length - 1];
		try {
			test.log(Status.FAIL, message + e + "Screencast below: ");
			ScreenCapture screenCapture = new ScreenCapture(path, "title",  path, "base64");
			test.addScreenCaptureFromPath(path);
			test.log(Status.FAIL, message + e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void printError(AssertionError e, String path) {
		try {
			test.log(Status.FAIL, "\n" + e + "\n Screencast below: " + test.addScreenCaptureFromPath(path));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	

}
