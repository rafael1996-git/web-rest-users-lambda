package com.talentport.users.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trader.core.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message extends BaseModel {
    @JsonProperty
    private String message;
}
