package kr.growth.eum.feign.client;

import kr.growth.eum.feign.client.dto.DataResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

//@Component
public class MyServiceFallback {
//    @Override
//    public ResponseEntity<DataResponse> getData() {
//        DataResponse serviceUnavailable = DataResponse.builder().content("Service unavailable").build();
//        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(serviceUnavailable);
//    }
}
