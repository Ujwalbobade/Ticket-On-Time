package com.example.BookingService.Service;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class RestTemplateClient {

    private final RestTemplate restTemplate;

    @Autowired
    public RestTemplateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getExampleData(String url) {
        // Example of a GET request
        return restTemplate.getForObject(url, String.class);
    }

    public String postExampleData(String url, Object request) {
        // Example of a POST request
        return restTemplate.postForObject(url, request, String.class);
    }
}
