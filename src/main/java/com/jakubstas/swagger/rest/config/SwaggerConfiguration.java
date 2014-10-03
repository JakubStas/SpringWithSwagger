package com.jakubstas.swagger.rest.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.FilterFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.converter.ModelConverters;
import com.wordnik.swagger.jaxrs.config.ReflectiveJaxrsScanner;
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader;
import com.wordnik.swagger.reader.ClassReaders;

/**
 * Configuration bean to set up Swagger.
 */
@Component
public class SwaggerConfiguration {

    @Value("${swagger.resourcePackage}")
    private String resourcePackage;

    @Value("${swagger.basePath}")
    private String basePath;

    @Value("${swagger.apiVersion}")
    private String apiVersion;

    @PostConstruct
    public void init() {
        final ReflectiveJaxrsScanner scanner = new ReflectiveJaxrsScanner();
        scanner.setResourcePackage(resourcePackage);

        ScannerFactory.setScanner(scanner);
        ClassReaders.setReader(new DefaultJaxrsApiReader());
        ModelConverters.addConverter(new AccessHiddenModelConverter(), true);
        FilterFactory.setFilter(new AccessHiddenSpecFilter());

        final SwaggerConfig config = ConfigFactory.config();
        config.setApiVersion(apiVersion);
        config.setBasePath(basePath);
    }

    public String getResourcePackage() {
        return resourcePackage;
    }

    public void setResourcePackage(String resourcePackage) {
        this.resourcePackage = resourcePackage;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
