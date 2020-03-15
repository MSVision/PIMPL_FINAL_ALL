package de.fhwedel.pimpl.masks;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import de.fhwedel.pimpl.model.Kunde;
import de.fhwedel.pimpl.model.Verfuegbarkeit;
import de.fhwedel.pimpl.model.Vertrag;
import de.fhwedel.pimpl.services.PIMPLService;
import de.fhwedel.pimpl.services.PIMPLServiceAsync;
import de.fhwedel.pimpl.widgets.BOSelectListBox;
import de.fhwedel.pimpl.widgets.InputEvent;
import de.fhwedel.pimpl.widgets.InputHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;

public class CustomerMask extends Composite {

	private static CustomerMaskUiBinder uiBinder = GWT.create(CustomerMaskUiBinder.class);

	interface CustomerMaskUiBinder extends UiBinder<Widget, CustomerMask> {
	}

	private final PIMPLServiceAsync server = GWT.create(PIMPLService.class);

	private Kunde currentSelectedConustomer = null;
	
	// Kundenliste

	@UiField
	TextBox customerSearchTextBox;

	@UiField
	BOSelectListBox<Kunde, Integer> customersListBox;

	// Kundendaten

	@UiField
	TextBox customerNumberTextBox;

	@UiField
	TextBox customerNameTextBox;

	@UiField
	TextBox customerForenameTextBox;

	@UiField
	TextBox customerStreetTextBox;

	@UiField
	TextBox customerZipTextBox;

	@UiField
	TextBox customerCityTextBox;

	// Kundenaktionsbuttons

	@UiField
	Button initNewCustomerBtn;

	@UiField
	Button addCustomerBtn;

	@UiField
	Button cancelAddCustomerBtn;

	@UiField
	Button editCustomerBtn;

	@UiField
	Button saveCustomerBtn;

	@UiField
	Button initNewContractBtn;
	
	// Vertragsliste
	
	@UiField
	BOSelectListBox<Vertrag, Integer> contractsListBox;
	
	// Angebotsliste

	@UiField
	BOSelectListBox<Verfuegbarkeit, Integer> offersListBox;
	
	public CustomerMask() {
		initWidget(uiBinder.createAndBindUi(this));

		contractsListBox.setEnabled(false);
		offersListBox.setEnabled(false);
		customerNumberTextBox.setEnabled(false);
		this.setCustomerDataTextFieldsEnabled(false);
		this.setButtonVisible(initNewCustomerBtn, true);
		this.setButtonVisible(addCustomerBtn, false);
		this.setButtonVisible(cancelAddCustomerBtn, false);
		this.setButtonVisible(editCustomerBtn, false);
		this.setButtonVisible(saveCustomerBtn, false);
		this.setButtonVisible(initNewContractBtn, false);

		this.updateCustomerList();
		this.initCustomerSearchTextBoxHandler();
	}

