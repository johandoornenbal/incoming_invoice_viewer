package org.incode.estatio.dom.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class InvoicePayloadObjectTest {

    @Test
    public void serialize() throws Exception {
        // given
        final InvoicePayloadObject obj = new InvoicePayloadObject();
        // when
        obj.setIncomingInvoiceType(new StringValue("MY_TYPE"));
        String json = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .writeValueAsString(obj);
        // then
        Assertions.assertThat(json).isEqualTo("{\"incomingInvoiceType\":{\"value\":\"MY_TYPE\"}}");

        // and when property mapped to custom name
        obj.setCreateNewSupplier(new StringValue("true"));
        json = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .writeValueAsString(obj);
        // then
        Assertions.assertThat(json).isEqualTo("{\"incomingInvoiceType\":{\"value\":\"MY_TYPE\"},\"createNewSupplier?\":{\"value\":\"true\"}}");
    }

    @Test
    public void deserialize() throws Exception {
        // given
        String json = "{\"incomingInvoiceType\":{\"value\":\"MY_TYPE\"}}";
        // when
        InvoicePayloadObject object = new ObjectMapper().reader().forType(InvoicePayloadObject.class).readValue(json);
        // then
        Assertions.assertThat(object.getIncomingInvoiceType().getValue()).isEqualTo("MY_TYPE");

        // and given - unknown property and property mapped to custom name
        json = "{\"something\":\"some thing\",\"incomingInvoiceType\":{\"value\":\"MY_TYPE\"},\"createNewSupplier?\":{\"value\":\"true\"}}";
        object = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .reader()
                .forType(InvoicePayloadObject.class)
                .readValue(json);
        // then
        Assertions.assertThat(object.getCreateNewSupplier().getValue()).isEqualTo("true");
        Assertions.assertThat(object.getIncomingInvoiceType().getValue()).isEqualTo("MY_TYPE");
    }

}