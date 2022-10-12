package com.talentport.users.dao;


import com.talentport.users.dto.ProductImg;
import com.talentport.users.dto.Token;
import com.talentport.users.dto.User;

public interface ICognitoDao {
	/* Get Token for user in cognito */
	public Token getToken(User user) throws Exception;
	
	public ProductImg getDataImg(String cer,String id )throws Exception;
	public ProductImg detailDataImg(String id )throws Exception;
	
}
