package de.fhwedel.pimpl.masks;

import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import de.fhwedel.pimpl.model.Angebot;
import de.fhwedel.pimpl.model.Helper;
import de.fhwedel.pimpl.model.Kunde;
import de.fhwedel.pimpl.model.Verfuegbarkeit;
import de.fhwedel.pimpl.model.Vertrag;
import de.fhwedel.pimpl.model.Vertrag.Vertragsstatus;
import de.fhwedel.pimpl.services.PIMPLService;
import de.fhwedel.pimpl.services.PIMPLServiceAsync;
import de.fhwedel.pimpl.widgets.BOSelectListBox;
import de.fhwedel.pimpl.widgets.InputEvent;
import de.fhwedel.pimpl.widgets.InputHandler;

public class OfferMask extends Composite {

	private static OfferMaskUiBinder uiBinder = GWT.create(OfferMaskUiBinder.class);

	interface OfferMaskUiBinder extends UiBinder<Widget, OfferMask> {
	}

	private final PIMPLServiceAsync server = GWT.create(PIMPLService.class);

	private Vertrag currentSelectedContract = null;

	private Angebot currentSelectedOffer = null;

	// Kundenliste

	@UiField
	TextBox offerSearchTextBox;

	@UiField
	BOSelectListBox<Angebot, Integer> offersListBox;

	// Kundendaten

	@UiField
	TextBox offerTitleTextBox;

	@UiField
	TextBox offerMinimumTimeTextBox;

	@UiField
	TextBox accessTechnologyTextBox;

	@UiField
	TextBox bandwithUplinkTextBox;

	@UiField
	TextBox bandwithDownTextBox;

	@UiField
	TextBox monthlyPriceTextBox;

	@UiField
	TextBox validFromTextBox;

	@UiField
	TextBox validToTextBox;

	// Vertragsdaten Optional

	@UiField
	Label contractDataLabel;

	@UiField
	Label contractNumberLabel;

	@UiField
	TextBox contractNumberTextBox;

	@UiField
	Label contractStartLabel;

	@UiField
	TextBox contractStartTextBox;

	@UiField
	Label contractStreetLabel;

	@UiField
	TextBox contractStreetTextBox;

	@UiField
	Label contractZipLabel;

	@UiField
	TextBox contractZipTextBox;

	@UiField
	Label contractCityLabel;

	@UiField
	TextBox contractCityTextBox;

	// Kundenaktionsbuttons

	@UiField
	Button editContractStartDateBtn;

	@UiField
	Button addOfferToContractBtn;

	@UiField
	Button cancelAddOfferToContractBtn;

	// Verfügbarkeitsliste

	@UiField
	ListBox availabilitiesDataListBox;

	public OfferMask() {
		initWidget(uiBinder.createAndBindUi(this));

//		this.initContractDateTextBoxHandler();

		this.updateOffersList();
		this.initOfferSearchTextBoxHandler();
		this.setVisibilityOfContractDataFields(false);
		this.disableUIElements();

		this.addOfferToContractBtn.setEnabled(false);
	}

	public OfferMask(Angebot offer) {
		this();
		this.currentSelectedOffer = offer;
		this.fillOfferTextBoxFields(offer);

	}

	public OfferMask(Vertrag contract) {
		this();

		this.currentSelectedContract = contract;
		this.setVisibilityOfContractDataFields(true);
		this.updateOffersList();
		this.initOfferSearchTextBoxHandler();
//		this.initContractDateTextBoxHandler();
		this.disableUIElements();

		Kunde customer = contract.getKunde();

		this.contractNumberTextBox.setValue(contract.getVertragsnummer());
		this.contractStartTextBox.setValue(Helper.parseDateToStr(contract.getVertragsbeginn()));
		this.contractStreetTextBox.setValue(customer.getStrasse());
		this.contractZipTextBox.setValue(customer.getPlz().toString());
		this.contractCityTextBox.setValue(customer.getOrt());

		this.addOfferToContractBtn.setEnabled(false);
	}

	private void setVisibilityOfContractDataFields(boolean visible) {
		this.contractNumberTextBox.setVisible(visible);
		this.contractStreetTextBox.setVisible(visible);
		this.contractZipTextBox.setVisible(visible);
		this.contractCityTextBox.setVisible(visible);
		this.contractStartTextBox.setVisible(visible);
		this.contractDataLabel.setVisible(visible);
		this.contractNumberLabel.setVisible(visible);
		this.contractStartLabel.setVisible(visible);
		this.contractStreetLabel.setVisible(visible);
		this.contractZipLabel.setVisible(visible);
		this.contractCityLabel.setVisible(visible);

		this.editContractStartDateBtn.setVisible(visible);
		this.addOfferToContractBtn.setVisible(visible);
		this.cancelAddOfferToContractBtn.setVisible(visible);
	}

