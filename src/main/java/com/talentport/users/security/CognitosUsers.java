package com.talentport.users.security;

import java.io.Serializable;

import lombok.Data;

@Data
public class CognitosUsers implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String clientId;
	private String userPoolId;
	
	private String acceskey;
	private String secretKey;
	

	
	@Override
	public String toString() {
		return "CognitoConfig [clientId=" + clientId + ", userPoolId=" + userPoolId + ", acceskey=" + acceskey
				+ ", secretKey=" + secretKey + "]";
	}



	public CognitosUsers(String clientId, String userPoolId, String acceskey, String secretKey) {
		
		this.clientId = clientId;
		this.userPoolId = userPoolId;
		this.acceskey = acceskey;
		this.secretKey = secretKey;
	}
	
	
	
}
