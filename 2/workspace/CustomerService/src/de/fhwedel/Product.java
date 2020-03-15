package de.fhwedel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	   @NamedQuery(
	           name="getProductByCustomerNumber",
	           query="SELECT c FROM ShippingCustomer c WHERE c.customerNumber = :customerNumber"
	   )
	})
public class Product {

	private Integer id;

	private String productCode;
	
	private String caption;
	
	private Integer runtimeHours;
	
	private Integer price;
	
	private Integer maxWeight;

	public Product() {
		this("", "", null, null, null);
	}

	public Product(String productCode, String caption, Integer runtimeHours, Integer price, Integer maxWeight) {
		this.productCode = productCode;
		this.caption = caption;
		this.runtimeHours = runtimeHours;
		this.price = price;
		this.maxWeight = maxWeight;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Integer getRuntimeHours() {
		return runtimeHours;
	}

	public void setRuntimeHours(Integer runtimeHours) {
		this.runtimeHours = runtimeHours;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(Integer maxWeight) {
		this.maxWeight = maxWeight;
	}
	
	@Override
	public String toString() {
		return "Product "
				+ "["
				+ "id=" + id + ", "
				+ "code=" + productCode + ", "
				+ "caption=" + caption + ", "
				+ "runtime=" + runtimeHours + ", "
				+ "price=" + price + ", "
				+ "maxWeight=" + maxWeight
				+ "]";
	}
	
}
