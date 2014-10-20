package com.cellrox.infra;

import java.io.IOException;

import jsystem.framework.RunProperties;
import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Summary;
import jsystem.framework.report.Reporter.ReportAttribute;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.ScenarioAsTest;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.sut.SutFactory;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

import com.cellrox.infra.enums.Color;
import com.cellrox.infra.log.LogParser;

public class CellroxTestListenr implements ExtendTestListener {

	String execution = "";
	boolean deviceCrashOnScenario = false;
	boolean personaCrashOnScenario = false;
	boolean fatalExceptionOnScenario = false;
	CellRoxDevice cellRoxDevice;
	
	public CellroxTestListenr() {
		// get the device properties
		try {
			primaryDeviceId = SutFactory.getInstance().getSutInstance().getValue("//primary");
			user = SutFactory.getInstance().getSutInstance().getValue("//user");
			password = SutFactory.getInstance().getSutInstance().getValue("//password");
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	String primaryDeviceId;
	String user;
	String password;
	boolean lastTestScenarioAsTest = false;
	private CellRoxDeviceManager devicesMannager;
	private String scenarioAsTestName;

	@Override
	public void addError(Test test, Throwable t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFailure(Test test, AssertionFailedError t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endTest(Test test) {
		if (lastTestScenarioAsTest) {
			try {
				ListenerstManager.getInstance().report("******************** SCENARIO MARKED AS TEST LOG RESULTS: ********************", ReportAttribute.BOLD);
				// init log parser
 				LogParser logParser = new LogParser();
				// adding the following for *all* logs
				logParser.addExpression(Color.RED, "\\bBUG\\b", "Bug", "testLogcat", "testRadioLogcat", "testKmsg");
				logParser.addExpression(Color.RED, "panic", "Panic!", "testLogcat", "testRadioLogcat", "testKmsg");
				// Verify for the following only in kmsg and not in logcat
				logParser.addExpression(Color.RED, "\\bWARNING\\b", "Warning", "testKmsg");
				
				// Verify logcat
				logParser.addExpression(Color.RED, "WATCHDOG KILLING SYSTEM PROCESS", "Watchdog", "testLogcat");
				logParser.addExpression(Color.RED, "FATAL EXCEPTION", "fatal exception", "testLogcat");
//				logParser.addExpression(Color.RED, "camera", "camera", "testLogcat");

				// ADD HERE MORE EXPRESSION IF NEEDED
//				CellRoxDevice cellRoxDevice = new CellRoxDevice(primaryDeviceId, user, password);
				cellRoxDevice.getLogs(logParser);
				lastTestScenarioAsTest = false;
				ListenerstManager.getInstance().report("NEW CODE8");
				validateCrashes();
				//report to log
				//reportToLogStash((ScenarioAsTest) test);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String reportToLogStash(ScenarioAsTest test){
		//logstash format: <version> <build> <status> <testName> <duration> <personaCrash> <deviceCrash> <FatalException> 
		StringBuilder log = new StringBuilder();
		log.append("hammerhead ");
		log.append("182 ");
		log.append(test.isPass());
		log.append(scenarioAsTestName);
		log.append(" 150 ");
		
		return log.toString();
		
	}

	private void validateCrashes() throws Exception {
		// report persona / device crash
		reportToSummary("deviceCrash",Reporter.FAIL);
		reportToSummary("personaCrash",Reporter.FAIL);
		//validate log errors coming from logParser
		for (Object key : RunProperties.getInstance().getRunProperties().keySet()){
			if (key.toString().contains("error")){
				reportToSummary(key.toString(),Reporter.WARNING);
			}
		}
	}

	private void reportToSummary(String property,int status) throws IOException, Exception {
		if (RunProperties.getInstance().getRunProperty(property) != null) {
			if (RunProperties.getInstance().getRunProperty(property).equals("1")) {
				ListenerstManager.getInstance().report(property+" has been detected in this scenario!", status);
				// add the scenario name to summary report which is later parsed to
				// the "manager report"
				String runProperty = (String) Summary.getInstance().getProperty(property);
				if (runProperty != null) {
					runProperty = runProperty + ";" + scenarioAsTestName;
				} else {
					runProperty = scenarioAsTestName;
				}
				Summary.getInstance().setProperty(property, runProperty);
				// and reset back to 0 for the next crash (tfu tfu tfu)
				RunProperties.getInstance().setRunProperty(property, "0");
			}
		}
	}

	@Override
	public void startTest(Test test) {

	}

	@Override
	public void addWarning(Test test) {

	}

	@Override
	public void startTest(TestInfo testInfo) {
		// if basicName is not null this is a test that is marked as scenario
		if (testInfo.basicName != null) {
			try {
//				CellRoxDevice cellRoxDevice = new CellRoxDevice(primaryDeviceId, user, password);
				cellRoxDevice.initLogs();
				scenarioAsTestName = testInfo.basicName;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lastTestScenarioAsTest = true;
		}

	}

	@Override
	public void endRun() {
		System.out.println("************************** THIS IS THE END OF THE WHOLE RUN **************************");

	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startContainer(JTestContainer container) {
		try {
			cellRoxDevice = new CellRoxDevice(primaryDeviceId, user, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("************************** THIS IS THE START OF THE WHOLE SCENARIO **************************");
	}

	@Override
	public void endContainer(JTestContainer container) {
		System.out.println("************************** THIS IS THE END OF THE WHOLE SCENARIO **************************");
	}

}
