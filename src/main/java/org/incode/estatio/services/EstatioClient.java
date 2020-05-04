package org.incode.estatio.services;

import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import org.incode.estatio.dom.DomainObject;
import org.incode.estatio.dom.InvoiceObject;
import org.incode.estatio.dom.TaskObject;
import org.incode.estatio.dom.payload.InvoicePayloadObject;

public class EstatioClient {

    final public static String defaultAcceptHeader = "application/json;profile=urn:org.apache.isis/v1;suppress=true";
    public final static String MYTASKS_URL = "https://estatio-test.int.ecpnv.com/restful/services/task.TaskMenu/actions/myTasks/invoke";
    public final static String ORGANISATIONS_REFDATA_URL = "https://estatio-test.int.ecpnv.com/restful/services/org.estatio.app.menus.party.OrganisationMenu/actions/allOrganisations/invoke";

    public static List<TaskObject> getTasks(final String username, final String pass) {
        final String jsonString = EstatioClient
                .getJson(username, pass, MYTASKS_URL,"application/json;profile=urn:org.apache.isis/v1;suppress=true", "POST", null);
        return JsonAdapter.taskObjectsFromJson(jsonString);
    }

    public static List<DomainObject> allOrganisations(final String username, final String pass){
        final String jsonString = EstatioClient.getJson(username, pass, ORGANISATIONS_REFDATA_URL,
                "application/json;profile=urn:org.restfulobjects:repr-types/action-result", "POST", null);
        return JsonAdapter.domainObjectsAsActionsResultFromJson(jsonString);
    }

    public static InvoiceObject getInvoiceObject(final String username, final String pass, final String encodedUrl){
        String decodedUrl = URLDecoder.decode(encodedUrl);
        String invoiceUrl = decodedUrl.replace("http", "https");
        final String payLoad = EstatioClient
                .getJson(username, pass, invoiceUrl, "application/json;profile=urn:org.apache.isis/v1;suppress=true",
                        "GET", null);
        if (payLoad!=null) {
            return JsonAdapter.invoiceObjectFromJson(payLoad, invoiceUrl);
        } else {
            return null;
        }
    }

    public static String submitInvoice(final String username, final String pass, final String invoiceUrl, final InvoicePayloadObject payload){
        if (payload!=null){
            return EstatioClient.getJson(username, pass, invoiceUrl + "/actions/completeInvoice/invoke", null, "PUT",
                    JsonAdapter.jsonFrom(payload));
        }
        return null;
    }

    private static String getJson(final String username, final String pass, final String url, final String accept, final String method, final String payload){
        final Client client = ClientBuilder.newClient();
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(username, pass);
        client.register(feature);
        String invoiceUrl = url;
        WebTarget webTarget = client.target(invoiceUrl);
        webTarget.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.TRUE); // tried in order to allow redirects to https, but does not work
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        invocationBuilder.accept(accept==null ? defaultAcceptHeader : accept);
        Response response;
        if (method == "GET") {
            response = invocationBuilder.get();
        } else {
            if (method == "PUT"){
                response = invocationBuilder.put(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE));
            } else {
                response = invocationBuilder.post(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE));
            }
        }
        final int status = response.getStatus();
        if (status==200) {
            return response.readEntity(String.class);
        } else {
            //TODO: create meaningfull message for user
            final String errorMessageBody = response.readEntity(String.class);
            return null;
        }
    }

}
