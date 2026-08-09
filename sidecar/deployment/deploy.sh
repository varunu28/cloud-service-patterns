#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SIDECAR_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
BOOK_SERVICE_DIR="$SIDECAR_DIR/book-service"
GATEWAY_API_VERSION="v1.2.0"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
info()    { echo -e "${CYAN}[INFO]${NC} $*"; }
success() { echo -e "${GREEN}[OK]${NC}   $*"; }
warn()    { echo -e "${YELLOW}[WARN]${NC} $*"; }
error()   { echo -e "${RED}[ERR]${NC}  $*"; exit 1; }


kubectl apply -f \
  "https://github.com/kubernetes-sigs/gateway-api/releases/download/${GATEWAY_API_VERSION}/standard-install.yaml"
success "Gateway API CRDs installed."

istioctl install -f "$SCRIPT_DIR/istio-operator.yml" -y
success "Istio installed."

kubectl rollout status deployment/istiod -n istio-system --timeout=120s
success "istiod is ready."

info "Building book-service Docker image via Spring Boot Maven plugin..."
cd "$BOOK_SERVICE_DIR"
./mvnw -q spring-boot:build-image -DskipTests
success "Image built: varunu2892/book-service:latest"

info "Loading image into Minikube..."
minikube image load varunu2892/book-service:latest
success "Image loaded into Minikube."

cd "$SCRIPT_DIR"

info "Applying namespace (with istio-injection label)..."
kubectl apply -f namespace.yml

info "Applying book-service Deployment & Service..."
kubectl apply -f book-service.yml

info "Applying Kubernetes Gateway API resources (GatewayClass, Gateway, HTTPRoute)..."
kubectl apply -f k8s-gateway.yml

info "Applying DestinationRule (mTLS + connection pool)..."
kubectl apply -f destination-rule.yml

info "Applying PeerAuthentication (strict mTLS)..."
kubectl apply -f peer-authentication.yml

info "Waiting for book-service pod to be ready..."
kubectl rollout status deployment/book-service -n sidecar --timeout=180s
success "book-service is ready."

info "Waiting for Gateway API to provision the ingress gateway pod..."
kubectl rollout status deployment/book-service-gateway-istio -n sidecar --timeout=120s 2>/dev/null \
  || kubectl wait --for=condition=programmed gateway/book-service-gateway -n sidecar --timeout=120s 2>/dev/null \
  || warn "Gateway may still be provisioning – check: kubectl get gateway -n sidecar"
success "Gateway is ready."