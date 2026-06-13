package service;

import businessObjects.ghsa.SecurityAdvisory;
import exceptions.ApiCallException;
import handlers.JsonResponseHandler;
import handlers.SecurityAdvisoryMarshaller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

@Tag("regression")
class GhsaApiServiceTest {
    
    @Mock
    private GhsaResponseProcessor processor;
    
    @Mock
    private SecurityAdvisoryMarshaller marshaller;
    
    @Mock
    private JsonResponseHandler handler;
    
    private GhsaApiService ghsaApiService;
    private static final String TEST_PAT = "test_github_pat_token";
    private static final String TEST_GHSA_ID = "GHSA-test-1234";
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ghsaApiService = new GhsaApiService(processor, marshaller, handler, TEST_PAT);
    }
    
    @Test
    void testGhsaApiServiceConstructorAcceptsToken() {
        assertNotNull(ghsaApiService);
    }
}