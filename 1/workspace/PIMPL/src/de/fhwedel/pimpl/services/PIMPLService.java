package de.fhwedel.pimpl.services;


import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.fhwedel.pimpl.model.Angebot;
import de.fhwedel.pimpl.model.IllegalValueException;
import de.fhwedel.pimpl.model.Kunde;
import de.fhwedel.pimpl.model.Verfuegbarkeit;
import de.fhwedel.pimpl.model.Vertrag;
import de.fhwedel.pimpl.model.Vertrag.Vertragsstatus;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface PIMPLService extends RemoteService {
	
	Kunde getCustomerByID(Integer custID) throws IllegalValueException;
	
	List<Kunde> getAllCustomers();
	
	List<Kunde> getCustomers(String custStr);
	
	Kunde getCustomerByCustomerNumber(String custNumber) throws IllegalValueException;
	
	Kunde addCustomer(String name, String forename, String street, String zip,
			String city) throws IllegalValueException;
	
	Kunde updateCustomer(String customerNumber, String name, String forename, String street, String zip, String city) throws IllegalValueException;
	
	Kunde updateCustomer(Kunde customer) throws IllegalValueException;
	
	List<Vertrag> getAllContracts();
	
	List<Vertrag> getAllContractsOfCustomer(Kunde customer);
	
	List<Angebot> getAllOffers();
	
	Angebot getOfferByID(Integer offerId) throws IllegalValueException;
	
	List<Verfuegbarkeit> getAllAvailabilities();
	
//	Map<Angebot, List<Verfuegbarkeit>> getAllOffersAtCustomerLocation(Kunde customer);
	
//	public List<Angebotspaar> getAllAvailableOffersAtCustomerLocation(Kunde customer);
	
	Vertrag initContract(Kunde customer, Date contractStart) throws IllegalValueException;
	
	Vertrag getContractByID(Integer contrID) throws IllegalValueException;
	
	Vertrag addOfferToContract(Vertrag contract, Angebot offer) throws IllegalValueException;

	List<Verfuegbarkeit> getAllAvailabilitiesFromOffer(Angebot offer);

	Vertrag adjustContractStartDate(Vertrag contract, Date newContractStartDate) throws IllegalValueException;
	
	Vertrag changeContractState(Vertrag contract, Vertragsstatus contractState) throws IllegalValueException;
	
	Integer getCustomerCreditRating(Kunde customer) throws IllegalValueException;
	
	Vertrag addDebitDataToContract(Vertrag Vertrag, String iban, String bic) throws IllegalValueException;
	
	Vertrag addSetUpDeviceShouldDate(Vertrag contract, Date setupDeviceShould) throws IllegalValueException;
	
	// TODO: Unsure!
	Vertrag checkActionForDeviceSendingByGivenDate(Date demoDate) throws IllegalValueException;
	
	Vertrag checkActionForContractStillInTimeByGivenDate(Date demoDate) throws IllegalValueException;
	
	Vertrag setSetupDeviceIsFinished(Vertrag contract) throws IllegalValueException;
	
	Vertrag recordRecall(Vertrag contract, Date recallDate) throws IllegalValueException;
	
	Vertrag setReceiveEndDevicesBack(Vertrag contract) throws IllegalValueException;
	
	Vertrag terminateContractTo(Vertrag contract, Date terminationDate) throws IllegalValueException;
	
	List<Verfuegbarkeit> getAllAvailablitiesAtCustomerLocation(Kunde customer);
	
	List<Angebot> getOffers(String offerSearchStr);
	
	List<Vertrag> getContracts(String contractsSearchStr);
	
	Vertrag updateContract(Vertrag contract) throws IllegalValueException;
	
	Vertrag setContractEnddevicesNeeded(Vertrag contract, Boolean devicesNeeded) throws IllegalValueException;
	
}
