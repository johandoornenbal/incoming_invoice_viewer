package org.incode.estatio.dom;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.vaadin.flow.server.StreamResource;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InvoiceObject {
    public InvoiceObject(){

    }
    private String approvalState;
    private DomainObject bankAccount;
    private DomainObject buyer;
    private DomainObject seller;
    private String dateReceived; //NOTE: NOT used by binder
    public void setDateReceivedAsLocalDate(final LocalDate date){
        this.dateReceived = parseLocalDateToString(date);
    }
    public LocalDate getDateReceivedAsLocalDate(){
        return parseStringToLocalDate(getDateReceived());
    }
    private String dueDate; //NOTE: NOT used by binder
    public void setDueDateAsLocalDate(final LocalDate date){
        this.dueDate = parseLocalDateToString(date);
    }
    public LocalDate getDueDateAsLocalDate(){
        return parseStringToLocalDate(getDueDate());
    }
    private String grossAmount;
    private String invoiceDate; //NOTE: NOT used by binder
    public void setInvoiceDateAsLocalDate(final LocalDate date){
        this.invoiceDate = parseLocalDateToString(date);
    }
    public LocalDate getInvoiceDateAsLocalDate(){
        return parseStringToLocalDate(getInvoiceDate());
    }
    private String invoiceNumber;
    private String netAmount;
    private String paymentMethod;
    private String pdf;
    private String previousComments;
    private DomainObject property;
    private String totalGrossAmount;
    private String totalNetAmount;
    private String totalVatAmount;
    private String type;
    private boolean useAsTemplate;
    private String vatAmount;
    private String communicationNumber;
    private DomainObject currency;

    private String someThingNotInJSON;
    private String url;

    private List<InvoiceItemObject> items;

    private StreamResource renderedPdf;

    public void setRenderedPdf(final StreamResource renderedPdf) {
        this.renderedPdf = renderedPdf;
    }

    public static LocalDate parseStringToLocalDate(final String dateString) {
        if (dateString == null)
            return null;
        final String[] split = dateString.split("-");
        return LocalDate.of(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]));
    }

    public static String parseLocalDateToString(final LocalDate date){
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}
