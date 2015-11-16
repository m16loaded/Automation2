package com.cellrox.infraReporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JsystemReporter {

	final static String propName = "/home/topq/git/automation/config.properties";
	final static String propTimes = "/home/topq/git/automation/configTimes.properties";
	final static String from = "auto@cellrox.com";
	final static String password = "%SMd*6ya";

	/**
	 * The application takes the .xml and make from it .html table with the
	 * wanted fields This application will compare to the last run from the
	 * config file
	 * 
	 * @param args
	 *            - the first arg should be : arg[0] - working dir - from it the
	 *            logs and the summary will be taken arg[1] - nameOfReport - the
	 *            place to save the .html name arg[2] - String to -the wanted
	 *            email to send to args[3] - the directory of the reports to
	 *            copy to args[4] - the working directory of the jenkins
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// args = new String []
		// {"/home/topq/main_jenkins/workspace/Automation_Nightly/cellrox-tests-project/",
		// "/home/topq/main_jenkins/workspace/Automation_Nightly/reports/managerReport.html",
		// " or.garfunkel@top-q.co.il",
		// "/home/topq/main_jenkins/workspace/Automation_Nightly/Logs",
		// "http://build.vm.cellrox.com:8080/job/Automation_Nightly/ws/Logs/"};
		// args = new String [] {"/home/topq/dev/runner/",
		// "/home/topq/main_jenkins/workspace/Automation_Nightly/reports/managerReport.html",
		// "or.garfunkel@top-q.co.il,",
		// "/home/topq/main_jenkins/workspace/Automation_Nightly/Logs",
		// "http://build.vm.cellrox.com:8080/job/Automation_Nightly/ws/Logs/"};
		// args = new String []
		// {"/home/topq/main_jenkins/workspace/Flo_Automation/cellrox-tests-project/",
		// "/home/topq/main_jenkins/workspace/Flo_Automation/reports/managerReport.html",
		// "or.garfunkel@top-q.co.il",
		// "/home/topq/main_jenkins/workspace/Flo_Automation/Logs",
		// "http://build.vm.cellrox.com:8080/job/Flo_Automation/ws/Logs/"};

		sendEmailFullReport(args);
	}

	/**
	 * The application takes the .xml and make from it .html table with the
	 * wanted fields This application will compare to the last run from the
	 * config file
	 * 
	 * @param args
	 *            - the first arg should be : arg[0] - working dir - from it the
	 *            logs and the summary will be taken arg[1] - nameOfReport - the
	 *            place to save the .html name arg[2] - String to -the wanted
	 *            email to send to args[3] - the directory of the reports to
	 *            copy to args[4] - the working directory of the jenkins
	 * @throws Exception
	 */
	public static boolean sendEmailFullReport(String[] args) throws Exception {
		String urltoReporter = "http://build.vm.cellrox.com:8080/job/Automation_Nightly/HTML_Report/?";
		Map<String, String> testsStatusMap = new HashMap<String, String>();
		Map<String, String> testsTimesMap = new HashMap<String, String>();
		String doaCrash = null, deviceCrash = null, personaCrash = null, deviceCrashScnarioName = null, personaCrashScenarioName = null;
		String compareStatus = null, seconedColor = null, lastTime = null, vellamoResults = "",cmdComparisonOutput,cmdComparisonOutputBefore,cmdComparisonOutputAfter/*added by Igor 15115*/, corpBootTime = null, privBootTime = null;
		int pass = 0, fail = 0, total = 0, index = 0 , warning = 0;
		String version = null, nameOfReport = null, summaryLocation = null, newNameOfReport = null, currentLogLocation = null, startTime = null, endTime = null, hardware = null, imei = null, macAdr = null, duration = null;
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String currentDate = sdf.format(cal.getTime()).replace(" ", "_").replace(":", "_");

		StringBuilder testsTable = new StringBuilder();
		StringBuilder docHtmlString = new StringBuilder();

		// get the args[] parm, if they not inserted use the default
		if (args.length < 5) {
			throw new Exception("Less than 5 args.");
		} else {
			currentLogLocation = args[0] + "log/current/reports.0.xml";
			nameOfReport = args[1];
			summaryLocation = args[0] + "summary.properties";
		}

		newNameOfReport = nameOfReport.replace(".html", "_").concat(currentDate.replace("/", "_")).concat(".html");
		new File(newNameOfReport);
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(currentLogLocation));
			PrintWriter pw = new PrintWriter(new FileWriter(newNameOfReport));
			doc.getDocumentElement().normalize();

			// reports version variables read the vars
			NodeList nList = doc.getElementsByTagName("reports");

			Properties prop = new Properties();
			InputStream input = null;
			try {
				input = new FileInputStream(summaryLocation);
				prop.load(input);
			} catch (Exception e1) {
				// in case that this is the first run the file not exist -
				// return empty map
			}

			/**
			 * Don't get lost, here I'm beginning to get all the properties,
			 * from the properties file(summary file), The next step is to make
			 * a table with the wanted color that represents the status of the
			 * test and the time of it.
			 * */
			if (prop.getProperty("Build_display_id") != null) {
				version = prop.getProperty("Build_display_id").split("\n")[0].trim();
			}

			doaCrash = prop.getProperty("Doa_Crash");
			deviceCrash = prop.getProperty("Device_Crash");
			deviceCrashScnarioName = prop.getProperty("deviceCrash");
			personaCrash = prop.getProperty("Persona_Crash");
			personaCrashScenarioName = prop.getProperty("personaCrash");
			startTime = prop.getProperty("Start_Time");
			endTime = prop.getProperty("End_Time");
			duration = getDateDuration(startTime, endTime);
			hardware = prop.getProperty("hardware").split("\n")[0].trim();
			macAdr = prop.getProperty("Mac_address");
			imei = prop.getProperty("IMEI");
			vellamoResults = prop.getProperty("Vellamo_Results").replace("\\", " "); 
			cmdComparisonOutputBefore=prop.getProperty("Memory_Before").replace("\\", " ");/*added by Igor 15115*/
			cmdComparisonOutputAfter=prop.getProperty("Memory_After").replace("\\", " ");/*added by Igor 15115*/
			cmdComparisonOutput=prop.getProperty("Cmd_Comparison_Output").replace("\\", " ");/*added by Igor 15115*/
			corpBootTime = prop.getProperty("hostCorpDuration");
			privBootTime = prop.getProperty("hostPrivDuration");

			// get errors from log

			// noCon = prop.getProperty("No_Connection");

			// begin to create the html file
			nList = doc.getElementsByTagName("test");
			docHtmlString.append("<!DOCTYPE html><html><head><title>Automation report for build : " + version + "</title></head><body>").append(
					System.getProperty("line.separator"));
			docHtmlString.append("<h1><em>Automation Report - for build : " + version + "<em></h1>").append(System.getProperty("line.separator"));
			docHtmlString.append("<p>Start time : " + startTime + "</p>").append(System.getProperty("line.separator"));
			docHtmlString.append("<p>End time : " + endTime + "</p>").append(System.getProperty("line.separator"));
			docHtmlString.append("<p>Duration : " + duration + "</p>").append(System.getProperty("line.separator"));
			docHtmlString.append("<p>Hardware : " + hardware + "</p>").append(System.getProperty("line.separator"));
			if (hardware.equalsIgnoreCase("flo")) {
				if (macAdr != null) {
					if (!macAdr.isEmpty()) {
						docHtmlString.append("<p>Mac address : " + macAdr + "</p>").append(System.getProperty("line.separator"));
					}
				}
			} else {
				if (!imei.isEmpty()) {
					docHtmlString.append("<p>IMEI : " + imei + "</p>").append(System.getProperty("line.separator"));
				}
			}
			docHtmlString.append("<p>Vellamo Results : " + vellamoResults + "</p>").append(System.getProperty("line.separator"));
			if (doaCrash != null) {
				if (!doaCrash.trim().equals("0")) {
					docHtmlString.append("<p>DOA : yes</p>").append(System.getProperty("line.separator"));
				} else {
					docHtmlString.append("<p>DOA : no</p>").append(System.getProperty("line.separator"));
				}
			} else {
				docHtmlString.append("<p>DOA : no</p>").append(System.getProperty("line.separator"));
			}

			docHtmlString.append("<p>Device Crash count: " + deviceCrash + "</p>").append(System.getProperty("line.separator"));
			// if device crash was detected
			if (deviceCrashScnarioName != null) {
				if (!deviceCrashScnarioName.isEmpty()) {
					docHtmlString.append("<p>Device Crash on Scenarios: <ul><li>" + deviceCrashScnarioName.replace(";", "</li><li>") + "</li></ul></p>")
							.append(System.getProperty("line.separator"));
				}
			}

			docHtmlString.append("<p>Persona Crash count: " + personaCrash + "</p>").append(System.getProperty("line.separator"));
			// if persona crash was detected
			if (personaCrashScenarioName != null) {
				if (!personaCrashScenarioName.isEmpty()) {
					docHtmlString.append("<p>Persona Crash on Scenarios: <ul><li>" + personaCrashScenarioName.replace(";", "</li><li>") + "</li></ul></p>")
							.append(System.getProperty("line.separator"));
				}
			}
			// get errors from log - this is a dynamic key, we only know that
			// the key name contains "error"
			for (Object key : prop.keySet()) {
				if (key.toString().contains("error")) {
					docHtmlString.append("<p>" + key + " : <ul><li>" + prop.getProperty(key.toString()).replace(";", "</li><li>") + "</li></ul></p>").append(
							System.getProperty("line.separator"));
				}
			}
			// print priv boot time
			if (privBootTime != null) {
				docHtmlString.append("<p>Priv Boot Time: " + privBootTime + "sec. </p>").append(System.getProperty("line.separator"));
			}
			// print corp boot time
			if (corpBootTime != null) {
				docHtmlString.append("<p>Corp Decryption window Boot Time: " + corpBootTime + "sec. </p>").append(System.getProperty("line.separator"));
			}

			// docHtmlString.append("<p>No Connection number: "+noCon+"</p>").append(System.getProperty("line.separator"));

			testsTable.append("<p><b>Test report : </b></p>").append(System.getProperty("line.separator"));
			testsTable
					.append("<TABLE BORDER=1 BORDERCOLOR=BLACK width=\"100\"><TR><b><TH>Index<TH>Test name<TH>Current result<TH>Current duration<TH>Previous result<TH>Previous duration<b></TR>")
					.append(System.getProperty("line.separator"));

			Map<String, String> testsStatusMapOld = getMapFromConfigFile(propName);
			Map<String, String> testsTimeMapOld = getMapFromConfigFile(propTimes);
			// writing to the html file all the lines
			for (int i = 0; i < nList.getLength(); i++) {
				++total;
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					double time = 0.0;
					try {
						time = ((Long.valueOf(eElement.getAttribute("endTime")) - Long.valueOf(eElement.getAttribute("startTime"))));
					} catch (Exception e) {
					}
					String status = eElement.getAttribute("status");
					String name = eElement.getAttribute("name");
					String color = null;
					try {
						testsTimesMap.put(name + "Time",
								String.valueOf(((Long.valueOf(eElement.getAttribute("endTime")) - Long.valueOf(eElement.getAttribute("startTime"))))));
					} catch (Exception e) {
					}
					testsStatusMap.put(name, status);
					if (status.equals("false")) {
						color = "RED";
						++fail;
					} else if (status.equals("warning")) {
						color = "YELLOW";
						++warning;
					} else if (status.equals("true")) {
						color = "GREEN";
						++pass;
					}
					// The comparing to the last run
					lastTime = "0";
					if (testsStatusMapOld.containsKey(name)) {
						if (testsStatusMapOld.get(name).equals("true")) {
							compareStatus = testsStatusMapOld.get(name);
							seconedColor = "GREEN";
						} else if (testsStatusMapOld.get(name).equals("false")) {
							compareStatus = testsStatusMapOld.get(name);
							seconedColor = "RED";
						}else if (testsStatusMapOld.get(name).equals("warning")){
							compareStatus = testsStatusMapOld.get(name);
							seconedColor = "YELLOW";
						}
						// if(status.equals(testsStatusMapOld.get(name))) {
						// compareStatus = testsStatusMapOld.get(name);
						// seconedColor = "GREEN";
						// }
						// else {
						// compareStatus = testsStatusMapOld.get(name);
						// seconedColor = "RED";
						// }
						// here the try to take the last time
						lastTime = testsTimeMapOld.get(name + "Time");
						if (lastTime == null)
							lastTime = "0";
					} else {
						compareStatus = "";
						seconedColor = "WHITE";
					}
					testsStatusMapOld.remove(name);
					// finally write the wanted line
					status = modifyTrueFalseToPassFail(status);
					compareStatus = modifyTrueFalseToPassFail(compareStatus);
					testsTable.append(
							"<TR BGCOLOR=" + color + "><em><TD>" + ++index + "<TD>" + name + "<TD>" + status + "<TD>" + getTimeFormat(time) + "<TD BGCOLOR="
									+ seconedColor + ">" + compareStatus + "<TD BGCOLOR=" + seconedColor + ">" + getTimeFormat(Double.valueOf(lastTime))
									+ "</em>").append(System.getProperty("line.separator"));
				}
			}

			// the tests from the last run that not exists
			for (Entry<String, String> entry : testsStatusMapOld.entrySet()) {
				testsTable.append("<TR ><em><TD>" + ++index + "<TD>" + entry.getKey() + "<TD><TD><TD BGCOLOR=WHITE>"/*
																													 * N
																													 * /
																													 * A
																													 */+ entry.getValue() + "<TD></em>")
						.append(System.getProperty("line.separator"));
			}

			testsTable.append("</TABLE>").append(System.getProperty("line.separator"));
			docHtmlString.append("<p><b>Summary : </b></p>").append(System.getProperty("line.separator"));
			docHtmlString.append("<TABLE BORDER=1 BORDERCOLOR=BLACK width=\"100\"><TR><b><TH>Pass"/*
																								 * <
																								 * TH
																								 * >
																								 * Warnning
																								 */+ "<TH>Fail<TH>Total<b></TR>").append(
					System.getProperty("line.separator"));
			docHtmlString.append("<TR ><em><TD>" + pass + /* "<TD>" + warning + */"<TD>" + fail + "<TD>" + total + "</em>").append(
					System.getProperty("line.separator"));
			docHtmlString.append("</TABLE>").append(System.getProperty("line.separator"));
			docHtmlString.append(testsTable.toString()).append(System.getProperty("line.separator"));

			docHtmlString.append("<p></p>").append(System.getProperty("line.separator"));

			// the report directory creating
			String newLogLocation = copyTheCurrentLogTo(args[0] + "log/current", args[3]);

			urltoReporter = args[4] + newLogLocation;

			docHtmlString.append("<a href=\"" + urltoReporter + "\"><b>Click here for the full automation report</b></a> ").append(
					System.getProperty("line.separator"));

			docHtmlString.append("</body></html>").append(System.getProperty("line.separator"));

			pw.println(docHtmlString.toString());
			pw.close();

			// copying the file to the wanted location
			File afile = new File(newNameOfReport);
			File bfile = new File(nameOfReport);
			InputStream inStream = new FileInputStream(afile);
			OutputStream outStream = new FileOutputStream(bfile);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}
			inStream.close();
			outStream.close();
			// write the map to the config file
			setConfigFileFromMap(testsStatusMap, propName);
			setConfigFileFromMap(testsTimesMap, propTimes);

			// the email to sending the message
			String to;
			if (args.length < 3)
				to = "or.garfunkel@top-q.co.il";
			else
				to = args[2];
			// the status for the summary email
			String status = "passed";
			if (fail > 0)
				status = "fail";
			// sending the email
			return sendEmail1(to, from, "[Automation report] - for build : " + version + " - results : " + status, docHtmlString.toString(), nameOfReport,
					password);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String modifyTrueFalseToPassFail(String status) {
		if (status.equals("true")) {
			return "pass";
		}
		if (status.equals("false")) {
			return "fail";
		}
		return status;
	}

	/**
	 * This function is returning the time in the currect format the time is get
	 * inside as miliseconeds
	 * */
	public static String getTimeFormat(Double time) {

		String timeStr = "";
		time = time / 1000;

		if (time == null) {
			return timeStr;
		}

		int sec = (int) (time % 60);
		int min = (int) (time / 60);

		if (min < 10)
			timeStr = timeStr + "0" + min + ":";
		else
			timeStr = timeStr + min + ":";

		if (sec < 10)
			timeStr = timeStr + "0" + sec;
		else
			timeStr = timeStr + sec;

		return timeStr;
	}

	/**
	 * This function returns a map from the properties file
	 * */
	public static Map<String, String> getMapFromConfigFile(String cfgFile) {
		Map<String, String> testsStatusMap = new HashMap<String, String>();
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(cfgFile);
			prop.load(input);
		} catch (Exception e1) {
			// in case that this is the first run the file not exist - return
			// empty map
			return testsStatusMap;
		}
		// read the data and add it to the testsStatusMapOld map
		Enumeration<?> e = prop.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			testsStatusMap.put(key, prop.getProperty(key));
		}
		return testsStatusMap;
	}

	/**
	 * Write config file from the map
	 * */
	public static void setConfigFileFromMap(Map<String, String> testsStatusMap, String cfgFile) {
		// create properties file from the new map
		Properties prop1 = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(cfgFile);
			for (Map.Entry<String, String> entry : testsStatusMap.entrySet()) {
				prop1.setProperty(entry.getKey(), entry.getValue());
			}
			prop1.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public static boolean sendEmail1(String to, String subject, String body) throws Exception {
		return sendEmail1(to, from, subject, body, "", password);
	}

	/**
	 * Sending email to the wanted persons
	 * 
	 * @throws Exception
	 * */
	public static boolean sendEmail1(String to, final String username, String subject, String bodyHtml, String fileName, final String password)
			throws Exception {
		try {
			MailUtil mail = new MailUtil();
			mail.setSmtpHostName("smtp.gmail.com");
			mail.setSmtpPort(465);
			mail.setSsl(true);
			mail.setUserName(username);
			mail.setPassword(password);
			mail.setMailMessageAsHtmlText(true);
			mail.setFromAddress("Cellrox Automation");
			mail.setSendTo(to.split(","));
			mail.sendMail(subject, bodyHtml);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	// /home/topq/main_jenkins/workspace/Automation_Nightly/Logs
	// /home/topq/dev/runnreNew/runner/log/current
	public static String copyTheCurrentLogTo(String logDir, String newLogDir) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String currentDate = sdf.format(cal.getTime()).replace(" ", "_").replace(":", "_");
		newLogDir = newLogDir + File.separator + currentDate;

		System.out.println("newLogDir : " + newLogDir);
		// Make a new dir
		FileUtils.mkdirs(newLogDir);

		// Copy all the current log dir
		File source = new File(logDir);
		File desc = new File(newLogDir);
		try {
			FileUtils.copyDirectory(source, desc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return currentDate + File.separator + "index.html";
	}

	public static String getDateDuration(String dateStart, String dateStop) throws ParseException {
		try {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

			Date d1 = null;
			Date d2 = null;

			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);

			DateTime dt1 = new DateTime(d1);
			DateTime dt2 = new DateTime(d2);

			System.out.print(Days.daysBetween(dt1, dt2).getDays() + " days, ");
			System.out.print(Hours.hoursBetween(dt1, dt2).getHours() % 24 + " hours, ");
			System.out.print(Minutes.minutesBetween(dt1, dt2).getMinutes() % 60 + " minutes, ");
			System.out.print(Seconds.secondsBetween(dt1, dt2).getSeconds() % 60 + " seconds.");

			String hours = null, minutes = null, seconeds = null;
			hours = String.valueOf(Hours.hoursBetween(dt1, dt2).getHours());
			if (hours.length() == 1) {
				hours = "0" + hours;
			}
			minutes = String.valueOf(Minutes.minutesBetween(dt1, dt2).getMinutes() % 60);
			if (minutes.length() == 1) {
				minutes = "0" + minutes;
			}
			seconeds = String.valueOf(Seconds.secondsBetween(dt1, dt2).getSeconds() % 60);
			if (seconeds.length() == 1) {
				seconeds = "0" + seconeds;
			}

			return hours + ":" + minutes + ":" + seconeds;
		} catch (Exception e) {
			return "Unknown";
		}

	}

	/*	*//**
	 * Sending email to the wanted persons
	 * 
	 * @throws UnsupportedEncodingException
	 * */
	/*
	 * public static void sendEmail(String to, final String username, String
	 * subject, String bodyHtml, String fileName , final String password) throws
	 * UnsupportedEncodingException {
	 * 
	 * Properties props = new Properties(); props.put("mail.transport.protocol",
	 * "smtp"); props.put("mail.smtp.auth", true);
	 * props.put("mail.smtp.starttls.enable", true); props.put("mail.smtp.host",
	 * "smtp.gmail.com"); props.put("mail.smtp.port", "587");
	 * 
	 * Session session = Session.getInstance(props, new
	 * javax.mail.Authenticator() { protected PasswordAuthentication
	 * getPasswordAuthentication() { return new PasswordAuthentication(username,
	 * password); } }); try {
	 * 
	 * Message message = new MimeMessage(session); message.setFrom(new
	 * InternetAddress(username , "Cellrox Automation"));
	 * message.setRecipients(Message
	 * .RecipientType.TO,InternetAddress.parse(to));
	 * message.setSubject(subject); message.setText(bodyHtml);
	 * 
	 * BodyPart messageBodyPart1 = new MimeBodyPart(); //
	 * messageBodyPart1.setText(body); messageBodyPart1.setContent(bodyHtml,
	 * "text/html");
	 * 
	 * MimeBodyPart messageBodyPart2 = new MimeBodyPart(); DataSource source =
	 * new FileDataSource(fileName); messageBodyPart2.setDataHandler(new
	 * DataHandler(source)); messageBodyPart2.setFileName(fileName);
	 * 
	 * Multipart multipart = new MimeMultipart();
	 * multipart.addBodyPart(messageBodyPart1);
	 * 
	 * multipart.addBodyPart(messageBodyPart2);
	 * 
	 * message.setContent(multipart); System.out.println("Sending"); String
	 * protocol = "smtp"; props.put("mail." + protocol + ".auth", "true");
	 * Transport t = session.getTransport(protocol); try { t.connect(username,
	 * password); t.sendMessage(message, message.getAllRecipients()); } finally
	 * { t.close(); } // Transport.send(message); System.out.println("Done");
	 * 
	 * } catch (MessagingException e) { e.printStackTrace();
	 * e.printStackTrace(); } }
	 */

}
