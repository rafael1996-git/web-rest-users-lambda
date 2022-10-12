package com.talentport.users.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.talentport.users.dao.ICognitoDao;
import com.talentport.users.dao.IUserDao;
import com.talentport.users.dto.ProductImg;
import com.talentport.users.dto.Token;
import com.talentport.users.dto.User;
import com.talentport.users.helpers.Helpers;
import com.talentport.users.model.Message;
import com.talentport.users.response.ListUsers;
import com.talentport.users.service.IUsersService;
import com.talentport.users.utils.CognitoClient;
import com.talentport.users.utils.Validator;
import com.trader.core.enums.ResponseEnum;
import com.trader.core.models.ResponseModel;
import com.trader.core.utils.ResponseUtils;

@Service
public class UsersServiceImpl  implements IUsersService{
	private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);

	@Autowired
	private IUserDao dao;
	
	@Autowired
	private ICognitoDao cognitoService;
	
	@Autowired
	private BCryptPasswordEncoder pswEncoder;
	
	@Override
	public ResponseModel Create(User user) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
    	Token token = null;
        try {
        	List<String> patternName = Validator.ValidatorCreate(user);
			if (patternName != null) {
				for (Integer i = 0; i < patternName.size(); i++) {
					message.setMessage("Field-".concat(i.toString())+" "+ patternName.get(i));
				}
				 response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.ERROR);
			}else if (!user.getPassword().equals(user.getConfirmPassword())) {
				message.setMessage("errors".concat("incorrect Confirma_password"));
				 response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.ERROR);
			} else {
				User userCognito = dao.FindByEmail(user.getEmail());
				if (userCognito.getIdUsername() != null) {
					if (userCognito.getStatus() == 2) {
						message.setMessage("Email: "+ user.getEmail().concat(" user with status 2 is blocked from the portal"));
						 response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.ERROR);
					} else if (userCognito.getStatus() == 0) {
						message.setMessage("Email: "+ user.getEmail().concat(" user with status 0 is delete for the portal"));
						 response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.ERROR);
					} else {
						message.setMessage("El correo "+user.getEmail()+" ya ha sido registrado anteriormente.");
						 response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.ERROR);
					}
				} else {
					List<String> role = Helpers.getAuthorities(user.getDevice());
					if (role.toString().substring(1, role.toString().length() - 1).toString() != null
							&& !role.toString().substring(1, role.toString().length() - 1).toString().isEmpty()) {
						user.setRoles(role);
						user.setCogname(Helpers.NameUUID());
						token = cognitoService.getToken(user);

						if (token.getIdCognito() != null) {
							String encodedPassword = pswEncoder.encode(user.getPassword());
							user.setPassword(encodedPassword);
							user.setConfirmPassword(encodedPassword);
							user.setIdUsername(token.getIdCognito());
							user.setStatus(1);
							dao.Create(user);
							List<User> list = new ArrayList();
							clienteActual.setIdUsername(user.getIdUsername());
							clienteActual.setName(user.getName());
							clienteActual.setLastName(user.getLastName());
							clienteActual.setPhone(user.getPhone());
							clienteActual.setEmail(user.getEmail());
							clienteActual.setBirthdate(user.getBirthdate());
							clienteActual.setStatus(user.getStatus());
							clienteActual.setNickname(user.getNickname());
							list.add(clienteActual);
							ListUsers information=new ListUsers();
							information.setUser(list);
							message.setMessage("Data successfully User Creation");
			                response = ResponseUtils.createResponse(Collections.singletonList(information.toJsonObject()),ResponseEnum.EXITO);
						} else {
							message.setMessage("El correo "+user.getEmail()+" ya ha sido registrado anteriormente.");
							 response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.ERROR);
							
						}

					} else {
						message.setMessage("the field divace :" + user.getDevice() + " not correct");
						 response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.ERROR);
						
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			 e.printStackTrace();
	            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
	            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseModel Edit(User user, String id) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel EditPassword(String encodedPassword, String id) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel EditImage(User user, String id) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel Delete(String id, int status) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel FindById(String id) {
		
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		try {
			if (id == null){
				message.setMessage("El valor ID del request no puesde ser null");
                response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.ERROR);
            }
			if (!cognito.ExistIdCognito(id)) {
				message.setMessage("Error: could not edit, user ID: " + id + " does not exist in the database!");
                response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.NO_SE_ENCONTRARON_DATOS);
				
			} else {
				ProductImg contentImg = cognitoService.detailDataImg(id);
				User info = dao.FindById(id);
				List<User> list = new ArrayList();
				clienteActual.setName(info.getName());
				clienteActual.setLastName(info.getLastName());
				clienteActual.setPhone(info.getPhone());
				clienteActual.setEmail(info.getEmail());
				clienteActual.setBirthdate(info.getBirthdate());
				clienteActual.setImages(contentImg.getImg());
				clienteActual.setNickname(info.getNickname());
				clienteActual.setStatus(info.getStatus());
				list.add(clienteActual);
				ListUsers information=new ListUsers();
				information.setUser(list);
				for (User data : list) {
					logger.info(""+data.getEmail());

				}
				message.setMessage("Data successfully");
                response = ResponseUtils.createResponse(Collections.singletonList(information.toJsonObject()),ResponseEnum.EXITO);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			 e.printStackTrace();
	            System.out.println("EnrollmentServiceImpl.DBException - FindById: " + e.getMessage());
	            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());

		}
		// TODO Auto-generated method stub
		return response;
	}

	@Override
	public ResponseModel FindByEmail(String email) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel FindByAmin() {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel CreateAdmin(User user) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel GetCode(User user) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel SharedCode(User user, User users) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel Enabled(String id, int status) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel FindByUser() {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel findByFacebookId(String facebookId) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel createFB(User user) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel findByAppleId(String appleId) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

	@Override
	public ResponseModel createApple(User user) {
		CognitoClient cognito = new CognitoClient();
        ResponseModel response = null;
        Message message = new Message();
        User clienteActual=new User();
		   try {
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				 e.printStackTrace();
		            System.out.println("EnrollmentServiceImpl.DBException - Create: " + e.getMessage());
		            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
			}
		return null;
	}

}
