package com.talentport.users.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.talentport.users.dto.User;
import com.trader.core.models.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListUsers extends BaseModel{
	
	@JsonProperty
	    private List<User> user;
}
