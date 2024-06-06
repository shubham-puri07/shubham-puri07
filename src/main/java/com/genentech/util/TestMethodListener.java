package com.genentech.util;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestClass;
import org.testng.ITestResult;
import org.testng.asserts.SoftAssert;



public class TestMethodListener implements IInvokedMethodListener {

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

		ITestClass invokingClass = method.getTestMethod().getTestClass();
		Object[] classInstance = invokingClass.getInstances(true);
		
		if (method.isTestMethod() && classInstance[0] instanceof TestCaseBase) {
			TestCaseBase testCase = (TestCaseBase) classInstance[0];
			SoftAssert softAssert = testCase.customAssertion.getSoftAssert();
			try {
				softAssert.assertAll();
			} catch (AssertionError e) {
				testResult.setStatus(ITestResult.FAILURE);
				testResult.setThrowable(e);

			}
		} 
	}

}
