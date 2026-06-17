# Bulkhead Pattern

## Demo for failing scenario
 - Start all 3 services
 - Run the below Go program to demo failure
```
package main

import (
	"fmt"
	"net/http"
	"sync"
	"time"
)

const (
	RequestCount = 1000
	ServiceAUrl  = "http://localhost:8000/api/v1/servicea" // The 10s delay
	ServiceBUrl  = "http://localhost:8000/api/v1/serviceb" // The healthy service
)

func main() {
	client := &http.Client{
		Timeout: 2 * time.Second,
	}

	fmt.Println("Starting Service B Heartbeat...")
	go func() {
		for {
			start := time.Now()
			_, err := client.Get(ServiceBUrl)
			if err != nil {
				fmt.Printf("❌ Service B FAILED: %v\n", err)
			} else {
				fmt.Printf("✅ Service B OK (%v)\n", time.Since(start))
			}
			time.Sleep(500 * time.Millisecond)
		}
	}()

	time.Sleep(3 * time.Second)

	fmt.Println("\n⚠️ INITIATING LOAD ON SERVICE A (The 10s delay). Watch Service B...")
	var wg sync.WaitGroup
	for range RequestCount {
		wg.Add(1)
		wg.Go(func() {
			_, _ = client.Get(ServiceAUrl)
		})
	}

	wg.Wait()
	fmt.Println("Load test complete.")
}
```
 - Monitor thread count on `http://localhost:8081/actuator/metrics/tomcat.threads.busy`