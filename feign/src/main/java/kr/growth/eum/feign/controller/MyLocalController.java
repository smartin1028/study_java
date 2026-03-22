package kr.growth.eum.feign.controller;

import kr.growth.eum.feign.client.MyLocalServiceClient;
import kr.growth.eum.feign.client.MyServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/local")
@RequiredArgsConstructor
public class MyLocalController {
    private final MyLocalServiceClient myLocalServiceClient;

    @GetMapping("/data-01")
    public ResponseEntity<String> getData() {
        return myLocalServiceClient.getTestData();
    }

    @GetMapping("/data-02")
    public ResponseEntity<String> getTestData() {
        return new ResponseEntity<>("Test data from local service", HttpStatus.OK);
    }
}
