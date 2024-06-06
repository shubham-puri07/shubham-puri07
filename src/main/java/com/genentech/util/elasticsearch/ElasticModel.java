package com.genentech.util.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ElasticModel {
    @JsonProperty("SCRIPT")
    private String scriptName;
    @JsonProperty("STEP")
    private String testClass;
    @JsonProperty("SCENARIO")
    private String description;
    @JsonProperty("STATUSES")
    private String status;
    @JsonProperty("RUNTIME")
    private long executionTime;
    @JsonProperty("PAGE")
    private String page;
    @JsonProperty("PROJECT")
    private String project;
    @JsonProperty("TIMESTAMP")
    private String timestamp;
    @JsonProperty("MESSAGES")
    private String messages;
    @JsonProperty("TESTCASE")
    private String suiteName;
    @JsonProperty("DEVICE")
    private String device;
    @JsonProperty("BROWSER")
    private String browser;
    @JsonProperty("TESTSUITE")
    private String testsuite;
    @JsonProperty("ENVIRONMENT")
    private String environment;
    @JsonProperty("TESTCASERESULT")
    private String suiteResult;
   

    public void setDescription(String description) {
        this.description = description;
    }
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setTestClass(String testClass) {
        this.testClass = testClass;
    }
    public void setProject(String project) {
        this.project = project;
    }
    public void setPage(String page) {
        this.page = page;
    }
    public void setTimestamp(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        this.timestamp = df.format(new Date());
    }
    public void setDevice(String device) {
        this.device = device;
    }
    public void setBrowser(String browser) {
        this.browser = browser;
    }
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }
    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }
    public void setMessage(String messages) {
    	 this.messages = messages;
    }
    public void setTestSuite(String testsuite) {
   	 this.testsuite = testsuite;
   }
   public void setEnvironment(String environment) {
        this.environment = environment;
   }
   public void setSuiteResult(String suiteResult) {
       this.suiteResult = suiteResult;
   }
    
	@Override
	public String toString() {
		return "ElasticModel [scriptName=" + scriptName + ", testClass=" + testClass + ", description=" + description
				+ ", status=" + status + ", executionTime=" + executionTime + ", page=" + page + ", project=" + project
				+ ", brand=" + ", timestamp=" + timestamp
				+ ", messages=" + messages + ", suiteName=" + suiteName + ", device=" + device + ", browser=" + browser
				+ ", testsuite=" + testsuite + ", environment=" + environment +", suiteResult=" + suiteResult+ " ]";
	}
    
    
    
}