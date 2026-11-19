package com.sentinelops.service;
import com.sentinelops.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
@Service
public class SecurityService {
  private final AtomicLong eventIds = new AtomicLong(0), incidentIds = new AtomicLong(0);
  private final List<SecurityEvent> events = new ArrayList<>();
  private final List<Incident> incidents = new ArrayList<>();
  @Value("${sentinelops.detection.failed-login-threshold:5}")
  private int threshold = 5;
  public synchronized SecurityEvent record(EventRequest req) {
    if(req.username==null || req.sourceIp==null || req.eventType==null || req.outcome==null) throw new IllegalArgumentException("username, sourceIp, eventType and outcome are required");
    String severity = "failed".equalsIgnoreCase(req.outcome) ? "MEDIUM" : "LOW";
    SecurityEvent ev = new SecurityEvent(eventIds.incrementAndGet(), req.username, req.sourceIp, req.eventType, req.outcome.toUpperCase(), severity, Instant.now());
    events.add(ev);
    if("LOGIN".equalsIgnoreCase(req.eventType) && "FAILED".equalsIgnoreCase(req.outcome)) {
      long count = events.stream().filter(e -> e.sourceIp.equals(req.sourceIp) && "LOGIN".equalsIgnoreCase(e.eventType) && "FAILED".equalsIgnoreCase(e.outcome)).count();
      boolean alreadyOpen = incidents.stream().anyMatch(i -> i.sourceIp.equals(req.sourceIp) && i.title.contains("Repeated login failures") && !"RESOLVED".equals(i.status));
      if(count >= threshold && !alreadyOpen) incidents.add(new Incident(incidentIds.incrementAndGet(), "Repeated login failures detected", count + " failed login attempts observed from source IP " + req.sourceIp, req.sourceIp, "HIGH", "OPEN", Instant.now()));
    }
    return ev;
  }
  public synchronized List<SecurityEvent> allEvents(){ return new ArrayList<>(events); }
  public synchronized Optional<SecurityEvent> event(long id){ return events.stream().filter(e->e.id==id).findFirst(); }
  public synchronized List<Incident> allIncidents(){ return new ArrayList<>(incidents); }
  public synchronized Optional<Incident> incident(long id){ return incidents.stream().filter(i->i.id==id).findFirst(); }
  public synchronized boolean updateIncident(long id,String status){ return incident(id).map(i->{i.status=status.toUpperCase(); return true;}).orElse(false); }
  public synchronized Map<String,Object> stats(){ Map<String,Object> m=new LinkedHashMap<>(); m.put("events",events.size()); m.put("incidents",incidents.size()); m.put("openIncidents",incidents.stream().filter(i->!"RESOLVED".equals(i.status)).count()); return m; }
}
