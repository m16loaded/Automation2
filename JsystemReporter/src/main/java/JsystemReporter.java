import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class JsystemReporter {

	final static String propName = "config.properties";
	final static String propTimes = "configTimes.properties";
	final static String from =  "auto@cellrox.com";
	final static String password = "%SMd*6ya";
	
	/**
	 * The application takes the .xml and make from it .html table with the wanted fields
	 * This application will compare to the last run from the config file
	 * 	@param args- the first arg should be : 
	 * arg[0] - currentLogLocation - the place of reports.0.xml
	 * arg[1] - nameOfReport - the place to save the .html name
	 * arg[2] - summary location 
	 * arg[3] - String to -the wanted email to send to
	 */
	public static void main(String[] args) {
		String urltoReporter = "http://build.vm.cellrox.com:8080/job/Automation_Nightly/HTML_Report/?";
		Map<String, String> testsStatusMap = new HashMap<String, String>();
		Map<String, String> testsTimesMap = new HashMap<String, String>();
		String doaCrash = null, deviceCrash = null, personaCrash = null;
		String compareStatus, seconedColor, lastTime = null;
		int pass = 0, fail = 0, total = 0, warning = 0, index = 0;
		String date = null, version = null, id = null, nameOfReport = null, summaryLocation = null, newNameOfReport = null, currentLogLocation = null, startTime = null,
				endTime = null, hardware = null, imei = null, macAdr = null, noCon = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String currentDate = sdf.format(cal.getTime()).replace(" ", "_").replace(":", "_");
	
		StringBuilder testsTable = new StringBuilder();
		StringBuilder docHtmlString = new StringBuilder();
		
		//get the args[] parm, if they not inserted use the default 
		if(args.length < 3 ) {
			currentLogLocation = "/home/topq/dev/runnreNew/runner/log/current/reports.0.xml";//"/home/topq/main_jenkins/workspace/Automation_Nightly/cellrox-tests-project/log/current/reports.0.xml";//"/home/topq/dev/runner6003/log/current/reports.0.xml";
			nameOfReport = "/home/topq/dev/managerReport.html";
			summaryLocation = "/home/topq/dev/runnreNew/runner/summary.properties";
		}
		else {
			currentLogLocation = args[0];
			nameOfReport = args[1];
			summaryLocation = args[2];
		}
			
		newNameOfReport = nameOfReport.replace(".html", "_").concat(currentDate).concat(".html");
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(currentLogLocation));
			PrintWriter pw = new PrintWriter(new FileWriter(newNameOfReport));
			doc.getDocumentElement().normalize();

			//reports version variables read the vars
			NodeList nList = doc.getElementsByTagName("reports");

			Properties prop = new Properties();
			InputStream input = null;
			try {
				input = new FileInputStream(summaryLocation);
				prop.load(input);
			} catch (Exception e1) {
				//in case that this is the first run the file not exist - return empty map
			}
				
			version = prop.getProperty("Build_display_id");
			id = prop.getProperty("Build_sdk_version");
			doaCrash = prop.getProperty("Doa_Crash");
			deviceCrash = prop.getProperty("Device_Crash");
			personaCrash = prop.getProperty("Persona_Crash");
			startTime = prop.getProperty("Start_Time");
			endTime = prop.getProperty("End_Time");
			hardware = prop.getProperty("hardware");
			macAdr = prop.getProperty("Mac_address");
			imei = prop.getProperty("IMEI");
//			noCon = prop.getProperty("No_Connection");
				
			//begin to create the html file
			nList = doc.getElementsByTagName("test");
			docHtmlString.append("<!DOCTYPE html><html><head><title>Automation report for build : "+ version +"</title></head><body>").append(System.getProperty("line.separator"));
			docHtmlString.append("<h1><em>Automaion Report - for build : "+version+"<em></h1>").append(System.getProperty("line.separator"));
			docHtmlString.append("<p>Start time : "+startTime+"</p>").append(System.getProperty("line.separator"));
			docHtmlString.append("<p>End time : "+endTime+"</p>").append(System.getProperty("line.separator"));
			docHtmlString.append("<p>Hardware : "+hardware+"</p>").append(System.getProperty("line.separator"));
			if(hardware.equalsIgnoreCase("flo")) {
				if(!macAdr.isEmpty()){
					docHtmlString.append("<p>Mac address : "+macAdr+"</p>").append(System.getProperty("line.separator"));
				}
			}
			else {
				if(!imei.isEmpty()){
					docHtmlString.append("<p>IMEI : "+imei+"</p>").append(System.getProperty("line.separator"));
				}
			}
			
			if (doaCrash.trim().equals("0")) {
				docHtmlString.append("<p>DOA crash count: false</p>").append(System.getProperty("line.separator"));
			}
			else {
				docHtmlString.append("<p>DOA crash count: true</p>").append(System.getProperty("line.separator"));
			}
			
			docHtmlString.append("<p>Device crash count: "+deviceCrash+"</p>").append(System.getProperty("line.separator"));
			docHtmlString.append("<p>Persona crash count: "+personaCrash+"</p>").append(System.getProperty("line.separator"));
//			docHtmlString.append("<p>No Connection number: "+noCon+"</p>").append(System.getProperty("line.separator"));
			
			testsTable.append("<p><b>Test report : </b></p>").append(System.getProperty("line.separator"));
			testsTable.append("<TABLE BORDER=1 BORDERCOLOR=BLACK width=\"100\"><TR><b><TH>Index<TH>Test name<TH>Test Duration<TH>Last Test Duration<TH>Result<TH>Last Run Result<b></TR>").append(System.getProperty("line.separator"));

			Map<String, String> testsStatusMapOld = getMapFromConfigFile(propName);
			Map<String, String> testsTimeMapOld = getMapFromConfigFile(propTimes);
			//writing to the html file all the lines
			for (int i = 0; i < nList.getLength(); i++) {
				++total;
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					double time = ((Long.valueOf(eElement.getAttribute("endTime")) - Long.valueOf(eElement.getAttribute("startTime"))));
							
					String status = eElement.getAttribute("status");
					String name = eElement.getAttribute("name");
					String color = null;
					testsTimesMap.put(name+"Time", String.valueOf(((Long.valueOf(eElement.getAttribute("endTime")) - Long.valueOf(eElement.getAttribute("startTime"))))));
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
					//The comparing to the last run
					lastTime = "0";
					if(testsStatusMapOld.containsKey(name)) {
						if(status.equals(testsStatusMapOld.get(name))) {
							compareStatus = testsStatusMapOld.get(name);
							seconedColor = "GREEN";
						}
						else {
							compareStatus = testsStatusMapOld.get(name);
							seconedColor = "RED";
						}
						//here the try to take the last time
						lastTime = testsTimeMapOld.get(name+"Time");
						if(lastTime == null)
							lastTime = "0";
					}
					else {
						compareStatus = "N/A";
						seconedColor = "YELLOW";
					}
					testsStatusMapOld.remove(name);
					//finally write the wanted line
					
					testsTable.append("<TR BGCOLOR=" + color + "><em><TD>"+ ++index +"<TD>" +name + "<TD>" + getTimeFormat(time)+"<TD>"+getTimeFormat(Double.valueOf(lastTime))+"<TD>" + status  + "<TD BGCOLOR="+seconedColor+">"+compareStatus+"</em>").append(System.getProperty("line.separator"));
				}
			}
			
			//the tests from the last run that not exists
			for (Entry<String, String> entry : testsStatusMapOld.entrySet()) {
				testsTable.append("<TR ><em><TD>"+ ++index +"<TD>" + entry.getKey() + "<TD><TD><TD BGCOLOR=YELLOW>N/A<TD BGCOLOR=YELLOW>"+entry.getValue()+"</em>").append(System.getProperty("line.separator"));
			}
			
			testsTable.append("</TABLE>").append(System.getProperty("line.separator"));
			docHtmlString.append("<p><b>Summary : </b></p>").append(System.getProperty("line.separator"));
			docHtmlString.append("<TABLE BORDER=1 BORDERCOLOR=BLACK width=\"100\"><TR><b><TH>Pass<TH>Warnning<TH>Fail<TH>Total<b></TR>").append(System.getProperty("line.separator"));
			docHtmlString.append("<TR ><em><TD>" + pass + "<TD>" + warning + "<TD>" + fail + "<TD>" + total + "</em>").append(System.getProperty("line.separator"));
			docHtmlString.append("</TABLE>").append(System.getProperty("line.separator"));
			docHtmlString.append(testsTable.toString()).append(System.getProperty("line.separator"));
			
			docHtmlString.append("<p></p>").append(System.getProperty("line.separator"));
			docHtmlString.append("<a href=\""+urltoReporter+"\"><b>Click here for the full automation report</b></a> ").append(System.getProperty("line.separator"));
			
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
			//write the map to the config file
			setConfigFileFromMap(testsStatusMap, propName);
			setConfigFileFromMap(testsTimesMap, propTimes);
			
			//the email to sending the message
			String to ;
			if(args.length < 3) 
				to = "or.garfunkel@top-q.co.il";
			else 
				to = args[3];
			//the status for the summary email
			String status = "passed";
			if(fail>0)
				status = "fail";
			//sending the email
			sendEmail(to, from, "Automation report - for build : "+ version +" - results : " +status, docHtmlString.toString()
					/*,"The automation run can be found at : http://build.vm.cellrox.com:8080/job/Automation_Nightly/HTML_Report/?"*/
					, nameOfReport, password);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	}
	
	/**
	 * This function is returning the time in the currect format 
	 * the time is get inside as miliseconeds
	 * */
	public static String getTimeFormat(Double time ) {
		
		String timeStr = "";
		time = time/1000;
		
		if(time==null) 
			return timeStr;
		
		int sec = (int)(time % 60);
		int min = (int)(time / 60);
		
		if(min<10)
			timeStr = timeStr+"0"+min+":";
		else 
			timeStr = timeStr+min+":";
		
		if(sec<10)
			timeStr = timeStr+"0"+sec;
		else 
			timeStr = timeStr+sec;
		
		return timeStr;
	}
	
	/**
	 * This function returns a map from the properties file
	 * */
	public static Map<String, String> getMapFromConfigFile(String cfgFile){
		Map<String, String> testsStatusMap = new HashMap<String, String>();
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(cfgFile);
			prop.load(input);
		} catch (Exception e1) {
			//in case that this is the first run the file not exist - return empty map
			return testsStatusMap;
		}
		//read the data and add it to the testsStatusMapOld map 
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
	public static void setConfigFileFromMap(Map<String, String>testsStatusMap , String cfgFile){
	//create properties file from the new map
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
	
	
	/**
	 * Sending email to the wanted persons
	 * */
	public static void sendEmail(String to, final String username, String subject, String bodyHtml, String fileName , final String password) {

	    Properties props = new Properties();
	    props.put("mail.smtp.auth", true);
	    props.put("mail.smtp.starttls.enable", true);
	    props.put("mail.smtp.host", "smtp.gmail.com");
	    props.put("mail.smtp.port", "587");

	    Session session = Session.getInstance(props,
	            new javax.mail.Authenticator() {
	                protected PasswordAuthentication getPasswordAuthentication() {
	                    return new PasswordAuthentication(username, password);
	                }
	            });
	    try {

	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(username));
	        message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
	        message.setSubject(subject);
	        message.setText(bodyHtml);
	        
	        
	        BodyPart messageBodyPart1 = new MimeBodyPart();  
//	        messageBodyPart1.setText(body);  
	        messageBodyPart1.setContent(bodyHtml, "text/html");
	        
	        MimeBodyPart messageBodyPart2 = new MimeBodyPart();  
	        DataSource source = new FileDataSource(fileName);  
	        messageBodyPart2.setDataHandler(new DataHandler(source));  
	        messageBodyPart2.setFileName(fileName);    
	        
	        Multipart multipart = new MimeMultipart();  
	        multipart.addBodyPart(messageBodyPart1); 
 
	        multipart.addBodyPart(messageBodyPart2);  
	        

	        message.setContent(multipart);
	        System.out.println("Sending");
	        Transport.send(message);
	        System.out.println("Done");

	    } catch (MessagingException e) {
	        e.printStackTrace();
	    }
	}

}
