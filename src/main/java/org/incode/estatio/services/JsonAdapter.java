package org.incode.estatio.services;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.server.StreamResource;

import org.incode.estatio.dom.ActionResult;
import org.incode.estatio.dom.DomainObject;
import org.incode.estatio.dom.InvoiceObject;
import org.incode.estatio.dom.TaskObject;
import org.incode.estatio.dom.payload.InvoicePayloadObject;

public class JsonAdapter {

    public static InvoiceObject invoiceObjectFromJson(final String json, final String invoiceUrl){
        final InvoiceObject invoiceObject;
        try {
            invoiceObject = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .reader()
                    .forType(InvoiceObject.class)
                    .readValue(json);
            if (invoiceObject.getPdf()!=null) {
                invoiceObject.setRenderedPdf(getPdfResource2(invoiceObject.getPdf()));
            }
            invoiceObject.setUrl(invoiceUrl);
            return invoiceObject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<DomainObject> domainObjectsAsActionsResultFromJson(final String json){
        if (json==null) return Collections.emptyList();
        final ActionResult result;
        try {
            result = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .reader()
                    .forType(ActionResult.class)
                    .readValue(json);
            return result.getResult().getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static List<TaskObject> taskObjectsFromJson(final String json){
        if (json==null) return Collections.emptyList();
        try {
            return new ObjectMapper()
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                            .reader()
                            .forType(new TypeReference<ArrayList<TaskObject>>(){})
                            .readValue(json);
        } catch (IOException e) {

        }
        return Collections.emptyList();
    }

    public static String jsonFrom(final InvoicePayloadObject payload) {
        try {
            return new ObjectMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static StreamResource getPdfResource2(final String encodedPdf){
        try {
            final String[] strings = encodedPdf.split(":");
            final String pdfName = strings[0];
            final String pdfEncoded = strings[2];
            final byte[] pdfDecoded = Base64.getDecoder().decode(pdfEncoded);
            final StreamResource resource = new StreamResource(pdfName,
                    () -> new BufferedInputStream(new ByteArrayInputStream(pdfDecoded)));

            return resource;
        } catch (Exception e){
            return null;
        }
    }

}
