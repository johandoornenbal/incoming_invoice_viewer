package org.incode.estatio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.incode.estatio.dom.DomainObject;
import org.incode.estatio.dom.InvoiceObject;
import org.incode.estatio.dom.payload.DomainObjectValue;
import org.incode.estatio.dom.payload.InvoicePayloadObject;
import org.incode.estatio.dom.payload.StringValue;
import org.incode.estatio.services.Choices;
import org.incode.estatio.services.EstatioClient;

import lombok.Getter;
import lombok.Setter;

@Route(value = CompleteInvoiceLayout.ROUTE, layout = MainView.class)
public class CompleteInvoiceLayout extends VerticalLayout implements HasUrlParameter<String> {

	static Logger LOG = LoggerFactory.getLogger(CompleteInvoiceLayout.class);

	public static final String ROUTE = "completeinvoice";
	public static final String TITLE = "invoice";
	private static final long serialVersionUID = 1L;

	@Getter
	private CredentialsService credentialsService;
	@Getter @Setter
	private List<DomainObject> allOrganisations;

	private HorizontalLayout layout = new HorizontalLayout();
	private Div content = new Div();
	@Getter
	private Div dataEntryDiv = new Div();
	private RefDataRepo refDataRepo;
	private Binder<InvoiceObject> binder;
	private InvoiceObject invoiceObject;
	private ComboBox<DomainObject> buyer;
	private ComboBox<DomainObject> supplier;

	public CompleteInvoiceLayout(@Autowired CredentialsService service, @Autowired RefDataRepo refDataRepo) {
		this.credentialsService = service;
		this.refDataRepo = refDataRepo;
		setId("applicationLayout");
		binder = new Binder<>(InvoiceObject.class);

		setSizeFull();
		setPadding(false);
		setSpacing(false);
		setDefaultHorizontalComponentAlignment(Alignment.STRETCH);

		final Div header = new Div();
		header.setText("This is the header. My height is 150 pixels");
		header.setClassName("header");
		header.setHeight("150px");
		header.getStyle().set("flexshrink", "0");

		layout.setHeightFull();
		layout.setSpacing(false);

		dataEntryDiv.setClassName("navigation");
		dataEntryDiv.setWidth("25%");
		dataEntryDiv.getStyle().set("flexshrink", "0");

		final ComboBox<String> type = new ComboBox<>("Type");
		type.setItems(Choices.choicesIncomingInvoiceType());
		binder.bind(type, InvoiceObject::getType, InvoiceObject::setType);

		buyer = new ComboBox<>();
		buyer.setItemLabelGenerator(DomainObject::getTitle);
		if (getAllOrganisations()!=null) buyer.setItems(getAllOrganisations());
		buyer.setLabel("Buyer");
		buyer.setClearButtonVisible(true);
		buyer.setId("Buyer");
		binder.bind(buyer, InvoiceObject::getBuyer, InvoiceObject::setBuyer);

		supplier = new ComboBox<>();
		supplier.setItemLabelGenerator(DomainObject::getTitle);
		if (getAllOrganisations()!=null) supplier.setItems(getAllOrganisations());
		supplier.setLabel("Supplier");
		supplier.setClearButtonVisible(true);
		supplier.setId("Supplier");
		binder.bind(supplier, InvoiceObject::getSeller, InvoiceObject::setSeller);

		final TextField invoiceNumber = new TextField("Invoice number");
		binder.bind(invoiceNumber, InvoiceObject::getInvoiceNumber, InvoiceObject::setInvoiceNumber);
		final DatePicker dueDate = new DatePicker("Due date");
		binder.bind(dueDate, InvoiceObject::getDueDateAsLocalDate, InvoiceObject::setDueDateAsLocalDate);
		final DatePicker invoiceDate = new DatePicker("Invoice date");
		binder.bind(invoiceDate, InvoiceObject::getInvoiceDateAsLocalDate, InvoiceObject::setInvoiceDateAsLocalDate);

		final ComboBox<String> paymentMethod = new ComboBox<>("Payment method");
		paymentMethod.setItems(Choices.choicesPaymentMethod());
		binder.bind(paymentMethod, InvoiceObject::getPaymentMethod, InvoiceObject::setPaymentMethod);

		dataEntryDiv.add(type, buyer, supplier, invoiceDate, dueDate, invoiceNumber, paymentMethod);

		Button button = new Button("submit");
		button.addClickListener(e->{
			if (invoiceObject!=null){
				binder.writeBeanIfValid(invoiceObject);
				log(invoiceObject);
				submit(invoiceObject);
			}
		});

		dataEntryDiv.add(button);

		content.setClassName("content");
		content.setText("This is the content area");
		content.setHeightFull();
		content.getStyle().set("display", "flex");
		content.getStyle().set("alignContent", "start");

		final Div footer = new Div();
		footer.setText("This is the footer area. My height is 100 pixels");
		footer.setClassName("footer");
		footer.setHeight("100px");
		footer.getStyle().set("flexshrink", "0");

		setHeight("100%");
		layout.add(dataEntryDiv, content);
		layout.expand(content);
		add(layout);

		allOrganisations = new ArrayList<>();
	}

