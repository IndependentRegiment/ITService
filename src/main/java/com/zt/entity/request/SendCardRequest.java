package com.zt.entity.request;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel
@ToString
public class SendCardRequest {

    private List<String> cardList;
}
