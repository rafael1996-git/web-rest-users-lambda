package com.talentport.users.dao;

import java.util.List;

import com.talentport.users.dto.User;

public interface IUserDao {

	public boolean Create(User user) throws Exception;

	public boolean Edit(User user, String id) throws Exception;

	public boolean EditPassword(String encodedPassword, String id) throws Exception;

	public boolean EditImage(User user, String id) throws Exception;

	public boolean Delete(String id, int status) throws Exception;

	public User FindById(String uuid) throws Exception;

	public User FindByEmail(String email) throws Exception;

	public List<User> FindByAmin() throws Exception;

	public boolean CreateAdmin(User user) throws Exception;

	public String GetCode(User user) throws Exception;

	public int SharedCode(User user, User users) throws Exception;

	boolean Enabled(String id, int status) throws Exception;

	public List<User> FindByUser() throws Exception;

	User findByFacebookId(String facebookId) throws Exception;

	boolean createFB(User user) throws Exception;

	User findByAppleId(String appleId) throws Exception;

	boolean createApple(User user) throws Exception;

}
