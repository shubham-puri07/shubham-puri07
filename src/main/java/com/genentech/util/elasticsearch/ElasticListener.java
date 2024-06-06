package com.genentech.util.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genentech.util.TestCaseBase;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ElasticListener implements ITestListener {
	private ElasticModel elasticModel;
	public String message;
	public String result;
	public String testSuite;
	private Boolean isValidElasticTestRun = false;
	public int totalTestStep;
	public int currentTestStep=0;
	public int failedTestCount=0;

	private final Logger log = LoggerFactory.getLogger(ElasticListener.class);

	@Override
	public void onTestStart(ITestResult iTestResult) {
		if (isValidElasticTestRun) {
			log.info("Preparing model for ElasticSearch");
			this.elasticModel = new ElasticModel();

			log.info("Preparing model ready for ElasticSearch");
			currentTestStep++;
		}
	}

	@Override
	public void onTestSuccess(ITestResult iTestResult) {
		Object currentClass = iTestResult.getInstance();
		this.sendStatus(iTestResult, "PASSED", currentClass);
	}

	@Override
	public void onTestFailure(ITestResult iTestResult) {
		Object currentClass = iTestResult.getInstance();
		this.sendStatus(iTestResult, "FAILED", currentClass);
	}

	@Override
	public void onTestSkipped(ITestResult iTestResult) {
		Object currentClass = iTestResult.getInstance();
		this.sendStatus(iTestResult, "SKIPPED", currentClass);
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
	}

	@Override
	public void onStart(ITestContext iTestContext) {
			isValidElasticTestRun = true;
			testSuitName(iTestContext);
		totalTestStep = iTestContext.getAllTestMethods().length;
		
		
	}

	@Override
	public void onFinish(ITestContext iTestContext) {

	}

	public void testSuitName(ITestContext iTestContext) {
		List<String> groupsName = iTestContext.getCurrentXmlTest().getIncludedGroups();
		if (groupsName.isEmpty() || groupsName.get(0).equals("regressions")) {
			testSuite = "Regression";
		} else {
			testSuite = groupsName.get(0);
		}
	}

	private void sendStatus(ITestResult iTestResult, String status, Object currentClass) {
		if (isValidElasticTestRun) {
			
			TestCaseBase testBase = ((TestCaseBase) currentClass);
			WebDriver webDriver = testBase.getRemoteDriver();
			final ObjectMapper OM = new ObjectMapper();

			// this is the STEP key
			this.elasticModel.setTestClass(iTestResult.getName());

			// this sets the SCENARIO key
			this.elasticModel.setDescription(iTestResult.getMethod().getDescription());

			// this sets the STATUSES key
			this.elasticModel.setStatus(status);

			// this sets the RUNTIME key
			this.elasticModel.setExecutionTime(iTestResult.getEndMillis() - iTestResult.getStartMillis());

			// this sets the PAGE key
			this.elasticModel.setPage(webDriver.getCurrentUrl());

			// this sets the Messages
			if ("FAILED".equals(status)) {
				result = iTestResult.getThrowable().toString();
				if (result.contains("java.lang")) {
					message = result;
				} else if (result.contains("org.openqa.selenium")) {
					message = result.substring(result.indexOf("org."), result.indexOf("(Session"));
				} else {
					message = "WebDriver Exception Occurs";
				}
			} else if ("PASSED".equals(status)) {
				message = "Test passed succesfully!";
			} else if ("SKIPPED".equals(status)) {
				message = "Test Skipped!";
			}
			this.elasticModel.setMessage(message);

			// this sets the BRAND key
			URL testURL = null;
			try {
				testURL = new URL(webDriver.getCurrentUrl());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			// this sets the PROJECT key
			this.elasticModel.setProject(testBase.getProjectCode());

			// this sets the ENVIRONMENT key
			this.elasticModel.setEnvironment(testBase.environment);

			// this sets the TestSuite
			this.elasticModel.setTestSuite(testSuite.substring(0, 1).toUpperCase() + testSuite.substring(1));

			// this sets the DEVICE key
			// this.testStatus.setDevice(testBase.getDevice());

			this.elasticModel.setBrowser(getBrowserName(webDriver));

			this.elasticModel.setTimestamp();

			String currentClassName = iTestResult.getInstanceName();
			
			if("FAILED".equals(status)) {
				failedTestCount++;
			}
			
			if(currentTestStep==totalTestStep) {
				if("FAILED".equals(status) || failedTestCount!=0) {
					this.elasticModel.setSuiteResult("FAILED");
				}else {
					this.elasticModel.setSuiteResult("PASSED");
				}
			}

			this.elasticModel.setScriptName(currentClassName);
			this.elasticModel.setSuiteName(currentClassName.substring(currentClassName.lastIndexOf(".") + 1));

			log.info("Sending Elasticsearch document {}", this.elasticModel);

			try {
				ElasticSender.sendStringAsJson(testBase.getElasticLocation(), OM.writeValueAsString(this.elasticModel));
			} catch (IOException e) {
				log.error("Error POSTing to ELK {}", e.getMessage());
			}
		}
	}

	protected String getBrowserName(WebDriver webDriver) {
		Capabilities cap = ((RemoteWebDriver) webDriver).getCapabilities();
		return cap.getBrowserName().toUpperCase();
	}
}
