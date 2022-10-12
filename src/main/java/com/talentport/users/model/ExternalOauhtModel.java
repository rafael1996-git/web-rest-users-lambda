package com.talentport.users.model;

import lombok.Data;

@Data
public class ExternalOauhtModel {

	private String device;

	private Integer plataform;

	private Boolean isPlataform;

	private String email;

	private String token;

	private String name;

	private String familyName;

}
