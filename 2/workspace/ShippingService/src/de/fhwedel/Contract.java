package de.fhwedel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Contract {

	private Integer id;
	
	private Integer contractNumber;
	
	private Integer weight;

	// Relations
	
	private Customer customer;

	private Product product;
	
	private Adress fromAdress;
	
	private Adress toAdress;

	public Contract() {
		this(null, null);
	}

	public Contract(Integer contractNumber, Integer weight) {
		this.contractNumber = contractNumber;
		this.weight = weight;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	

	public Integer getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(Integer contractNumber) {
		this.contractNumber = contractNumber;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Adress getFromAdress() {
		return fromAdress;
	}

	public void setFromAdress(Adress fromAdress) {
		this.fromAdress = fromAdress;
	}

	public Adress getToAdress() {
		return toAdress;
	}

	public void setToAdress(Adress toAdress) {
		this.toAdress = toAdress;
	}

	@Override
	public String toString() {
		return "Contract "
				+ "["
				+ "id=" + id + ", "
				+ "contractNumber=" + contractNumber + ", "
				+ "weight=" + weight + ", "
				+ "fromAdress=" + fromAdress.getName() + ", "
				+ "toAdress=" + toAdress.getName() + ", "
				+ "customer=" + customer.getName() + ", "
				+ "product=" + product.getCaption() 
				+ "]";
	}
	
}
