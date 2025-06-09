# INSTRUCTIONS.md

This doc provides instructions to deploy and manage the Kubernetes resources in this repo.

## Directory Structure
```
k8s/
├── namespace.yaml           # Defines the turron-app namespace
├── storage/                 # PersistentVolumeClaims (PVCs) for data persistence
│   ├── postgres-pvc.yaml
│   ├── kafka-pvc.yaml
│   ├── redis-pvc.yaml
│   ├── minio-pvc.yaml
│   └── grafana-pvc.yaml
├── config/                  # ConfigMaps and Secrets for configuration
│   ├── configmap.yaml
│   └── secret.yaml
├── services/                # Deployments and Services for microservices
│   ├── eureka-server.yaml
│   ├── upload-service.yaml
│   ├── extraction-service.yaml
│   ├── hashing-service.yaml
│   ├── search-service.yaml
│   └── api-gateway.yaml
├── infra/                   # Infrastructure components
│   ├── kafka.yaml
│   ├── redis.yaml
│   ├── minio.yaml
│   ├── prometheus.yaml
│   └── grafana.yaml
└── README.md
```

## Deployment Instructions

### 1. Namespace
The `namespace.yaml` file creates the `turron-app` namespace, isolating all resources.

Apply it first:
```
kubectl apply -f k8s/namespace.yaml
```

### 2. Storage (PVCs)
The `storage/` directory contains PersistentVolumeClaims for services requiring persistent storage (Postgres, Kafka, Redis, Minio, Grafana). Apply these before services that depend on them.

Apply all PVCs:
```
kubectl apply -f k8s/storage/
```

### 3. Configurations
The `config/` directory includes:
- `configmap.yaml`: Environment variables and Prometheus configuration.
- `secret.yaml`: Sensitive data (e.g., passwords, keys).

Apply configurations:
```
kubectl apply -f k8s/config/
```

### 4. Infrastructure Components
The `infra/` directory contains deployments and services for Kafka, Redis, Minio, Prometheus, and Grafana. Deploy these to set up the core infrastructure.

Apply infrastructure components:
```
kubectl apply -f k8s/infra/
```

### 5. Microservices
The `services/` directory includes deployments and services for microservices (Eureka, Upload, Extraction, Hashing, Search, API Gateway). Deploy these last, as they depend on infrastructure and configurations.

Apply microservices:
```
kubectl apply -f k8s/services/
```

## Key Notes
- All resources are in the `turron-app` namespace. Set the `kubectl` context or use `-n turron-app`.
- Handle `k8s/config/secret.yaml` with care due to sensitive data.
- ConfigMaps enable updates to environment variables and Prometheus settings without redeploying images.
- Ensure the cluster’s storage class is configured for PVCs to bind correctly.
- Services and Deployments are independent, allowing separate updates.
- Restart deployments after config changes with:
  ```
  kubectl rollout restart deployment/<deployment-name> -n turron-app
  ```
- The `-deployment` suffix in metadata names is optional.
- Prometheus uses cluster DNS names for reliable internal scraping.

## Deployment Order
```
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/storage/
kubectl apply -f k8s/config/
kubectl apply -f k8s/infra/
kubectl apply -f k8s/services/
```

## Best Practices
- Store manifests and ConfigMaps in version control.
- Use namespaces and RBAC for enhanced security.
- Implement CI/CD for automated deployments.
- Monitor logs and metrics post-deployment to identify issues early.