package kr.growth.eum.feign.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataResponse {
    private String content;
}
