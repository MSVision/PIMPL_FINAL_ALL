package de.fhwedel;

import java.io.Serializable;

import javax.persistence.Column;
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
	           name="getAllItems",
	           query="SELECT i FROM Item i"
	   )
	})
public class Item implements Serializable {
	
	@Id
	@Column(updatable = false, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	private Integer id;

	@Column(nullable = false)
	private String productCode;

	@Column(nullable = false)
	private String caption;
	
	@Column(nullable = false)
	private Integer price;
	
	@Column(nullable = false)
	private Integer stock;

	public Item() {
		this("", "", null, null);
	}

	public Item(String productCode, String caption, Integer price, Integer stock) {
		this.productCode = productCode;
		this.caption = caption;
		this.price = price;
		this.stock = stock;
	}

	public Integer getId() {
		return id;
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

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}
	
	@Override
	public String toString() {
		return "Item "
				+ "["
				+ "id=" + id + ", "
				+ "productCode=" + productCode + ", "
				+ "caption=" + caption + ", "
				+ "price=" + price + ", "
				+ "stock=" + stock 
				+ "]";
	}

}
