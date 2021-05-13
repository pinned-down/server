package de.pinneddown.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BackendServiceInterface {
    private final DiscoveryClient discoveryClient;
    private final HttpHeaders httpHeaders;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public BackendServiceInterface(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;

        // Setup HTTP headers.
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        List<MediaType> acceptedTypes = new ArrayList<>();
        acceptedTypes.add(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(acceptedTypes);
    }

    public Optional<URI> discoverService(String serviceName) {
        List<ServiceInstance> instances = this.discoveryClient.getInstances(serviceName);

        if (instances.isEmpty()) {
            logger.error("Unable to connect to service: {}", serviceName);
            return Optional.empty();
        }

        ServiceInstance instance = instances.get(0);
        URI serviceUri = instance.getUri();

        logger.info("Found {} at {}.",  instance.getServiceId(), serviceUri);

        return Optional.of(serviceUri);
    }

    public <TRequest, TResponse> TResponse sendRequest(URI serviceUri, String relativeUri, TRequest request,
                                                       Class<TResponse> responseClass) {
        HttpEntity<TRequest> httpEntity = new HttpEntity<>(request, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(serviceUri + relativeUri, httpEntity, responseClass);
    }

    public <TRequest> void sendRequest(URI serviceUri, String relativeUri, TRequest request) {
        HttpEntity<TRequest> httpEntity = new HttpEntity<>(request, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForLocation(serviceUri + relativeUri, httpEntity);
    }

}
