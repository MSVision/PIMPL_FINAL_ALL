package de.fhwedel.pimpl.services;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.fhwedel.pimpl.model.Angebot;
import de.fhwedel.pimpl.model.Kunde;
import de.fhwedel.pimpl.model.Verfuegbarkeit;
import de.fhwedel.pimpl.model.Vertrag;
import de.fhwedel.pimpl.model.Vertrag.Vertragsstatus;

public interface PIMPLServiceAsync {

	void getCustomerByID(Integer custID, AsyncCallback<Kunde> callback);

	void getAllCustomers(AsyncCallback<List<Kunde>> callback);

	void getCustomers(String custStr, AsyncCallback<List<Kunde>> callback);

	void getCustomerByCustomerNumber(String custNumber, AsyncCallback<Kunde> callback);

	void addCustomer(String name, String forename, String street, String zip,
			String city, AsyncCallback<Kunde> callback);

	void updateCustomer(String customerNumber, String name, String forename, String street, String zip, String city,
			AsyncCallback<Kunde> callback);

	void updateCustomer(Kunde customer, AsyncCallback<Kunde> callback);
	
	void getAllContracts(AsyncCallback<List<Vertrag>> callback);

	void getAllContractsOfCustomer(Kunde customer, AsyncCallback<List<Vertrag>> callback);

	void getAllOffers(AsyncCallback<List<Angebot>> callback);

	void getOfferByID(Integer offerId, AsyncCallback<Angebot> callback);

	void getAllAvailabilities(AsyncCallback<List<Verfuegbarkeit>> callback);

//	void getAllOffersAtCustomerLocation(Kunde customer, AsyncCallback<Map<Angebot, List<Verfuegbarkeit>>> callback);

//	void getAllAvailableOffersAtCustomerLocation(Kunde customer, AsyncCallback<List<Angebotspaar>> callback);

	void initContract(Kunde customer, Date contractStart, AsyncCallback<Vertrag> callback);

	void getContractByID(Integer contrID, AsyncCallback<Vertrag> callback);

	void addOfferToContract(Vertrag contract, Angebot offer, AsyncCallback<Vertrag> callback);
	
	void getAllAvailabilitiesFromOffer(Angebot offer, AsyncCallback<List<Verfuegbarkeit>> callback);
	
	void adjustContractStartDate(Vertrag contract, Date newContractStartDate, AsyncCallback<Vertrag> callback);

	void changeContractState(Vertrag contract, Vertragsstatus contractState, AsyncCallback<Vertrag> callback);

	void getCustomerCreditRating(Kunde customer, AsyncCallback<Integer> callback);

	void addDebitDataToContract(Vertrag contract, String iban, String bic, AsyncCallback<Vertrag> callback);

	void addSetUpDeviceShouldDate(Vertrag contract, Date setupDeviceShould, AsyncCallback<Vertrag> callback);

	void checkActionForDeviceSendingByGivenDate(Date demoDate, AsyncCallback<Vertrag> callback);

	void checkActionForContractStillInTimeByGivenDate(Date demoDate, AsyncCallback<Vertrag> callback);

	void setSetupDeviceIsFinished(Vertrag contract, AsyncCallback<Vertrag> callback);
	
	void recordRecall(Vertrag contract, Date recallDate, AsyncCallback<Vertrag> callback);

	void setReceiveEndDevicesBack(Vertrag contract, AsyncCallback<Vertrag> callback);

	void terminateContractTo(Vertrag contract, Date terminationDate, AsyncCallback<Vertrag> callback);

	void getAllAvailablitiesAtCustomerLocation(Kunde customer, AsyncCallback<List<Verfuegbarkeit>> callback);

	void getOffers(String offerSearchStr, AsyncCallback<List<Angebot>> callback);

	void getContracts(String contractsSearchStr, AsyncCallback<List<Vertrag>> callback);

	void updateContract(Vertrag contract, AsyncCallback<Vertrag> callback);

	void setContractEnddevicesNeeded(Vertrag contract, Boolean devicesNeeded, AsyncCallback<Vertrag> callback);
	
}
