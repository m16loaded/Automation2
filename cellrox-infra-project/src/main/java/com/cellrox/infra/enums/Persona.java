package com.cellrox.infra.enums;

public enum Persona {

	PRIV("priv", "/data/containers/priv/data/local/tmp/"), CORP("corp",
			"/data/containers/corp/data/local/tmp/");
	String value;
	String tmpLib;

	public String getTmpLib() {
		return tmpLib;
	}

	public void setTmpLib(String tmpLib) {
		this.tmpLib = tmpLib;
	}

	private Persona(String value, String tmpLib) {
		this.value = value;
		this.tmpLib = tmpLib;
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
