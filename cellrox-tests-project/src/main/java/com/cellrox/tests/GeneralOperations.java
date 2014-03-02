package com.cellrox.tests;

import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase4;

import org.junit.Test;

import com.cellrox.infraReporter.JsystemReporter;


public class GeneralOperations extends SystemTestCase4 {
	
	private String workingDir = "/home/topq/main_jenkins/workspace/Automation_Nightly/cellrox-tests-project/"
			, locationForSavingHtml = "/home/topq/main_jenkins/workspace/Automation_Nightly/reports/managerReport.html"
			, to = "or.garfunkel@top-q.co.il"
			, directoryToSaveFullLog = "/home/topq/main_jenkins/workspace/Automation_Nightly/Logs"
			, jenkinsLogLocation = "http://build.vm.cellrox.com:8080/job/Automation_Nightly/ws/Logs/";
	
	/**
	* This function send email the next vars should be inserted
	* workingDir - working dir - from it the logs and the summary will be taken
	* locationForSavingHtml - nameOfReport - the place to save the .html name
	* to - String to -the wanted email to send to
	* directoryToSaveFullLog - the directory of the reports to copy to
	* jenkinsLogLocation- the working directory of the jenkins
	 * @throws Exception 
	*/
	@Test
	@TestProperties(name = "Send email full jsystem report" , paramsInclude = "workingDir,locationForSavingHtml,to,directoryToSaveFullLog,jenkinsLogLocation") 
	public void sendEmail() throws Exception {
		report.report("before!!!!!");
		String []args = new String[] {workingDir,locationForSavingHtml,to,directoryToSaveFullLog,jenkinsLogLocation};
		JsystemReporter.sendEmailFullReport(args);
		report.report("after!!!!!");
	}
	
	
	
	public String getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}

	public String getLocationForSavingHtml() {
		return locationForSavingHtml;
	}

	public void setLocationForSavingHtml(String locationForSavingHtml) {
		this.locationForSavingHtml = locationForSavingHtml;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getDirectoryToSaveFullLog() {
		return directoryToSaveFullLog;
	}

	public void setDirectoryToSaveFullLog(String directoryToSaveFullLog) {
		this.directoryToSaveFullLog = directoryToSaveFullLog;
	}

	public String getJenkinsLogLocation() {
		return jenkinsLogLocation;
	}

	public void setJenkinsLogLocation(String jenkinsLogLocation) {
		this.jenkinsLogLocation = jenkinsLogLocation;
	}


}
