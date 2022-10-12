package com.talentport.users.dao;

import org.springframework.stereotype.Service;

import com.talentport.users.dto.ProductImg;
import com.talentport.users.dto.Token;
import com.talentport.users.dto.User;
import com.talentport.users.utils.CognitoService;
import com.talentport.users.utils.ConfigureS3Implement;

@Service
public class ICognitoImplement implements ICognitoDao{

	@Override
	public Token getToken(User user) throws Exception {
		CognitoService opj =new CognitoService();
		return opj.createNewUser(user);
	}

	@Override
	public ProductImg getDataImg(String cer,String id) throws Exception {
		ConfigureS3Implement opj =new ConfigureS3Implement();
		return opj.imgToBase64(cer,id);
	}

	@Override
	public ProductImg detailDataImg(String id) throws Exception {
		ConfigureS3Implement opj =new ConfigureS3Implement();
		return opj.detailImg(id);
	}


}
