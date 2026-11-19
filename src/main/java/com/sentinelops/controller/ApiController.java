package com.sentinelops.controller;
import com.sentinelops.model.*; import com.sentinelops.service.SecurityService;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController
public class ApiController {
  private final SecurityService service; public ApiController(SecurityService s){this.service=s;}
  @GetMapping("/") public Map<String,Object> root(){return Map.of("service","SentinelOps","description","Security Event Monitoring & Incident Management API","version","1.0.0");}
  @GetMapping("/health") public Map<String,String> health(){return Map.of("status","UP");}
  @PostMapping("/api/events") public ResponseEntity<?> createEvent(@RequestBody EventRequest req){try{return ResponseEntity.status(HttpStatus.CREATED).body(service.record(req));}catch(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}}
  @GetMapping("/api/events") public List<SecurityEvent> events(){return service.allEvents();}
  @GetMapping("/api/events/{id}") public ResponseEntity<?> event(@PathVariable long id){return service.event(id).<ResponseEntity<?>>map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}
  @GetMapping("/api/incidents") public List<Incident> incidents(){return service.allIncidents();}
  @GetMapping("/api/incidents/{id}") public ResponseEntity<?> incident(@PathVariable long id){return service.incident(id).<ResponseEntity<?>>map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}
  @PatchMapping("/api/incidents/{id}/status") public ResponseEntity<?> status(@PathVariable long id,@RequestBody StatusRequest req){if(req.status==null || !List.of("OPEN","INVESTIGATING","RESOLVED").contains(req.status.toUpperCase()))return ResponseEntity.badRequest().body(Map.of("error","status must be OPEN, INVESTIGATING or RESOLVED")); return service.updateIncident(id,req.status)?ResponseEntity.ok(service.incident(id).get()):ResponseEntity.notFound().build();}
  @GetMapping("/api/stats") public Map<String,Object> stats(){return service.stats();}
}
