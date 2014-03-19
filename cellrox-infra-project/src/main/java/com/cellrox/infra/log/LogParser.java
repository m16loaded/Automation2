package com.cellrox.infra.log;

import java.io.File;
import java.io.IOException;

import jsystem.extensions.analyzers.text.CountText;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Reporter.ReportAttribute;
import jsystem.framework.scenario.SystemObjectOperation;
import jsystem.utils.FileUtils;

import com.cellrox.infra.enums.Color;
import com.cellrox.infra.object.LogParserExpression;

public class LogParser extends SystemObjectOperation {

	LogParserExpression[] expressions;

	public LogParser(LogParserExpression[] expressions) {
		super();
		this.expressions = expressions;
	}

	File[] logs;

	public void validateLogs() throws IOException {
		for (File logFile : logs) {
			String log = FileUtils.read(logFile);
			for (LogParserExpression expression : expressions) {
				CountText countText = new CountText(expression.getExpression(), 0, 0, true);
				countText.setTestAgainst(log);
				countText.analyze();
				int result = countText.getActualCount();
				if (result > 0) {
					expression.setColor(Color.RED);
					log = log.replace(expression.getExpression(), "<b><font size=\"4\" "+expression.getHtmlColor()+">" + expression.getExpression()
							+ "</font></b>");
					report.report(
							"found error " + expression.getNiceName() + " in "
									+ logFile.getName(), Reporter.FAIL);
				}
			}
			// print log
			log = log.replace("\n", "<br>");
			report.report("Click Here to See Results " + logFile.getName(),
					log, ReportAttribute.HTML);
		}
	}

	public File[] getLogs() {
		return logs;
	}

	public void setLogs(File... logs) {
		this.logs = logs;
	}

}
