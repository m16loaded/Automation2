package com.cellrox.infra;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StaticUtils {
	
	public static String scenarioAsTestName;
	public static boolean personaCrash = false;
	public static boolean deviceCrash = false;
	
	public static Date getTimeFromLogcat (String line,String timeFormat) throws ParseException{
		//logcat time format for example 09-08 14:15:47.125 "MM-dd hh:mm:ss.SSS"
		SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
		Date date = formatter.parse(line);
		return date;
	}


}
