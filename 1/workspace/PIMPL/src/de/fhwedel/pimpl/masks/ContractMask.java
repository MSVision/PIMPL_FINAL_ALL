package de.fhwedel.pimpl.masks;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.Max;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import de.fhwedel.pimpl.model.Helper;
import de.fhwedel.pimpl.model.Kunde;
import de.fhwedel.pimpl.model.Vertrag;
import de.fhwedel.pimpl.model.Vertrag.Vertragsstatus;
import de.fhwedel.pimpl.services.PIMPLService;
import de.fhwedel.pimpl.services.PIMPLServiceAsync;
import de.fhwedel.pimpl.widgets.BOSelectListBox;
import de.fhwedel.pimpl.widgets.InputEvent;
import de.fhwedel.pimpl.widgets.InputHandler;

public class ContractMask extends Composite {

	private static ContractMaskUiBinder uiBinder = GWT.create(ContractMaskUiBinder.class);

	interface ContractMaskUiBinder extends UiBinder<Widget, ContractMask> {
	}

	private final PIMPLServiceAsync server = GWT.create(PIMPLService.class);

	private Vertrag currentSelectedContract = null;
	
	// Wird nur belegt, wenn ein Vertrag neu angelegt wird.
	private Kunde currentSelectedCustomer = null;

	// Vertragsliste

	@UiField
	TextBox contractsSearchTextBox;

	@UiField
	BOSelectListBox<Vertrag, Integer> contractsListBox;

	// Kundeninformationsbreich

	@UiField
	ListBox customerDataListBox;

	// Angebotsinformationsbereich

	@UiField
	ListBox offerDataListBox;

	// Vertragstagen

	@UiField
	TextBox contractNumberTextBox;

	@UiField
	TextBox contractStateTextBox;

	@UiField
	TextBox dateOfEntryTextBox;

	@UiField
	TextBox contractStartTextBox;

	@UiField
	TextBox ibanTextBox;

	@UiField
	TextBox bicTextBox;

	@UiField
	TextBox setUpShouldTextBox;

	@UiField
	TextBox setUpIsTextBox;

	@UiField
	TextBox enddevicesSendTextBox;

	@UiField
	TextBox enddevicesRecivedTextBox;

	@UiField
	TextBox recallRecivedTextBox;

	@UiField
	TextBox contractEndTextBox;

	// Vertragsbuttons

	@UiField
	Button initContractBtn;

	@UiField
	Button addOfferBtn;
	
	@UiField
	Button checkCustomerCreditRatingBtn;
	
	@UiField
	Button saveCreditDataBtn;
	
	@UiField
	Button contractDeclarationObtainedBtn;
	
	@UiField
	Button setUpShouldBtn;
	
	@UiField
	Button enddevicesNeededBtn;
	
	@UiField
	Button enddevicesNotNeededBtn;
	
	@UiField
	Button devicesSendCheckerBtn;
	
//	@UiField
//	Button saveTerminationDateBtn;
//	@UiField
//	Button saveTerminationDateBtn;
//	@UiField
//	Button saveTerminationDateBtn;
	
	@UiField
	Button saveTerminationDateBtn;
	
	public ContractMask() {
		initWidget(uiBinder.createAndBindUi(this));
		this.updateContractsList();
		this.initOfferSearchTextBoxHandler();
		this.customerDataListBox.setEnabled(false);
		this.offerDataListBox.setEnabled(false);
		this.hideAllBtn();
		this.disableAllTextBoxFields();
	}

	public ContractMask(Kunde customer) {
		this();
		this.currentSelectedCustomer = customer;
		// Macht es bis zur Vertragsspeicherung locked!
		this.contractsListBox.setEnabled(false);
		this.contractsSearchTextBox.setEnabled(false);

		initContractBtn.setVisible(true);
		contractStartTextBox.setEnabled(true);
	}

	public ContractMask(Vertrag contract) {
		this();
		this.setUpUIForSelectedContractState(contract);
	}

