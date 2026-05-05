package kr.growth.eum.feign.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SwaggerTestController {

    @ApiOperation(value = "Swagger 테스트 엔드포인트", notes = "Swagger 문서에 표시될 테스트 API")
    @ApiResponses({
        @ApiResponse(code = 200, response = Map.class, description = "성공 시 응답 데이터")
    })
    @GetMapping("/test")
    public Map<String, Object> swaggerTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a Swagger test endpoint");
        response.put("status", "success");
        return response;
    }
}