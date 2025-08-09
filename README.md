<a id="readme-top"></a>

<!-- PROJECT SHIELDS -->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![Apache 2.0 License][license-shield]][license-url]

<!-- PROJECT LOGO -->

<div align="center">  
<p align="center">
  <img width="480" height="480" alt="turron-logo-readme" src="https://github.com/user-attachments/assets/d0ba8800-3067-4f7c-a554-19aac4833c7b" />
</p>

https://github.com/user-attachments/assets/8bf90c7a-ce68-4d38-a140-e363fc421395

https://github.com/user-attachments/assets/5651a9fb-029d-4126-b192-ba42ca269a5d

https://github.com/user-attachments/assets/db4aef2f-349c-499e-bcb6-a3aac1d57e1a

·
    <a href="https://github.com/fl1s/turron"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/fl1s/turron/issues/new?labels=bug">Report Bug</a>
    ·
    <a href="https://github.com/fl1s/turron/issues/new?labels=enhancement">Request Feature</a>
  </p>
</div>

<!-- SKILL ICONS -->
<p align="center">
  <img src="https://skillicons.dev/icons?i=java,spring,postgres,kafka,docker,kubernetes,prometheus,grafana,gradle,postman,git" />
</p>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#microservices">Microservices</a></li>
        <li><a href="#infrastructure">Infrastructure</a></li>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#ci-cd">CI/CD</a></li>
    <li><a href="#monitoring">Monitoring</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

## About The Project

A video recognition system that works like Shazam — but for video. It analyzes short snippets (2–5 seconds), breaks them
into keyframes, and uses perceptual hashing to identify the exact or near-exact source, even if the clip has been edited
or altered. This preserves the full context of the snippet and enables reliable tracking of original video content.

Key features:

* Upload full video snippets, not just images — automatic extraction of keyframes for context-aware matching.
* Accurate source identification via perceptual hashing tolerant to modifications.
* Optimized for quick, precise matching of short video fragments.
* Scalable microservices architecture built to handle heavy traffic without performance loss.

