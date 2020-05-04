package org.incode.estatio.dom.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/*
{
                "incomingInvoiceType": {
                    "value": null
                },
                "createNewSupplier?": {
                    "value": null
                },
                "supplier": {
                    "value": null
                },
                "createRoleIfRequired": {
                    "value": null
                },
                "bankAccount": {
                    "value": null
                },
                "newSupplierCandidate": {
                    "value": null
                },
                "newSupplierCountry": {
                    "value": null
                },
                "newSupplierChamberOfCommerceCode": {
                    "value": null
                },
                "newSupplierIban": {
                    "value": null
                },
                "invoiceNumber": {
                    "value": null
                },
                "communicationNumber": {
                    "value": null
                },
                "dateReceived": {
                    "value": null
                },
                "invoiceDate": {
                    "value": null
                },
                "dueDate": {
                    "value": null
                },
                "paymentMethod": {
                    "value": null
                },
                "currency": {
                    "value": null
                }
            }
*/
@Getter @Setter
public class InvoicePayloadObject {
    private StringValue incomingInvoiceType;
    private DomainObjectValue supplier;
    private DomainObjectValue bankAccount;
    private StringValue invoiceNumber;
    private StringValue communicationNumber;
    private StringValue dateReceived;
    private StringValue invoiceDate;
    private StringValue dueDate;
    private StringValue paymentMethod;
    private DomainObjectValue currency;
    @JsonProperty("createNewSupplier?")
    private StringValue createNewSupplier;
}
