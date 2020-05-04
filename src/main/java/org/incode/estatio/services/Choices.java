package org.incode.estatio.services;

import java.util.Arrays;
import java.util.List;

public class Choices {

    public static List<String> choicesIncomingInvoiceType(){
        return Arrays.asList(
                "Capex",
                "Property Expenses",
                "Service Charges",
                "Local Expenses",
                "Corporate Expenses",
                "Tangible Fixed Asset",
                "Intercompany",
                "Re Invoicing"
        );
    }

    public static List<String> choicesPaymentMethod(){
        return Arrays.asList("Direct Debit",
            "Billing Account",
            "Bank Transfer",
            "Cash",
            "Cheque",
            "Credit Card",
            "Refund By Supplier",
            "Manual Process");
    }

}
