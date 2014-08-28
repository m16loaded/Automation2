package com.cellrox.infra;

import java.io.IOException;

import org.springframework.context.support.StaticApplicationContext;

import jsystem.extensions.report.html.Report;
import jsystem.framework.RunProperties;
import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Summary;
import jsystem.framework.report.Reporter.ReportAttribute;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.sut.SutFactory;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

import com.cellrox.infra.enums.Color;
import com.cellrox.infra.log.LogParser;
import com.sun.swing.internal.plaf.basic.resources.basic;

public class CellroxTestListenr implements ExtendTestListener {

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
				logParser.addExpression(Color.RED, "\\bBUG\\b", "Bug", "logcat", "logcat-radio", "kmsg");
				logParser.addExpression(Color.RED, "panic", "Panic!", "logcat", "logcat-radio", "kmsg");
				// Verify for the following only in kmsg and not in logcat
				logParser.addExpression(Color.RED, "\\bWARNING\\b", "Warning", "kmsg");
				// ADD HERE MORE EXPRESSION IF NEEDED
				CellRoxDevice cellRoxDevice = new CellRoxDevice(primaryDeviceId, user, password);
				cellRoxDevice.getLogs(logParser);
				lastTestScenarioAsTest = false;
				ListenerstManager.getInstance().report("NEW CODE3");

				validateCrashes();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void validateCrashes() throws Exception {
		// report persona / device crash
		if (RunProperties.getInstance().getRunProperty("deviceCrash") != null) {
			if (RunProperties.getInstance().getRunProperty("deviceCrash").equals("1")) {
				ListenerstManager.getInstance().report("Device Crash has been detected in this scenario!", Reporter.FAIL);
				// add the scenraio name to summary report which is later parsed
				// to the "manager report"
				String deviceCrashes = (String) Summary.getInstance().getProperty("deviceCrash");
				if (deviceCrashes != null) {
					deviceCrashes += deviceCrashes + ";" + scenarioAsTestName;
				} else {
					deviceCrashes = scenarioAsTestName;
				}
				Summary.getInstance().setProperty("deviceCrash", deviceCrashes);
				// and reset back to 0 for the next crash (tfu tfu tfu)
				RunProperties.getInstance().setRunProperty("deviceCrash", "0");
			}
		}
		if (RunProperties.getInstance().getRunProperty("personaCrash") != null) {
			if (RunProperties.getInstance().getRunProperty("personaCrash").equals("1")) {
				ListenerstManager.getInstance().report("Persona Crash has been detected in this scenario!", Reporter.FAIL);
				// add the scenraio name to summary report which is later parsed to
				// the "manager report"
				String personaCrashes = (String) Summary.getInstance().getProperty("personaCrash");
				if (personaCrashes != null) {
					personaCrashes += personaCrashes + ";" + scenarioAsTestName;
				} else {
					personaCrashes = scenarioAsTestName;
				}
				Summary.getInstance().setProperty("personaCrash", personaCrashes);
				// and reset back to 0 for the next crash (tfu tfu tfu)
				RunProperties.getInstance().setRunProperty("personaCrash", "0");
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
				CellRoxDevice cellRoxDevice = new CellRoxDevice(primaryDeviceId, user, password);
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
		// TODO Auto-generated method stub

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

	}

	@Override
	public void endContainer(JTestContainer container) {

	}

}
