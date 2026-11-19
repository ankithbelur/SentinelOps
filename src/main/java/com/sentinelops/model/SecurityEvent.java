package com.sentinelops.model;
import java.time.Instant;
public class SecurityEvent {
  public Long id;
  public String username;
  public String sourceIp;
  public String eventType;
  public String outcome;
  public String severity;
  public Instant timestamp;
  public SecurityEvent() {}
  public SecurityEvent(Long id, String username, String sourceIp, String eventType, String outcome, String severity, Instant timestamp) {
    this.id=id; this.username=username; this.sourceIp=sourceIp; this.eventType=eventType; this.outcome=outcome; this.severity=severity; this.timestamp=timestamp;
  }
}
