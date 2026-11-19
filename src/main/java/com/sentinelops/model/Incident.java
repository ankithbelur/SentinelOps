package com.sentinelops.model;
import java.time.Instant;
public class Incident {
  public Long id;
  public String title;
  public String description;
  public String sourceIp;
  public String severity;
  public String status;
  public Instant createdAt;
  public Incident() {}
  public Incident(Long id, String title, String description, String sourceIp, String severity, String status, Instant createdAt) {
    this.id=id; this.title=title; this.description=description; this.sourceIp=sourceIp; this.severity=severity; this.status=status; this.createdAt=createdAt;
  }
}
