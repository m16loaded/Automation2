package com.cellrox.infra.ImageFlowReporter;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageFlowHtmlReport {

	private static final String SIMPLE_TITLE_FORMAT = "<p>%s</p>";
	private static final String SIMPLE_IMG_FORMAT = "<img class=\"screenshot\" src=\"%s\"/>";
	private static final String BLANK_ROW = "<br/>";
	private String jqueryLocation;
	private String jqueryUiLocation;
	private String widgetLocation;
	private String cssLocation;
	private String cssUiLocation;
	private String widgetIconLocation;

	StringBuilder htmlBody;

	public ImageFlowHtmlReport() throws URISyntaxException {
		try {
			URL resourceUrl = getClass().getResource("/jquery-2.0.3.min.js");
			Path resourcePath = Paths.get(resourceUrl.toURI());
			jqueryLocation = resourcePath.toFile().getAbsolutePath();

			resourceUrl = getClass().getResource("/jquery-ui-1.10.4.min.js");
			resourcePath = Paths.get(resourceUrl.toURI());
			jqueryUiLocation = resourcePath.toFile().getAbsolutePath();

			resourceUrl = getClass().getResource("/screenflow.js");
			resourcePath = Paths.get(resourceUrl.toURI());
			widgetLocation = resourcePath.toFile().getAbsolutePath();

			resourceUrl = getClass().getResource("/screenflow.css");
			resourcePath = Paths.get(resourceUrl.toURI());
			cssLocation = resourcePath.toFile().getAbsolutePath();

			resourceUrl = getClass().getResource("/jquery-ui.min.css");
			resourcePath = Paths.get(resourceUrl.toURI());
			cssUiLocation = resourcePath.toFile().getAbsolutePath();

			resourceUrl = getClass().getResource("/Image.png");
			resourcePath = Paths.get(resourceUrl.toURI());
			widgetIconLocation = resourcePath.toFile().getAbsolutePath();

			htmlBody = new StringBuilder("<h3>Test Screenshot flow</h3>");
			addScaleButtonWidget();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void addTitledImage(String title, File imagefile) {
		htmlBody.append(BLANK_ROW);
		htmlBody.append(String.format(SIMPLE_TITLE_FORMAT, title));
		htmlBody.append(String.format(SIMPLE_IMG_FORMAT,
				imagefile.getAbsoluteFile()));
		htmlBody.append(BLANK_ROW);
	}

	public String getHtmlReport() {
		if (htmlBody == null){
			htmlBody = new StringBuilder("");
		}
		return "<html><head>" + "<link type=\"text/css\" href=\""
				+ cssUiLocation + "\" rel=\"stylesheet\">"
				+ "<link type=\"text/css\" href=\"" + cssLocation
				+ "\" rel=\"stylesheet\">" + "<script src='" + jqueryLocation
				+ "'></script>" + "<script src='" + jqueryUiLocation
				+ "'></script>" + "<script src='" + widgetLocation
				+ "'></script>" + "</head><body>" + htmlBody.toString()
				+ "</body></html>";
	}

	public void addScaleButtonWidget() {
		String scaleButtonWidget = "<div id=\"masterController\"><table id=\"masterScale\"><tbody><tr id=\"masterScaleHeader\">"
				+ "<td><img class=\"widgetIcon\" src=\""
				+ widgetIconLocation
				+ "\"/></td><td><h3> Image Resizer</h3></td></tr>"
				+ "<tr id=\"contentScale\"><td id=\"scaleValue\"><div id=\"value\">50</div></td><td id=\"scaleSlider\"><div id=\"slider\">"
				+ "</div></td></tr></tbody></table></div>";
		htmlBody.append(scaleButtonWidget);
	}
}
