package de.fhwedel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@SuppressWarnings("serial")
@NamedQueries({
	   @NamedQuery(
	           name="getCustomerByName",
	           query="SELECT c FROM Customer c WHERE c.name = :name"
	   )
	})
public class Customer {
	
	private Integer id;
	private String cust_no;
	private String name;
	private Integer reputation;

	public Customer() {
		this("", "", null);
	}
	
	public Customer(String cust_no, String name, Integer reputation) {
		this.cust_no = cust_no;
		this.name = name;
		this.reputation = reputation;
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
	
	public Integer getReputation() {
		return reputation;
	}
	
	public void setReputation(Integer reputation) {
		this.reputation = reputation;
	}

	public String getCust_no() {
		return cust_no;
	}

	public void setCust_no(String cust_no) {
		this.cust_no = cust_no;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", cust_no=" + cust_no + ", name=" + name + ", reputation=" + reputation + "]";
	}	

}