	private void disableAllTextBoxFields() {
		contractNumberTextBox.setEnabled(false);
		contractStateTextBox.setEnabled(false);
		dateOfEntryTextBox.setEnabled(false);
		contractStartTextBox.setEnabled(false);
		ibanTextBox.setEnabled(false);
		bicTextBox.setEnabled(false);
		setUpShouldTextBox.setEnabled(false);
		setUpIsTextBox.setEnabled(false);
		enddevicesSendTextBox.setEnabled(false);
		enddevicesRecivedTextBox.setEnabled(false);
		recallRecivedTextBox.setEnabled(false);
		contractEndTextBox.setEnabled(false);
	}

	private void hideAllBtn() {
		initContractBtn.setVisible(false);
		addOfferBtn.setVisible(false);
		checkCustomerCreditRatingBtn.setVisible(false);
		contractDeclarationObtainedBtn.setVisible(false);
		saveCreditDataBtn.setVisible(false);
		setUpShouldBtn.setVisible(false);
		saveTerminationDateBtn.setVisible(false);
		enddevicesNeededBtn.setVisible(false);
		enddevicesNotNeededBtn.setVisible(false);
		devicesSendCheckerBtn.setVisible(false);
		
		addOfferBtn.setVisible(false);
	}

	private void setUpUIForSelectedContractState(Vertrag contract) {
		this.currentSelectedContract = contract;
		this.setUpUIForSelectedContractState();
	}

	private void setUpUIForSelectedContractState() {
		if (this.currentSelectedContract == null || !this.currentSelectedContract.isPersisted()) {
			Window.alert("Funktion darf nicht aufgerufen werden, ohne das ein bereits persistierter Vertrag ausgewählt ist.");
			return;
		}
		this.updateCustomerList();
		this.updateOfferList();
		
		// DAS PASSIERT IN JEDEM FALL! -> DA EIN VERTRAG EH EXISTIEREN MUSS MIT DIESEN EIGENSCHAFTEN!
		this.contractNumberTextBox.setValue(this.currentSelectedContract.getVertragsnummer());
		this.contractStateTextBox.setValue(this.currentSelectedContract.getVertragsstatus().toString());
		this.dateOfEntryTextBox.setValue(Helper.parseDateToStr(this.currentSelectedContract.getErfassungsdatum()));
		this.contractStartTextBox.setValue(Helper.parseDateToStr(this.currentSelectedContract.getVertragsbeginn()));
		
		switch (this.currentSelectedContract.getVertragsstatus()) {
		case ANFRAGE:
			if (this.currentSelectedContract.getAngebot() == null) {
				this.addOfferBtn.setVisible(true);
			} else if (this.currentSelectedContract.getIban() == null || this.currentSelectedContract.getIban().isEmpty()) {
				this.checkCustomerCreditRatingBtn.setVisible(true);
			} else {
				this.ibanTextBox.setEnabled(false);
				this.bicTextBox.setEnabled(false);
				this.ibanTextBox.setValue(this.currentSelectedContract.getIban());
				this.bicTextBox.setValue(this.currentSelectedContract.getBic());
				this.contractDeclarationObtainedBtn.setVisible(true);
			}
			break;
		case AUFTRAG:
			this.saveCreditDataBtn.setVisible(false);
			this.contractDeclarationObtainedBtn.setVisible(false);
			this.setUpShouldTextBox.setEnabled(true);
			this.setUpShouldBtn.setVisible(true);
			if (this.currentSelectedContract.getEinrichtungAnschlussSoll() != null) {
				setUpShouldTextBox.setValue(Helper.parseDateToStr(this.currentSelectedContract.getEinrichtungAnschlussSoll()));
				this.setUpShouldTextBox.setEnabled(false);
				this.setUpShouldBtn.setVisible(false);
				this.enddevicesNeededBtn.setVisible(true);
				this.enddevicesNotNeededBtn.setVisible(true);
				
				 
				
//				// TODO: continue! do all the other stuff! 
//				EndgeräteVersendenTimerBtn -> go -> Anschluss Einrichten -> Vertrag aktivieren! -> Kündigungsdatum speichern.
//				
//				Prüfe auf wiederruf -> OPTION 2 auswahlen
//				Wiederruf -> Go -> bis max 14 Tage
//					-> Engeräte in EMpfang nehmen -> Endgeräte zurückerhalten enable
					
				
				
				
			}
			break;
		case WIDERRUF:
			break;
		case AKTIV:
			this.saveTerminationDateBtn.setVisible(true);
			this.contractEndTextBox.setEnabled(true);
			break;
		case GEKUENDIGT:
			this.saveTerminationDateBtn.setVisible(false);
			this.contractEndTextBox.setEnabled(false);
			break;
		default:
			this.checkCustomerCreditRatingBtn.setVisible(false);
			this.initContractBtn.setVisible(false);
			this.saveCreditDataBtn.setVisible(false);
			this.setUpShouldBtn.setVisible(false);
			this.setUpShouldTextBox.setEnabled(false);
			this.saveTerminationDateBtn.setVisible(false);
			this.contractEndTextBox.setEnabled(false);
			break;
		}
		
		
		setUpIsTextBox.setEnabled(false);
		enddevicesSendTextBox.setEnabled(false);
		enddevicesRecivedTextBox.setEnabled(false);
		recallRecivedTextBox.setEnabled(false);
		contractEndTextBox.setEnabled(false);

	}

