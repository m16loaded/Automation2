Better off to view with chrome, firefox did not present it well.

All files except the ImageFlowReport.java files goes to src/main/resources of the test project.
The java file MUST be in the test project and not in the infra (could be under its own package).

Then in order to use it:

...
create: (once, this class is not reusable.. if you want to create more than one each will be in the reporter as seperate title)
	ImageFlowHtmlReport imageFlowHtmlReport = new ImageFlowHtmlReport();

add image: (as many as needed)    param{1}: String - title above each image, param{2}: File - some reference to image file 
	imageFlowHtmlReport.addTitledImage("Before App Launch", adb.getScreenshotWithAdb(null));

send to report: (once)
	report.report("screen flow", imageFlowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);