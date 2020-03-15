package de.fhwedel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Adress {

	private Integer id;

	private String name;
	
	private String street;
	
	private String zip;
	
	private String place;

	public Adress() {
		this("", "", "", "");
	}

	public Adress(String name, String street, String zip, String place) {
		this.name = name;
		this.street = street;
		this.zip = zip;
		this.place = place;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	@Override
	public String toString() {
		return "Adress "
				+ "["
				+ "id=" + id + ", "
				+ "name=" + name + ", "
				+ "street=" + street + ", "
				+ "zip=" + zip + ", "
				+ "place=" + place
				+ "]";
	}
	
}
