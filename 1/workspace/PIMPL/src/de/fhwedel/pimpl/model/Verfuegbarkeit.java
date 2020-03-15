package de.fhwedel.pimpl.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;


@Entity
@SuppressWarnings("serial")
public class Verfuegbarkeit extends BaseBusinessObject implements Comparable<Verfuegbarkeit> {

	@Column(nullable = false)
	private Integer plzAnfang;

	@Column(nullable = false)
	private Integer plzEnde;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date gueltigVon;

	// null == unbeschränkt
	@Column(nullable = true)
	@Temporal(TemporalType.DATE)
	private Date gueltigBis;
	
	@ManyToOne(fetch=FetchType.EAGER, optional = false)
	Angebot angebot;
	
	public static Verfuegbarkeit initAvailability(Integer zipStart, Integer zipEnd, 
			Date validFrom, Date validTo, Angebot offer) 
					throws IllegalValueException {
		Verfuegbarkeit availability = new Verfuegbarkeit();
		availability.setPlzAnfang(zipStart);
		availability.setPlzEnde(zipEnd);
		availability.setGueltigVon(validFrom);
		availability.setGueltigBis(validTo);
		availability.setAngebot(offer);
		return availability;
	}

	@Override
	public String getCaption() {
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yyyy");
        String guetigVonStr = dtf.format(this.gueltigVon);
        String guetigBisStr = this.gueltigBis == null ? "unbegrenzt" : dtf.format(this.gueltigBis);
		return this.angebot.getBezeichnung() + "\r\n" + "-> (" + guetigVonStr + " - " + guetigBisStr + ")";
	}

	public Integer getPlzAnfang() {
		return plzAnfang;
	}

	private void setPlzAnfang(Integer plzAnfang) throws IllegalValueException {
		if (!Helper.validZip(plzAnfang)
				|| (this.plzEnde != null && this.plzEnde < plzAnfang)) {
			throw new IllegalValueException();
		}
		this.plzAnfang = plzAnfang;
	}

	public Integer getPlzEnde() {
		return plzEnde;
	}
	
	private void setPlzEnde(Integer plzEnde) throws IllegalValueException {
		if (!Helper.validZip(plzEnde)
				|| (this.plzAnfang != null && plzEnde < this.plzAnfang)) {
			throw new IllegalValueException();
		}
		this.plzEnde = plzEnde;
	}

	public Date getGueltigVon() {
		return gueltigVon;
	}

	private void setGueltigVon(Date gueltigVon) throws IllegalValueException {
		if (gueltigVon == null 
				|| (this.gueltigBis != null && this.gueltigBis.before(gueltigVon))) {
			throw new IllegalValueException();
		}
		this.gueltigVon = gueltigVon;
	}

	public Date getGueltigBis() {
		return gueltigBis;
	}

	//TODO: Integritätsbedingungen überprüfen
	//null ok -> unbeschränkt
	private void setGueltigBis(Date gueltigBis) throws IllegalValueException {
		if (this.gueltigVon != null 
				&& gueltigBis != null 
				&& gueltigBis.before(this.gueltigVon)) {
			throw new IllegalValueException();
		}
		this.gueltigBis = gueltigBis;
	}
	
	// Angebot muss mindestens schonmal persisted worden sein!
	private void setAngebot(Angebot angebot) throws IllegalValueException {
		if (angebot == null || !angebot.isPersisted()) {
			throw new IllegalValueException();
		}
		this.angebot = angebot;
	}
	
	public Angebot getAngebot() {
		return this.angebot;
	}
	
	public boolean isInfiniteAvailable() {
		return this.gueltigBis == null;
	}
	
	public boolean isAvailable() {
		return this.isInfiniteAvailable()
				|| this.gueltigBis.compareTo(new Date()) >= 0;
	}
	
	public boolean zipInRange(Integer checkZip) {
		if (checkZip == null ) {
			throw new IllegalArgumentException("Parameter mit Wert -> null");
		}
		return this.plzAnfang <= checkZip 
				&&  this.plzEnde >= checkZip;
	}

	@Override
	public int compareTo(Verfuegbarkeit availability) {
	    return this.plzAnfang.compareTo(availability.getPlzAnfang());
	}
	
	@Override
	public String toString() {
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yyyy");
        String guetigVonStr = dtf.format(this.gueltigVon);
        String guetigBisStr = this.gueltigBis == null ? "unbegrenzt" : dtf.format(this.gueltigBis);
        String plzAnfang = NumberFormat.getFormat("00000").format(this.plzAnfang);
        String plzEnde = NumberFormat.getFormat("00000").format(this.plzEnde);
		return plzAnfang + " - " + plzEnde + " -> (" + guetigVonStr + " - " + guetigBisStr + ")";
	}

}
