package com.talentport.users.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.talentport.users.dto.User;

public class Validator {

	/* Validator User for create */
	public static List<String> ValidatorCreate(User user) {
		List<String> models = new ArrayList<>();

		Pattern USERNAME_PATTERN = Pattern.compile("[A-Za-z0-9ñÑáíóúÁÉÍÓÚ ]{0,150}$");
		Pattern LASTNAME_PATTERN = Pattern.compile("[A-Za-z0-9ñÑáíóúÁÉÍÓÚ ]{0,150}$");
		Pattern EMAIL_PATTERN = Pattern
				.compile("^[a-zA-Z0-9ñÑáíóúÁÉÍÓÚ.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$");
		Pattern PHONE_PATTERN = Pattern.compile("^\\+\\d{12,13}");
		Pattern PASSWORD_PATTERN = Pattern
				.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{9,500}");

		if (USERNAME_PATTERN.matcher(user.getName()).matches() && EMAIL_PATTERN.matcher(user.getEmail()).matches()
				&& LASTNAME_PATTERN.matcher(user.getLastName()).matches()
				&& PHONE_PATTERN.matcher(user.getPhone()).matches()
				&& PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
			return null;
		} else {
			models.add(" the fields do not comply with the validation");
			return models;
		}
	}
	
	/* Validator User for create admin */
	public static List<String> ValidatorCreateAdmin(User user) {
		List<String> models = new ArrayList<>();

		Pattern EMAIL_PATTERN = Pattern
				.compile("^[a-zA-Z0-9ñÑáíóúÁÉÍÓÚ.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$");
		
		if (EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
			return null;
		} else {
			models.add(" the fields do not comply with the validation");
			return models;
		}
	}

	/* Validator User for edit */
	public static List<String> ValidatorEdit(User user) {
		List<String> models = new ArrayList<>();

		Pattern USERNAME_PATTERN = Pattern.compile("[A-Za-z0-9ñÑáíóúÁÉÍÓÚ ]{0,150}$");
		Pattern LASTNAME_PATTERN = Pattern.compile("[A-Za-z0-9ñÑáíóúÁÉÍÓÚ ]{0,150}$");
		Pattern EMAIL_PATTERN = Pattern
				.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*" + "@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
		Pattern PHONE_PATTERN = Pattern.compile("^\\+\\d{12,13}");

		if (USERNAME_PATTERN.matcher(user.getNickname()).matches() && EMAIL_PATTERN.matcher(user.getEmail()).matches()
				&& LASTNAME_PATTERN.matcher(user.getLastName()).matches()
				&& PHONE_PATTERN.matcher(user.getPhone()).matches()) {
			return null;
		} else {
			models.add(" the fields do not comply with the validation");
			return models;
		}
	}

	/* Validator User for edit 
	public static List<String> ValidatorEditAdmin(User user) {
		List<String> models = new ArrayList<>();

		
		if (USERNAME_PATTERN.matcher(user.getName()).matches() && EMAIL_PATTERN.matcher(user.getEmail()).matches()
				&& LASTNAME_PATTERN.matcher(user.getLastName()).matches()
				&& PHONE_PATTERN.matcher(user.getPhone()).matches()) {
			return null;
		} else {
			models.add(" the fields do not comply with the validation");
			return models;
		}
	}*/
}
