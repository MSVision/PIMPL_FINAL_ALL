package de.fhwedel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@SuppressWarnings("serial")
public class ContractPosition implements Serializable {
	
	@Id
	@Column(updatable = false, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	private Integer id;
	
	@Column(nullable = false)
	private Integer price;
	
	@Column(nullable = false)
	private Integer targetQuantity;
	
	@Column(nullable = false)
	private Integer actualQuantity;

//	@OneToMany(mappedBy = "contractPosition", cascade = CascadeType.PERSIST)
	@OneToMany(cascade = CascadeType.PERSIST)
	private List<ShippingPosition> shippingPositions;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Item item;

	public static ContractPosition initContractPosition(Item item, Integer targetQuantity, Integer actualQuantity) {
		ContractPosition result = new ContractPosition(item.getPrice() * targetQuantity, targetQuantity, actualQuantity);
		result.setItem(item);
		return result;
	}
	
	public ContractPosition() {
		this(null, null, null);
	}

	public ContractPosition(Integer price, Integer targetQuantity, Integer actualQuantity) {
		this.price = price;
		this.targetQuantity = targetQuantity;
		this.actualQuantity = actualQuantity;
		this.shippingPositions = new ArrayList<ShippingPosition>();
	}
	
	public Integer getId() {
		return id;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getTargetQuantity() {
		return targetQuantity;
	}

	public void setTargetQuantity(Integer targetQuantity) {
		this.targetQuantity = targetQuantity;
	}

	public Integer getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(Integer actualQuantity) {
		this.actualQuantity = actualQuantity;
	}

	public List<ShippingPosition> getShippingPositions() {
		return shippingPositions;
	}

	public void setShippingPositions(List<ShippingPosition> shippingPositions) {
		this.shippingPositions = shippingPositions;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public String toString() {
		return "ContractPosition "
				+ "["
				+ "id=" + id + ", "
				+ "price=" + price + ", "
				+ "targetQuantity=" + targetQuantity + ", "
				+ "actualQuantity=" + actualQuantity + ", "
				+ "shippingPositions=" + shippingPositions + ", "
				+ "item=" + item.getCaption() 
				+ "]";
	}
	
}
