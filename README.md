# Estatia

Estatia is a website for posting and browsing real estate listings.
Anyone can register and post a listing of any type (house, apartment, garage, office)
for sale or rent. Users can also upload images for the listing and fully manage the listing
including editing, deleting. Buyers can browse by applying various filters such as 
price, city, neighbourhood and many more.
Built using: Java, Spring MVC, Thymeleaf, PostgreSQL, Docker, Auth0, MiniO, nginx

## Table of Contents
 
- [Architecture](#architecture)
- [Components](#components)
- [Configuration](#configuration)
- [Running the project](#running-the-project)
- [Project structure](#project-structure)


## Architecture
 
```
            HTTPS (443)
 Browser ───────────────► Nginx (load balancer + TLS)
                             │
                 ┌───────────┴───────────┐
                 ▼                       ▼
             app1 (Spring)           app2 (Spring)
                 └─────┬─────────┬───────┘
                       ▼         ▼
                  PostgreSQL   MinIO
                               (photos)
```

The class diagram is in [`docs/class-diagram.svg`](docs/class-diagram.svg).

## Components
 
| Component | Technology | Purpose |
|---|---|---|
| `nginx` | Nginx | Terminates HTTPS and distributes requests between the app instances |
| `app1`, `app2` | Spring MVC, Java | The web application: controllers, services, repositories, Thymeleaf views |
| `db` | PostgreSQL | Stores users, listings, cities, neighbourhoods |
| `minio` | MinIO (local AWS S3 alternative) | Stores uploaded listing photos; the database only keeps the photo URL/key |
| `dozzle` | Dozzle | Container monitoring, resources, app logs |

### Application layers
 
- **Controllers**: handle HTTP requests and return views (`ListingController`, `CityController`, `ImageStoreController`)
- **Services**: business logic (publishing a listing, editing, deleting, adding an image, editing user)
- **Repositories**: data access with Spring Data JPA
- **Entities**: `Profile`, `Listing`, (`Apartment`, `House`, `Garage`, `Office`), `Photo`, `City`, `Neighbourhood`

### Configuration

All secrets are stored in a .env file which is not commited to a version control software. You can create your .env file
from the example at .env.example (use your secrets and URLs)

- `docker-compose.yml`: defines the services, network and volumes
- `nginx/nginx.conf`: load balancer and HTTPS settings
- `src/main/resources/application.properties`: Spring settings (reads values from environment variables)

## Running the project
 
### Requirements
 
- Git
- Docker and Docker Compose
- OpenSSL (to generate the certificate)
### Steps
 
1. Clone the repository:
```bash
git clone https://github.com/iamspase/estatia
cd estatia
```
 
2. Create the environment file and fill in the values:
```bash
cp .env.example .env
```
 
3. Generate a self-signed certificate:
```bash
mkdir -p nginx/certs
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout nginx/certs/server.key \
  -out nginx/certs/server.crt \
  -subj "/CN=localhost"
```
 
4. Build and start everything:
```bash
docker compose up --build
```
 
5. Open **https://localhost** in your browser. Because the certificate is self-signed, accept the browser warning.
### Stopping
 
```bash
docker compose down        # stop containers
docker compose down -v     # stop and delete the database volume
```
 
## Project structure
 
```
.
├── docker-compose.yml
├── .env.example
├── nginx/
│   ├── nginx.conf
│   └── certs/              # generated locally, not committed
├── docs/
│   └── class-diagram.svg
├── src/
│   └── main/
│       ├── java/...        # controllers, services, repositories, entities
│       └── resources/      # templates, static files, application.properties
├── Dockerfile
└── README.md
```