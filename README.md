# SentinelOps — Security Event Monitoring & Incident Management API

DevOps course project: Java 17, Spring Boot, Maven, Jenkins, JUnit, Ansible, Docker and Kubernetes.\n
## API
- `GET /` service metadata
- `GET /health` health probe
- `POST /api/events` record security event
- `GET /api/events` list events
- `GET /api/events/{id}` get event
- `GET /api/incidents` list generated incidents
- `GET /api/incidents/{id}` get incident
- `PATCH /api/incidents/{id}/status` update incident status (OPEN, INVESTIGATING, RESOLVED)
- `GET /api/stats` basic counters

## Run locally
```bash
mvn clean test
mvn clean package
java -jar target/sentinelops-1.0.0.jar
```

## Trigger sample failed login events
```bash
for i in 1 2 3 4 5; do curl -s -X POST http://localhost:8080/api/events -H 'Content-Type: application/json' -d '{"username":"demo","sourceIp":"192.0.2.10","eventType":"LOGIN","outcome":"FAILED"}'; done
curl http://localhost:8080/api/incidents
```
The demonstration rule creates a HIGH incident at the configured failed-login threshold. This is an educational simulation, not a production security detector.

## CI Pipeline
Builds and tests automatically through Jenkins when changes are pushed to the main branch.
