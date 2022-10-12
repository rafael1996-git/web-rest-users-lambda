package com.talentport.users.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.google.gson.Gson;
import com.talentport.users.dao.IUserDao;
import com.talentport.users.dao.implemet.UserDao;
import com.talentport.users.dto.User;
import com.talentport.users.security.CognitosUsers;
import com.talentport.users.security.Credentials;
import com.talentport.users.utils.Exceptions.TradeException;
import com.trader.core.security.TraderEncriptorKey;

@Component
public class CognitoClient {
	
	private static final Logger log = LoggerFactory.getLogger(CognitoClient.class);	
	private static IUserDao service;

	/* Recovery Password User */
	public boolean RecoveryPwsCognito(String email, String newPassword) throws Exception {
		boolean response = false;
		try {

			AWSCognitoIdentityProvider cognitoClient = getAWSCognitoIdentityClient();

			final AdminSetUserPasswordRequest adminResetUserPasswordRequest = new AdminSetUserPasswordRequest()
					.withUserPoolId(getCognitosUsers().getUserPoolId()).withUsername(email).withPassword(newPassword).withPermanent(true);

			cognitoClient.adminSetUserPassword(adminResetUserPasswordRequest);
			response = true;
		} catch (Exception e) {
			System.out.println(e);
		}
		return response;

	}
	
	/* Change Password User */
	public boolean ChangePswCognito(User user, String uuid) throws Exception {
		boolean response = false;
		
		try {

			AWSCognitoIdentityProvider cognitoClient = getAWSCognitoIdentityClient();
			
				AdminSetUserPasswordRequest adminSetUserPasswordRequest = new AdminSetUserPasswordRequest()
                    .withUsername(uuid)
                    .withUserPoolId(getCognitosUsers().getUserPoolId())
                    .withPassword(user.getNewPassword())
                    .withPermanent(true);
            cognitoClient.adminSetUserPassword(adminSetUserPasswordRequest);            
          
            response = true;
		} catch (Exception e) {
			log.error("ERROR CHANGE PASSWORD : " + e.toString());
		}
		return response;

	}
	
	/* Change User email in cognito */
	public boolean ChangeEmail(String username, String email) throws Exception {
		boolean response = false;
		
		AWSCognitoIdentityProvider cognitoClient = getAWSCognitoIdentityClient();
		AdminUpdateUserAttributesRequest request = new AdminUpdateUserAttributesRequest();
        AttributeType data = new AttributeType();
        data.setName("email");
        data.setValue(email);
        request.withUserAttributes(data);
        request.withUserAttributes(
                new AttributeType().withName("email").withValue(email),
                new AttributeType().withName("email_verified").withValue("true")             
        );
        request.withUsername(username);
        request.withUserPoolId(getCognitosUsers().getUserPoolId());
        cognitoClient.adminUpdateUserAttributes(request);
		response = true;
			
		return response;
	}
	
	/* Function */
	/* Get Info user {email} */
	public User GetInfoCognito(String email) throws Exception {
		User userInfo = new User();
		
		try {
			AWSCognitoIdentityProvider cognitoClient = getAWSCognitoIdentityClient();
			List<User> users = new ArrayList<User>();

			/** prepare Cognito list users request */
			ListUsersRequest listUsersRequest = new ListUsersRequest();
			listUsersRequest.withUserPoolId(getCognitosUsers().getUserPoolId());
			
			/** send list users request */
			ListUsersResult result = cognitoClient.listUsers(listUsersRequest);

			for (UserType user : result.getUsers()) {
				if (user.getAttributes().get(2).getValue().equals(email)) {
					userInfo.setIdUsername(user.getUsername());
				}
			}

		} catch (Exception e) {
			log.info(""+e.getMessage());
		}
		return userInfo;
	}
	public boolean ValidEmail(String email, String emailOld) throws Exception {
		boolean response = false;
		
		try {
			if (!email.equals(emailOld)) {
				AWSCognitoIdentityProvider cognitoClient = getAWSCognitoIdentityClient();
				/** prepare Cognito list users request */
				ListUsersRequest listUsersRequest = new ListUsersRequest();
				listUsersRequest.withUserPoolId(getCognitosUsers().getUserPoolId());
				ListUsersResult result = cognitoClient.listUsers(listUsersRequest);
				for (UserType user : result.getUsers()) {
					log.info(user.getAttributes().get(2).getValue());
					if (user.getAttributes().get(2).getValue().equals(email)) {
						response = true;
						return response;
					}
				}
				
				
			}else {
				response=false;
				return response;
			}
		} catch (Exception e) {
			log.info(""+e.getMessage());
		}
	
		
		return response;

	}
	
