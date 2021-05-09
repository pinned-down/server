package de.pinneddown.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

public class BackendService {
    private final DiscoveryClient discoveryClient;
    private final HttpHeaders httpHeaders;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private URI serviceUri;

    public BackendService(DiscoveryClient discoveryClient, HttpHeaders httpHeaders) {
        this.discoveryClient = discoveryClient;
        this.httpHeaders = httpHeaders;
    }

    protected boolean discoverService(String serviceName) {
        List<ServiceInstance> instances = this.discoveryClient.getInstances(serviceName);

        if (instances.isEmpty()) {
            logger.error("Unable to connect to service: {}", serviceName);
            return false;
        }

        ServiceInstance instance = instances.get(0);
        serviceUri = instance.getUri();

        logger.info("Found {} at {}.",  instance.getServiceId(), serviceUri);

        return true;
    }

    protected <TRequest, TResponse> TResponse sendRequest(String relativeUri, TRequest request, Class<TResponse> responseClass) {
        HttpEntity<TRequest> httpEntity = new HttpEntity<>(request, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(serviceUri + relativeUri, httpEntity, responseClass);
    }

    protected <TRequest> void sendRequest(String relativeUri, TRequest request) {
        HttpEntity<TRequest> httpEntity = new HttpEntity<>(request, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForLocation(serviceUri + relativeUri, httpEntity);
    }

    protected boolean hasService() {
        return serviceUri != null;
    }
}
