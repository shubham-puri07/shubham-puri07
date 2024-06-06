package com.genentech.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;
import com.aventstack.extentreports.Status;

import net.lingala.zip4j.exception.ZipException;

@Listeners({ TestMethodListener.class })
public class TestCaseBase {
	private com.aventstack.extentreports.ExtentTest test;
	protected String testName;
	protected String className;
	protected String userStoryName;
	protected String buildNumber;
	protected PageManager pageManager;
	protected ExcelReader excelReader;
	protected RestAPIUtility restApiutility;
    private WebDriver driver_original;
	protected String browserFlag;
	private String onGrid;
	private String host;
	private String port;
	private static int ieCountCurrent = 0;
	private static int firefoxCountCurrent = 0;
	private static int chromeCountCurrent = 0;
	private static int edgeCountCurrent = 0;
	private LogWebdriverEventListener eventListener;
	protected Logger log = LogManager.getLogger(this.getClass());
	private String actualResult;
	private HashMap<String, String> expected;
	protected CustomAssertion customAssertion;
	protected static String errorImagesPath="./errorImages"; 
	protected File errorImageFolder;
	protected String[] mailIds;
	protected String mailAddress; 
	protected String xmlFilesPath=System.getProperty("user.dir") + "/testdata/xmlfiles";
	protected File xmlFileFolder;
	protected File extentReportFileFolder;
	protected String testPackageName;
	protected SoftAssert softAssert;
	public String environment;
	protected String elasticLocation;
	protected String elasticIp;
	protected String elasticPort;
	protected String elasticIndex;
	protected String elasticContentType;
	protected String projectCode;

	@BeforeSuite
	public void generateBuildNumber() throws IOException {
		errorImageFolder = new File(errorImagesPath);
		if (!errorImageFolder.exists()) {
			errorImageFolder.mkdir();
		}
		FileUtils.cleanDirectory(errorImageFolder);
		xmlFileFolder = new File(xmlFilesPath);
		if (!xmlFileFolder.exists()) {
			xmlFileFolder.mkdir();
		}
		FileUtils.cleanDirectory(xmlFileFolder);

		/*
		 * extentReportFileFolder = new File(ComplexReportFactory.returnPath); if
		 * (extentReportFileFolder.exists()) {
		 * FileUtils.cleanDirectory(extentReportFileFolder); }
		 */
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd_HH.mm");
		buildNumber = df.format(new Date());//
	}