	private void updateContractsList() {
		this.contractsListBox.setEnabled(true);
		this.contractsSearchTextBox.setEnabled(true);
		server.getAllContracts(new AsyncCallback<List<Vertrag>>() {
			@Override
			public void onSuccess(List<Vertrag> result) {
				contractsListBox.setAcceptableValues(result);
				contractsListBox.setEnabled(true);
				contractsSearchTextBox.setEnabled(true);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Fehler beim Laden der Angebotsliste!");
			}
		});
	}

	private void offersListBoxByStr(String contractsSearchStr) {
		server.getContracts(contractsSearchStr.trim(), new AsyncCallback<List<Vertrag>>() {
			@Override
			public void onSuccess(List<Vertrag> result) {
				// Window.alert("Suche abgeschlossen, Ergebnisse: " + result.size());
				contractsListBox.setAcceptableValues(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Fehler beim Laden der Vertragsliste!");
			}
		});
	}

	private void initOfferSearchTextBoxHandler() {
		this.contractsSearchTextBox.addDomHandler(new InputHandler() {
			@Override
			public void onInput(InputEvent event) {
				offersListBoxByStr(contractsSearchTextBox.getText());
			}
		}, InputEvent.getType());
	};

	private void updateCustomerList() {
		this.customerDataListBox.clear();
		if (this.currentSelectedContract == null || this.currentSelectedContract.getKunde() == null) {
			return;
		}
		this.currentSelectedContract.getKunde().getAllDataAsList().stream()
				.forEachOrdered(r -> customerDataListBox.addItem(r));
	}

	private void updateOfferList() {
		this.offerDataListBox.clear();
		if (this.currentSelectedContract == null || this.currentSelectedContract.getAngebot() == null) {
			return;
		}
		this.currentSelectedContract.getAngebot().getAllDataAsList().stream()
				.forEachOrdered(r -> offerDataListBox.addItem(r));
	}

	@UiHandler("contractsListBox")
	public void selectContract(ClickEvent c) {
		if (this.contractsListBox.getSelectedIndex() >= 0) {
			Vertrag contract = this.contractsListBox.getValue();
			Window.alert("Vertrag mit der Vertragnummer: <" + contract.getVertragsnummer() + "> ausgewählt.");
			this.setUpUIForSelectedContractState(contract);
		}
	}
	
	@UiHandler("initContractBtn")
	public void initContract(ClickEvent c) {
		if (this.currentSelectedCustomer == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		String contractStartDateStr = this.contractStartTextBox.getText();
		if (contractStartDateStr.isEmpty() || !Helper.isDateValid(contractStartDateStr)) {
			Window.alert("Fehler: Es muss erst ein gültiges Datum für den Vertragsbeginn festgelegt werden! (dd.mm.yyyy)");
			return;
		}
		server.initContract(this.currentSelectedCustomer, Helper.parseStrToDate(contractStartDateStr), new AsyncCallback<Vertrag>() {
			@Override
			public void onSuccess(Vertrag result) {
				initContractBtn.setVisible(false);
				setUpUIForSelectedContractState(result);
				updateContractsList();
				Window.alert("Der Vertrag wurde Erfolgreich angelegt mit der Vertragsnummer: " + currentSelectedContract.getVertragsnummer());
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Fehler beim initialisieren des Vertrags. -> Vertragsbeginn muss >= <Heute> sein!");
			}
		});
	}
	
	@UiHandler("addOfferBtn")
	public void addOfferToContract(ClickEvent c) {
		if (this.currentSelectedContract == null 
				|| !this.currentSelectedContract.isPersisted() 
				|| this.currentSelectedContract.getKunde() == null) {
			Window.alert("Fehler es muss für diese Funktion erst ein bereits persistierter Vertrag ausgewählt sein!");
			return;
		}
		Panel parent = (Panel) this.getParent();
		parent.clear();
		parent.add(new OfferMask(this.currentSelectedContract));
	}
	
	private void closeContractDueToCreditRating() {
		server.changeContractState(this.currentSelectedContract, Vertragsstatus.ABGELEHNT_WEGEN_BONITAET,
				new AsyncCallback<Vertrag>() {
					@Override
					public void onSuccess(Vertrag result) {
						Window.alert("Der Vertrag wurde erfolgreich wegen fehlender Bonität abgelehnt!");
						setUpUIForSelectedContractState(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Fehler beim Schließen des Vertrags wegen fehlender Bonität!");
					}
				});
	}
	
	
	@UiHandler("checkCustomerCreditRatingBtn")
	public void checkCustomersCreditRating(ClickEvent c) {
		if (this.currentSelectedContract == null 
				|| !this.currentSelectedContract.isPersisted() 
				|| this.currentSelectedContract.getKunde() == null) {
			Window.alert("Fehler es muss für diese Funktion erst ein bereits persistierter Vertrag ausgewählt sein!");
			return;
		}
		server.getCustomerCreditRating(this.currentSelectedContract.getKunde(), new AsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				// TODO: im live modus abschalten!!!
				result = result + 100;
				if (result.intValue() < 300) {
					Window.alert("Die Bonität des Kunden beträgt: <" + result.toString() 
						+ "> und ist damit leider nicht ausreichend "
						+ "-> der Vertrag wird geschlossen!");
					closeContractDueToCreditRating();
					return;
				} 
				Window.alert("Die Bonität des Kunden beträgt: <" + result.toString() 
					+ "> und ist damit ausreichend!");
				checkCustomerCreditRatingBtn.setVisible(false);
				ibanTextBox.setEnabled(true);
				bicTextBox.setEnabled(true);
				saveCreditDataBtn.setVisible(true);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Woops! Etwas ist bei der Bonitätsberechnung fehltgelaufen! -> FATAL ERROR! Programmierfehler!");
			}
		});
	}
	
	@UiHandler("saveCreditDataBtn")
	public void saveCredidDebitData(ClickEvent c) {
		if (this.currentSelectedContract == null 
				|| !this.currentSelectedContract.isPersisted() 
				|| this.currentSelectedContract.getKunde() == null) {
			Window.alert("Fehler es muss für diese Funktion erst ein bereits persistierter Vertrag ausgewählt sein!");
			return;
		}
		server.addDebitDataToContract(this.currentSelectedContract, this.ibanTextBox.getText(), this.bicTextBox.getText(), new AsyncCallback<Vertrag>() {
			@Override
			public void onSuccess(Vertrag result) {
				Window.alert("Die Daten der Bankverbindung wurden erfolgreich erfasst!");
				setUpUIForSelectedContractState(result);
				saveCreditDataBtn.setVisible(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Die übergebenen Bankdaten sind höchstwahrscheinlich nicht korrekt. Bitte überprüfen Sie die Eingabe!");
			}
		});
	}
	
	
	@UiHandler("contractDeclarationObtainedBtn")
	public void contractDeclarationObtained(ClickEvent c) {
		if (this.currentSelectedContract == null 
				|| !this.currentSelectedContract.isPersisted() 
				|| this.currentSelectedContract.getKunde() == null) {
			Window.alert("Fehler es muss für diese Funktion erst ein bereits persistierter Vertrag ausgewählt sein!");
			return;
		}
		server.changeContractState(this.currentSelectedContract, Vertragsstatus.AUFTRAG,
				new AsyncCallback<Vertrag>() {
					@Override
					public void onSuccess(Vertrag result) {
						Window.alert("Der Vertrag wurde erfolgreich in den Status: <Auftrag> geändert!");
						setUpUIForSelectedContractState(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Fehler beim erteilen des Auftrags!");
					}
				});
	}
	
	@UiHandler("setUpShouldBtn")
	public void setUpShouldSaved(ClickEvent c) {
		if (this.currentSelectedContract == null 
				|| !this.currentSelectedContract.isPersisted() 
				|| this.currentSelectedContract.getKunde() == null) {
			Window.alert("Fehler es muss für diese Funktion erst ein bereits persistierter Vertrag ausgewählt sein!");
			return;
		}
		if (!Helper.isDateValid(this.setUpShouldTextBox.getText())) {
			Window.alert("Das eingegebene Datum entspricht nicht dem geforderten Format! -> (dd.mm.yyyy)!");
			return;
		}
		server.addSetUpDeviceShouldDate(currentSelectedContract, Helper.parseStrToDate(this.setUpShouldTextBox.getText()), new AsyncCallback<Vertrag>() {
					@Override
					public void onSuccess(Vertrag result) {
						currentSelectedContract = result;
						setUpShouldTextBox.setEnabled(false);
						setUpShouldBtn.setVisible(false);
						
						Window.alert("Es wurde erfolgreich der <" + Helper.parseDateToStr(currentSelectedContract.getEinrichtungAnschlussSoll()) + "> als Einrichtungszeitraum festgelegt.");
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
				});
	}
	
	@UiHandler("saveTerminationDateBtn")
	public void saveTerminationDate(ClickEvent c) {
		if (this.currentSelectedContract == null 
				|| !this.currentSelectedContract.isPersisted() 
				|| this.currentSelectedContract.getKunde() == null) {
			Window.alert("Fehler es muss für diese Funktion erst ein bereits persistierter Vertrag ausgewählt sein!");
			return;
		}
		if (!Helper.isDateValid(this.contractEndTextBox.getText())) {
			Window.alert("Das eingegebene Datum entspricht nicht dem geforderten Format! -> (dd.mm.yyyy)!");
			return;
		}
		server.terminateContractTo(this.currentSelectedContract, Helper.parseStrToDate(this.contractEndTextBox.getText()), new AsyncCallback<Vertrag>() {
					@Override
					public void onSuccess(Vertrag result) {
						currentSelectedContract = result;
						setUpShouldTextBox.setEnabled(false);
						setUpShouldBtn.setVisible(false);
						
						Window.alert("Es wurde erfolgreich der <" + Helper.parseDateToStr(currentSelectedContract.getVertragsende()) + "> als Vertragsende festgelegt.");
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
				});
	}
	
	
	@UiHandler("enddevicesNeededBtn")
	public void enddevicesNeeded(ClickEvent c) {
		if (this.currentSelectedContract == null 
				|| !this.currentSelectedContract.isPersisted() 
				|| this.currentSelectedContract.getKunde() == null) {
			Window.alert("Fehler es muss für diese Funktion erst ein bereits persistierter Vertrag ausgewählt sein!");
			return;
		}
		server.setContractEnddevicesNeeded(this.currentSelectedContract, true, new AsyncCallback<Vertrag>() {
			@Override
			public void onSuccess(Vertrag result) {
				currentSelectedContract = result;
				setUpShouldTextBox.setEnabled(false);
				setUpShouldBtn.setVisible(false);
				
				Window.alert("Es wurde erfolgreich der <" + Helper.parseDateToStr(currentSelectedContract.getVertragsende()) + "> als Vertragsende festgelegt.");
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}
	
	
	@UiHandler("enddevicesNotNeededBtn")
	public void enddevicesNotNeeded(ClickEvent c) {
		if (this.currentSelectedContract == null 
				|| !this.currentSelectedContract.isPersisted() 
				|| this.currentSelectedContract.getKunde() == null) {
			Window.alert("Fehler es muss für diese Funktion erst ein bereits persistierter Vertrag ausgewählt sein!");
			return;
		}
		server.setContractEnddevicesNeeded(this.currentSelectedContract, false, new AsyncCallback<Vertrag>() {
					@Override
					public void onSuccess(Vertrag result) {
						currentSelectedContract = result;
						setUpShouldTextBox.setEnabled(false);
						setUpShouldBtn.setVisible(false);
						
						Window.alert("Es wurde erfolgreich der <" + Helper.parseDateToStr(currentSelectedContract.getVertragsende()) + "> als Vertragsende festgelegt.");
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
				});
	}
	
}
