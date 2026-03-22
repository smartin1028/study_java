package kr.growth.eum.feign.controller;

import kr.growth.eum.feign.client.MyServiceClient;
import kr.growth.eum.feign.client.dto.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyController {
    private final MyServiceClient myServiceClient;

    @GetMapping("/data-01")
    public ResponseEntity<String> getData() {
        return myServiceClient.getOllamaTestData();
    }
}
