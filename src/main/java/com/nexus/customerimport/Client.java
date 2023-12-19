package com.nexus.customerimport;

// Add your package information here

// Required imports
import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.sync.commons.utils.ClientConfigurationUtils;
import io.vrap.rmf.base.client.oauth2.*;
import org.springframework.beans.factory.annotation.Value;

public class Client {
    @Value("${ClientId}")
    private String ClientId;

    @Value("${ClientSecret}")
    private String ClientSecret;

    @Value("${ProjectKey}")
    private String ProjectKey;
    public ProjectApiRoot createApiClient() {
        final ClientCredentials clientCredentials =
                new ClientCredentialsBuilder()
                        .withClientId("kQ7dCwrrkWS_0iutJu8qUmiZ")
                        .withClientSecret("faFLPzhDwaeQeoCGBmq0Rd5Ic0Y-mxyh")
                        .withScopes("manage_project:customer-sync4 view_audit_log:customer-sync4 manage_api_clients:customer-sync4 view_api_clients:customer-sync4")
                        .build();


        return ClientConfigurationUtils.createClient(ProjectKey, clientCredentials, "https://auth.us-central1.gcp.commercetools.com/oauth/token", "https://api.us-central1.gcp.commercetools.com");
    }
}
