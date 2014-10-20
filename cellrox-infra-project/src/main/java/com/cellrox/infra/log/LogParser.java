package com.cellrox.infra.log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.login.LoginException;

import jsystem.extensions.analyzers.text.CountText;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Reporter.ReportAttribute;
import jsystem.framework.scenario.SystemObjectOperation;
import jsystem.utils.FileUtils;

import com.cellrox.infra.CellroxTestListenr;
import com.cellrox.infra.enums.Color;
import com.cellrox.infra.object.LogParserExpression;

public class LogParser extends SystemObjectOperation {

	LogParserExpression[] expressions;
	HashMap<String, ArrayList<LogParserExpression>> tmpExpressions;
	HashMap<String, File> logs;

	// File[] logs;

	/**
	 * This C'tor should only be use from GUI
	 * 
	 * @param expressions
	 */
	public LogParser(LogParserExpression[] expressions) {
		super();
		tmpExpressions = new HashMap<String, ArrayList<LogParserExpression>>();
		logs = new HashMap<String, File>();
		// for each expression get the relevant logs and add it
		for (LogParserExpression expression : expressions){
			if (expression.getRelvantLogs()==null){
				continue;
			}
			String[] logNames = expression.getRelvantLogs().split(",");
			addExpression(expression, logNames);
		}
		
		this.expressions = expressions;
	}

	public LogParser() {
		super();
		tmpExpressions = new HashMap<String, ArrayList<LogParserExpression>>();
		logs = new HashMap<String, File>();
	}

	/**
	 * This function will add the requested regular expression to find to the
	 * relvanet log(s)
	 * 
	 * @param color
	 *            - the color of the text, if found, in the report
	 * @param expression
	 *            - regex to find (i.e. \d*Warning\s*\w+)
	 * @param niceName
	 *            - nice name to show in the report i.e. Warning, Error, Crash
	 *            etc.
	 * @param logNames
	 *            - the logs name which are relevant to this regex
	 */
	public void addExpression(Color color, String expression, String niceName, String... logNames) {
		LogParserExpression logParserExpression = new LogParserExpression();
		logParserExpression.setColor(color);
		logParserExpression.setExpression(expression);
		logParserExpression.setNiceName(niceName);
		// for each requested log file add the given regex
		for (String logName : logNames) {
			// validate if log file name is exists in the HashMap - if not add
			// it first
			if (!tmpExpressions.keySet().contains(logName)) {
				tmpExpressions.put(logName, new ArrayList<LogParserExpression>()); //
			}
			tmpExpressions.get(logName).add(logParserExpression);
		}
	}
	
	private void addExpression(LogParserExpression expression, String... logNames) {
		addExpression(expression.getColor(),expression.getExpression(),expression.getNiceName(), logNames);
	}

	public void validateLogs() throws Exception {
	
		// for each log name that we are interested in:
		for (String logName : tmpExpressions.keySet()) {
			if (!logs.keySet().contains(logName)){
				continue;
			}
			// get the log into an object
			String log = FileUtils.read(logs.get(logName));
			// for each regex
			for (LogParserExpression expression : tmpExpressions.get(logName)) {
				CountText countText = new CountText(expression.getExpression(), 0, 0, true);
				countText.setTestAgainst(log);
				countText.analyze();
				int result = countText.getActualCount();
				if (result > 0) {
					// expression.setColor(Color.RED);
					log = log.replace(expression.getExpression(), "<b><font size=\"4\" " + expression.getHtmlColor() + ">" + expression.getExpression()
							+ "</font></b>");
					// CHANGE HERE IF YOU'D  LIKE OTHER REPORT LEVEL I.E WRANING , BOLD, PASS
					report.report("found error " + expression.getNiceName() + " in " + logName, Reporter.WARNING);
					reportToRunProperty(expression.getNiceName());
				}
			}
			// print log
			log = log.replace("\n", "<br>");
			report.report("Click Here to See Results " + logName, log, ReportAttribute.HTML);
		}
	}
	
	/**
	 * this function will report to runProperty that an error was found 
	 * the {@link CellroxTestListenr} will later parse the errors and add them to summary report
	 * @param niceName
	 * @throws Exception
	 */
	public void reportToRunProperty(String niceName) throws Exception {
		runProperties.setRunProperty(niceName+" error", "1");
	}

	public void addLogFile(String logName, File logcat) throws Exception {
		logs.put(logName, logcat);
	}

	public HashMap<String, File> getLogs() {
		return logs;
	}

	public void setLogs(HashMap<String, File> logs) {
		this.logs = logs;
	}

}