	@Override
	public void setParameter(final BeforeEvent beforeEvent, @OptionalParameter final String s) {
		setAllOrganisations(refDataRepo.getOrganisations());
		if (getCredentialsService().hasCredentials()){
			invoiceObject = EstatioClient
					.getInvoiceObject(getCredentialsService().getUsername(), getCredentialsService().getPassword(), s);
		} else {
			invoiceObject = EstatioClient.getInvoiceObject("estatio-user-fr", "pass", s);
		}
		if (invoiceObject.getRenderedPdf()!=null){
			EmbeddedPdfDocument embeddedPdfDocument = new EmbeddedPdfDocument(
					invoiceObject.getRenderedPdf());
			content.removeAll();
			content.add(embeddedPdfDocument);
		} else {
			content.setText("No pdf found");
		}

		if (invoiceObject.getBuyer()!=null){
			if (getAllOrganisations()!=null){
				buyer.setItems(getAllOrganisations());
			} else {
				buyer.setItems(Arrays.asList(invoiceObject.getBuyer()));
			}
		} else {
			if (getAllOrganisations()!=null){
				buyer.setItems(getAllOrganisations());
			} else {
				buyer.setItems(Collections.EMPTY_LIST);
			}
		}

		if (invoiceObject.getSeller()!=null){
			if (getAllOrganisations()!=null){
				supplier.setItems(getAllOrganisations());
			} else {
				supplier.setItems(Arrays.asList(invoiceObject.getSeller()));
			}
		} else {
			if (getAllOrganisations()!=null){
				supplier.setItems(getAllOrganisations());
			} else {
				supplier.setItems(Collections.EMPTY_LIST);
			}
		}
		binder.readBean(invoiceObject);
	}

	@Tag("object")
	public class EmbeddedPdfDocument extends Component implements HasSize {

		public EmbeddedPdfDocument(StreamResource resource) {
			this();
			getElement().setAttribute("data", resource);
		}

		public EmbeddedPdfDocument(String url) {
			this();
			getElement().setAttribute("data", url);
		}

		protected EmbeddedPdfDocument() {
			getElement().setAttribute("type", "application/pdf");
			setSizeFull();
		}
	}

	public void log(final InvoiceObject invoiceObject){

		LOG.info("Logging invoiceObjectValues");
		LOG.info("buyer ".concat(invoiceObject.getBuyer()!=null ? invoiceObject.getBuyer().getTitle(): "no buyer"));
		LOG.info("seller ".concat(invoiceObject.getSeller()!=null ? invoiceObject.getSeller().getTitle(): "no seller"));
		LOG.info("type " + invoiceObject.getType());
		LOG.info("invoice number " + invoiceObject.getInvoiceNumber());
		LOG.info("due date " + invoiceObject.getDueDateAsLocalDate());
		LOG.info("invoice date " + invoiceObject.getInvoiceDateAsLocalDate());

	}

	public void submit(final InvoiceObject invoiceObject){

		InvoicePayloadObject payloadObject = new InvoicePayloadObject();
		if (invoiceObject.getSeller()!=null ) payloadObject.setSupplier(new DomainObjectValue(invoiceObject.getSeller()));
		if (invoiceObject.getInvoiceDateAsLocalDate()!=null ) payloadObject.setInvoiceDate(new StringValue(InvoiceObject.parseLocalDateToString(invoiceObject.getInvoiceDateAsLocalDate())));
		if (invoiceObject.getBankAccount()!=null ) payloadObject.setBankAccount(new DomainObjectValue(invoiceObject.getBankAccount()));
		if (invoiceObject.getCommunicationNumber()!=null ) payloadObject.setCommunicationNumber(new StringValue(invoiceObject.getCommunicationNumber()));
		if (invoiceObject.getCurrency()!=null ) payloadObject.setCurrency(new DomainObjectValue(invoiceObject.getCurrency()));
		if (invoiceObject.getDateReceivedAsLocalDate()!=null ) payloadObject.setDateReceived(new StringValue(InvoiceObject.parseLocalDateToString(invoiceObject.getDateReceivedAsLocalDate())));
		if (invoiceObject.getDueDateAsLocalDate()!=null ) payloadObject.setDueDate(new StringValue(InvoiceObject.parseLocalDateToString(invoiceObject.getDueDateAsLocalDate())));
		if (invoiceObject.getType()!=null ) payloadObject.setIncomingInvoiceType(new StringValue(invoiceObject.getType()));
		if (invoiceObject.getPaymentMethod()!=null ) payloadObject.setPaymentMethod(new StringValue(invoiceObject.getPaymentMethod()));
		payloadObject.setCreateNewSupplier(new StringValue("false")); // TODO: FOR THE TIME BEING
		if (invoiceObject.getInvoiceNumber()!=null ) payloadObject.setInvoiceNumber(new StringValue(invoiceObject.getInvoiceNumber()));
		EstatioClient.submitInvoice(getCredentialsService().getUsername(), getCredentialsService().getPassword(), invoiceObject.getUrl(), payloadObject);

	}

}