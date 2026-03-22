package kr.growth.eum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "local-service-name", url = "http://localhost:8086")
public interface MyLocalServiceClient {

    @GetMapping("/api/local/data-02")
    ResponseEntity<String> getTestData();
}
