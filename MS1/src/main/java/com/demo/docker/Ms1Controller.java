package com.demo.docker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/ms1")
public class Ms1Controller {

    private static final Logger log = LoggerFactory.getLogger(Ms1Controller.class);

    private final RestTemplate restTemplate;
    private final String ms2ApiBaseUrl;

    @Autowired
    public Ms1Controller(RestTemplate restTemplate, @Value("${ms2.service.base-url}") String ms2ApiBaseUrl) {
        this.restTemplate = restTemplate;
        this.ms2ApiBaseUrl = ms2ApiBaseUrl;
    }

    @GetMapping("/greet")
    public String greet() {
        return "Greetings from Microservice 1!";
    }

    @GetMapping("/persons")
    public ResponseEntity<?> fetchAllPersonsFromMs2() {
        String targetUrl = ms2ApiBaseUrl + "/persons";
        log.info("MS1 calling MS2 to get all persons: GET {}", targetUrl);
        try {
            ResponseEntity<PersonDto[]> response = restTemplate.getForEntity(targetUrl, PersonDto[].class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                PersonDto[] persons = response.getBody();
                log.info("MS1 successfully received {} persons from MS2.", persons.length);
                return ResponseEntity.ok(persons);
            } else {
                log.warn("MS1: Received non-2xx response or empty body from MS2 {}: Status={}, Body={}",
                        targetUrl, response.getStatusCode(), response.getBody());
                return ResponseEntity.status(response.getStatusCode())
                        .body("Could not fetch persons from MS2. Status: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            log.error("MS1: Error calling MS2 at {}: {}", targetUrl, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Error communicating with MS2: " + e.getMessage());
        }
    }

    @GetMapping("/persons/{id}")
    public ResponseEntity<?> fetchPersonByIdFromMs2(@PathVariable Long id) {
        String targetUrl = ms2ApiBaseUrl + "/persons/" + id;
        log.info("MS1 calling MS2 to get person by ID: GET {}", targetUrl);

        try {
            ResponseEntity<PersonDto> response = restTemplate.getForEntity(targetUrl, PersonDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                PersonDto person = response.getBody();
                log.info("MS1 successfully received person from MS2: {}", person);
                return ResponseEntity.ok(person);
            } else {
                log.warn("MS1: Received non-2xx response or empty body from MS2 {}: Status={}, Body={}",
                        targetUrl, response.getStatusCode(), response.getBody());

                if (response.getStatusCode().value() == 404) {
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.status(response.getStatusCode())
                        .body("Could not fetch person with ID " + id + " from MS2. Status: "
                                + response.getStatusCode());
            }
        } catch (RestClientException e) {
            log.error("MS1: Error calling MS2 at {}: {}", targetUrl, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Error communicating with MS2 for person ID " + id + ": " + e.getMessage());
        }
    }
}