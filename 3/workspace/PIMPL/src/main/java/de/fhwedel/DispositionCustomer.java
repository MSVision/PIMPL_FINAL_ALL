package de.fhwedel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
@SuppressWarnings("serial")
public class DispositionCustomer implements Serializable {

	@Id
	@Column(updatable = false, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	private Integer id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private Integer customerNumber;
	
	@Column(nullable = false)
	private String street;
	
	@Column(nullable = false)
	private String zip;
	
	@Column(nullable = false)
	private String city;

	@OneToMany(cascade = CascadeType.PERSIST)
	private List<ShippingOrder> shippingOrders;

	public DispositionCustomer() {
		this("", null, "", "", "");
	}

	public DispositionCustomer(String name, Integer customerNumber, String street, String zip, String city) {
		this.name = name;
		this.customerNumber = customerNumber;
		this.street = street;
		this.zip = zip;
		this.city = city;
		this.shippingOrders = new ArrayList<ShippingOrder>();
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(Integer customerNumber) {
		this.customerNumber = customerNumber;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	public List<ShippingOrder> getShippingOrders() {
		return shippingOrders;
	}

	public void setShippingOrders(List<ShippingOrder> shippingOrders) {
		this.shippingOrders = shippingOrders;
	}

	@Override
	public String toString() {
		return "DispositionCustomer "
				+ "["
				+ "name=" + name + ", "
				+ "customerNumber=" + customerNumber + ", "
				+ "street=" + street + ", "
				+ "zip=" + zip + ", "
				+ "city=" + city + ", "
				+ "shippingOrders=" + shippingOrders 
				+ "]";
	}
	
}
