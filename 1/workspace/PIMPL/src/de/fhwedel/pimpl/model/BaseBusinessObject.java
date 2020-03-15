package de.fhwedel.pimpl.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
@SuppressWarnings("serial")
public abstract class BaseBusinessObject implements BusinessObject<Integer> , Serializable {
	
	@Id
	@Column(updatable = false, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	public Integer id;
	
	@Override
	public Integer getID() {
		return this.id;
	}

	public boolean isPersisted() {
		return this.id != null;
	}
	
	public boolean isSame(BaseBusinessObject baseBusinessObject) {
		if (baseBusinessObject == null || !baseBusinessObject.isPersisted()) {
			throw new IllegalArgumentException("Parameter mit Wert -> null oder noch nicht persisted");
		}
		return this.hasSameID(baseBusinessObject.getID());
	}

	public boolean hasSameID(Integer id) {
		if (!this.isPersisted() || id == null) {
			throw new IllegalArgumentException("Parameter mit Wert -> null");
		}
		return this.id == id;
	}
	
}
