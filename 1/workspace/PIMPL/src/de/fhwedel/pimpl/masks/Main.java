package de.fhwedel.pimpl.masks;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class Main implements EntryPoint {

	// UiBinder
	interface MainMaskUiBinder extends UiBinder<Widget, Main> {
	}

	private final static MainMaskUiBinder uiBinder = GWT.create(MainMaskUiBinder.class);

	@UiField
	FlowPanel content;
	
	@UiField
	Button customer;
	
	@UiField
	Button contract;
	
	@UiField
	Button offer;

	public void onModuleLoad() {
		RootPanel.get("pimpl").add(uiBinder.createAndBindUi(this));
//		DateBox db = new DateBox();
//		content.add(db);
//		content.add(new CustomerMask());
		content.add(new ContractMask());
		
	}
	
	public void setMenuButtonsEnabled(boolean enabled) {
		customer.setEnabled(enabled);
		contract.setEnabled(enabled);
		offer.setEnabled(enabled);
	}
	
	@UiHandler("customer")
	public void toCustomerMask(ClickEvent c) {
		content.clear();
		content.add(new CustomerMask());
	}
	
	@UiHandler("contract")
	public void toContractMask(ClickEvent c) {
		content.clear();
		content.add(new ContractMask());
	}
	
	@UiHandler("offer")
	public void toOffertMask(ClickEvent c) {
		content.clear();
		content.add(new OfferMask());
	}

}
