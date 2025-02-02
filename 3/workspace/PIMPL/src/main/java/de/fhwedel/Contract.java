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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@SuppressWarnings("serial")
@NamedQueries({
	   @NamedQuery(
	           name="getAllContracts",
	           query="SELECT c FROM Contract c"
	   )
	})
public class Contract implements Serializable {
	
	public enum DeliveryMethod {
		
		COMPLETE("Complete"),
		PORTION("Portion");
		
		private String caption;

		DeliveryMethod() {
			this.caption = this.name();
		}
		
		DeliveryMethod(String caption) {
			this.caption = caption;
		}

		public String toString() {
			return this.caption;
		}
		
	}
	
	@Id
	@Column(updatable = false, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	private Integer id;
	
	@Column(nullable = false)
	private Integer contractNumber;

	@Column(nullable = false)
	private DeliveryMethod deliveryMethod;

	@ManyToOne(fetch=FetchType.EAGER)
	private DispositionCustomer dispositionCustomer;

	@OneToMany(cascade = CascadeType.PERSIST)
	private List<ContractPosition> contractPositions;

	public static Contract initContract(Integer contractNumber, DeliveryMethod deliveryMethod, 
			DispositionCustomer dispositionCustomer, List<ContractPosition> contractPositions) {
		Contract result = new Contract(contractNumber, deliveryMethod);
		result.setDispositionCustomer(dispositionCustomer);
		result.setContractPositions(contractPositions);
		return result;
	}
	
	public Contract() {
		this(null, null);
	}

	public Contract(Integer contractNumber, DeliveryMethod deliveryMethod) {
		this.contractNumber = contractNumber;
		this.deliveryMethod = deliveryMethod;
		this.contractPositions = new ArrayList<ContractPosition>();
	}

	public Integer getId() {
		return id;
	}

	public Integer getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(Integer contractNumber) {
		this.contractNumber = contractNumber;
	}

	public DeliveryMethod getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public DispositionCustomer getDispositionCustomer() {
		return dispositionCustomer;
	}

	public void setDispositionCustomer(DispositionCustomer dispositionCustomer) {
		this.dispositionCustomer = dispositionCustomer;
	}

	public List<ContractPosition> getContractPositions() {
		return contractPositions;
	}

	public void setContractPositions(List<ContractPosition> contractPositions) {
		this.contractPositions = contractPositions;
	}

	@Override
	public String toString() {
		return "Contract "
				+ "["
				+ "id=" + id + ", "
				+ "contractNumber=" + contractNumber + ", "
				+ "deliveryMethod=" + deliveryMethod + ", "
				+ "dispositionCustomer=" + dispositionCustomer + ", "
				+ "contractPositions=" + contractPositions 
				+ "]";
	}

}
