package com.cellrox.infra.object;

import com.google.common.collect.Sets;

public class DataSharingProperty {

	public enum properties {
		Thumbnail("Thumbnail"), Phone_Number("Phone number"), Email_Address("Email address"), Contact_Details("Contact details"), Event_details("Event details");

		String property;

		private properties(String property) {
			this.property = property;
		}

		public String getProperty() {
			return property;
		}

		public void setProperty(String property) {
			this.property = property;
		}

	}

	public enum PropertyStatus {
		SHOW("SHOW"), HIDE("HIDE");

		String status;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		private PropertyStatus(String status) {
			this.status = status;
		}

	}

	private properties property;
	private PropertyStatus status;

	public properties getProperty() {
		return property;
	}

	public void setProperty(properties property) {
		this.property = property;
	}

	public PropertyStatus getStatus() {
		return status;
	}

	public void setStatus(PropertyStatus status) {
		this.status = status;
	}

	public DataSharingProperty() {
		super();
	}

}
