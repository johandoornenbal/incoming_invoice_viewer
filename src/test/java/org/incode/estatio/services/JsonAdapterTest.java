package org.incode.estatio.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.incode.estatio.dom.DomainObject;
import org.incode.estatio.dom.InvoiceItemObject;
import org.incode.estatio.dom.InvoiceObject;
import org.incode.estatio.dom.payload.DomainObjectValue;
import org.incode.estatio.dom.payload.InvoicePayloadObject;
import org.incode.estatio.dom.payload.StringValue;

class JsonAdapterTest {

    private static String resourceBase = "file:/Users/johan/src/incoming-invoice-viewer/src/test/java/org/incode/estatio/services/";

    @Test
    void invoiceObjectFromJson() throws MalformedURLException {

        // given
        URL url = new URL(resourceBase + "invoice.json");
        //                URL url = Resources.getResource(getClass(), "invoice.json"); // reads from target dir!! How can I make it read from test dir ..? SEE: https://www.baeldung.com/junit-src-test-resources-directory-path
        String data = null;
        try {
            data = Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load a JSON file.", e);
        }

        Assertions.assertThat(data).isNotEmpty();

        // when
        final InvoiceObject invoiceDataObject = JsonAdapter.invoiceObjectFromJson(data, null);
        Assertions.assertThat(invoiceDataObject.getApprovalState()).isEqualTo("New");
        Assertions.assertThat(invoiceDataObject.getBankAccount().getHref()).isEqualTo("http://estatio-test.int.ecpnv.com/restful/objects/org.estatio.dom.financial.bankaccount.BankAccount/6882");
        Assertions.assertThat(invoiceDataObject.getBankAccount().getTitle()).isEqualTo("FR60 1234 1234 1234 1234 1234 XXX - FR11269 (VERIFIED)");
        Assertions.assertThat(invoiceDataObject.getBuyer().getHref()).isEqualTo("http://estatio-test.int.ecpnv.com/restful/objects/org.estatio.dom.party.Organisation/1019");
        Assertions.assertThat(invoiceDataObject.getBuyer().getTitle()).isEqualTo("TEST PROPERTY FRANCE SAS [FR01]");
        Assertions.assertThat(invoiceDataObject.getDateReceived()).isEqualTo("2019-06-26");
        Assertions.assertThat(invoiceDataObject.getDueDate()).isEqualTo("2019-07-24");
        Assertions.assertThat(invoiceDataObject.getGrossAmount()).isEqualTo("20251.20");
        Assertions.assertThat(invoiceDataObject.getInvoiceDate()).isEqualTo("2019-06-24");
        Assertions.assertThat(invoiceDataObject.getInvoiceNumber()).isEqualTo("1234.1644.2019");
        Assertions.assertThat(invoiceDataObject.getNetAmount()).isEqualTo("16876.00");
        Assertions.assertThat(invoiceDataObject.getProperty().getHref()).isEqualTo("http://estatio-test.int.ecpnv.com/restful/objects/org.estatio.dom.asset.Property/1301");
        Assertions.assertThat(invoiceDataObject.getProperty().getTitle()).isEqualTo("Test property [GH]");
        Assertions.assertThat(invoiceDataObject.getSeller().getHref()).isEqualTo("http://estatio-test.int.ecpnv.com/restful/objects/org.estatio.dom.party.Organisation/4594");
        Assertions.assertThat(invoiceDataObject.getSeller().getTitle()).isEqualTo("TEST SUPPLIER [FR11269]");
        // etc...
        Assertions.assertThat(invoiceDataObject.getPdf()).isNotEmpty();
        Assertions.assertThat(invoiceDataObject.isUseAsTemplate()).isFalse();

        Assertions.assertThat(invoiceDataObject.getSomeThingNotInJSON()).isNull();

        Assertions.assertThat(invoiceDataObject.getItems()).hasSize(1);
        final InvoiceItemObject invoiceItemObject = invoiceDataObject.getItems().get(0);
        Assertions.assertThat(invoiceItemObject.get$$href()).isEqualTo("http://estatio-test.int.ecpnv.com/restful/objects/incomingInvoice.IncomingInvoiceItem/404467");
        Assertions.assertThat(invoiceItemObject.getCharge().getHref()).isEqualTo("http://estatio-test.int.ecpnv.com/restful/objects/org.estatio.dom.charge.Charge/242");
        Assertions.assertThat(invoiceItemObject.getCharge().getTitle()).isEqualTo("PROJECT MANAGEMENT [PROJECT MANAGEMENT]");

        // etc...
    }

    @Test
    void domainObjectsAsActionsResultFromJson() throws MalformedURLException {
        // given
        URL url2 = new URL(resourceBase + "allorgs.json");
        String data = null;
        try {
            data = Resources.toString(url2, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load a JSON file.", e);
        }
        // when
        List<DomainObject> domainObjects = JsonAdapter.domainObjectsAsActionsResultFromJson(data);
        // then
        Assertions.assertThat(domainObjects).hasSize(3);
    }

    @Test
    void taskObjectsFromJson() {
    }

    @Test
    void jsonFrom() {
        //given
        InvoicePayloadObject object = new InvoicePayloadObject();
        object.setInvoiceDate(new StringValue("2020-2-28"));
        final DomainObject supplier = new DomainObject();
        supplier.setTitle("TestSupplier");
        supplier.setHref("http://etc");
        object.setSupplier(new DomainObjectValue(supplier));
        object.setCreateNewSupplier(new StringValue("true"));
        // when
        final String json = JsonAdapter.jsonFrom(object);
        // then
        Assertions.assertThat(json).contains("{\"supplier\":{\"value\":{\"href\":\"http://etc\",\"title\":\"TestSupplier\"}},\"invoiceDate\":{\"value\":\"2020-2-28\"},\"createNewSupplier?\":{\"value\":\"true\"}}");
    }

}