	// TODO: Erstmal drin behalten falls ich doch nochmal den Button changes mehr funktionalität geben will...
	private void setButtonVisible(Button btn, boolean visible) {
		if (btn == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		if (visible) {
			btn.setVisible(true);
			return;
		}
		btn.setVisible(false);
	}

	private void updateCustomerList() {
		server.getAllCustomers(new AsyncCallback<List<Kunde>>() {
			@Override
			public void onSuccess(List<Kunde> result) {
				//Window.alert("Laden aller Kunden abgeschlossen, Ergebnisse: " + result.size());
//				List<Kunde> customers = new LinkedList<>(result);
				CustomerMask.this.customersListBox.setAcceptableValues(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Fehler beim Laden der Kundenliste!");
			}
		});
	}
	
	private void updateContractList(Kunde customer) {
		server.getAllContractsOfCustomer(customer, new AsyncCallback<List<Vertrag>>() {
			@Override
			public void onSuccess(List<Vertrag> result) {
				if (result.isEmpty()) {
					contractsListBox.setEnabled(false);
					return;
				}
				CustomerMask.this.contractsListBox.setAcceptableValues(result);
				contractsListBox.setEnabled(true);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Zum übergebenen Kundenobjekt kann keine Vertragsliste geladen werden!");
			}
		});
	}
	
	private void updateOfferList(Kunde customer) {
		server.getAllAvailablitiesAtCustomerLocation(customer, new AsyncCallback<List<Verfuegbarkeit>>() {
			@Override
			public void onSuccess(List<Verfuegbarkeit> result) {
				if (result.isEmpty()) {
					offersListBox.setEnabled(false);
					return;
				}
				CustomerMask.this.offersListBox.setAcceptableValues(result);
				offersListBox.setEnabled(true);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Zum übergebenen Kundenobjekt können keine passenden Angebote geladen werden!");
			}
		});
	}

	private void fillCustomersListBoxByStr(String custStr) {
		server.getCustomers(custStr.trim(), new AsyncCallback<List<Kunde>>() {
			@Override
			public void onSuccess(List<Kunde> result) {
				// Window.alert("Suche abgeschlossen, Ergebnisse: " + result.size());
				CustomerMask.this.customersListBox.setAcceptableValues(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Fehler beim Laden der Kundenliste!");
			}
		});
	}

	private void initCustomerSearchTextBoxHandler() {
		customerSearchTextBox.addDomHandler(new InputHandler() {
			@Override
			public void onInput(InputEvent event) {
				fillCustomersListBoxByStr(customerSearchTextBox.getText());
			}
		}, InputEvent.getType());

	};

	private void clearCustomerDataTextFields() {
		customerNumberTextBox.setValue("");
		customerNumberTextBox.setValue("");
		customerNameTextBox.setValue("");
		customerForenameTextBox.setValue("");
		customerStreetTextBox.setValue("");
		customerZipTextBox.setValue("");
		customerCityTextBox.setValue("");
	}

	private void setCustomerDataTextFieldsEnabled(boolean val) {
		customerNameTextBox.setEnabled(val);
		customerForenameTextBox.setEnabled(val);
		customerStreetTextBox.setEnabled(val);
		customerZipTextBox.setEnabled(val);
		customerCityTextBox.setEnabled(val);
	}

	private void fillCustomerDataTextFields(Kunde customer) {
		customerNumberTextBox.setValue(customer.getKundenNr());
		customerNameTextBox.setValue(customer.getName());
		customerForenameTextBox.setValue(customer.getVorname());
		customerStreetTextBox.setValue(customer.getStrasse());
		customerZipTextBox.setValue(customer.getPlz().toString());
		customerCityTextBox.setValue(customer.getOrt());
	}

	@UiHandler("customersListBox")
	public void selectCustomer(ClickEvent c) {
		if (this.customersListBox.getSelectedIndex() >= 0) {
			// Window.alert("Der Name des Kunden lautet: " +
			// this.customersListBox.getValue().getFullName());
			Kunde customer = this.customersListBox.getValue();
			this.fillCustomerDataTextFields(customer);
			this.setButtonVisible(initNewCustomerBtn, true);
			this.setButtonVisible(addCustomerBtn, false);
			this.setButtonVisible(cancelAddCustomerBtn, false);
			this.setButtonVisible(editCustomerBtn, true);
			this.setButtonVisible(saveCustomerBtn, false);
			this.setButtonVisible(initNewContractBtn, true);
			this.updateContractList(customer);
			this.updateOfferList(customer);
		}
	}

	@UiHandler("initNewCustomerBtn")
	public void initNewCustomer(ClickEvent c) {
		this.clearCustomerDataTextFields();
		this.setCustomerDataTextFieldsEnabled(true);
		this.setButtonVisible(initNewCustomerBtn, false);
		this.setButtonVisible(addCustomerBtn, true);
		this.setButtonVisible(cancelAddCustomerBtn, true);
		this.setButtonVisible(editCustomerBtn, false);
		this.setButtonVisible(saveCustomerBtn, false);
		this.setButtonVisible(initNewContractBtn, false);
		this.customersListBox.setEnabled(false);
		this.contractsListBox.clear();
		this.contractsListBox.setEnabled(false);
		this.offersListBox.clear();
		this.offersListBox.setEnabled(false);
	}

	@UiHandler("addCustomerBtn")
	public void addCustomer(ClickEvent c) {
		server.addCustomer(customerNameTextBox.getValue(), customerForenameTextBox.getValue(),
				customerStreetTextBox.getValue(), customerZipTextBox.getValue(), customerCityTextBox.getValue(),
				new AsyncCallback<Kunde>() {
					@Override
					public void onSuccess(Kunde result) {
						Window.alert("Der Kunde wurde erfolgreich angelegt!" + "\n" + "Kunden-ID: " + result.getID()
								+ "\n" + "Kundennummer: " + result.getKundenNr() + "\n" + "Name: "
								+ result.getFullName() + "\n" + "Straße: " + result.getStrasse() + "\n" + "Plz: "
								+ result.getPlz() + "\n" + "Ort: " + result.getOrt());
						setCustomerDataTextFieldsEnabled(false);
						setButtonVisible(initNewCustomerBtn, true);
						setButtonVisible(addCustomerBtn, false);
						setButtonVisible(cancelAddCustomerBtn, false);
						setButtonVisible(initNewContractBtn, true);
						customerNumberTextBox.setValue(result.getKundenNr());
						updateCustomerList();
						customersListBox.setEnabled(true);
						updateContractList(result);
						updateOfferList(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Die eingebebenen Daten sind invalide. Bitte überprüfen Sie ihre Eingaben.");
					}
				});
	}

	@UiHandler("cancelAddCustomerBtn")
	public void cancelAddCustomer(ClickEvent c) {
		this.clearCustomerDataTextFields();
		this.setCustomerDataTextFieldsEnabled(false);
		this.clearCustomerDataTextFields();
		this.setButtonVisible(initNewCustomerBtn, true);
		this.setButtonVisible(addCustomerBtn, false);
		this.setButtonVisible(cancelAddCustomerBtn, false);
		this.setButtonVisible(initNewContractBtn, false);
		this.customersListBox.setEnabled(true);
	}

	@UiHandler("editCustomerBtn")
	public void editCustomer(ClickEvent c) {
		this.setCustomerDataTextFieldsEnabled(true);
		this.setButtonVisible(initNewCustomerBtn, false);
		this.setButtonVisible(addCustomerBtn, false);
		this.setButtonVisible(cancelAddCustomerBtn, false);
		this.setButtonVisible(editCustomerBtn, false);
		this.setButtonVisible(saveCustomerBtn, true);
		this.setButtonVisible(initNewContractBtn, false);
		this.customersListBox.setEnabled(false);
	}

	@UiHandler("saveCustomerBtn")
	public void saveCustomer(ClickEvent c) {
		server.updateCustomer(customerNumberTextBox.getValue(), customerNameTextBox.getValue(),
				customerForenameTextBox.getValue(), customerStreetTextBox.getValue(), customerZipTextBox.getValue(),
				customerCityTextBox.getValue(), new AsyncCallback<Kunde>() {
					@Override
					public void onSuccess(Kunde result) {
						Window.alert("Der Kunde wurde erfolgreich aktualisiert!" + "\n" + "Kunden-ID: " + result.getID()
								+ "\n" + "Kundennummer: " + result.getKundenNr() + "\n" + "Name: "
								+ result.getFullName() + "\n" + "Straße: " + result.getStrasse() + "\n" + "Plz: "
								+ result.getPlz() + "\n" + "Ort: " + result.getOrt());
						setCustomerDataTextFieldsEnabled(false);
						setButtonVisible(initNewCustomerBtn, true);
						setButtonVisible(addCustomerBtn, false);
						setButtonVisible(cancelAddCustomerBtn, false);
						setButtonVisible(editCustomerBtn, true);
						setButtonVisible(saveCustomerBtn, false);
						setButtonVisible(initNewContractBtn, true);
						updateCustomerList();
						customersListBox.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
				});
	}

	@UiHandler("initNewContractBtn")
	public void initNewContract(ClickEvent c) {
		// TODO: add this maybe latr wenn noch Zeit ist!
//		if (this.currentSelectedConustomer == null || this.currentSelectedConustomer.getID() == null) {
//			Window.alert("Es muss zuerst ein Kunden ausgewählt werden, bevor man einen Vertrag angelegen kann.");
//			return;
//		}
		
		String custNumber = this.customerNumberTextBox.getValue();
		Panel parent = (Panel) this.getParent();
		server.getCustomerByCustomerNumber(custNumber, new AsyncCallback<Kunde>() {
			@Override
			public void onSuccess(Kunde result) {
				parent.clear();
				parent.add(new ContractMask(result));
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}
	
	@UiHandler("contractsListBox")
	public void selectContract(ClickEvent c) {
		if (this.contractsListBox.getSelectedIndex() >= 0) {
			Panel parent = (Panel) this.getParent();
			parent.clear();
			parent.add(new ContractMask(this.contractsListBox.getValue()));
		}
	}
	
	@UiHandler("offersListBox")
	public void selectOffer(ClickEvent c) {
		if (this.offersListBox.getSelectedIndex() >= 0) {
			Panel parent = (Panel) this.getParent();
			parent.clear();
			parent.add(new OfferMask(this.offersListBox.getValue().getAngebot()));
		}
	}

}
