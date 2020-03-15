package de.fhwedel.pimpl.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@SuppressWarnings("serial")
public class Vertrag extends BaseBusinessObject {

	public enum Vertragsstatus {

		ANFRAGE("Anfrage", "ANG"), ABGELEHNT_WEGEN_BONITAET("Abgelehnt wegen Bonitaet", "AWB"),
		ANGEBOT_NICHT_VERFUEGBAR("Angebot nicht verfuegbar", "ANV"), AUFTRAG("Auftrag", "AUF"), AKTIV("Aktiv", "AKT"),
		WIDERRUF("Widerruf", "WID"), GEKUENDIGT("Gekuendigt", "GEK"), INAKTIV("Inaktiv", "INAK");

		private String caption;

		private String contraction;
		
		public static String getContractionForText(String value) {
			return value;

		}

		Vertragsstatus() {
			this.caption = this.name();
		}

		private Vertragsstatus(String caption, String contraction) {
			this.caption = caption;
			this.contraction = contraction;
		}

		public String toString() {
			return this.caption;
		}
		
		public String getContraction() {
			return this.contraction;
		}

	}

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date erfassungsdatum;

	@Column(nullable = false, unique = true)
	private String vertragsnummer;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Vertragsstatus vertragsstatus;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date vertragsbeginn;

	@Column(nullable = true)
	private String iban;

	@Column(nullable = true)
	private String bic;

	@Column(nullable = true)
	@Temporal(TemporalType.DATE)
	private Date einrichtungAnschlussSoll;

	@Column(nullable = true)
	@Temporal(TemporalType.DATE)
	private Date einrichtungAnschlussIst;

	@Column(nullable = true)
	@Temporal(TemporalType.DATE)
	private Date versandEndgeraete;

	@Column(nullable = true)
	@Temporal(TemporalType.DATE)
	private Date endgeraeteZurueckErhalten;

	@Column(nullable = true)
	@Temporal(TemporalType.DATE)
	private Date eingangWiderruf;

	@Column(nullable = true)
	@Temporal(TemporalType.DATE)
	private Date vertragsende;
	
	@Column(nullable = true)
	private Boolean endgeraeteErforderlich;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	Kunde kunde;

	@ManyToOne(fetch = FetchType.EAGER)
	Angebot angebot;

