package com.jakubstas.swagger;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("resources")
public class SpringWithSwagger extends ResourceConfig {

    public SpringWithSwagger() {
        final String myRestPackage = "com.jakubstas.swagger.rest";
        final String jacksonPackage = "org.codehaus.jackson.jaxrs";

        final String swaggerJaxrsJsonPackage = "com.wordnik.swagger.jaxrs.json";
        final String swaggerJaxrsListingPackage = "com.wordnik.swagger.jaxrs.listing";

        packages(swaggerJaxrsJsonPackage, swaggerJaxrsListingPackage, jacksonPackage, myRestPackage);

        // enable multipart
        register(MultiPartFeature.class);
    }
}
