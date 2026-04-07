package com.rubilia.exercise201.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodRestConfig implements RepositoryRestConfigurer, WebMvcConfigurer {
    @Autowired
    private EntityManager entityManager;

    private String url = "https://rubilia.store";

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(entityManager.getMetamodel().getEntities().stream().map(Type::getJavaType).toArray(Class[]::new));
    }

    @Override
    public void addCorsMappings(CorsRegistry cors) {
        cors.addMapping("/**")
            .allowedOrigins(url)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }

    private void blockHttpMethods(Class c, RepositoryRestConfiguration config, HttpMethod[] methods) {
        config.getExposureConfiguration()
            .forDomainType(c)
            .withItemExposure((metadata, httpMethods) -> httpMethods.disable(methods))
            .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable(methods));
    }
}