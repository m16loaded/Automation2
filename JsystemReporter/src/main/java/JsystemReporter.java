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

//
	final static String propName = "config.properties";
//	final static String propNameTmporery = "config_tmp_new.properties"; 
	final static String from =  "cellrox99@gmail.com";
	final static String password = "cellrox2011";
	
	
	/**
	 * The application takes the .xml and make from it .html table with the wanted fields
	 * This application will compare to the last run from the config file
	 * 	@param args- the first arg should be : 
	 * arg[0] - currentLogLocation - the place of reports.0.xml
	 * arg[1] - nameOfReport - the place to save the .html name
	 * arg[2] - String to -the wanted email to send to
	 */
	public static void main(String[] args) {
		Map<String, String> testsStatusMap = new HashMap<String, String>();
		String doaCrash = null, deviceCrash = null, personaCrash = null;
		String compareStatus, seconedColor;
		int pass = 0, fail = 0, total = 0, warning = 0, index = 0;
		String date = null, version = null, id = null, nameOfReport = null, newNameOfReport = null, currentLogLocation = null, startTime = null, endTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String currentDate = sdf.format(cal.getTime()).replace(" ", "_").replace(":", "_");

		//get the args[] parm, if they not inserted use the default 
		if(args.length < 2 ) {
			currentLogLocation = "/home/topq/main_jenkins/workspace/Automation_Nightly/cellrox-tests-project/log/current/reports.0.xml";//"/home/topq/dev/runner6003/log/current/reports.0.xml";
			nameOfReport = "/home/topq/dev/managerReport.html";
		}
		else {
			currentLogLocation = args[0];
			nameOfReport = args[1];
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
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				Element eElement = (Element) nNode;
				date = eElement.getAttribute("Build_date");
				version = eElement.getAttribute("Build_display_id");
				id = eElement.getAttribute("Build_sdk_version");
				startTime = eElement.getAttribute("Date");
				try {
				doaCrash = eElement.getAttribute("Doa_Crash");
				}catch (Exception e){}
				try {
				deviceCrash = eElement.getAttribute("Device_Crash");
				}catch (Exception e){}
				try {
				personaCrash = eElement.getAttribute("Persona_Crash");
				}catch (Exception e){}
				try {
					endTime = eElement.getAttribute("End_Time");
				}catch (Exception e){}
			}
			//begin to create the html file
			nList = doc.getElementsByTagName("test");
			pw.println("<!DOCTYPE html><html><head><title>Automaion Report for build : "+version+"</title></head><body>");
			pw.println("<h1><em>Automaion Report for build : "+version+"<em></h1>");
//			pw.println("<p><b>Build_date : " + date + ", Build_sdk_version : " + version + ", Build_display_id : " + id+ " </b></p>");
			pw.println("<p><b>Tests report : </b></p>");
			pw.println("<p>Start time : "+startTime+"</p>");
			if(!endTime.isEmpty())
				pw.println("<p>End time : "+endTime+"</p>");
			if(!doaCrash.isEmpty())
				pw.println("<p>Doa Crash : true</p>");
			if(!deviceCrash.isEmpty())
				pw.println("<p>Device Crash : true</p>");
			if(!personaCrash.isEmpty())
				pw.println("<p>Persona Crash : true</p>");
			pw.println("<TABLE BORDER=1 BORDERCOLOR=BLACK width=\"100\"><TR><b><TH>Index<TH>Test name<TH>Time<TH>Result<TH>Last Run Result<b></TR>");

			Map<String, String> testsStatusMapOld = getMapFromConfigFile(propName);
			//writing to the html file all the lines
			for (int i = 0; i < nList.getLength(); i++) {
				++total;
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					double time = ((Long.valueOf(eElement.getAttribute("endTime")) - Long.valueOf(eElement.getAttribute("startTime")))) / 1000;
					String status = eElement.getAttribute("status");
					String name = eElement.getAttribute("name");
					String color = null;
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
					if(testsStatusMapOld.containsKey(name)) {
						if(status.equals(testsStatusMapOld.get(name))) {
						compareStatus = testsStatusMapOld.get(name);
						seconedColor = "GREEN";
						}
						else {
							compareStatus = testsStatusMapOld.get(name);
							seconedColor = "RED";
						}
					}
					else {
						compareStatus = "N/A";
						seconedColor = "YELLOW";
					}
					testsStatusMapOld.remove(name);
					//finally write the wanted line
					pw.println("<TR BGCOLOR=" + color + "><em><TD>"+ ++index +"<TD>" +name + "<TD>" + time+" sec<TD>" + status  + "<TD BGCOLOR="+seconedColor+">"+compareStatus+"</em>");
				}
			}
			
			//the tests from the last run that not exists
			for (Entry<String, String> entry : testsStatusMapOld.entrySet()) {
				pw.println("<TR ><em><TD>"+ ++index +"<TD>" + entry.getKey() + "<TD><TD BGCOLOR=YELLOW>N/A<TD BGCOLOR=YELLOW>"+entry.getValue()+"</em>");
			}
			
			pw.println("</TABLE>");
			pw.println("<p><b>Summary report : </b></p>");
			pw.println("<TABLE BORDER=1 BORDERCOLOR=BLACK width=\"100\"><TR><b><TH>Pass<TH>Warnning<TH>Fail<TH>Total<b></TR>");
			pw.println("<TR ><em><TD>" + pass + "<TD>" + warning + "<TD>" + fail + "<TD>" + total + "</em>");
			pw.println("</TABLE>");
			pw.println("</body></html>");
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
			setConfigFileFromMap(testsStatusMap, propName);;
			
			//the email to sending the message
			String to ;
			if(args.length < 3) 
				to = "or.garfunkel@top-q.co.il";
			else 
				to = args[2];
			//the status for the summary email
			String status = "passed";
			if(fail>0)
				status = "fail";
			//sending the email
			sendEmail(to, from, "Automation summary report"+status, "Here the report of the automation", nameOfReport, password);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
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
	 * 
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
	public static void sendEmail(String to, final String username, String subject, String body, String fileName , final String password) {

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
	        message.setText(body);

	        MimeBodyPart messageBodyPart = new MimeBodyPart();
	        Multipart multipart = new MimeMultipart();

	        messageBodyPart = new MimeBodyPart();
	        DataSource source = new FileDataSource(fileName);
	        messageBodyPart.setDataHandler(new DataHandler(source));
	        messageBodyPart.setFileName(fileName);
	        multipart.addBodyPart(messageBodyPart);

	        message.setContent(multipart);
	        System.out.println("Sending");
	        Transport.send(message);
	        System.out.println("Done");

	    } catch (MessagingException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*	public static void addToHtmlLastRunCompare(PrintWriter pw, Map<String, String> testsStatusMapNew) throws Exception {
		
		Map<String, String> testsStatusMapOld = new HashMap<String, String>();
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream(propName);
		prop.load(input);
		
		//read the data and add it to the testsStatusMapOld map 
		Enumeration<?> e = prop.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			testsStatusMapOld.put(key, prop.getProperty(key));
		}
		
		//create properties file from the new map
		Properties prop1 = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(propNameTmporery);
			for (Map.Entry<String, String> entry : testsStatusMapNew.entrySet()) {
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
		//finish to create new file
		
		pw.println("<p><b>Last Run compare : </b></p>");
		
		pw.println("<TABLE BORDER=1 BORDERCOLOR=BLACK width=\"100\"><TR><b><TH>Name<TH>Last Run<TH>Current Run<b></TR>");
		
		for (Map.Entry<String, String> entry : testsStatusMapOld.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			String secValue = "not exist";
			if(testsStatusMapNew.containsKey(key)) {
				secValue =testsStatusMapNew.get(key);
				String color = "GREEN";
				if(!secValue.equals(value)){
					color = "RED";
				}
				
				pw.println("<TR BGCOLOR=" + color +"><em><TD>" + key + "<TD>" + value + "<TD>" + secValue +"</em>");
				testsStatusMapNew.remove(key);
			}
		}
		for (Map.Entry<String, String> entry : testsStatusMapNew.entrySet()) {
			pw.println("<TR BGCOLOR=RED><em><TD>"+entry.getKey()+"<TD>  not exist  <TD>" + entry.getValue() +"</em>");
		}
		
//		
//		pw.println("<TR ><em><TD>" + pass + "<TD>" + warning + "<TD>" + fail + "<TD>" + total + "</em>");
		pw.println("</TABLE>");
		
		FileUtils.copyFile(propNameTmporery, propName);
		
//		//rename the new file 
//		File oldfile =new File(propNameTmporery);
//		File newfile =new File(propName);
//		if(oldfile.renameTo(newfile)){
//			System.out.println("Rename succesful");
//		}else{
//			System.out.println("Rename failed");
//		}
		

	}*/
 

}
