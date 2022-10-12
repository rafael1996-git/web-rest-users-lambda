package com.talentport.users.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.MessageActionType;
import com.talentport.users.dto.Token;
import com.talentport.users.dto.User;
import com.talentport.users.security.CognitosUsers;
import com.talentport.users.security.Credentials;
import com.talentport.users.utils.Exceptions.TradeException;

@Component
public class CognitoService {
	private static final Logger log = LoggerFactory.getLogger(CognitoService.class);
	Token token = new Token();
	public Token createNewUser(User user) throws Exception {

		try {
			AWSCognitoIdentityProvider cognitoClient = getAWSCognitoIdentityClient();
			AttributeType userAttrs = new AttributeType().withName("email").withValue(user.getEmail());
			AttributeType userAttrs1 = new AttributeType().withName("email_verified").withValue("true");

			List<AttributeType> userAttrsList = new ArrayList<>();
			userAttrsList.add(userAttrs);
			userAttrsList.add(userAttrs1);

			AdminCreateUserRequest userRequest = new AdminCreateUserRequest()

					.withUserPoolId(getCognitosUsers().getUserPoolId()).withUsername(user.getCogname())
					.withTemporaryPassword(user.getPassword()).withUserAttributes(userAttrs, userAttrs1)
					.withMessageAction(MessageActionType.SUPPRESS);
			AdminCreateUserResult createUserResult = cognitoClient.adminCreateUser(userRequest);
			user.getRoles().forEach(r -> {
				try {

					addUserToGroup(user.getCogname(), r);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			AdminSetUserPasswordRequest adminSetUserPasswordRequest = new AdminSetUserPasswordRequest()
					.withUsername(user.getCogname()).withUserPoolId(getCognitosUsers().getUserPoolId())
					.withPassword(user.getPassword()).withPermanent(true);
			cognitoClient.adminSetUserPassword(adminSetUserPasswordRequest);

			token.setIdCognito(createUserResult.getUser().getUsername());
		} catch (Exception e) {
			log.info("" + e.getMessage());

		}
		return token;
	}

	public void addUserToGroup(String username, String groupName) throws Exception {

		try {
			AWSCognitoIdentityProvider cognitoClient = getAWSCognitoIdentityClient();
			AdminAddUserToGroupRequest OPJ = new AdminAddUserToGroupRequest().withGroupName(groupName)
					.withUserPoolId(getCognitosUsers().getUserPoolId()).withUsername(username);

			cognitoClient.adminAddUserToGroup(OPJ);

			cognitoClient.shutdown();
		} catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
			log.info("" + e.getMessage());
		}
	}

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
