package com.cellrox.infra;

import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter.ReportAttribute;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.sut.SutFactory;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

import com.cellrox.infra.enums.Color;
import com.cellrox.infra.log.LogParser;

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
				logParser.addExpression(Color.RED, "\\bBUG\\b", "Bug");
				
				// Verify for the following only in dmesg and not in logcat
//				logParser.addExpression(Color.RED, "\\bWARNING\\b", "Warning");
				logParser.addExpression(Color.RED, "panic", "Panic!");
				//ADD HERE MORE EXPRESSION IF NEEDED
				CellRoxDevice cellRoxDevice = new CellRoxDevice(primaryDeviceId, user, password);
				cellRoxDevice.getLogs(logParser);
				lastTestScenarioAsTest = false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