[//]: # (* Instant search results thanks to Redis caching.)

[//]: # (* Open and extensible API for easy integration and community-driven improvements &#40;coming soon&#41;.)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Microservices

Turron is structured into 6 microservices, each with bounded responsibilities:

- **Eureka Server**: Manages service discovery using Netflix Eureka with `@DiscoveryClient`.

- **Upload Service**: Accepts short videos via REST API, stores them in MinIO, and sends processing tasks to Kafka.
<img width="900" alt="upload-service" src="https://github.com/user-attachments/assets/21d8f136-0eab-40de-9be3-21d73094c03a" />


- **Frame Extraction Service**: Extracts 5-10 keyframes from videos using FFmpeg, normalizes orientation for robustness, and forwards frames to Kafka for hashing.

<img width="900" alt="extraction-first" src="https://github.com/user-attachments/assets/6d6e7850-062d-45bb-81b2-ddde60c6a0fb" />

...

<img width="900" alt="extraction-third" src="https://github.com/user-attachments/assets/3effd378-634a-41c2-b0c2-2cd770780647" />

- **Hashing Service**: Computes pHashes for keyframes and stores it in database.
<img width="1130" alt="hashing-first" src="https://github.com/user-attachments/assets/db3dfc52-741a-41ea-bdba-6ca57b2c4ef1" />
...
<img width="1269" alt="hashing-third" src="https://github.com/user-attachments/assets/5bbfea23-7659-4613-b427-4298cd9dfe6b" />

- **Search Service**: Matches snippet videos to source videos using perceptual hash comparisons with sliding-window Hamming distance, storing results in database.
<img width="999" alt="cleanup" src="https://github.com/user-attachments/assets/635f377e-6233-4a19-9e22-5d8541cbd4f0" />


<img width="1232" alt="another snippet bc previous is cleaned up" src="https://github.com/user-attachments/assets/8d11e6ca-143b-463b-be5e-10450c162156" />


- **API Gateway**: Centralized REST API endpoint managing requests, authentication, and response aggregation.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

* [![Java][Java]][Java-url]
* [![Spring Boot][Spring]][Spring-url]
* [![PostgreSQL][PostgreSQL]][PostgreSQL-url]
* [![Kafka][Kafka]][Kafka-url]
* [![Docker][Docker]][Docker-url]
* [![Kubernetes][Kubernetes]][Kubernetes-url]
* [![Prometheus][Prometheus]][Prometheus-url]
* [![Grafana][Grafana]][Grafana-url]
* [![Gradle][Gradle]][Gradle-url]

<p align="right"> Built With(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->

## Getting Started

Set up Turron locally using Docker Compose for the dev environment or deploy to Kubernetes for production.

### Prerequisites

Ensure you have the following installed:

* Java 21
* Gradle
* Docker and Docker Compose
* kubectl (for prod environment)
* PostgreSQL, Kafka, MinIO (or use Docker Compose)
  ```sh
  java --version
  gradle --version
  docker --version
  ```

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/fl1s/turron.git
   ```
2. Navigate to the project directory:
   ```sh
   cd turron
   ```
3. Build all microservices with Gradle:
   ```sh
   cd /microservice-name
   ./gradlew clean build
   ```
4. Start the dev environment with Docker Compose:
   ```sh
   docker-compose up -d
   ```
5. Verify services are running:
   ```sh
   docker ps
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- API ENDPOINTS -->

## API Endpoints

## Upload snippet-video

**POST** `{{api-gateway}}/api/v1/upload/snippet`

**Form Data:**

| Field | Description    | Type       |
|-------|----------------|------------|
| file  | MP4 video file | file (mp4) |

**Response:**

```json
{
  "snippetId": "...",
  "sourceUrl": "..."
}
```

---

## Upload source-video

**POST** `{{api-gateway}}/api/v1/upload/source`

**Form Data:**

| Field | Description    | Type       |
|-------|----------------|------------|
| file  | MP4 video file | file (mp4) |

**Response:**

```json
{
  "sourceId": "...",
  "sourceUrl": "..."
}
```

---

## Find best-match

**GET** `{{api-gateway}}/api/v1/search/best-match/:snippetId`

**Path Parameter:**

| Parameter | Description      | Type   |
|-----------|------------------|--------|
| snippetId | Snippet video ID | string |

**Response:**

```json
{
  "downloadUrl": "..."
}
```

## CI/CD

This project uses GitHub Actions.

The `.github/workflows/build.yml` workflow runs on every push and pull request to the `main` branch. It performs the following steps:

1. Checks out the repository
2. Sets up Java 21 using the Temurin distribution
3. Caches Gradle dependencies to speed up builds
4. Builds each service defined in the job matrix:
    - `eureka-server`
    - `upload-service`
    - `extraction-service`
    - `hashing-service`
    - `search-service`
    - `api-gateway`
5. Authenticates to GitHub Container Registry (GHCR)
6. Builds and pushes Docker images using a composite action located at `.github/actions/docker-build-push`

Each Docker image is tagged with:
- `latest`
- a short Git commit SHA
- a date-based tag in `YYYYMMDD` format

<!-- MONITORING -->

## Monitoring

Our project includes out-of-the-box monitoring setup using **Prometheus** and **Grafana**.

### How it works

- Each microservice exposes metrics via Spring Boot Actuator on the `/actuator/prometheus` endpoint.
- Prometheus scrapes these endpoints regularly to collect metrics.
- Grafana connects to Prometheus as a data source to visualize metrics on dashboards.

### Prometheus configuration

Prometheus config is located at:  
`monitoring/prometheus/prometheus.yml`

### Folder structure

* `monitoring/prometheus/` — Prometheus config files
* `monitoring/grafana/` — Grafana dashboards and config files

### Getting started

1. Start **Prometheus** using the config from `monitoring/prometheus/prometheus.yml`(it's already configured in docker-compose).
2. Start **Grafana** and add Prometheus as a data source (`http://localhost:9090`).
3. Create your dashboards in Grafana or import community dashboards for Spring Boot metrics.
4. Access your dashboards to monitor service health, performance, and custom metrics.
<!-- CONTRIBUTING -->

## Contributing

Contributions are welcome to enhance Turron! Follow these steps:

1. Fork the Project.
2. Create your Feature Branch:
   ```sh
   git checkout -b feature/fuzz-buzz-creature
   ```
3. Commit your Changes:
   ```sh
   git commit -m 'feat: add some fuzzBuzzCreatureFeature'
   ```
4. Push to the Branch:
   ```sh
   git push --set-upstream origin feature/fuzz-buzz-creature
   ```
5. Open a Pull Request.

Read our [Contributing Guidelines](CONTRIBUTING.md) for more details(I also do it later).

### Top Contributors

<a href="https://github.com/fl1s/turron/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=fl1s/turron" alt="contrib.rocks image" />
</a>

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->

## License

Distributed under the Apache 2.0 License. See [LICENSE](LICENSE) for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->

## Contact

fl1s - [GitHub](https://github.com/fl1s)

Project Link: [https://github.com/fl1s/turron](https://github.com/fl1s/turron)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->

[contributors-shield]: https://img.shields.io/github/contributors/fl1s/turron.svg?style=for-the-badge

[contributors-url]: https://github.com/fl1s/turron/graphs/contributors

[forks-shield]: https://img.shields.io/github/forks/fl1s/turron.svg?style=for-the-badge

[forks-url]: https://github.com/fl1s/turron/network/members

[stars-shield]: https://img.shields.io/github/stars/fl1s/turron.svg?style=for-the-badge

[stars-url]: https://github.com/fl1s/turron/stargazers

[issues-shield]: https://img.shields.io/github/issues/fl1s/turron.svg?style=for-the-badge

[issues-url]: https://github.com/fl1s/turron/issues

[license-shield]: https://img.shields.io/github/license/fl1s/turron.svg?style=for-the-badge

[license-url]: https://github.com/fl1s/turron/blob/main/LICENSE

[Java]: https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white

[Java-url]: https://www.java.com/

[Spring]: https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white

[Spring-url]: https://spring.io/projects/spring-boot

[PostgreSQL]: https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white

[PostgreSQL-url]: https://www.postgresql.org/

[Kafka]: https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white

[Kafka-url]: https://kafka.apache.org/

[Docker]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white

[Docker-url]: https://www.docker.com/

[Kubernetes]: https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white

[Kubernetes-url]: https://kubernetes.io/

[Prometheus]: https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white

[Prometheus-url]: https://prometheus.io/

[Grafana]: https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white

[Grafana-url]: https://grafana.com/

[Gradle]: https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white

[Gradle-url]: https://gradle.org/
