package com.cellrox.infra.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.analyzer.AnalyzerException;

public class LogMessageGetter {

	private String[] currentLog;
	private ArrayList<String> regexToFind;

	public LogMessageGetter() {
		super();

		// init the regex array
		regexToFind = new ArrayList<String>();
	}

	public void addRegexToFind(String regex) throws Exception {
		regexToFind.add(regex);
	}

	public ArrayList<String> analyze() throws Exception {
		if (regexToFind.isEmpty()) {
			throw new AnalyzerException("There are no regular expression(s) to find");
		}
		if (currentLog.length == 0) {
			throw new AnalyzerException("Current log is empty");
		}
		ArrayList<String> results = new ArrayList<String>();
		// for each regex
		for (String regex : regexToFind) {
			Pattern p = Pattern.compile(regex);
			for (String line : currentLog) {
				Matcher matcher = p.matcher(line);
				//find in line
				while (matcher.find()) {
					results.add(matcher.group());
				}
			}
		}

		return results;
	}

	public void setCurrentLog(int startingLine, String currentLog) throws Exception {
		// current log is the whole log we'll need only the log from the marked
		// point
		String[] currentLogAsArray = currentLog.split("\\n");
		// validate there are lines in log from mark
		if (startingLine > currentLogAsArray.length) {
			throw new Exception("There are no new lines in log since markdown");
			//	throw new AnalyzerException("There are no new lines in log since markdown"); TODO ask Nir why AnalyzerException - was not shown in report with message 
		}
		// copy the log from starting line to the end of the log and convert it
		// back to string
		this.currentLog = Arrays.copyOfRange(currentLogAsArray, startingLine, currentLogAsArray.length);
	}

	public String[] getCurrentLog() {
		return currentLog;
	}

}