package com.example.Ticket.Service.Service;

import com.example.Ticket.Service.DTOs.TicketRequestDTO;
import com.example.Ticket.Service.DTOs.ValidationResDTo;
import com.example.Ticket.Service.MongoModel.Ticket;
import com.example.Ticket.Service.MongoModel.TicketStatus;
import com.example.Ticket.Service.MongoModel.TicketType;
import com.example.Ticket.Service.MongoRepository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    @Autowired
    private RestTemplate restTemplate;

    private final P_D_FService gridFSService; // Inject GridFSService if needed

    public Ticket createTicket(TicketRequestDTO ticketRequestDTO) {
        // Log ticket number
        log.info(ticketRequestDTO.getTicketNo());

        // Validate mandatory fields
        if (ticketRequestDTO.getTicketNo() == null) {
            throw new IllegalArgumentException("Ticket number is required");
        }
        if (ticketRequestDTO.getTicketType() == null) {
            throw new IllegalArgumentException("Ticket type is required");
        }
        if (ticketRequestDTO.getStatus() == null) {
            throw new IllegalArgumentException("Ticket status is required");
        }

        // Check if a ticket with the same ticket number already exists
        Optional<Ticket> existingTicket = ticketRepository.findByTicketNo(ticketRequestDTO.getTicketNo());

        if (existingTicket.isPresent()) {
            throw new IllegalArgumentException("Ticket with this number already exists");
        }
        log.info("Ticket no is unique");
        // Create and populate the Ticket object
        Ticket ticket = new Ticket();
        ticket.setTicketNo(ticketRequestDTO.getTicketNo());
        ticket.setUserid(ticketRequestDTO.getPostedByUserId());
        ticket.setDescription(ticketRequestDTO.getDescription());
        ticket.setPrice(ticketRequestDTO.getPrice());

        try {
            ticket.setTicketType(TicketType.valueOf(ticketRequestDTO.getTicketType()));
            ticket.setStatus(TicketStatus.valueOf(ticketRequestDTO.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ticket type or status", e);
        }

        if (ticketRequestDTO.getTicketType().equals(TicketType.TRAIN)) {
            ticket.setFrom(ticketRequestDTO.getFrom());
            ticket.setTo(ticketRequestDTO.getTo());
        } else {
            ticket.setFrom("NA");
            ticket.setTo("NA");
        }

        // Handle file if provided
        MultipartFile file = ticketRequestDTO.getTicket_file();
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = gridFSService.uploadFile(file);
                ticket.setTicket_file_path(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Error uploading file", e);
            }
        }

        // Validate the ticket
        ValidationResDTo validationResult = validatetheTicket(ticketRequestDTO);
        ticket.setValidationerror(validationResult.getValidationerror());
        log.info(validationResult.toString());

        if ("Success".equals(validationResult.getStatus())) {
            ticket.setValidtionStatus(true);
            log.info("Ticket save successfully");
            return ticketRepository.save(ticket);
        } else {
            return ticket;
        }
    }




    public Optional<Ticket> Get_ticket_with_ticketNo(String ticketNo) {
        return ticketRepository.findByTicketNo(ticketNo);
    }

    public List<Ticket> getallValidtickets() {
        List<Ticket> a = ticketRepository.findByStatus(TicketStatus.AVAILIABLE);
        return a;
    }


    public ValidationResDTo validatetheTicket(TicketRequestDTO ticketRequestDTO) {
        MultipartFile file = ticketRequestDTO.getTicket_file();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required for validation");
        }

        try {
            // Create a resource from the MultipartFile
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            // Create the multipart body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            body.add("data", ticketRequestDTO);
            // Add other fields if necessary
            // body.add("other_field", ticketRequestDTO.getOtherField());

            // Create the request entity
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Send the request
            ResponseEntity<ValidationResDTo> responseEntity = restTemplate.exchange(
                    "http://127.0.0.1:5000/upload",
                    HttpMethod.POST,
                    requestEntity,
                    ValidationResDTo.class
            );

            // Handle the response
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            } else {
                // Handle non-successful responses
                throw new RuntimeException("Failed to validate ticket: " + responseEntity.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            // Handle 400 BAD REQUEST by extracting the error message from the response body
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String errorMessage = e.getResponseBodyAsString(); // Extracts the error message
                log.error("Error during ticket validation: " + errorMessage);
                // Create a response object to send to the frontend
                ValidationResDTo errorResponse = new ValidationResDTo();
                errorResponse.setStatus("Error");
                errorResponse.setValidationerror(errorMessage);
                return errorResponse;
            } else {
                throw new RuntimeException("Failed to validate ticket", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing file", e);
        }
    }
}
