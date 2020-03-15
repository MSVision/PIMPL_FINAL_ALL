package de.fhwedel.pimpl.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@SuppressWarnings("serial")
public class Angebot extends BaseBusinessObject {

	public enum Technologie {

		KLINGELDRAHT("Klingeldraht"), KABEL("Kabel"), FIBER("Fiber"), LTE("LTE");

		private String caption;

		private Technologie(String caption) {
			this.caption = caption;
		}

		@Override
		public String toString() {
			return this.caption;
		}

	}

	@Column(nullable = false)
	private String bezeichnung;

	@Column(nullable = false)
	private Integer mindestlaufzeitMonate;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Technologie technologie;

	@Column(nullable = false)
	private Integer bandbreiteDownlink;

	@Column(nullable = false)
	private Integer bandbreiteUplink;

	@Column(nullable = false)
	private Integer monatspreis;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date gueltigVon;

	@Column(nullable = true)
	@Temporal(TemporalType.DATE)
	private Date gueltigBis;

	public static Angebot initOffer(String bezeichnung, Integer mindestlaufzeitMonate, Technologie technologie,
			Integer bandbreiteDownlink, Integer bandbreiteUplink, Integer monatspreis, Date gueltigVon, Date gueltigBis)
			throws IllegalValueException {
		Angebot angebot = new Angebot();
		angebot.setBezeichnung(bezeichnung);
		angebot.setMindestlaufzeitMonate(mindestlaufzeitMonate);
		angebot.setTechnologie(technologie);
		angebot.setBandbreiteDownlink(bandbreiteDownlink);
		angebot.setBandbreiteUplink(bandbreiteUplink);
		angebot.setMonatspreis(monatspreis);
		angebot.setGueltigVon(gueltigVon);
		angebot.setGueltigBis(gueltigBis);
		return angebot;
	}

	// TODO: DELETE -> ONLY FOR TESTS
	public Angebot() {
		super();
	}

	// TODO: DELETE -> ONLY FOR TESTS
	public Angebot(Integer id, String bezeichnung, Integer mindestlaufzeitMonate, Technologie technologie,
			Integer bandbreiteDownlink, Integer bandbreiteUplink, Integer monatspreis, Date gueltigVon,
			Date gueltigBis) {
		super();
		super.id = id;
		this.bezeichnung = bezeichnung;
		this.mindestlaufzeitMonate = mindestlaufzeitMonate;
		this.technologie = technologie;
		this.bandbreiteDownlink = bandbreiteDownlink;
		this.bandbreiteUplink = bandbreiteUplink;
		this.monatspreis = monatspreis;
		this.gueltigVon = gueltigVon;
		this.gueltigBis = gueltigBis;
	}

	@Override
	public String getCaption() {
		return this.bezeichnung + " | " + this.technologie.toString() + " | " + "DL: "
				+ this.bandbreiteDownlink.toString() + " | " + "UL: " + this.bandbreiteUplink;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) throws IllegalValueException {
		if (!Helper.validStringBaseValue(bezeichnung)) {
			throw new IllegalValueException();
		}
		this.bezeichnung = bezeichnung;
	}

	public Integer getMindestlaufzeitMonate() {
		return mindestlaufzeitMonate;
	}

	private void setMindestlaufzeitMonate(Integer mindestlaufzeitMonate) throws IllegalValueException {
		if (!Helper.validIntegerBaseValue(mindestlaufzeitMonate)) {
			throw new IllegalValueException();
		}
		this.mindestlaufzeitMonate = mindestlaufzeitMonate;
	}

	public Technologie getTechnologie() {
		return technologie;
	}

	private void setTechnologie(Technologie technologie) throws IllegalValueException {
		if (technologie == null) {
			throw new IllegalValueException();
		}
		this.technologie = technologie;
	}

	public Integer getBandbreiteDownlink() {
		return bandbreiteDownlink;
	}

	private void setBandbreiteDownlink(Integer bandbreiteDownlink) throws IllegalValueException {
		if (!Helper.validIntegerBaseValue(bandbreiteDownlink)) {
			throw new IllegalValueException();
		}
		this.bandbreiteDownlink = bandbreiteDownlink;
	}

	public Integer getBandbreiteUplink() {
		return bandbreiteUplink;
	}

	private void setBandbreiteUplink(Integer bandbreiteUplink) throws IllegalValueException {
		if (!Helper.validIntegerBaseValue(bandbreiteUplink)) {
			throw new IllegalValueException();
		}
		this.bandbreiteUplink = bandbreiteUplink;
	}

	public Integer getMonatspreis() {
		return monatspreis;
	}
	
	public String getMonatspreisFormatted() {
		return (Integer.valueOf((this.monatspreis / 100)).toString()) + ","
				+ (Integer.valueOf((this.monatspreis % 100)).toString()) + "E";
	}

	private void setMonatspreis(Integer monatspreis) throws IllegalValueException {
		if (!Helper.validIntegerBaseValue(monatspreis)) {
			throw new IllegalValueException();
		}
		this.monatspreis = monatspreis;
	}

	public Date getGueltigVon() {
		return gueltigVon;
	}

	private void setGueltigVon(Date gueltigVon) throws IllegalValueException {
		if (gueltigVon == null || (this.gueltigBis != null && gueltigBis.before(gueltigVon))) {
			throw new IllegalValueException();
		}
		this.gueltigVon = gueltigVon;
	}

	public Date getGueltigBis() {
		return gueltigBis;
	}

	private void setGueltigBis(Date gueltigBis) throws IllegalValueException {
		if (gueltigBis != null && (this.gueltigVon != null && gueltigBis.before(this.gueltigVon))) {
			throw new IllegalValueException();
		}
		this.gueltigBis = gueltigBis;
	}

	public boolean isInfiniteAvailable() {
		return this.gueltigBis == null;
	}

	public boolean isAvailable() {
		return this.isInfiniteAvailable() || this.gueltigBis.compareTo(new Date()) >= 0;
	}

	public boolean relatesToStr(String offerSearchStr) {
		if (offerSearchStr == null) {
			throw new IllegalArgumentException("Parameter mit Wert -> null");
		}
		return this.bezeichnung.toUpperCase().startsWith(offerSearchStr.toUpperCase())
				|| this.technologie.toString().toUpperCase().startsWith(offerSearchStr.toUpperCase())
				|| this.bandbreiteDownlink.toString().toUpperCase().startsWith(offerSearchStr)
				|| this.bandbreiteUplink.toString().toUpperCase().startsWith(offerSearchStr);
	}
	
	public List<String> getAllDataAsList() {
		List<String> result = new LinkedList<>();
		result.add("Bezeichnung: " + this.bezeichnung);
		result.add("Mindestlaufzeit (Monate): " + this.mindestlaufzeitMonate);
		result.add("Technologie: " + this.technologie);
		result.add("Bandbreite Downlink: " + this.bandbreiteDownlink);
		result.add("Bandbreite Uplink: " + this.bandbreiteUplink);
		result.add("Monatspreis: " + this.getMonatspreisFormatted());
		result.add("Gültig von: " + Helper.parseDateToStr(gueltigVon));
		String guetigBisStr = this.gueltigBis == null ? "unbeschränkt" : Helper.parseDateToStr(gueltigBis);
		result.add("Gültig bis: " + guetigBisStr);
		return result;
	}

}
