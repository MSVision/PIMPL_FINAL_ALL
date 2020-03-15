package de.fhwedel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries({
	   @NamedQuery(
	           name="getShippingCustomerByCustomerNumber",
	           query="SELECT c FROM ShippingCustomer c WHERE c.customerNumber = :customerNumber"
	   )
	})
public class ShippingCustomer {
	
	private Integer id;
	
	private String custumerNumber;
	
	private String name;
	
	private String identifier;

	public ShippingCustomer() {
		this("", "", "");
	}
	
	public ShippingCustomer(String custumerNumber, String name, String identifier) {
		this.custumerNumber = custumerNumber;
		this.name = name;
		this.identifier = identifier;
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

	public String getCustumerNumber() {
		return custumerNumber;
	}

	public void setCustumerNumber(String custumerNumber) {
		this.custumerNumber = custumerNumber;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public String toString() {
		return "Customer "
				+ "["
				+ "id=" + id + ", "
				+ "custumerNumber=" + custumerNumber + ", "
				+ "name=" + name + ", "
				+ "identifier=" + identifier 
				+ "]";
	}
	
}