	@Parameters({ "browserUsed", "runningOnGrid", "hubHost", "hubPort", "environment", "elasticIp",
		"elasticPort", "elasticIndex", "elasticContentType", "projectCode"})
	@BeforeClass(alwaysRun = true)
	public void setUpBrowser( @Optional("chrome") String browserUsed, @Optional("false") String runningOnGrid,
			@Optional("localhost") String hubHost, @Optional("4444") String hubPort, @Optional("QA") String environment,
			@Optional("10.128.190.5") String elasticIp, @Optional("9200") String elasticPort,
			@Optional("ngpautomation") String elasticIndex, @Optional("_doc") String elasticContentType,
			@Optional("testing") String projectCode) throws Exception {
		
		
		log.info("Running setupBrowser for Test Class: " + this.getClass().getName());
		
		initParams(browserUsed, runningOnGrid, hubHost, hubPort, environment, elasticIp, elasticPort,
				elasticIndex, elasticContentType, projectCode);
		
		selectBrowser();
		eventListener = new LogWebdriverEventListener();
		driver_original = new EventFiringDecorator<WebDriver>(eventListener).decorate(driver_original);
		//driver.register(eventListener);
		
		
		if (browserFlag.equals("ie") || browserFlag.equals("chrome") ||browserFlag.equals("edge") || browserFlag.equals("firefox") || browserFlag.equals("safari"))
			driver_original.manage().window().maximize();
			driver_original.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			driver_original.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));
			//setDefaultTestData();
	}

	@BeforeMethod(alwaysRun = true)
	public void beforeMethod(Method caller) {
		
		String[] classes = caller.getDeclaringClass().getName().split("\\.");
		className = classes[classes.length - 1];
		userStoryName = classes[classes.length - 2];
		testName = browserFlag + "-" + className + "-" + caller.getName();
		final String description = "The Extent Report for:"+className;

		test = ComplexReportFactory.getTest(testName, className, description);
		test.log(Status.INFO, "Test Started!");
		softAssert = new SoftAssert();
		customAssertion = new CustomAssertion(driver_original, test, softAssert);
		pageManager = new PageManager(driver_original, browserFlag, environment, test);
		excelReader = new ExcelReader(test);
		restApiutility = new RestAPIUtility();
	}

	@AfterMethod(alwaysRun = true)
	public void afterMethod(Method caller) {
		ComplexReportFactory.closeTest(testName);
		log.info(caller.getName() + " : " + test.getStatus());
		//Assert.assertSame(ComplexReportFactory.getTest(testName).getRunStatus(),  Status.PASS);  
		//Assert.assertEquals(ComplexReportFactory.getTest(testName).getStatus(), Status.PASS);
		
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() throws ZipException {
		TestData.load("mailreport.properties");
		TestData.get("recepientEmailIds");
		mailAddress= TestData.get("recepientEmailIds");
		ComplexReportFactory.closeReport();
		//zipAndMail();
	}

	private void selectBrowser() throws Exception {
		if (browserFlag.equals("ie")) {
			setUpIEWin64(onGrid);
		} else if (browserFlag.equals("firefox")) {
			setUpFirefoxWithDefaultProfile(onGrid);
		} else if (browserFlag.equals("chrome")) {
			setUpChromeWin32(onGrid);
		} else if (browserFlag.equals("edge")) {
			setUpEdgeWin32(onGrid);
		} else if (browserFlag.equals("safari")) {
			setUpSafari(onGrid);
		} else if (browserFlag.equals("random")) {
			setUpRandomBrowserPerCase(onGrid);
		} else if (browserFlag.equals("percentage_specified")) {
			setupBrowserPerPercentage();
		} else if (browserFlag.equals("mobileByCBT")) {
			setMobileByCBT();
		} else if (browserFlag.equals("mobileByBrowserStack")) {
			setMobileByBrowserStack();
		}
		else if (browserFlag.equals("appium")) {
			setUpAppium();
		}
		else if (browserFlag.equals("chromeHeadless")) {
			setUpChromeHeadless(onGrid);
		}
		else if (browserFlag.equals("edgeHeadless")) {
			setUpEdgeHeadless(onGrid);
		}
		else if (browserFlag.equals("firefoxHeadless")) {
			setUpFirefoxHeadless(onGrid);
		}
	}

	private void setupBrowserPerPercentage() throws Exception {
		Properties PROPERTIES_RESOURCES = SystemUtil.loadPropertiesResources("/browser-percentage.properties");
		String ie = PROPERTIES_RESOURCES.getProperty("ie.percentage");
		String firefox = PROPERTIES_RESOURCES.getProperty("firefox.percentage");
		String chrome = PROPERTIES_RESOURCES.getProperty("googlechrome.percentage");
		String edge = PROPERTIES_RESOURCES.getProperty("edge.percentage");
		int testcaseCount = Integer.parseInt(PROPERTIES_RESOURCES.getProperty("testcase.count"));
		newBrowserPerPercentage(ie, firefox, chrome, edge, testcaseCount, onGrid);
	}

	private void initParams(String browserUsed, String runningOnGrid, String hubHost, String hubPort, String env, String elasticIp, String elasticPort, String elasticIndex,
			String elasticContentType, String projectCode) {
		
		browserFlag = browserUsed;
		onGrid = runningOnGrid;
		host = hubHost;
		port = hubPort;
		environment = env;
		this.projectCode = System.getProperty("projectCode") != null ? System.getProperty("projectCode") : projectCode;
		this.elasticIp = System.getProperty("elasticIp") != null ? System.getProperty("elasticIp") : elasticIp;
		this.elasticPort = System.getProperty("elasticPort") != null ? System.getProperty("elasticPort") : elasticPort;
		this.elasticIndex = System.getProperty("elasticIndex") != null ? System.getProperty("elasticIndex")
				: elasticIndex;
		this.elasticContentType = System.getProperty("elasticContentType") != null
				? System.getProperty("elasticContentType")
				: elasticContentType;
		this.elasticLocation = "http://" + this.elasticIp + ":" + this.elasticPort + "/" + this.elasticIndex + "/"
				+ this.elasticContentType + "/";
		actualResult = null;
		expected = new HashMap<String, String>();

		log.info("onGrid=" + runningOnGrid);
		log.info("environment=" + environment);
		log.info("browserFlag=" + browserFlag);
		log.info("elasticLocation=" + elasticLocation);
		log.info("projectCode=" + projectCode);
		
		if (!onGrid.equals("false")) {
			log.info("hubHost=" + hubHost);
			log.info("hubPort=" + hubPort);
		}
	}

	/**
	 * Objective: Randomize the browser for each test case
	 *
	 * @param onGrid
	 * @throws Exception
	 *             Updated by colin @2013-10-24, now this method will not be
	 *             directly used it only be called withthin setUpBrowser when
	 *             browserFlag in testNG.xml is set to 'random'
	 */
	private void setUpRandomBrowserPerCase(String onGrid) throws Exception {
		log.info("Setting up random browser...");
		Random rndObj = new Random();
		int rndBrowserIndex = rndObj.nextInt(3);
		if (rndBrowserIndex == 0) {
			setUpIEWin64(onGrid);
			browserFlag = "ie";
		} else if (rndBrowserIndex == 1) {
			setUpFirefoxWithDefaultProfile(onGrid);
			browserFlag = "firefox";
		} else if (rndBrowserIndex == 2) {
			setUpChromeWin32(onGrid);
			browserFlag = "chrome";
		} else if (rndBrowserIndex == 3) {
			setUpEdgeWin32(onGrid);
			browserFlag = "edge";
		} else {
			log.error("Random select browser fails");
			throw new Exception("No browser is specified for the random number: " + rndBrowserIndex + ".");
		}
	}

	/**
	 * Objective: Set up the browser per percentage by different browsers
	 *
	 * @param iePercentage
	 *            : The percentage of test cases which to be executed in IE
	 * @param firefoxPercentage
	 *            :The percentage of test cases which to be executed in Firefox
	 * @param chromePercentage
	 *            : The percentage of test cases which to be executed in chrome
	 * @param testCaseCount
	 *            : Total count of test cases
	 * @param onGrid
	 * @throws Exception
	 */
	private void newBrowserPerPercentage(String iePercentage, String firefoxPercentage, String chromePercentage, String edgePercentage,
			int testCaseCount, String onGrid) throws Exception {
		log.info("Setting up browser per percentage: ie=" + iePercentage + " firefox=" + firefoxPercentage + " chrome="
				+ chromePercentage + " test case count=" + testCaseCount);
		// Convert the percentage to float
		float iePercent = Float.valueOf(iePercentage.substring(0, iePercentage.indexOf("%"))) / 100;
		float firefoxPercent = Float.valueOf(firefoxPercentage.substring(0, firefoxPercentage.indexOf("%"))) / 100;
		// Get the rounded ieMaxCount, if ieMaxCount<1, plus 1
		int ieMaxCount = Math.round(iePercent * testCaseCount);
		if (ieMaxCount < 1) {
			ieMaxCount = 1;
		}
		// Get the rounded firefoxMaxCount, if ieMaxCount<1, plus 1
		int firefoxMaxCount = Math.round(firefoxPercent * testCaseCount);
		if (firefoxMaxCount < 1) {
			firefoxMaxCount = 1;
		}
		// Get the chromeMaxCount by math
		int chromeMaxCount = testCaseCount - ieMaxCount - firefoxMaxCount;
		
		// Get the chromeMaxCount by math
		int edgeMaxCount = testCaseCount - ieMaxCount - firefoxMaxCount - chromeMaxCount;
				
		// set up the browser by the specified percentage
		if (ieCountCurrent < ieMaxCount) {
			setUpIEWin64(onGrid);
			browserFlag = "ie";
			ieCountCurrent++;
		} else if (ieCountCurrent == ieMaxCount && firefoxCountCurrent < firefoxMaxCount) {
			setUpFirefoxWithDefaultProfile(onGrid);
			browserFlag = "firefox";
			firefoxCountCurrent++;
		} else if (ieCountCurrent == ieMaxCount && firefoxCountCurrent == firefoxMaxCount
				&& chromeCountCurrent < chromeMaxCount) {
			setUpChromeWin32(onGrid);
			browserFlag = "chrome";
			chromeCountCurrent++;
		} else if (ieCountCurrent == ieMaxCount && firefoxCountCurrent == firefoxMaxCount
				&& chromeCountCurrent == chromeMaxCount && edgeCountCurrent < edgeMaxCount) {
			setUpEdgeWin32(onGrid);
			browserFlag = "edge";
			edgeCountCurrent++;
		} else {
			throw new Exception("The current ieCount:" + ieCountCurrent + ", firefoxCount:" + firefoxCountCurrent
					+ "and chromeCount: " + chromeCountCurrent + "and edgeCount: " + edgeCountCurrent + " doesn't fit the conditions");
		}
		log.info("ie Count Current=" + ieCountCurrent);
		log.info("ie Count Max=" + ieMaxCount);
		log.info("firefox Count Current=" + firefoxCountCurrent);
		log.info("firefox Count Max=" + firefoxMaxCount);
		log.info("googlechrome Count Current=" + chromeCountCurrent);
		log.info("googlechrome Count Max=" + chromeMaxCount);
		log.info("edge Count Current=" + edgeCountCurrent);
		log.info("edge Count Max=" + edgeMaxCount);
	}

	/**
	 * Objective: Close the opened browser which was opened by WebDriver
	 */
	@AfterClass(alwaysRun = true)
	public void tearDown(ITestContext context) throws Exception {
		String[] property = System.getProperty("user.dir").split("\\\\");
		String projectName = property[property.length - 1];
		String testCaseName = className.substring(className.lastIndexOf(".") + 1);
		Set<ITestResult> result = context.getFailedTests().getAllResults();
		if (result.isEmpty()) {
			actualResult = "PASS";
		} else {
			actualResult = "FAIL";
		}
		log.info(buildNumber + " " + projectName + " " + userStoryName + " " + testCaseName + " " + browserFlag + " "
				+ actualResult);
		// Thread.sleep(4000);
		driver_original.quit();
		// if (OS.isFamilyWindows())
		// Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");

	}

	private void setUpFirefoxWithDefaultProfile(String onGrid) throws Exception {
		FirefoxOptions options = new FirefoxOptions();
		if (onGrid.equals("false")) {
			driver_original = new FirefoxDriver(options);
		} else {
			driver_original = new RemoteWebDriver(new URL("http://" + host + ":" + port + "/wd/hub"), options);
		}

	}
	
	private void setUpFirefoxHeadless(String onGrid) throws Exception {
		FirefoxOptions options = new FirefoxOptions();
		options.addArguments("--headless");
		if (onGrid.equals("false")) {
			options.addArguments("start-maximized"); 
			driver_original = new FirefoxDriver(options);
		} else {
			  driver_original = new RemoteWebDriver(new URL("http://" + host + ":" + port + "/wd/hub"), options);
		}
	}

	private void setUpSafari(String onGrid) throws Exception {
		SafariOptions options = new SafariOptions();
		if (onGrid.equals("false")) {
			driver_original = new SafariDriver(options);
		} else {
			driver_original = new RemoteWebDriver(new URL("http://" + host + ":" + port + "/wd/hub"), options);
		}
	}

	private void setUpIEWin64(String onGrid) throws Exception {
		InternetExplorerOptions options = new InternetExplorerOptions();
		if (onGrid.equals("false")) {
			driver_original = new InternetExplorerDriver(options);
		} else {
			driver_original = new RemoteWebDriver(new URL("http://" + host + ":" + port + "/wd/hub"), options);
		}
	}
	

	private void setUpEdgeWin32(String onGrid) throws Exception {
		EdgeOptions options = new EdgeOptions();
		if (onGrid.equals("false")) {
			options.setAcceptInsecureCerts(true);
			driver_original = new EdgeDriver(options);
		} else {
			driver_original = new RemoteWebDriver(new URL("http://" + host + ":" + port + "/wd/hub"), options);
		}

	}
	
	
	private void setUpEdgeHeadless(String onGrid) throws Exception {
		EdgeOptions options = new EdgeOptions();
		options.addArguments("--headless"); 
		if (onGrid.equals("false")) {
			options.setAcceptInsecureCerts(true);
			options.addArguments("start-maximized"); 
			driver_original = new EdgeDriver(options);
		} else {
			driver_original = new RemoteWebDriver(new URL("http://" + host + ":" + port + "/wd/hub"), options);
		}
	}
	
	
	private void setUpChromeWin32(String onGrid) throws Exception {

		ChromeOptions options = new ChromeOptions();
		
		if (onGrid.equals("false")) {
			options.setAcceptInsecureCerts(true);
			driver_original = new ChromeDriver(options);
		} else {
			driver_original = new RemoteWebDriver(new URL("http://" + host + ":" + port + "/wd/hub"), options);
		}
	}
	
	
	private void setUpChromeHeadless(String onGrid) throws Exception{
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		if (onGrid.equals("false")) {
			options.addArguments("start-maximized"); 
			options.setAcceptInsecureCerts(true);
			driver_original = new ChromeDriver(options);
		} 
		else {
			driver_original = new RemoteWebDriver(new URL("http://" + host + ":" + port + "/wd/hub"), options);
		}
	}
	
	
	private void setMobileByCBT() throws MalformedURLException {
		String URL = "https://us-west-desktop-hub.bitbar.com/wd/hub";  // for mobile url is https://us-west-desktop-hub.bitbar.com/wd/hub
		
		DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platform", "macOS");
        capabilities.setCapability("osVersion", "13");
        capabilities.setCapability("browserName", "safari");
        capabilities.setCapability("version", "16");
        capabilities.setCapability("resolution", "2560x1920");
        capabilities.setCapability("bitbar_apiKey", "<insert your BitBar API key here>");
        capabilities.setCapability("bitbar_project", "DemoProject");
        capabilities.setCapability("bitbar_testrun", "test");
        
        driver_original = new RemoteWebDriver(new URL(URL), capabilities);
		
	}
	
	private void setMobileByBrowserStack() throws MalformedURLException {
		String USERNAME = "";// Your username
		String AUTOMATE_KEY = "";// Your authkey
		String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";
		
		// Created object of DesiredCapabilities
		MutableCapabilities caps = new MutableCapabilities();
		HashMap<String, Object> browserstackOptions = new HashMap<String, Object>();
		browserstackOptions.put("osVersion", "16");
		browserstackOptions.put("deviceName", "iPhone 14");
		browserstackOptions.put("projectName", "Browser Stack Example");
		browserstackOptions.put("buildName", "v2");
		browserstackOptions.put("sessionName", "Browser Stack Sample Test");
		browserstackOptions.put("local", "false");
		caps.setCapability("bstack:options", browserstackOptions);
		
		//Run scripts on BS with Local VPN Connection
		/*DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "latest");
        HashMap<String, Object> browserstackOptions = new HashMap<String, Object>();
        browserstackOptions.put("os", "OS X");
        browserstackOptions.put("osVersion", "Sierra");
        browserstackOptions.put("local", "true");
        browserstackOptions.put("forcelocal", "true");
        browserstackOptions.put("seleniumVersion", "4.0.0");
        capabilities.setCapability("bstack:options", browserstackOptions);

        //Creates an instance of Local
        Local bsLocal = new Local();

        // You can also set an environment variable - "BROWSERSTACK_ACCESS_KEY".
        HashMap<String, String> bsLocalArgs = new HashMap<String, String>();
        bsLocalArgs.put("key", AUTOMATE_KEY);

        // Starts the Local instance with the required arguments
        try {
			bsLocal.start(bsLocalArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}

        // Check if BrowserStack local instance is running
        try {
			System.out.println(bsLocal.isRunning());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		// run bsLocal.stop(); in AfterClass steps
		*/
		
		driver_original = new RemoteWebDriver(new URL(URL), caps);
	}
	
	private void setUpAppium() throws MalformedURLException, InterruptedException {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("BROWSER_NAME", "Android");
		capabilities.setCapability("VERSION", "1.0.41"); 
		capabilities.setCapability("deviceName","37d5e2e1");
		capabilities.setCapability("platformName","Android");
		// This package name of your app
		capabilities.setCapability("appPackage", "com.android.calculator2");
		// This is Launcher activity of your app
		capabilities.setCapability("appActivity","com.android.calculator2.Calculator");
		Thread.sleep(300);
		driver_original = new RemoteWebDriver(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);	
		Thread.sleep(3000);		
	}
	
	/**
	 * Objective: set the default test data file name 1.get the current test
	 * case class name and use this name to get a test data file name. For
	 * example, a class "TC01BingWebSearch_High" will get a testdata file name
	 * "testdata_TC_01_BingWebSearch" 2.load this properties file
	 */
	private void setDefaultTestData() {
		String s = this.getClass().getName();
		String filename = ("testdata_" + s.split("\\.")[s.split("\\.").length - 1] + ".properties");
		log.debug("Setting TestData file = " + filename);
		TestData.load(filename);
	}
	
	//Remove @SuppressWarning("unused") if you wish to use the zipAndMail() method
	@SuppressWarnings("unused")
	private void zipAndMail() throws ZipException
	{
		//Convert the report to zip.
		ZipAndMail.convertToZip();
		mailIds = splitMailAddress(mailAddress);
		//Send the zip file of report in email. 
		for (String clientEmail : mailIds) {
			clientEmail.trim();
			ZipAndMail.sendMail(clientEmail);
		}
	}
	
	protected WebDriver getDriver() {
		return driver_original;
	}
	
	public WebDriver getRemoteDriver() {
			return driver_original;
	}
	
	public String getElasticLocation() {
		return elasticLocation;
	}

	public String getProjectCode() {
		return projectCode;
	}
	
	private String[] splitMailAddress(String mailAddress){
		mailIds = mailAddress.split(",");
		return mailIds;
	}
}