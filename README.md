<!-- Improved compatibility of back to top link -->
<a id="readme-top"></a>
<!--
*** Thanks for checking out Turron, Turron, TURRON! If you have any suggestions
*** that would make this service much(or for a bit) better, please fork the repo and create a pull request
*** or simply open an issue with the tag *enhancement*.
*** Don't forget to give the project a star!
*** Thanks again! Now go away and create something good! =]
-->

<!-- PROJECT SHIELDS -->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![Apache 2.0 License][license-shield]][license-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">

<h3 align="center">Turron</h3>

  <p align="center">
    A video-recognizer system that analyzes short video excerpts and finds highly accurate matches.
    <br />
    <a href="https://github.com/fl1s/turron"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/fl1s/turron">View Demo</a>
    ·
    <a href="https://github.com/fl1s/turron/issues/new?labels=bug">Report Bug</a>
    ·
    <a href="https://github.com/fl1s/turron/issues/new?labels=enhancement">Request Feature</a>
  </p>
</div>

<!-- SKILL ICONS -->
<p align="center">
  <img src="https://skillicons.dev/icons?i=java,spring,postgres,kafka,redis,docker,kubernetes,prometheus,grafana,gradle,postman,git" />
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

A video recognition system that works like Shazam — but for video. It analyzes short snippets (3–5 seconds), breaks them
into keyframes, and uses perceptual hashing to identify the exact or near-exact source, even if the clip has been edited
or altered. This preserves the full context of the snippet and enables reliable tracking of original video content
across platforms.

Key features:

* Upload full video snippets, not just images — automatic extraction of keyframes for context-aware matching.
* Accurate source identification via perceptual hashing tolerant to modifications.
* Optimized for quick, precise matching of short video fragments.
* Scalable microservices architecture built to handle heavy traffic without performance loss.
* Instant search results thanks to Redis caching.

[//]: # (* Open and extensible API for easy integration and community-driven improvements &#40;coming soon&#41;.)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Microservices

Turron is structured into 6 microservices, each with bounded responsibilities:

- **Eureka Server**: Manages service discovery using Netflix Eureka with `@DiscoveryClient`.

- **Upload Service**: Accepts short videos via REST API, stores them in MinIO, and sends processing tasks to Kafka.
- **Frame Extraction Service**: Extracts 5-10 keyframes from videos using FFmpeg, normalizes orientation for robustness,
  and forwards frames to Kafka for hashing.
- **Hashing Service**: Computes pHashes for keyframes and stores it in PostgreSQL.
- **Search Service**: Performs similarity search using Locality-Sensitive Hashing on PostgreSQL, caching results in
  Redis.
- **API Gateway**: Centralized REST API endpoint managing requests, authentication, and response aggregation.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Infrastructure

#### Nah...Later.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

* [![Java][Java]][Java-url]
* [![Spring Boot][Spring]][Spring-url]
* [![PostgreSQL][PostgreSQL]][PostgreSQL-url]
* [![Kafka][Kafka]][Kafka-url]
* [![Redis][Redis]][Redis-url]
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
* PostgreSQL, Kafka, Redis, MinIO (or use Docker Compose)
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
   ./gradlew build
   ```
4. Start the dev environment with Docker Compose:
   ```sh
   docker-compose up -d
   ```
5. Verify services are running:
   ```sh
   docker ps
   ```
6. (Optional) For prod, apply Kubernetes manifests:
   ```sh
   kubectl apply -f k8s/
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- API ENDPOINTS -->

## API Endpoints

#### ~Later-r.

## CI/CD

#### ...I'll do it later.

<!-- MONITORING -->

## Monitoring

#### ...Umm, skip-skip-skip. Definitely not today!

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

[product-screenshot]: images/screenshot.png

[Java]: https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white

[Java-url]: https://www.java.com/

[Spring]: https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white

[Spring-url]: https://spring.io/projects/spring-boot

[PostgreSQL]: https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white

[PostgreSQL-url]: https://www.postgresql.org/

[Kafka]: https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white

[Kafka-url]: https://kafka.apache.org/

[Redis]: https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white

[Redis-url]: https://redis.io/

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