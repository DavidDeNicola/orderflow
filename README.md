# OrderFlow

[![CI order-service](https://github.com/DavidDeNicola/orderflow/actions/workflows/ci-order-service.yml/badge.svg)](https://github.com/DavidDeNicola/orderflow/actions/workflows/ci-order-service.yml)
[![CI inventory-service](https://github.com/DavidDeNicola/orderflow/actions/workflows/ci-inventory-service.yml/badge.svg)](https://github.com/DavidDeNicola/orderflow/actions/workflows/ci-inventory-service.yml)

Mini e-commerce a microservizi con **Spring Boot 4**, **Kafka**, **PostgreSQL**, **Docker**, **Kubernetes** e **GitHub Actions**.
I due servizi comunicano **solo tramite eventi Kafka** e hanno ciascuno il proprio database.

## Architettura

```
Client
  │ POST /orders
  ▼
order-service :8081 ──▶ topic "orders-created" (3 partizioni, key = customerId) ──▶ inventory-service :8082
  │                                                                                   │
  ▼                                                                                   ├─▶ messaggi illeggibili → "orders-created-dlt"
orders_db (PostgreSQL, JSONB)                                                         ▼
                                                                        inventory_db (PostgreSQL)
                                                                        UPSERT dello stock + idempotenza
```

## Cosa mostra il progetto

- **order-service**: API REST, dettagli dell'ordine in una colonna **JSONB** con indice GIN, pubblicazione di eventi su Kafka
- **inventory-service**: consumer Kafka con **idempotenza** (tabella `processed_events`), UPSERT dello stock, **Dead Letter Topic** per i messaggi non leggibili
- **Test**: integrazione con MockMvc su H2 ed EmbeddedKafka, senza infrastruttura esterna
- **Docker**: Dockerfile multi-stage (JDK per la build, solo JRE per l'esecuzione)
- **Docker Compose**: Kafka con listener interni ed esterni, PostgreSQL con due database, healthcheck
- **Kubernetes** (minikube): Deployment con readiness e liveness probe, Service, ConfigMap, Secret, scaling a 3 repliche (una per partizione), rolling update e rollback
- **CI**: un workflow GitHub Actions per servizio con path filter; dopo i test su `main` le immagini vengono costruite e pubblicate su **GitHub Container Registry**

## Decisioni tecniche

| Scelta | Motivo |
|---|---|
| Comunicazione solo via Kafka | i servizi sono disaccoppiati: order-service non sa chi consuma i suoi eventi |
| Un database per servizio | nessuna dipendenza tra i dati dei servizi, niente chiavi esterne tra database |
| Chiave del messaggio = `customerId` | gli ordini dello stesso cliente finiscono nella stessa partizione e vengono letti in ordine |
| Idempotenza con `processed_events` | Kafka garantisce una consegna *at-least-once*: un evento duplicato non scala lo stock due volte |
| Dead Letter Topic | un messaggio non leggibile viene spostato da parte invece di bloccare il consumer |
| 3 repliche di inventory-service | pari alle 3 partizioni: il numero di partizioni è il limite di parallelismo del consumer group |
| Immagini costruite in CI | il runner è amd64 come i server, e il server non deve compilare con Maven |

## Limiti noti e possibili miglioramenti

- **Dual write**: il salvataggio dell'ordine e l'invio dell'evento non sono atomici. Soluzione: pattern **Transactional Outbox**
- **Schema del database** gestito con `ddl-auto=update`: in produzione userei **Flyway** per versionare lo schema e gli indici
- **Test sulle query native PostgreSQL** (JSONB, `ON CONFLICT`) disattivati, perché H2 non supporta quella sintassi: con **Testcontainers** girerebbero su un PostgreSQL reale
- **Errori di validazione**: un `@RestControllerAdvice` restituirebbe nel 400 quale campo non è valido

## Avvio in locale

Requisiti: Docker Desktop.

```bash
cp .env.example .env          # poi imposta POSTGRES_PASSWORD
docker compose up -d --build
```

Creare un ordine:

```bash
curl -X POST localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"CUST-1","shipping":{"street":"Via Roma 1","city":"Brindisi","zip":"72100"},"items":[{"productCode":"KB-001","quantity":2,"unitPrice":49.90}]}'
```

## Struttura

```
orderflow/
 ├─ order-service/          servizio ordini (Spring Boot)
 ├─ inventory-service/      servizio magazzino (Spring Boot)
 ├─ k8s/                    manifest Kubernetes di inventory-service
 ├─ postgres/init.sql       creazione dei due database
 ├─ docker-compose.yml
 └─ .github/workflows/      pipeline CI e pubblicazione delle immagini
```