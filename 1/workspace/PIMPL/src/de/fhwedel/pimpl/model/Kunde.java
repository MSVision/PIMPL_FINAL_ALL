package de.fhwedel.pimpl.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@SuppressWarnings("serial")
public class Kunde extends BaseBusinessObject {

	@Column(nullable = false, unique = true)
	private String kundenNr;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String vorname;

	@Column(nullable = false)
	private String strasse;

	@Column(nullable = false)
	private Integer plz;

	@Column(nullable = false)
	private String ort;
	
	public static Kunde initCustomer(String name, String forename, String kundenNr, String street, Integer zip,
			String city) throws IllegalValueException {
		Kunde customer = new Kunde();
		customer.setName(name);
		customer.setKundenNr(kundenNr);
		customer.setVorname(forename);
		customer.setStrasse(street);
		customer.setPlz(zip);
		customer.setOrt(city);
		return customer;
	}

	private Kunde() {
		super();
	}

	@Override
	public String getCaption() {
		return this.kundenNr + " | " + this.getFullName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) throws IllegalValueException {
		if (!Helper.validStringBaseValue(name)) {
			throw new IllegalValueException();
		}
		this.name = name;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) throws IllegalValueException {
		if (!Helper.validStringBaseValue(vorname)) {
			throw new IllegalValueException();
		}
		this.vorname = vorname;
	}

	public String getKundenNr() {
		return kundenNr;
	}

	private void setKundenNr(String kundenNr) throws IllegalValueException {
		if (!Helper.validStringBaseValue(name)) {
			throw new IllegalValueException();
		}
		this.kundenNr = kundenNr;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) throws IllegalValueException {
		if (!Helper.validStringBaseValue(strasse)) {
			throw new IllegalValueException();
		}
		this.strasse = strasse;
	}

	public Integer getPlz() {
		return plz;
	}

	public void setPlz(Integer plz) throws IllegalValueException {
		if (!Helper.validZip(plz)) {
			throw new IllegalValueException();
		}
		this.plz = plz;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) throws IllegalValueException {
		if (!Helper.validStringBaseValue(ort)) {
			throw new IllegalValueException();
		}
		this.ort = ort;
	}

	public String getFullName() {
		return this.vorname + " " + this.name;
	}
	
	public String getFullNameTwisted() {
		return this.name + " " + this.vorname;
	}

	public boolean relatesToStr(String checkStr) {
		if (checkStr == null) {
			throw new IllegalArgumentException("Parameter mit Wert -> null");
		}
		return this.kundenNr.toUpperCase().startsWith(checkStr.toUpperCase()) 
				|| this.name.toUpperCase().startsWith(checkStr.toUpperCase())
				|| this.vorname.toUpperCase().startsWith(checkStr.toUpperCase())
				|| this.getFullName().toUpperCase().startsWith(checkStr.toUpperCase())
				|| this.getFullNameTwisted().toUpperCase().startsWith(checkStr.toUpperCase());
	}
	
	public List<String> getAllDataAsList() {
		List<String> result = new LinkedList<>();
		result.add("Kundennummer: " + this.kundenNr);
		result.add("Name: " + this.vorname);
		result.add("Nachname: " + this.name);
		result.add("Straﬂe: " + this.strasse);
		result.add("Plz: " + this.plz);
		result.add("Ort: " + this.ort);
		return result;
	}

}
