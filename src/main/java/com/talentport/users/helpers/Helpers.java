package com.talentport.users.helpers;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.talentport.users.controller.UsersController;
import com.talentport.users.dao.IUserDao;
import com.talentport.users.dao.implemet.UserDao;
import com.talentport.users.dto.User;

public class Helpers {
	private static final Logger logger = LoggerFactory.getLogger(Helpers.class);

	public static String NameUUID() {
		// TODO Auto-generated method stub
		return UUID.randomUUID().toString();
	}

	public static String generateRandomPassword() {
		// ASCII range – alphanumeric (0-9, a-z, A-Z)
		final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		SecureRandom random = new SecureRandom();

		// each iteration of the loop randomly chooses a character from the given
		// ASCII range and appends it to the `StringBuilder` instance
		return IntStream.range(0, 10)
				.map(i -> random.nextInt(chars.length()))
				.mapToObj(randomIndex -> String.valueOf(chars.charAt(randomIndex)))
				.collect(Collectors.joining()) + ".@";
	}

	public static ResponseEntity<Map<String, Object>> ResponseClass(int codeError, Map<String, Object> object,
			String msgSucces, String msgError) {
		Map<String, Object> response = new HashMap<>();
		response.put("statusCode", codeError);
		response.put("Data", object);
		response.put("Message", msgSucces);
		response.put("Error", msgError);
		return new ResponseEntity<Map<String, Object>>(response, codeError == 500 ? HttpStatus.INTERNAL_SERVER_ERROR : codeError == 201 ? HttpStatus.CREATED 
				: codeError == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
	}

	public static String getCode(){

		String uuid = UUID.randomUUID().toString().toUpperCase();
		Stream<Character> digit = uuid.chars().mapToObj(i -> (char) i).filter(Character::isDigit).limit(3);
		Stream<Character> alpha = uuid.chars().mapToObj(i -> (char) i).filter(Character::isAlphabetic).limit(3);
		List<Character> collect = Stream.concat(alpha,digit).collect(Collectors.toList());
		String finalString = collect.stream().map(Object::toString).collect(Collectors.joining());

		return finalString;
	}

	public static List<String> getAuthorities(String access)  {
		List<String> authList = new ArrayList<>();
		if (access.equals("WEB")) {
			authList.add(new String("ROLE_ADMIN"));
		} else if (access.equals("IOS")) {
			authList.add(new String("ROLE_MOBILE"));
		} else if (access.equals("ANDROID")) {
			authList.add(new String("ROLE_MOBILE"));
		}else if (access.equals("WEBSUPER")) {
			authList.add(new String("ROLE_SUPERADMIN"));
		}
		return authList;
	}
	public static boolean ValidEmail(String email) throws Exception {
		boolean response = false;
		try {

			IUserDao service = new UserDao();

			List<User> userList = service.FindByAmin();
				for (User data : userList) {
					if (data.getEmail().equals(email)) {
						response = true;
						break;
					}
				}
		} catch (Exception e) {
		}
	
		return response;

	}
	public static boolean ValidEmailList(String email) throws Exception {
		boolean response = false;
		try {

			IUserDao service = new UserDao();

			List<User> userList = service.FindByUser();
				for (User data : userList) {
						if ( data.getEmail().equals(email)) {
							response = true;
							break;
						}
					}
	
		} catch (Exception e) {
			logger.info(""+e.getMessage());
		}
	
		
		return response;

	}


}
