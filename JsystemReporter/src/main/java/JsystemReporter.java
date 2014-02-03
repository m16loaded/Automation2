import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class JsystemReporter {

	/**
	 * The application takes the .xml and make from it .html table with the wanted fields
	 * 	@param args- the first arg should be : 
	 * arg[0] - currentLogLocation - the place of reports.0.xml
	 * arg[1] - nameOfReport - the place to save the .html name
	 * arg[2] - String to -the wanted email to send to
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int pass = 0, fail = 0, total = 0, warning = 0;
		String date = null, version = null, id = null, nameOfReport = null, newNameOfReport = null, currentLogLocation = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String currentDate = sdf.format(cal.getTime()).replace(" ", "_").replace(":", "_");

		//get the args[] parm, if they not inserted use the default 
		
		if(args.length < 2 ) {
			currentLogLocation = "/home/topq/dev/runner6003/log/current/reports.0.xml";
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

			//reports version variables
			NodeList nList = doc.getElementsByTagName("reports");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				Element eElement = (Element) nNode;
				date = eElement.getAttribute("Build_date");
				version = eElement.getAttribute("Build_display_id");
				id = eElement.getAttribute("Build_sdk_version");
			}

			nList = doc.getElementsByTagName("test");
			pw.println("<!DOCTYPE html><html><head><title>Manager Report for " + sdf.format(cal.getTime())+"</title></head><body>");
			pw.println("<h1><em>Manager Report for " + sdf.format(cal.getTime()) + "<em></h1>");
			pw.println("<p><b>Build_date : " + date + ", Build_sdk_version : " + version + ", Build_display_id : " + id+ " </b></p>");
			pw.println("<p><b>Tests report : </b></p>");
			pw.println("<TABLE BORDER=1 BORDERCOLOR=BLACK width=\"100\"><TR><b><TH>Name of test<TH>Status<TH>Time<b></TR>");

			//printing all the lines
			for (int i = 0; i < nList.getLength(); i++) {
				++total;
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					double time = ((Long.valueOf(eElement.getAttribute("endTime")) - Long.valueOf(eElement.getAttribute("startTime")))) / 1000;
					String status = eElement.getAttribute("status");
					String name = eElement.getAttribute("name");
					String color = null;
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
					pw.println("<TR BGCOLOR=" + color + "><em><TD>" + name + "<TD>" + status + "<TD>" + time + " sec</em>");
				}
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
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}
			inStream.close();
			outStream.close();
			
			String to ;
			if(args.length < 3) {
				to = "or.garfunkel@top-q.co.il";
			}
			else {
				to = args[2];
			}
			
			final String from =  "cellrox99@gmail.com";
			final String password = "cellrox2011";
			
			EmailSender.sendEmail(to, from, "Automation summary report", "Here the report of the automation", nameOfReport, password);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		
	}
 

}
