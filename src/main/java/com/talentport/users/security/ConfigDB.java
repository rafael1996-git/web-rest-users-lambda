package com.talentport.users.security;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConfigDB  implements Serializable{
	
		private static final long serialVersionUID = 1L;
		private String user ;
		private String pass;
		private String engine;
		private String url;
		private String port;

	
	
}
