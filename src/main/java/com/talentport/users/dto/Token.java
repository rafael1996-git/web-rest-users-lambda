package com.talentport.users.dto;

import lombok.Data;

@Data
public class Token  {
	 private String AccessToken;
	    private int ExpiresIn;
	    private String TokenType;
	    private String idCognito;
	    private String username;
	    private String img;
  
}
