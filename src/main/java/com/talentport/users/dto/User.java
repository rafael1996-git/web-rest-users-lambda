package com.talentport.users.dto;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User  {
	@JsonProperty
	private String id;
	@JsonProperty
	private String name;
	@JsonProperty
	private String lastName;
	@JsonProperty
	private String phone;
	@JsonProperty
	private String email;
	@JsonProperty
	private String password;
	@JsonProperty
	private String confirmPassword;
	@JsonProperty
	private String newPassword;
	@JsonProperty
	private String birthdate;
	@JsonProperty
	private String device;
	@JsonProperty
	private String images;
	@JsonProperty
	private int type;
	@JsonProperty
	private String idUsername;
	@JsonProperty
	private String cogname;
	@JsonProperty
	private int status;
	@JsonProperty
	private String nickname;
	@JsonProperty
	private List<String> roles;
	@JsonProperty
	private String codes;
	@JsonProperty
	private Date created_at;
	@JsonProperty
	@DateTimeFormat(iso = ISO.DATE)
	private Date updated_at;
	@JsonProperty
	private Integer step;
	@JsonProperty
	private String facebookId;
	@JsonProperty
	private String appleId;

	
}
