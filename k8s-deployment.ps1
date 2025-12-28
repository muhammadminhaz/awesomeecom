# =========================================
# 0. Set namespace
# =========================================
$namespace = "awesomeecom"

# Create namespace if it doesn't exist
$nsExists = kubectl get namespace $namespace -o jsonpath='{.metadata.name}' 2>$null
if ($LASTEXITCODE -ne 0) {
    kubectl create namespace $namespace
    Write-Host "Namespace '$namespace' created."
} else {
    Write-Host "Namespace '$namespace' already exists."
}

# =========================================
# 1. Start Minikube
# =========================================
Write-Host "Starting Minikube..."
minikube start --memory 6096 --cpus 2 --driver=docker

# =========================================
# 2. Configure Docker to use Minikube's daemon
# =========================================
Write-Host "Configuring Docker to use Minikube..."
& minikube -p minikube docker-env --shell powershell | Invoke-Expression

# =========================================
# 3. Build Docker images for all services
# =========================================
$services = @(
    "api-gateway",
    "eureka-server",
    "customer-service",
    "product-service",
    "cart-service",
    "order-service",
    "payment-service",
    "notification-service"
)

foreach ($svc in $services) {
    Write-Host "Building Docker image for $svc..."
    docker build -t $svc:1.0 ./../$svc
}

# =========================================
# 4. Apply Kubernetes manifests in dependency order
# =========================================

Write-Host "Applying PVCs"
kubectl apply -f ./k8s/pvc-deployment.yaml -n $namespace

Write-Host "Applying config (ConfigMaps / Secrets)..."
kubectl apply -f ./k8s/env --recursive -n $namespace

Write-Host "Applying Databases..."
kubectl apply -f ./k8s/db --recursive -n $namespace

Write-Host "Applying Redis..."
kubectl apply -f ./k8s/redis --recursive -n $namespace

Write-Host "Applying Kafka..."
kubectl apply -f ./k8s/kafka --recursive -n $namespace

# Uncomment if you want logging/monitoring
#Write-Host "Applying Logging..."
#kubectl apply -f ./k8s/logging --recursive -n $namespace
#Write-Host "Applying Monitoring..."
#kubectl apply -f ./k8s/monitoring --recursive -n $namespace

Write-Host "Applying Microservices..."
kubectl apply -f ./k8s/services --recursive -n $namespace

# =========================================
# 5. Wait for pods to be ready
# =========================================
Write-Host "Waiting for DB pod to be ready..."
kubectl wait --for=condition=Ready pod -l app=postgres -n $namespace --timeout=180s

Write-Host "Waiting for services to be ready..."
$serviceLabels = @("api-gateway","eureka-server","customer-service","product-service","cart-service","order-service","payment-service","notification-service")
foreach ($svc in $serviceLabels) {
    kubectl wait --for=condition=Ready pod -l app=$svc -n $namespace --timeout=180s
}

# =========================================
# 6. Port-forward key services for local access
# =========================================
Write-Host "Port-forwarding services..."

Start-Process powershell -ArgumentList "kubectl port-forward svc/api-gateway 4006:4006 -n $namespace"
Start-Process powershell -ArgumentList "kubectl port-forward svc/eureka-server 8761:8761 -n $namespace"
Start-Process powershell -ArgumentList "kubectl port-forward svc/grafana 3000:3000 -n $namespace"
Start-Process powershell -ArgumentList "kubectl port-forward svc/kibana 5601:5601 -n $namespace"

Write-Host "Deployment complete. Services are accessible via localhost ports."
