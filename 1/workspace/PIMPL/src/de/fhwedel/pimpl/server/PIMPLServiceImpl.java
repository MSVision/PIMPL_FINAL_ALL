package de.fhwedel.pimpl.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ibm.icu.text.SimpleDateFormat;

import de.fhwedel.pimpl.model.Angebot;
import de.fhwedel.pimpl.model.Helper;
import de.fhwedel.pimpl.model.IllegalValueException;
import de.fhwedel.pimpl.model.Kunde;
import de.fhwedel.pimpl.model.Verfuegbarkeit;
import de.fhwedel.pimpl.model.Vertrag;
import de.fhwedel.pimpl.model.Vertrag.Vertragsstatus;
import de.fhwedel.pimpl.services.PIMPLService;

@SuppressWarnings("serial")
public class PIMPLServiceImpl extends RemoteServiceServlet implements PIMPLService {

	private static final int DAYS_TO_RECALL = 14;
	private static Properties props = new Properties();
	private static EntityManager em;
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

	protected static EntityManager getEM() {
		if (PIMPLServiceImpl.em == null) {
			try {
				if (props.getProperty("regenerate", "0").equals("1")) {
					props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
					props.put(PersistenceUnitProperties.DDL_GENERATION_MODE,
							PersistenceUnitProperties.DDL_DATABASE_GENERATION);
				}

				EntityManagerFactory emf = Persistence.createEntityManagerFactory(
						PIMPLServiceImpl.props.getProperty("persistence_unit", "pu"), props);
				if (emf != null) {
					PIMPLServiceImpl.em = emf.createEntityManager();
					PIMPLServiceImpl.em.setFlushMode(FlushModeType.COMMIT);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return PIMPLServiceImpl.em;
	}

	// NOTE: Ab hier meine Hilfsmethoden ------------------------------->

	/**
	 * Generiert ein Datum auf Basis eines Strings Nutzt die Konstante
	 * "SIMPLE_DATE_FORMAT"
	 * 
	 * @param dateStr "dd-MM-yyyy"
	 * @return Datum Date -> wenn dateStr valide, null -> dateStr invalid
	 */
	private static Date generateDate(String dateStr) {
		if (dateStr.length() > 10) {
			throw new IllegalArgumentException("Date String zu lang! Invalides Format!");
		}
		try {
			return SIMPLE_DATE_FORMAT.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

//	/**
//	 * Sammelt alle Verfügbarkeiten einer Übergebenen Liste zu einem übergebem
//	 * Angebot
//	 * 
//	 * @param availabilities übergebene Verfügbarkeiten
//	 * @param offer          übergebenen Angebot
//	 * @return eine Liste von Verfügbarkeiten "availabilities" die zum übergebenen
//	 *         Angebot "offer" gehören.
//	 */
//	private static List<Verfuegbarkeit> matchAvailabilitiesToOffer(List<Verfuegbarkeit> availabilities, Angebot offer) {
//		return availabilities.stream().filter(a -> offer.isSame(a.getAngebot())).collect(Collectors.toList());
//	}

	public static Date addDaysToDate(Date oldDate, int numbOfDaysToAdd) {
		Calendar c = Calendar.getInstance();
		c.setTime(oldDate);
		c.add(Calendar.DAY_OF_MONTH, numbOfDaysToAdd);
		return c.getTime();
	}

	public static Date addMonthsToDate(Date oldDate, int numbOfMonthsToAdd) {
		Calendar c = Calendar.getInstance();
		c.setTime(oldDate);
		c.add(Calendar.MONTH, numbOfMonthsToAdd);
		return c.getTime();
	}

	public static int getDayOfMonth() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	private static String generateUuidOfIntWithLenght(int lenght) {
		// V1
//		int upperBorder = (int) Math.pow(10, lenght);
//		int lowerBorder = (int) Math.pow(10, (lenght - 1));
//		int val = Random.nextInt(upperBorder);
//		return String.valueOf(val >= lowerBorder ? val : val + lowerBorder);
//		V2 
//		final IntStream intStr = RANDOM.ints(lenght);
//		return intStr.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
//		V3
		return UUID.randomUUID().toString().substring(1, lenght).toUpperCase();
	}

	public static String generateCustomerNumber() {
		return "K-" + generateUuidOfIntWithLenght(8);
	}

	public static String generateContractNumber() {
		return "V-" + generateUuidOfIntWithLenght(8);
	}

	// NOTE: Bis hier meine Hilfsmethoden ------------------------------->

	static {
		PIMPLServiceImpl.props = new Properties();
		try {
			PIMPLServiceImpl.props.load(new FileInputStream("pimpl.conf"));
		} catch (IOException e) {
			System.err.println("Failed to load configuration file (" + new File(".\\pimpl.conf").getAbsolutePath()
					+ "), continuing with defaults...");
		}

		if (PIMPLServiceImpl.props.getProperty("regenerate", "0").equals("1")) {
			EntityManager em = PIMPLServiceImpl.getEM();
			em.getTransaction().begin();

			// Auswerfen einer Fehlermeldung wird hier ignoriert, da dies Testdaten sind und
			// man diese
			// in der Datenbank direkt überprüfen kann.
			try {
				Angebot offer1 = Angebot.initOffer("Fiber Nord", 36, Angebot.Technologie.FIBER, 100, 40, 2999,
						generateDate("01-01-2020"), generateDate("31-12-2026"));
				em.persist(offer1);
				em.flush();

				Angebot offer2 = Angebot.initOffer("Fiber Mitte", 36, Angebot.Technologie.FIBER, 100, 40, 3299,
						generateDate("01-01-2020"), generateDate("31-12-2026"));
				em.persist(offer2);
				em.flush();

				Angebot offer3 = Angebot.initOffer("Fiber Süd", 36, Angebot.Technologie.FIBER, 100, 40, 3499,
						generateDate("01-01-2020"), generateDate("31-12-2026"));
				em.persist(offer3);
				em.flush();

				Angebot offer4 = Angebot.initOffer("LTE MAX", 36, Angebot.Technologie.LTE, 200, 100, 5999,
						generateDate("01-03-2020"), null);
				em.persist(offer4);
				em.flush();

				Angebot offer5 = Angebot.initOffer("Kabel DE", 36, Angebot.Technologie.KABEL, 50, 20, 1999,
						generateDate("01-03-2020"), null);
				em.persist(offer5);
				em.flush();

//				offer4.setBezeichnung("PENS");
//				offer4 = em.merge(offer4);
//				em.getTransaction().commit();
//				em.getTransaction().begin();
//				
//				Angebot offer6 = em.find(Angebot.class, offer5.getID());
//				offer6.setBezeichnung("KABEL FP");
//				em.getTransaction().commit();
//				em.getTransaction().begin();
//				
//				Angebot offer6 = new Angebot(offer5.getID(),"Kabel FUCKED UP", 12, Angebot.Technologie.KABEL, 50, 20, 1999,
//						generateDate("01-03-2020"), null);
//				em.persist(offer6);
//				em.flush();
//				em.getTransaction().begin();

				Verfuegbarkeit availability1 = Verfuegbarkeit.initAvailability(00001, 29999, generateDate("01-01-2020"),
						generateDate("31-12-2020"), offer1);
				em.persist(availability1);
				em.flush();

				Verfuegbarkeit availability2 = Verfuegbarkeit.initAvailability(30000, 59999, generateDate("01-01-2020"),
						generateDate("31-12-2020"), offer2);
				em.persist(availability2);
				em.flush();

				Verfuegbarkeit availability3 = Verfuegbarkeit.initAvailability(60000, 99999, generateDate("01-01-2020"),
						generateDate("31-12-2020"), offer3);
				em.persist(availability3);
				em.flush();

				Verfuegbarkeit availability4 = Verfuegbarkeit.initAvailability(00001, 99999, generateDate("01-03-2020"),
						null, offer4);
				em.persist(availability4);
				em.flush();

				Verfuegbarkeit availability5 = Verfuegbarkeit.initAvailability(00001, 59999, generateDate("01-03-2020"),
						null, offer5);
				em.persist(availability5);
				em.flush();

				em.getTransaction().commit();
			} catch (IllegalValueException e) {
				em.getTransaction().rollback();
			}

			// TODO: AB HIER FÜR FINAL LÖSCHEN! -> EIGENE TESTDATEN!
//			EntityManager em2 = PIMPLServiceImpl.getEM();
//			em.getTransaction().begin();
//			try {
//				Kunde cust1 = Kunde.initCustomer("Peterson", "Joe", generateCustomerNumber(), "Pimmelux", 24576,
//						"Bad Bramstedt");
//				em2.persist(cust1);
//				em2.flush();
//
//				Kunde cust2 = Kunde.initCustomer("Seener", "Jon", generateCustomerNumber(), "Pipalu", 24576,
//						"Bad Bramstedt");
//				em2.persist(cust2);
//				em2.flush();
//
//				Kunde cust3 = Kunde.initCustomer("Laux", "Jussy", generateCustomerNumber(), "Pimmelux", 24576,
//						"Bad Bramstedt");
//				em2.persist(cust3);
//				em2.flush();
//
//				Kunde cust4 = Kunde.initCustomer("Emonight", "Laura", generateCustomerNumber(), "Pimmelux", 24576,
//						"Bad Bramstedt");
//				em2.persist(cust4);
//				em2.flush();
//
//				em2.getTransaction().commit();
//			} catch (IllegalValueException e) {
//				em2.getTransaction().rollback();
//			}
		}
	}

	@Override
	public Kunde getCustomerByID(Integer custID) throws IllegalValueException {
		if (custID == null) {
			throw new IllegalValueException();
		}
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		Kunde costumer = em.find(Kunde.class, custID);
		em.getTransaction().commit();
		if (costumer == null) {
			throw new IllegalValueException();
		}
		return costumer;
	}

	@Override
	public List<Kunde> getAllCustomers() {
//		EntityManager em = PIMPLServiceImpl.getEM();
//		em.getTransaction().begin();
		@SuppressWarnings("unchecked")
		List<Kunde> costumers = em.createQuery("SELECT k FROM Kunde k").getResultList();
//		em.getTransaction().commit();
		return costumers;
	}

	@Override
	public List<Kunde> getCustomers(String custStr) {
		if (custStr == null) {
			return Collections.emptyList();
		}
		return this.getAllCustomers().stream().filter(c -> c.relatesToStr(custStr)).collect(Collectors.toList());
	}

	@Override
	public Kunde getCustomerByCustomerNumber(String custNumber) throws IllegalValueException {
		List<Kunde> customers = this.getAllCustomers().stream().filter(c -> c.getKundenNr().equals(custNumber))
				.collect(Collectors.toList());
		if (customers.isEmpty()) {
			throw new IllegalValueException("Es existiert kein Kunde mit dieser Kundennummer");
		}
		if (customers.size() > 1) {
			throw new IllegalValueException("Es existieren zu viele Customers für diesen String! -> PROGRAMMFEHLER!");
		}
		return customers.get(0);
	}

//	@Override
//	public Kunde addCustomer(Kunde customer) throws IllegalValueException {
//		if (customer == null || customer.isPersisted()) {
//			throw new IllegalValueException();
//		}
//		
//		blaaa
//		
//		EntityManager em = PIMPLServiceImpl.getEM();
//		em.getTransaction().begin();
//		em.persist(customer);
//		em.getTransaction().commit();
//		return customer;
//	}

	@Override
	public Kunde addCustomer(String name, String forename, String street, String zip, String city)
			throws IllegalValueException {
		if (!Helper.validStringBaseValue(name) || !Helper.validStringBaseValue(forename)
				|| !Helper.validStringBaseValue(street) || !Helper.validStringBaseValue(zip)
				|| !Helper.validStringBaseValue(city)) {
			throw new IllegalValueException();
		}
		Integer zipInt = null;
		try {
			zipInt = Integer.parseInt(zip.trim());
		} catch (NumberFormatException e) {
			throw new IllegalValueException("Die übergebene Postleitzahl ist invalid!");
		}
		if (!Helper.validZip(zipInt)) {
			throw new IllegalValueException("Die übergebene Postleitzahl ist invalid!");
		}
		Kunde customer = Kunde.initCustomer(name, forename, generateCustomerNumber(), street, zipInt, city);
		// so fange ich den Fall ab, falls eine Cust-Nr bereits existiert!
		try {
			EntityManager em = PIMPLServiceImpl.getEM();
			em.getTransaction().begin();
			em.persist(customer);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			return addCustomer(name, forename, street, zip, city);
		}
		return customer;
	}

	@Override
	public Kunde updateCustomer(String customerNumber, String name, String forename, String street, String zip,
			String city) throws IllegalValueException {
		if (customerNumber == null || name == null || forename == null || street == null || zip == null
				|| city == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		Integer zipInt = null;
		try {
			zipInt = Integer.parseInt(zip.trim());
		} catch (NumberFormatException e) {
			throw new IllegalValueException("Die übergebene Postleitzahl ist invalid!");
		}
		if (!Helper.validZip(zipInt)) {
			throw new IllegalValueException("Die übergebene Postleitzahl ist invalid!");
		}
		Kunde customerSearchResult = this.getCustomerByCustomerNumber(customerNumber);
		Kunde customer = this.getCustomerByID(customerSearchResult.getID());
		customer.setName(name);
		customer.setVorname(forename);
		customer.setStrasse(street);
		customer.setPlz(zipInt);
		customer.setOrt(city);

		return this.updateCustomer(customer);
	}

	@Override
	public Kunde updateCustomer(Kunde customer) throws IllegalValueException {
		if (customer == null || !customer.isPersisted()) {
			throw new IllegalValueException();
		}
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		customer = em.merge(customer);
		em.getTransaction().commit();
		this.updateContractDataForContact(customer);
		return customer;
	}

	private void updateContractDataForContact(Kunde customer) throws IllegalValueException {
		if (customer == null || !customer.isPersisted()) {
			throw new IllegalValueException();
		}
		List<Vertrag> contracts = this.getAllContractsOfCustomer(customer);

		for (Vertrag contract : contracts) {
			contract.setKunde(customer);
			EntityManager em = PIMPLServiceImpl.getEM();
			em.getTransaction().begin();
			contract = em.merge(contract);
			em.getTransaction().commit();
		}
	}

	@Override
	public List<Vertrag> getAllContracts() {
//		EntityManager em = PIMPLServiceImpl.getEM();
//		em.getTransaction().begin();
		@SuppressWarnings("unchecked")
		List<Vertrag> contracts = em.createQuery("SELECT v FROM Vertrag v").getResultList();
//		em.getTransaction().commit();
		return contracts;
	}

	@Override
	public List<Vertrag> getAllContractsOfCustomer(Kunde customer) {
		if (customer == null || !customer.isPersisted()) {
			return Collections.emptyList();
		}
		return this.getAllContracts().stream().filter(c -> customer.isSame(c.getKunde())).collect(Collectors.toList());
	}

	@Override
	public List<Angebot> getAllOffers() {
//		EntityManager em = PIMPLServiceImpl.getEM();
//		em.getTransaction().begin();
		@SuppressWarnings("unchecked")
		List<Angebot> offers = em.createQuery("SELECT a FROM Angebot a").getResultList();
//		em.getTransaction().commit();
		return offers;
	}

	@Override
	public Angebot getOfferByID(Integer offerId) throws IllegalValueException {
		if (offerId == null) {
			throw new IllegalValueException();
		}
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		Angebot offer = em.find(Angebot.class, offerId);
		em.getTransaction().commit();
		if (offer == null) {
			throw new IllegalValueException();
		}
		return offer;
	}

	@Override
	public List<Verfuegbarkeit> getAllAvailabilities() {
//		EntityManager em = PIMPLServiceImpl.getEM();
//		em.getTransaction().begin();
		@SuppressWarnings("unchecked")
		List<Verfuegbarkeit> availabilities = em.createQuery("SELECT v FROM Verfuegbarkeit v").getResultList();
//		em.getTransaction().commit();
		return availabilities;
	}

//	@Override
//	public Map<Angebot, List<Verfuegbarkeit>> getAllOffersAtCustomerLocation(Kunde customer) {
//		if (customer == null || !customer.isPersisted()) {
//			return Collections.emptyMap();
//		}
//		List<Verfuegbarkeit> availabilities = this.getAllAvailabilities().stream().filter(a -> a.isAvailable())
//				.filter(a -> a.zipInRange(customer.getPlz())).collect(Collectors.toList());
//		return this.getAllOffers().stream()
//				.collect(Collectors.toMap(o -> o, o -> matchAvailabilitiesToOffer(availabilities, o)));
//	}

//	@Override
//	public List<Angebotspaar> getAllAvailableOffersAtCustomerLocation(Kunde customer) {
//		List<Angebotspaar> result = Collections.emptyList();
//		if (customer == null || !customer.isPersisted()) {
//			return result;
//		}
//		List<Verfuegbarkeit> availabilities = this.getAllAvailabilities().stream().filter(a -> a.isAvailable())
//				.filter(a -> a.zipInRange(customer.getPlz())).collect(Collectors.toList());
//		for (Verfuegbarkeit a : availabilities) {
//			result.add(Angebotspaar.initAngebotspaar(a.getAngebot(), a));
//		}
//		return result;
//	}

	@Override
	public List<Verfuegbarkeit> getAllAvailablitiesAtCustomerLocation(Kunde customer) {
		if (customer == null || !customer.isPersisted()) {
			return Collections.emptyList();
		}
		return this.getAllAvailabilities().stream().filter(a -> a.isAvailable())
				.filter(a -> a.zipInRange(customer.getPlz())).collect(Collectors.toList());
	}

	@Override
	public Vertrag initContract(Kunde customer, Date contractStart) throws IllegalValueException {
		if (customer == null || !customer.isPersisted() || contractStart == null) {
			throw new IllegalValueException();
		}
		Vertrag contract = Vertrag.initVertrag(customer, generateContractNumber(), contractStart);
		// so fange ich den Fall ab, falls eine Contract-Nr bereits existiert!
		try {
			EntityManager em = PIMPLServiceImpl.getEM();
			em.getTransaction().begin();
			em.persist(contract);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			return initContract(customer, contractStart);
		}
		return contract;

	}

	@Override
	public Vertrag getContractByID(Integer contrID) throws IllegalValueException {
		if (contrID == null) {
			throw new IllegalValueException();
		}
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		Vertrag contract = em.find(Vertrag.class, contrID);
		em.getTransaction().commit();
		if (contract == null) {
			throw new IllegalValueException();
		}
		return contract;
	}

	@Override
	public Vertrag addOfferToContract(Vertrag contract, Angebot offer) throws IllegalValueException {
		if (contract == null || !contract.isPersisted() || offer == null || !offer.isPersisted()) {
			throw new IllegalValueException("Der Vertrag und das Angebot müssen bereits persistiert sein!");
		}
		final Date contractStart = contract.getVertragsbeginn();
		final Integer customerPLZ = contract.getKunde().getPlz();

		// TODO: streng genommen macht die Regel wenig sinn... wieso nur == null? bei
		// gueltigBis...

		List<Verfuegbarkeit> availabilities = this.getAllAvailabilitiesFromOffer(offer).stream().filter(
				a -> !contractStart.before(a.getGueltigVon()) && a.isInfiniteAvailable() && a.zipInRange(customerPLZ))
				.collect(Collectors.toList());

		if (contract.getVertragsbeginn().before(offer.getGueltigVon()) || !offer.isInfiniteAvailable()
				|| availabilities.isEmpty()) {
			throw new IllegalValueException(
					"Die Daten des Angebots passen nicht zum Kunde, oder sind nicht verfügbar. Bitte passe ggf. den Vertragsbeginn oder wähle ein anderen Angebot!");
		}

		contract.setAngebot(offer);
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		contract = em.merge(contract);
		em.getTransaction().commit();
		return contract;
	}

	@Override
	public List<Verfuegbarkeit> getAllAvailabilitiesFromOffer(Angebot offer) {
		if (offer == null || !offer.isPersisted()) {
			return Collections.emptyList();
		}
		return this.getAllAvailabilities().stream().filter(a -> offer.isSame(a.getAngebot()))
				.collect(Collectors.toList());
	}

	@Override
	public Vertrag adjustContractStartDate(Vertrag contract, Date newContractStartDate) throws IllegalValueException {
		if (contract == null || !contract.isPersisted() || newContractStartDate == null) {
			throw new IllegalValueException();
		}
		contract.setVertragsbeginn(newContractStartDate);
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		contract = em.merge(contract);
		em.getTransaction().commit();
		return contract;
	}

	@Override
	public Vertrag changeContractState(Vertrag contract, Vertragsstatus contractState) throws IllegalValueException {
		if (contract == null || !contract.isPersisted() || contractState == null) {
			throw new IllegalValueException("Vertrag wurde noch nicht persistiert!");
		}
		contract.setVertragsstatus(contractState);
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		contract = em.merge(contract);
		em.getTransaction().commit();
		return contract;
	}

	@Override
	public Integer getCustomerCreditRating(Kunde customer) throws IllegalValueException {
		if (customer == null || !customer.isPersisted()) {
			throw new IllegalValueException();
		}
		return ((getDayOfMonth() + customer.getPlz()) % 500) + 100;
	}

	@Override
	public Vertrag addDebitDataToContract(Vertrag contract, String iban, String bic) throws IllegalValueException {
		if (contract == null || !contract.isPersisted()) {
			throw new IllegalValueException();
		}
		contract.setIban(iban);
		contract.setBic(bic);
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		contract = em.merge(contract);
		em.getTransaction().commit();
		return contract;
	}

	@Override
	public Vertrag addSetUpDeviceShouldDate(Vertrag contract, Date setupDeviceShould) throws IllegalValueException {
		if (contract == null || !contract.isPersisted() || setupDeviceShould == null) {
			throw new IllegalValueException();
		}
		if (contract.getVertragsbeginn() == null) {
			throw new IllegalValueException("Es muss erst der <vertragsbeginn> erfasst werden!");
		}
		// TODO: Die Frage ist, wie das mit den Versandtagen gemeint ist... -> PRÜFEN!
		// Kann auch 3 tage ab Erfassungsdatum sein!
		if (setupDeviceShould.before(addDaysToDate(contract.getVertragsbeginn(), 3))) {
			throw new IllegalValueException("Das Einrichtungsdatum darf nicht (<Vertragsbeginn> + 3 Tage)  sein sein!");
		}
		contract.setEinrichtungAnschlussSoll(setupDeviceShould);
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		contract = em.merge(contract);
		em.getTransaction().commit();
		return contract;
	}

	@Override
	public Vertrag checkActionForDeviceSendingByGivenDate(Date demoDate) throws IllegalValueException {
		// TODO Auto-generated method stub
		return getContractByID(1);
	}

	@Override
	public Vertrag checkActionForContractStillInTimeByGivenDate(Date demoDate) throws IllegalValueException {
		// TODO Auto-generated method stub
		return getContractByID(1);
	}

	@Override
	public Vertrag setSetupDeviceIsFinished(Vertrag contract) throws IllegalValueException {
		if (contract == null || !contract.isPersisted()) {
			throw new IllegalValueException();
		}
		contract.setEinrichtungAnschlussIst(new Date());
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		contract = em.merge(contract);
		em.getTransaction().commit();
		return contract;
	}

	@Override
	public Vertrag recordRecall(Vertrag contract, Date recallDate) throws IllegalValueException {
		if (contract == null || !contract.isPersisted() || recallDate == null) {
			throw new IllegalValueException();
		}
		if (contract.getErfassungsdatum() == null) {
			throw new IllegalValueException("Es muss erst das <erfassungsdatum> erfasst werden!");
		}
		// Note: laut Modell nicht Modelliert, aber sinnvoll...
		if (recallDate.after(addDaysToDate(contract.getErfassungsdatum(), DAYS_TO_RECALL))) {
			throw new IllegalValueException("<eingangWiderruf> darf nicht > <vertragsbeginn> + 14 Tage sein!");
		}
		contract.setEingangWiderruf(new Date());
		contract.setVertragsstatus(Vertragsstatus.WIDERRUF);
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		contract = em.merge(contract);
		em.getTransaction().commit();
		return contract;
	}

	@Override
	public Vertrag setReceiveEndDevicesBack(Vertrag contract) throws IllegalValueException {
		if (contract == null || !contract.isPersisted()) {
			throw new IllegalValueException();
		}
		contract.setEndgeraeteZurueckErhalten((new Date()));
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		contract = em.merge(contract);
		em.getTransaction().commit();
		return contract;
	}

	@Override
	public Vertrag terminateContractTo(Vertrag contract, Date terminationDate) throws IllegalValueException {
		if (contract == null || !contract.isPersisted() || terminationDate == null) {
			throw new IllegalValueException();
		}
		if (contract.getVertragsbeginn() == null) {
			throw new IllegalValueException("Es muss erst das <vertragsbeginn> erfasst werden!");
		}
		// Note: laut Modell nicht Modelliert, aber sinnvoll...
		if (terminationDate.before(
				addMonthsToDate(contract.getVertragsbeginn(), contract.getAngebot().getMindestlaufzeitMonate()))) {
			throw new IllegalValueException("<eingangWiderruf> darf nicht > <vertragsbeginn> + 14 Tage sein!");
		}
		contract.setVertragsende(terminationDate);
		contract.setVertragsstatus(Vertragsstatus.GEKUENDIGT);
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		contract = em.merge(contract);
		em.getTransaction().commit();
		return contract;
	}

	@Override
	public List<Angebot> getOffers(String offerSearchStr) {
		if (offerSearchStr == null) {
			return Collections.emptyList();
		}
		return this.getAllOffers().stream().filter(a -> a.relatesToStr(offerSearchStr)).collect(Collectors.toList());
	}

	@Override
	public List<Vertrag> getContracts(String contractsSearchStr) {
		if (contractsSearchStr == null) {
			return Collections.emptyList();
		}
		return this.getAllContracts().stream().filter(c -> c.relatesToStr(contractsSearchStr))
				.collect(Collectors.toList());
	}

	@Override
	public Vertrag updateContract(Vertrag contract) throws IllegalValueException {
		if (contract == null || !contract.isPersisted()) {
			throw new IllegalValueException();
		}
		contract = this.getContractByID(contract.getID());
		try {
			EntityManager em = PIMPLServiceImpl.getEM();
			em.getTransaction().begin();
			contract = em.merge(contract);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw new IllegalValueException("Merge failed");
		}
		return contract;
	}

	@Override
	public Vertrag setContractEnddevicesNeeded(Vertrag contract, Boolean devicesNeeded) throws IllegalValueException {
		if (contract == null || !contract.isPersisted()) {
			throw new IllegalValueException();
		}
		contract.setEndgeraeteErforderlich(devicesNeeded);
		EntityManager em = PIMPLServiceImpl.getEM();
		em.getTransaction().begin();
		contract = em.merge(contract);
		em.getTransaction().commit();
		return contract;
	}

}
