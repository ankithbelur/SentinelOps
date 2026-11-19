package com.sentinelops.service;

import com.sentinelops.model.EventRequest;
import com.sentinelops.model.Incident;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityServiceTest {

    private SecurityService service = new SecurityService();

    private EventRequest createEvent(String ip, String outcome) {
        EventRequest request = new EventRequest();
        request.username = "admin";
        request.sourceIp = ip;
        request.eventType = "LOGIN";
        request.outcome = outcome;
        return request;
    }

    @Test
    void shouldRecordEventWithoutIncidentForSingleFailedLogin() {
        service.record(createEvent("192.0.2.10", "FAILED"));

        assertEquals(1, service.allEvents().size());
        assertEquals(0, service.allIncidents().size());
    }

    @Test
    void shouldGenerateHighSeverityIncidentAfterFiveFailedLogins() {
        String ip = "192.0.2.10";

        for (int i = 0; i < 5; i++) {
            service.record(createEvent(ip, "FAILED"));
        }

        assertEquals(5, service.allEvents().size());
        assertEquals(1, service.allIncidents().size());

        Incident incident = service.allIncidents().get(0);

        assertEquals("HIGH", incident.severity);
        assertEquals("OPEN", incident.status);
        assertEquals(ip, incident.sourceIp);
    }

    @Test
    void shouldNotGenerateIncidentBeforeThreshold() {
        for (int i = 0; i < 4; i++) {
            service.record(createEvent("192.0.2.20", "FAILED"));
        }

        assertEquals(4, service.allEvents().size());
        assertTrue(service.allIncidents().isEmpty());
    }

    @Test
    void shouldNotGenerateIncidentForSuccessfulLogins() {
        for (int i = 0; i < 6; i++) {
            service.record(createEvent("192.0.2.30", "SUCCESS"));
        }

        assertEquals(6, service.allEvents().size());
        assertTrue(service.allIncidents().isEmpty());
    }

    @Test
    void shouldTrackFailedLoginsFromDifferentIpsSeparately() {
        for (int i = 0; i < 3; i++) {
            service.record(createEvent("192.0.2.40", "FAILED"));
            service.record(createEvent("192.0.2.41", "FAILED"));
        }

        assertEquals(6, service.allEvents().size());
        assertTrue(service.allIncidents().isEmpty());
    }
}