	/* Get Info user {email} */
	public boolean ExistIdCognito(String uuid) throws Exception {
		boolean response = false;
		
		try {
			AWSCognitoIdentityProvider cognitoClient = getAWSCognitoIdentityClient();
			AdminGetUserRequest getUserRequest = new AdminGetUserRequest();
	        getUserRequest.setUserPoolId(getCognitosUsers().getUserPoolId());
	        getUserRequest.setUsername(uuid);
			
	        AdminGetUserResult getUserResult = cognitoClient.adminGetUser(getUserRequest);
	        response = true;
		} catch (UserNotFoundException userNotFoundException) {
			log.info(""+userNotFoundException.getMessage());
		}
		return response;
	}
	
	/* Get Info user {id} */
	public static User GetInfoIdCognito(String uuid) throws Exception {
		User userInfo = new User();
		
		try {
			AWSCognitoIdentityProvider cognitoClient = getAWSCognitoIdentityClient();
			List<User> users = new ArrayList<User>();

			/** prepare Cognito list users request */
			ListUsersRequest listUsersRequest = new ListUsersRequest();
			listUsersRequest.withUserPoolId(getCognitosUsers().getUserPoolId());
			
			/** send list users request */
			ListUsersResult result = cognitoClient.listUsers(listUsersRequest);

			for (UserType user : result.getUsers()) {
				if (user.getUsername().equals(uuid)) {
					userInfo.setIdUsername(user.getUsername());
					userInfo.setEmail(user.getAttributes().get(2).getValue().toString());
				}
			}

		} catch (Exception e) {
			log.info(""+e.getMessage());
		}
		return userInfo;
	}
	
	/* List admin */
	public static List<User> ListAdminCognito() throws Exception {
		List<User> userInfo = new ArrayList<User>();
		
		try {
			AWSCognitoIdentityProvider cognitoClient = getAWSCognitoIdentityClient();
			
			/** prepare Cognito list users request */
			ListUsersRequest listUsersRequest = new ListUsersRequest();
			listUsersRequest.withUserPoolId(getCognitosUsers().getUserPoolId());
			
			/** send list users request */
			ListUsersResult result = cognitoClient.listUsers(listUsersRequest);

			for (UserType user : result.getUsers()) {
				User userData = new User();
				try {
					service = new UserDao();
					System.out.println(user);
					User clienteActual = service.FindById(user.getUsername());
					if(clienteActual.getRoles().get(0).equals("ROLE_ADMIN")) {
						userData.setIdUsername(user.getUsername());
						userData.setEmail(user.getAttributes().get(2).getValue());
						userData.setStatus(clienteActual.getStatus());
						userInfo.add(userData);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

		} catch (Exception e) {
			log.info(""+e.getMessage());
		}
		return userInfo;
	}

	/* Keys AWS */
	public static AWSCognitoIdentityProvider getAWSCognitoIdentityClient() throws Exception {
		return AWSCognitoIdentityProviderClientBuilder.standard().withRegion("us-east-1").build();
	}

	public static CognitosUsers getCognitosUsers() throws Exception {
		 
		        try {
		            if(Credentials.COGNITO_KEYS == null) {
		                throw new TradeException(
		                        Constantes.FAILED_GET_KEYS,
		                        Constantes.FAILED_GET_KEYS_CONFIG
		                );
		            }
		           
		        }
		        catch (Exception e) {
		            e.printStackTrace();
		        }
		        CognitosUsers code =new  CognitosUsers(
	            		Credentials.COGNITO_KEYS.getClientId(),
	            		Credentials.COGNITO_KEYS.getUserPoolId(), 
	            		Credentials.COGNITO_KEYS.getAcceskey(), 
	            		Credentials.COGNITO_KEYS.getSecretKey());
	            return code;
		      
		    }
	

}
