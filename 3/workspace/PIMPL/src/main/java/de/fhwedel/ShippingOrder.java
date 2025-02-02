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
public class ShippingOrder implements Serializable {

	@Id
	@Column(updatable = false, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	private Integer id;
	
	@Column(nullable = false)
	private Integer shippingOrderNumber;
	
	@Column(nullable = true)
	private String trackingNumber; 
	
	@Column(nullable = true)
	private String deliveryService;

//	@OneToMany(cascade = CascadeType.PERSIST)
//	private List<ShippingPosition> shippingPositions;

	public ShippingOrder() {
		this(null, "", "");
	}
	
	public ShippingOrder(Integer shippingOrderNumber, String trackingNumber, String deliveryService) {
		this.shippingOrderNumber = shippingOrderNumber;
		this.trackingNumber = trackingNumber;
		this.deliveryService = deliveryService;
	}

//	public ShippingOrder(Integer shippingOrderNumber, String trackingNumber, String deliveryService) {
//		this.shippingOrderNumber = shippingOrderNumber;
//		this.trackingNumber = trackingNumber;
//		this.deliveryService = deliveryService;
//		this.shippingPositions = new ArrayList<ShippingPosition>();
//	}
	
	public Integer getId() {
		return id;
	}

	public Integer getShippingOrderNumber() {
		return shippingOrderNumber;
	}

	public void setShippingOrderNumber(Integer shippingOrderNumber) {
		this.shippingOrderNumber = shippingOrderNumber;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getDeliveryService() {
		return deliveryService;
	}

	public void setDeliveryService(String deliveryService) {
		this.deliveryService = deliveryService;
	}

//	public List<ShippingPosition> getShippingPositions() {
//		return shippingPositions;
//	}
//
//	public void setShippingPositions(List<ShippingPosition> shippingPositions) {
//		this.shippingPositions = shippingPositions;
//	}

	@Override
	public String toString() {
		return "ShippingOrder "
				+ "["
				+ "id=" + id + ", "
				+ "shippingOrderNumber=" + shippingOrderNumber + ", "
				+ "trackingNumber=" + trackingNumber + ", "
				+ "deliveryService=" + deliveryService + ", "
//				+ "shippingPositions=" + shippingPositions.toString()
				+ "]";
	}
	
}
