package de.fhwedel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@SuppressWarnings("serial")
public class ShippingPosition implements Serializable {
	
	@Id
	@Column(updatable = false, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	private Integer id;
	
	@Column(nullable = false)
	private Integer quantity;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private ShippingOrder shippingOrder;
	
//	@Column(nullable = false)
//	@ManyToOne
//	private ContractPosition contractPosition;

	public ShippingPosition() {
		this(null);
	}
	
	public ShippingPosition(Integer quantity) {
		this.quantity = quantity;
	}
	
	public ShippingPosition(Integer quantity, ShippingOrder shippingOrder) {
		this.quantity = quantity;
		this.shippingOrder = shippingOrder;
	}

//	public ShippingPosition(Integer quantity, ContractPosition contractPosition) {
//		this.quantity = quantity;
//		this.contractPosition = contractPosition;
//	}

	public Integer getId() {
		return this.id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public ShippingOrder getShippingOrder() {
		return shippingOrder;
	}

	public void setShippingOrder(ShippingOrder shippingOrder) {
		this.shippingOrder = shippingOrder;
	}

	@Override
	public String toString() {
		return "ShippingPosition "
				+ "["
				+ "id=" + id + ", "
				+ "quantity=" + quantity + ", "
				+ "shippingOrder=" + shippingOrder
				+ "]";
	}

}
