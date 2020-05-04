package org.incode.estatio.dom;

import lombok.Getter;

@Getter
public class InvoiceItemObject {
    private String $$href;

    public String get$$href() {
        return $$href;
    }

    private DomainObject charge;
    private String description;
    private String dueDate;
    private String endDate;
    private DomainObject fixedAsset;
    private String grossAmount;
    private String incomingInvoiceType;
    private String linkedAmount;
    private String netAmount;
    private DomainObject orderItem;
    private DomainObject project;
    private String sequence;
    private String startDate;
    private DomainObject tax;
    private String vatAmount;
}
