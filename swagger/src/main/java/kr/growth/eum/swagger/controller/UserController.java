package kr.growth.eum.swagger.controller;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Api(tags = "User Management")
@RestController
@RequestMapping("/api/users")
public class UserController {

//    @ApiOperation("사용자 목록 조회")
    @GetMapping
    public List<User> getAllUsers() {
        // 로직
        return null;
    }

//    @ApiOperation("사용자 생성")
    @PostMapping
    public User createUser(@RequestBody User user) {
        // 로직

        return null;
    }
}