	public static Vertrag initVertrag(Kunde customer, String contractNr, Date contractStart)
			throws IllegalValueException {
		Date today = new Date();
		if (contractStart == null || contractNr == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		if (contractNr.isEmpty()) {
			throw new IllegalArgumentException("String -> <contractNr> darf nicht leer sein.");
		}
		if (contractStart.before(today)) {
			throw new IllegalValueException("Das Start-Datum darf nicht < <today> sein!");
		}

		Vertrag contract = new Vertrag();
		contract.setKunde(customer);
		contract.initVertragsstatus();
		contract.setVertragsnummer(contractNr);
		contract.setErfassungsdatum(today);
		contract.setVertragsbeginn(contractStart);
		return contract;
	}

	private Vertrag() {
		super();
	}

	@Override
	public String getCaption() {
		return this.vertragsnummer;
	}
	public Boolean getEndgeraeteErforderlich() {
		return endgeraeteErforderlich;
	}

	public void setEndgeraeteErforderlich(Boolean endgeraeteErforderlich) {
		this.endgeraeteErforderlich = endgeraeteErforderlich;
	}

	public Date getErfassungsdatum() {
		return erfassungsdatum;
	}
	
	private void initVertragsstatus() {
		this.vertragsstatus = Vertragsstatus.ANFRAGE;
	}

	private void setErfassungsdatum(Date erfassungsdatum) throws IllegalValueException {
		if (erfassungsdatum == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		if (this.vertragsbeginn != null && this.vertragsbeginn.before(erfassungsdatum)) {
			throw new IllegalValueException("Das Erfassungsdatum muss <= <Vertragsbeginn> sein!");
		}
		this.erfassungsdatum = erfassungsdatum;
	}

	public String getVertragsnummer() {
		return vertragsnummer;
	}

	private void setVertragsnummer(String vertragsnummer) {
		if (vertragsnummer == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		if (vertragsnummer.isEmpty()) {
			throw new IllegalArgumentException("String -> <vertragsnummer> darf nicht leer sein.");
		}
		this.vertragsnummer = vertragsnummer;
	}

	public Vertragsstatus getVertragsstatus() {
		return vertragsstatus;
	}

	public void setVertragsstatus(Vertragsstatus status) {
		if (status == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		this.vertragsstatus = status;
	}

	public Date getVertragsbeginn() {
		return vertragsbeginn;
	}

	/**
	 * Setzt den Vertragsbeginn. WICHTIG: -> Erfassungsdatum sollte vor dem
	 * Vertragsbeginn festgelegt werden!
	 * 
	 * @param vertragsbeginn Vertragsbeginn >= <Heute>
	 * @throws IllegalValueException Wenn invalide Parameter übergeben werden.
	 */
	public void setVertragsbeginn(Date vertragsbeginn) throws IllegalValueException {
		if (vertragsbeginn == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		if (this.erfassungsdatum == null) {
			throw new IllegalValueException("Es muss erst ein Erfassungsdatum erfasst werden!");
		}
		if (vertragsbeginn.before(this.erfassungsdatum)) {
			throw new IllegalValueException("Das Start-Datum darf nicht < <today> sein!");
		}
		this.vertragsbeginn = vertragsbeginn;
	}

	public String getIban() {
		return iban;
	}

	/**
	 * Legt die Iban fest.
	 * 
	 * @param iban 22 Zeichen -> Deutscher Standard
	 * @throws IllegalValueException
	 */
	public void setIban(String iban) throws IllegalValueException {
		// TODO: Deutsche IBAN -> 22 Zeichen...
		// Für Einfachheit der Testdaten deaktiviert...
//		if (!Helper.validStringBaseValue(iban) /* || iban.length() != 22 */) {
//			throw new IllegalValueException();
//		}
		this.iban = iban;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) throws IllegalValueException {
//		if (!Helper.validStringBaseValue(bic) /* || bic.length() != 11 */) {
//			throw new IllegalValueException();
//		}
		this.bic = bic;
	}

	public Date getEinrichtungAnschlussSoll() {
		return einrichtungAnschlussSoll;
	}

	public void setEinrichtungAnschlussSoll(Date einrichtungAnschlussSoll) throws IllegalValueException {
//		if (einrichtungAnschlussSoll == null) {
//			throw new IllegalArgumentException(new NullPointerException());
//		}
//		if (this.vertragsbeginn == null) {
//			throw new IllegalValueException("Es muss erst der <vertragsbeginn> erfasst werden!");
//		}
		this.einrichtungAnschlussSoll = einrichtungAnschlussSoll;
	}

	public Date getEinrichtungAnschlussIst() {
		return einrichtungAnschlussIst;
	}

	public void setEinrichtungAnschlussIst(Date einrichtungAnschlussIst) throws IllegalValueException {
		// TODO: Hier entwickeln wenn Logic im Service gebaut wird!
//		if (einrichtungAnschlussIst == null) {
//			throw new IllegalArgumentException(new NullPointerException());
//		}
//		if (this.einrichtungAnschlussSoll == null) {
//			throw new IllegalValueException("Es muss erst das <einrichtungAnschlussSoll> erfasst werden!");
//		}
		// Note: laut Modell kann die Einrichtung er nach ablaufen der Zeit zum
		// "Einrichten Anschluss Soll" stattfinden!
//		if (einrichtungAnschlussIst.before(this.einrichtungAnschlussSoll)) {
//			throw new IllegalValueException(
//					"Das <einrichtungAnschlussIst> darf nicht < <einrichtungAnschlussSoll> sein!");
//		}
		this.einrichtungAnschlussIst = einrichtungAnschlussIst;
	}

	public Date getVersandEndgeraete() {
		return versandEndgeraete;
	}

	public void setVersandEndgeraete(Date versandEndgeraete) throws IllegalValueException {
//		if (versandEndgeraete == null) {
//			throw new IllegalArgumentException(new NullPointerException());
//		}
//		if (this.einrichtungAnschlussSoll == null) {
//			throw new IllegalValueException("Es muss erst das <einrichtungAnschlussSoll> erfasst werden!");
//		}
		// Note: laut Modell nicht Modelliert, aber sinnvoll...
//		if (versandEndgeraete.before(this.erfassungsdatum)) {
//			throw new IllegalValueException("<versandEndgeraete> darf nicht < <erfassungsdatum> sein!");
//		}
		this.versandEndgeraete = versandEndgeraete;
	}

	public Date getEndgeraeteZurueckErhalten() {
		return endgeraeteZurueckErhalten;
	}

	public void setEndgeraeteZurueckErhalten(Date endgeraeteZurueckErhalten) throws IllegalValueException {
//		if (endgeraeteZurueckErhalten == null) {
//			throw new IllegalArgumentException(new NullPointerException());
//		}
//		if (this.versandEndgeraete == null) {
//			throw new IllegalValueException("Es muss erst das <versandEndgeräte> erfasst werden!");
//		}
		// Note: laut Modell nicht Modelliert, aber sinnvoll...
//		if (endgeraeteZurueckErhalten.before(this.versandEndgeraete)) {
//			throw new IllegalValueException("<endgeraeteZurueckErhalten> darf nicht < <versandEndgeraete> sein!");
//		}
		this.endgeraeteZurueckErhalten = endgeraeteZurueckErhalten;
	}

	public Date getEingangWiderruf() {
		return eingangWiderruf;
	}

	public void setEingangWiderruf(Date eingangWiderruf) throws IllegalValueException {
//		if (eingangWiderruf == null) {
//			throw new IllegalArgumentException(new NullPointerException());
//		}
//		if (this.erfassungsdatum == null) {
//			throw new IllegalValueException("Es muss erst das <erfassungsdatum> erfasst werden!");
//		}
		this.eingangWiderruf = eingangWiderruf;
	}

	public Date getVertragsende() {
		return vertragsende;
	}

	public void setVertragsende(Date vertragsende) throws IllegalValueException {
//		if (vertragsende == null) {
//			throw new IllegalArgumentException(new NullPointerException());
//		}
//		if (this.vertragsbeginn == null) {
//			throw new IllegalValueException("Es muss erst das <vertragsbeginn> erfasst werden!");
//		}
		this.vertragsende = vertragsende;
	}

	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
//		if (kunde == null) {
//			throw new IllegalArgumentException(new NullPointerException());
//		}

		this.kunde = kunde;
	}

	public Angebot getAngebot() {
		return angebot;
	}

	public void setAngebot(Angebot angebot) {
//		if (angebot == null) {
//			throw new IllegalArgumentException(new NullPointerException());
//		}
//		if (!angebot.isPersisted()) {
//			throw new IllegalValueException("Das Angebot muss bereits persistiert sein!");
//		}
		this.angebot = angebot;
	}
	
	public boolean relatesToStr(String contractsSearchStr) {
		if (contractsSearchStr == null) {
			throw new IllegalArgumentException("Parameter mit Wert -> null");
		}
		return this.vertragsnummer.toUpperCase().startsWith(contractsSearchStr.toUpperCase());
	}

}