	private void disableUIElements() {
		this.offerTitleTextBox.setEnabled(false);
		this.offerMinimumTimeTextBox.setEnabled(false);
		this.accessTechnologyTextBox.setEnabled(false);
		this.bandwithUplinkTextBox.setEnabled(false);
		this.bandwithDownTextBox.setEnabled(false);
		this.monthlyPriceTextBox.setEnabled(false);
		this.validFromTextBox.setEnabled(false);
		this.validToTextBox.setEnabled(false);
		this.contractNumberTextBox.setEnabled(false);
		this.contractStreetTextBox.setEnabled(false);
		this.contractZipTextBox.setEnabled(false);
		this.contractCityTextBox.setEnabled(false);
		this.availabilitiesDataListBox.setEnabled(false);
	}

	private void updateOffersList() {
		server.getAllOffers(new AsyncCallback<List<Angebot>>() {
			@Override
			public void onSuccess(List<Angebot> result) {
				offersListBox.setAcceptableValues(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Fehler beim Laden der Angebotsliste!");
			}
		});

	}

	private void updateAvailabilitiesList(Angebot offer) {
		this.availabilitiesDataListBox.clear();
		server.getAllAvailabilitiesFromOffer(offer, new AsyncCallback<List<Verfuegbarkeit>>() {
			@Override
			public void onSuccess(List<Verfuegbarkeit> result) {
				Collections.sort(result);
				result.stream().forEachOrdered(r -> availabilitiesDataListBox.addItem(r.toString()));
//				result.stream().sorted(Comparator.comparing(Verfuegbarkeit::getGueltigVon))
//					.forEachOrdered( r -> availabilitiesDataListBox.addItem(r.toString()));
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Fehler beim Laden der Verfügbarkeiten!");
			}
		});
	}

	private void offersListBoxByStr(String offerSearchStr) {
		server.getOffers(offerSearchStr.trim(), new AsyncCallback<List<Angebot>>() {
			@Override
			public void onSuccess(List<Angebot> result) {
				// Window.alert("Suche abgeschlossen, Ergebnisse: " + result.size());
				offersListBox.setAcceptableValues(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Fehler beim Laden der Angebotsliste!");
			}
		});
		// Denke diese Interpretation der Regel war falsch
//		if (this.currentSelectedContract == null) {
//			server.getOffers(offerSearchStr.trim(), new AsyncCallback<List<Angebot>>() {
//				@Override
//				public void onSuccess(List<Angebot> result) {
//					// Window.alert("Suche abgeschlossen, Ergebnisse: " + result.size());
//					offersListBox.setAcceptableValues(result);
//				}
//	
//				@Override
//				public void onFailure(Throwable caught) {
//					Window.alert("Fehler beim Laden der Angebotsliste!");
//				}
//			});
//		} else {
//			server.getAllOffersForContract(offerSearchStr.trim(), this.currentSelectedContract, new AsyncCallback<List<Angebot>>() {
//				@Override
//				public void onSuccess(List<Angebot> result) {
//					offersListBox.setAcceptableValues(result);
//				}
//	
//				@Override
//				public void onFailure(Throwable caught) {
//					Window.alert("Fehler beim Laden der Angebotsliste!");
//				}
//			});
//		}
	}

	private void initOfferSearchTextBoxHandler() {
		this.offerSearchTextBox.addDomHandler(new InputHandler() {
			@Override
			public void onInput(InputEvent event) {
				offersListBoxByStr(offerSearchTextBox.getText());
			}
		}, InputEvent.getType());
	};

//	private void initContractDateTextBoxHandler() {
//		contractStartTextBox.addDomHandler(new InputHandler() {
//			@Override
//			public void onInput(InputEvent event) {
//				if (contractStartTextBox.getText().isEmpty() || !Helper.isDateValid(contractStartTextBox.getText())) {
//					System.out.println("FIRE");
//					editContractStartDateBtn.setEnabled(false);
//				}
//				editContractStartDateBtn.setEnabled(true);
//			}
//		}, InputEvent.getType());
//	};

	private void fillOfferTextBoxFields(Angebot offer) {
		this.offerTitleTextBox.setValue(offer.getBezeichnung());
		this.offerMinimumTimeTextBox.setValue(offer.getMindestlaufzeitMonate().toString());
		this.accessTechnologyTextBox.setValue(offer.getTechnologie().toString());
		this.bandwithUplinkTextBox.setValue(offer.getBandbreiteUplink().toString());
		this.bandwithDownTextBox.setValue(offer.getBandbreiteDownlink().toString());
		this.monthlyPriceTextBox.setValue(offer.getMonatspreisFormatted());
		this.validFromTextBox.setValue(Helper.parseDateToStr(offer.getGueltigVon()));
		String guetigBis = offer.getGueltigBis() == null ? "unbeschränkt"
				: Helper.parseDateToStr(offer.getGueltigBis());
		this.validToTextBox.setValue(guetigBis);
	}

	@UiHandler("offersListBox")
	public void selectOffer(ClickEvent c) {
		if (this.offersListBox.getSelectedIndex() >= 0) {
			Angebot offer = this.offersListBox.getValue();
			this.currentSelectedOffer = offer;
			this.fillOfferTextBoxFields(offer);
//			Window.alert("Der Name des Kunden lautet: " + offer.getBezeichnung());
			if (addOfferToContractBtn.isVisible()) {
				this.addOfferToContractBtn.setEnabled(true);
			}
			this.updateAvailabilitiesList(offer);
		}
	}

	@UiHandler("editContractStartDateBtn")
	public void saveContractDate(ClickEvent c) {
		if (this.contractStartTextBox.getText().isEmpty() || !Helper.isDateValid(this.contractStartTextBox.getText())) {
			Window.alert(
					"FEHLERHAFTE EINGABE! Das eingegebene Datum muss dem folgendem Format entsprechen! -> (dd.MM.yyyy) -> Beispiel: (01.12.2020)");
			return;
		}
		if (this.currentSelectedContract == null) {
			throw new IllegalStateException(
					"Es ist kein Vertrag für diese Maske defininiert worden! KRITISCHER FEHLER");
		}
		server.adjustContractStartDate(this.currentSelectedContract,
				Helper.parseStrToDate(this.contractStartTextBox.getText()), new AsyncCallback<Vertrag>() {
					@Override
					public void onSuccess(Vertrag result) {
						Window.alert("Der Vertragsbeginn wurde erfolgreich geändrt und ist nun: "
								+ Helper.parseDateToStr(result.getVertragsbeginn()));
						currentSelectedContract = result;
						contractStartTextBox
								.setValue(Helper.parseDateToStr(currentSelectedContract.getVertragsbeginn()));
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(
								"Fehler beim speichern des neues Vertragsbeginns. Bitte überprüfen Sie die Eingabe. Format -> (dd.mm.yyyy)!");
					}
				});
	}

	@UiHandler("addOfferToContractBtn")
	public void addOfferToContract(ClickEvent c) {
		if (this.currentSelectedContract == null) {
			Window.alert("Es ist kein Vertrag für diese Maske defininiert worden! KRITISCHER FEHLER");
		}
		if (this.currentSelectedOffer == null) {
			Window.alert("Es muss erst ein Vertrag aus der Liste ausgewählt werden!");
			return;
		}
		if (this.currentSelectedOffer.getGueltigBis() != null && this.currentSelectedContract.getVertragsbeginn().before(this.currentSelectedOffer.getGueltigBis())) {
			Window.alert("Der derzeitige Vertragsbeginn ist: <"
					+ Helper.parseDateToStr(this.currentSelectedContract.getVertragsbeginn())
					+ "> und darf nicht vor dem Endzeitpumkt des ausgewählten Angebots liegen.");
			return;
		}
		Panel parent = (Panel) this.getParent();
		server.addOfferToContract(this.currentSelectedContract, this.currentSelectedOffer,
				new AsyncCallback<Vertrag>() {
					@Override
					public void onSuccess(Vertrag result) {
						Window.alert("Das Angebot wurde erfolgreich dem Vertrag hinzugefügt.");
						parent.clear();
						parent.add(new ContractMask(result));
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
				});
	}

	@UiHandler("cancelAddOfferToContractBtn")
	public void cancelAddOfferToContract(ClickEvent c) {
		if (this.currentSelectedContract == null) {
			Window.alert("Es ist kein Vertrag für diese Maske defininiert worden! KRITISCHER FEHLER");
		}
		Panel parent = (Panel) this.getParent();

		server.changeContractState(this.currentSelectedContract, Vertragsstatus.ANGEBOT_NICHT_VERFUEGBAR,
				new AsyncCallback<Vertrag>() {
					@Override
					public void onSuccess(Vertrag result) {
						Window.alert("Der Vertrag wurde erfolgreich wegen fehlendem Angebot geschlossen!");
						parent.clear();
						parent.add(new ContractMask(result));
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Fehler beim Schließen des Vertrags wegen Verfügbarkeit!");
					}
				});
	}

}
