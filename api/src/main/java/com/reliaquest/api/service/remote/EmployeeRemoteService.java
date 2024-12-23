package com.reliaquest.api.service.remote;

import com.reliaquest.api.config.AppConfig;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceRemoteException;
import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.model.EmployeeRequestDto;
import com.reliaquest.api.service.remote.dto.DeleteEmployeeRequestDto;
import com.reliaquest.api.service.remote.dto.DeleteEmployeeResponseDto;
import com.reliaquest.api.service.remote.dto.EmployeeResponseDto;
import com.reliaquest.api.service.remote.dto.GetAllEmployeesResponseDto;
import com.reliaquest.api.service.remote.dto.GetEmployeeResponseDto;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

/**
 * This Java class, EmployeeRemoteService, is a Spring service designed to manage employee-related operations
 *  by interacting with a remote employee service. It employs Resilience4j annotations for fault tolerance,
 *  including retry, circuit breaker, time limiter, and bulkhead patterns. The service provides methods to
 *  retrieve all employees, fetch an employee by ID, delete an employee by name, and create a new employee.
 *  Each method constructs the appropriate HTTP request using Spring's WebClient and handles responses,
 *  throwing custom exceptions for various error scenarios. A fallback method is provided for handling
 *  failures in retrieving all employees, returning an empty response.
 */
@Service
@RequiredArgsConstructor
public class EmployeeRemoteService {
    @Qualifier("employeeServiceClient") private final WebClient webClient;

    private final AppConfig appConfig;

    @Retry(name = "employeeService")
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getAllEmployeesFallback")
    @TimeLimiter(name = "employeeService")
    @Bulkhead(name = "employeeService", type = Bulkhead.Type.SEMAPHORE)
    public CompletableFuture<GetAllEmployeesResponseDto> getAllEmployees() {
        return CompletableFuture.supplyAsync(() -> {
            String employeeServiceUrl = getEmployeeServiceUrl();
            try {
                ResponseEntity<GetAllEmployeesResponseDto> response = getAllEmployees(employeeServiceUrl);
                return handleServiceResponse(response, "fetching all employees");
            } catch (WebClientException ex) {
                handleWebClientException();
                throw new RuntimeException("Failed to fetch employees", ex);
            }
        });
    }

    private ResponseEntity<GetAllEmployeesResponseDto> getAllEmployees(String url) {
        return webClient
                .get()
                .uri(url)
                .exchangeToMono(response -> response.toEntity(GetAllEmployeesResponseDto.class))
                .block();
    }

    @Retry(name = "employeeService")
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getAllEmployeesFallback")
    @TimeLimiter(name = "employeeService")
    @Bulkhead(name = "employeeService", type = Bulkhead.Type.SEMAPHORE)
    public GetEmployeeResponseDto getEmployeeById(UUID id) {
        String employeeServiceUrl = getEmployeeServiceUrl() + "/" + id;
        try {
            ResponseEntity<GetEmployeeResponseDto> response = getEmployeeById(employeeServiceUrl);
            if (response == null) {
                throw new EmployeeNotFoundException("Employee not found");
            }
            return handleServiceResponse(response, "fetching employee by ID: " + id);
        } catch (WebClientException ex) {
            handleWebClientException();
            return null;
        }
    }

    private ResponseEntity<GetEmployeeResponseDto> getEmployeeById(String url) {
        return webClient
                .get()
                .uri(url)
                .exchangeToMono(response -> response.toEntity(GetEmployeeResponseDto.class))
                .block();
    }

    @Retry(name = "employeeService")
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getAllEmployeesFallback")
    @TimeLimiter(name = "employeeService")
    @Bulkhead(name = "employeeService", type = Bulkhead.Type.SEMAPHORE)
    public DeleteEmployeeResponseDto deleteEmployeeByName(String name) {
        String employeeServiceUrl = getEmployeeServiceUrl();
        DeleteEmployeeRequestDto request = new DeleteEmployeeRequestDto(name);
        try {
            ResponseEntity<DeleteEmployeeResponseDto> response = deleteEmployee(employeeServiceUrl, request);
            return handleServiceResponse(response, "deleting employee with name: " + name);
        } catch (WebClientException ex) {
            handleWebClientException();
            return null;
        }
    }

    private ResponseEntity<DeleteEmployeeResponseDto> deleteEmployee(String url, DeleteEmployeeRequestDto request) {
        return webClient
                .method(HttpMethod.DELETE)
                .uri(url)
                .body(Mono.just(request), DeleteEmployeeRequestDto.class)
                .exchangeToMono(response -> response.toEntity(DeleteEmployeeResponseDto.class))
                .block();
    }

    public EmployeeResponseDto createEmployee(EmployeeRequestDto employeeRequestDto) {
        String employeeServiceUrl = getEmployeeServiceUrl();
        try {
            ResponseEntity<EmployeeResponseDto> response = insertEmployee(employeeRequestDto, employeeServiceUrl);
            return handleServiceResponse(response, "creating employee with name: " + employeeRequestDto.getName());
        } catch (WebClientException ex) {
            handleWebClientException();
            return null;
        }
    }

    @Retry(name = "employeeService")
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getAllEmployeesFallback")
    @TimeLimiter(name = "employeeService")
    @Bulkhead(name = "employeeService", type = Bulkhead.Type.SEMAPHORE)
    private ResponseEntity<EmployeeResponseDto> insertEmployee(EmployeeRequestDto employeeRequestDto, String url) {
        return webClient
                .post()
                .uri(url)
                .body(Mono.just(employeeRequestDto), EmployeeRequestDto.class)
                .exchangeToMono(response -> response.toEntity(EmployeeResponseDto.class))
                .block();
    }

    private <T> T handleServiceResponse(ResponseEntity<T> response, String operation) {
        HttpStatus status = (HttpStatus) response.getStatusCode();
        if (status == HttpStatus.OK) {
            return response.getBody();
        } else if (status == HttpStatus.TOO_MANY_REQUESTS) {
            throw new TooManyRequestsException("Max retries exceeded: 429 Too Many Requests");
        } else if (status == HttpStatus.NOT_FOUND) {
            throw new EmployeeNotFoundException("Employee not found");
        } else {
            throw new EmployeeServiceRemoteException("Error during " + operation + ". Status: " + status);
        }
    }

    private String getEmployeeServiceUrl() {
        return appConfig.getEmployeeServiceBaseUrl() + appConfig.getEmployeeServiceResourceUrl();
    }

    private void handleWebClientException() {
        throw new EmployeeServiceRemoteException("Error connecting to employee service. Please try again later.");
    }

    public CompletableFuture<GetAllEmployeesResponseDto> getAllEmployeesFallback(Throwable throwable) {

        GetAllEmployeesResponseDto fallbackResponse = new GetAllEmployeesResponseDto();
        System.err.println("Fallback method invoked: " + throwable.getMessage());
        return CompletableFuture.completedFuture(fallbackResponse);
    }
}
