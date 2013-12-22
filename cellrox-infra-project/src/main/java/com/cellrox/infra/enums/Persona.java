package com.cellrox.infra.enums;

public enum Persona {

	PRIV("priv"), CORP("corp");
	String value;

	private Persona(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	public Persona getPersona(String persona) {
		if (persona.toLowerCase().trim().equals("priv")) {
			return PRIV;
		} else if (persona.toLowerCase().trim().equals("corp")) {
			return CORP;
		}
		return null;
	}

}
