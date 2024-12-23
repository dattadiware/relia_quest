package com.reliaquest.api.service.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.reliaquest.api.config.AppConfig;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.EmployeeRequestDto;
import com.reliaquest.api.service.remote.dto.DeleteEmployeeRequestDto;
import com.reliaquest.api.service.remote.dto.DeleteEmployeeResponseDto;
import com.reliaquest.api.service.remote.dto.EmployeeResponseDto;
import com.reliaquest.api.service.remote.dto.GetAllEmployeesResponseDto;
import com.reliaquest.api.service.remote.dto.GetEmployeeResponseDto;
import com.reliaquest.api.testutils.TestDataBuilder;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class EmployeeRemoteServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private AppConfig appConfig;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private EmployeeRemoteService employeeRemoteService;

    private static final String BASE_URL = "http://localhost/8080";
    private static final String RESOURCE_URL = "/employee";

    @BeforeEach
    void setUp() {

        Mockito.when(appConfig.getEmployeeServiceBaseUrl()).thenReturn(BASE_URL);
        Mockito.when(appConfig.getEmployeeServiceResourceUrl()).thenReturn(RESOURCE_URL);
    }

    @Test
    void getAllEmployees_ValidData_ReturnsSuccess() {
        GetAllEmployeesResponseDto mockResponse = new GetAllEmployeesResponseDto();
        mockResponse.setData(TestDataBuilder.createMockEmployeeList());

        ResponseEntity<GetAllEmployeesResponseDto> responseEntity = ResponseEntity.ok(mockResponse);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        CompletableFuture<GetAllEmployeesResponseDto> futureResult = employeeRemoteService.getAllEmployees();
        GetAllEmployeesResponseDto result = futureResult.join();
        assertNotNull(result);
        assertEquals(3, result.getData().size());
    }

    @Test
    void getAllEmployees_RateLimit_ThrowsException() {

        GetAllEmployeesResponseDto mockResponse = new GetAllEmployeesResponseDto();
        mockResponse.setData(TestDataBuilder.createMockEmployeeList());

        ResponseEntity<GetAllEmployeesResponseDto> responseEntity =
                ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(mockResponse);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.exchangeToMono(any()))
                .thenReturn(Mono.error(new WebClientResponseException(
                        HttpStatus.TOO_MANY_REQUESTS.value(), "Too Many Requests", null, null, null)));

        Assertions.assertThrows(RuntimeException.class, () -> {
            employeeRemoteService.getAllEmployees().join(); // Trigger the CompletableFuture
        });

        // Verify the method was retried the expected number of times
        verify(requestHeadersUriSpec, times(1)).exchangeToMono(any());
    }

    @Test
    void getAllEmployees_BadRequest_ThrowsException() {
        // Create a mock response with BAD_REQUEST
        GetAllEmployeesResponseDto mockResponse = new GetAllEmployeesResponseDto();
        mockResponse.setData(TestDataBuilder.createMockEmployeeList());

        ResponseEntity<GetAllEmployeesResponseDto> responseEntity =
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockResponse);

        // Mock WebClient behavior
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.exchangeToMono(any()))
                .thenReturn(Mono.error(new WebClientResponseException(
                        HttpStatus.BAD_REQUEST.value(), "Bad Request", null, null, null)));

        // Assert that the method throws RuntimeException
        Assertions.assertThrows(RuntimeException.class, () -> {
            employeeRemoteService.getAllEmployees().join();
        });

        verify(requestHeadersUriSpec, times(1)).exchangeToMono(any());
    }

    @Test
    void getEmployeeById_ValidData_ReturnsSuccess() {

        ResponseEntity<GetEmployeeResponseDto> responseEntity =
                ResponseEntity.ok(TestDataBuilder.createGetEmployeeResponse());

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        GetEmployeeResponseDto result =
                employeeRemoteService.getEmployeeById(UUID.fromString("9b4ae777-3df8-41ee-aabd-c603f43487dc"));

        assertNotNull(result);
        assertEquals("abc", result.getData().getEmployeeName());
    }

    @Test
    void getEmployeeById_EmployeeNotFound_ThrowsEmployeeNotFoundException() {

        ResponseEntity<GetEmployeeResponseDto> responseEntity =
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(TestDataBuilder.createGetEmployeeResponse());

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        Assertions.assertThrows(EmployeeNotFoundException.class, () -> {
            employeeRemoteService.getEmployeeById(UUID.fromString("9b4ae777-3df8-41ee-aabd-c603f43487dc"));
        });
    }

    @Test
    void deleteEmployeeByName_ValidData_ReturnsSuccess() {

        ResponseEntity<DeleteEmployeeResponseDto> responseEntity =
                ResponseEntity.ok(TestDataBuilder.createDeleteEmployeeResponse());

        when(webClient.method(HttpMethod.DELETE)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(), eq(DeleteEmployeeRequestDto.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(Mockito.any())).thenReturn(Mono.just(responseEntity));

        DeleteEmployeeResponseDto result = employeeRemoteService.deleteEmployeeByName("abc");

        assertNotNull(result);
        assertTrue(result.getData());
    }

    @Test
    public void testCreateEmployeeSuccess() {

        ResponseEntity<EmployeeResponseDto> mockResponse =
                ResponseEntity.ok(TestDataBuilder.createCreateEmployeeResponse());

        Mockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(Mockito.any(Mono.class), Mockito.eq(EmployeeRequestDto.class)))
                .thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.exchangeToMono(Mockito.any())).thenReturn(Mono.just(mockResponse));

        EmployeeResponseDto result =
                employeeRemoteService.createEmployee(TestDataBuilder.createMockCreateEmployeeRequestDto());

        assertNotNull(result);
        assertEquals("abc", result.getData().getEmployeeName());
    }
}
