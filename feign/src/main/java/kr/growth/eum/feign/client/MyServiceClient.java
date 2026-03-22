package kr.growth.eum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "service-name", url = "http://192.168.0.192:11434")
public interface MyServiceClient {
//    @GetMapping("/")
//    ResponseEntity<DataResponse> getData();
//

    @GetMapping("/")
    ResponseEntity<String> getOllamaTestData();
}
