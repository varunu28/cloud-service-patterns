package traefik_plugin

import (
	"bytes"
	"context"
	"net/http"
	"strings"
)

type Config struct {
	UserServiceUrl string `json:"userServiceUrl"`
}

func CreateConfig() *Config {
	return &Config{}
}

type JwtAuth struct {
	next           http.Handler
	userServiceUrl string
}

func New(ctx context.Context, next http.Handler, config *Config, name string) (http.Handler, error) {
	return &JwtAuth{
		next:           next,
		userServiceUrl: config.UserServiceUrl,
	}, nil
}

func (j *JwtAuth) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	if strings.HasPrefix(r.URL.Path, "/auth/login") || strings.HasPrefix(r.URL.Path, "/auth/register") {
		j.next.ServeHTTP(w, r)
		return
	}
	// parse authorization header
	authHeader := r.Header.Get("Authorization")
	if authHeader == "" {
		http.Error(w, "Missing JWT", http.StatusForbidden)
		return
	}
	splitToken := strings.Split(authHeader, "Bearer ")
	if len(splitToken) != 2 {
		http.Error(w, "Missing JWT", http.StatusForbidden)
		return
	}
	// Call user-service for token validation
	token := strings.TrimSpace(splitToken[1])
	req, _ := http.NewRequest("POST", j.userServiceUrl+"/auth/validate", bytes.NewBufferString(token))
	resp, err := http.DefaultClient.Do(req)
	if err != nil || resp.StatusCode != http.StatusOK {
		http.Error(w, "Invalid JWT", http.StatusForbidden)
		return
	}
	// Add a custom header and forward request
	r.Header.Add("X-From-Gateway", "true")
	j.next.ServeHTTP(w, r)
}
