package com.talentport.users.service;

import java.util.List;

import com.talentport.users.dto.User;
import com.trader.core.models.ResponseModel;

public interface IUsersService {
	
	ResponseModel Create(User user);
	ResponseModel Edit(User user, String id);
	ResponseModel EditPassword(String encodedPassword, String id);
	ResponseModel EditImage(User user, String id);
	ResponseModel Delete(String id, int status);
	ResponseModel FindById(String uuid);
	ResponseModel FindByEmail(String email);
	ResponseModel FindByAmin();
	ResponseModel CreateAdmin(User user);
	ResponseModel GetCode(User user);
	ResponseModel SharedCode(User user, User users);
	ResponseModel Enabled(String id, int status) ;
	ResponseModel FindByUser() ;
	ResponseModel findByFacebookId(String facebookId);
	ResponseModel createFB(User user);
	ResponseModel findByAppleId(String appleId);
	ResponseModel createApple(User user);

}
