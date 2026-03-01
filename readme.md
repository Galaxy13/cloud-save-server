# Cloud Save Game Server (Spring Boot + PostgreSQL + S3/MinIO)

A backend service for storing and syncing game save files in the cloud.  
Built with **Java Spring Boot**, **PostgreSQL** for metadata, and **S3-compatible object storage** (default: **MinIO**) for save file blobs.

## Features

- User accounts + authentication (JWT-ready)
- Upload / download save files (binary / multipart)
- Versioning support (optional)
- Per-game organization
- Admin endpoints
- S3-compatible storage (AWS S3, MinIO, etc.)
- PostgreSQL-backed metadata
- Healthcheck

## Tech Stack

- Java 17+ (recommended Java 21+)
- Spring Boot (Web, Validation, Security)
- PostgreSQL
- S3-compatible storage (MinIO)
- Flyway migrations

## Architecture (high level)

- **PostgreSQL** stores metadata:
    - users, games, save slots
    - save file records (bucket/key, size, checksum, createdAt, etc.)
- **S3/MinIO** stores the actual save data (binary objects)
- API handles:
    - auth + authorization
    - presigned URLs *or* direct streaming upload/download
    - metadata validation and ownership checks

---

## Getting Started 

### Prerequisites

- Java 17+
- Docker

### 1) Clone

```bash
git clone https://github.com/Galaxy13/cloud-save-server.git
cd cloud-save-server
```
