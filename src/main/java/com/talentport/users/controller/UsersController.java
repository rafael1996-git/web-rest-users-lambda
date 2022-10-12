package com.talentport.users.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;

import com.talentport.users.dao.ICognitoDao;
import com.talentport.users.dao.ICognitoImplement;
import com.talentport.users.dao.IUserDao;
import com.talentport.users.dto.ProductImg;
import com.talentport.users.dto.Response;
import com.talentport.users.dto.Token;
import com.talentport.users.dto.User;
import com.talentport.users.helpers.Helpers;
import com.talentport.users.model.ExternalOauhtModel;
import com.talentport.users.model.ResponseOauthGoogle;
import com.talentport.users.service.IUsersService;
import com.talentport.users.utils.CognitoClient;
import com.talentport.users.utils.SecurityUtils;
import com.talentport.users.utils.Validator;

import com.trader.core.controllers.TraderBaseController;
import com.trader.core.domain.request.Request;
import com.trader.core.security.TraderEncriptorKey;
import com.trader.core.utils.GsonParserUtils;

@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
		RequestMethod.DELETE })
@RestController
@EnableWebMvc
@RequestMapping("/user")
public class UsersController extends TraderBaseController{

	private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final String canonicalName = UsersController.class.getCanonicalName();

	@Autowired
	private BCryptPasswordEncoder pswEncoder;

	@Autowired
	private IUserDao service;
	
	@Autowired
	private IUsersService repository;

	@Autowired
	private ICognitoDao cognitoService;

	private User userCognito;
	
    @RequestMapping(path = "/Detail/{id}", method = RequestMethod.GET)
	public Object Detalle(@PathVariable String id) throws Exception {
		return SecurityUtils.parseResponse(repository.FindById(id),false);
	}
    /* Create User */
	@RequestMapping(path = "/Create", method = RequestMethod.POST)
	public Object CreateNew(@RequestBody final Request request) throws Exception {
	        Object object = SecurityUtils.decrypt(request.getData(), User.class);

	        if (object == null) {
	            object = errorParsingRequest();
	        }
	        if (object instanceof HashMap) {
	            return object;
	        }
	        User data = (User) object;
	        return SecurityUtils.parseResponse(repository.Create(data), false);
	}


	/* Details User */
	
	@RequestMapping(path = "/detail/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> Detail(@PathVariable String id) throws Exception {

		Map<String, Object> dataResponse = new HashMap<>();
		CognitoClient cognito = new CognitoClient();
		Response response = new Response();

		try {

			User clienteActual = service.FindById(id);
			ProductImg contentImg = cognitoService.detailDataImg(id);
			// List<String> patternName = Validator.ValidatorEdit(clienteActual);

			if (!cognito.ExistIdCognito(id)) {
				response.setCode(400);
				response.setMessage("Error: could not edit, user ID: " + id + " does not exist in the database!");

			} else {
				// User usercognito = cognito.GetInfoIdCognito(id);
				dataResponse.put("Name", clienteActual.getName());
				dataResponse.put("LastName", clienteActual.getLastName());
				dataResponse.put("Phone", clienteActual.getPhone());
				dataResponse.put("Email", clienteActual.getEmail());
				dataResponse.put("Birthdate", clienteActual.getBirthdate());
				dataResponse.put("Images", contentImg.getImg());
				dataResponse.put("Nickname", clienteActual.getNickname());
				dataResponse.put("Status", clienteActual.getStatus());

				response.setCode(200);
				response.setMessage("The client has been successfully!");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(500);
			response.setMessage("");
			response.setError(e.getMessage().toString());

		}
		return Helpers.ResponseClass(response.getCode(), dataResponse, response.getMessage(), response.getError());
	}

	/* Create User */
	@RequestMapping(path = "/create", method = RequestMethod.POST)
	public ResponseEntity<?> Create(@RequestBody User user) throws Exception {

		Map<String, Object> dataResponse = new HashMap<>();
		Token token = null;

		Response response = new Response();

		try {
			List<String> patternName = Validator.ValidatorCreate(user);
			if (patternName != null) {
				for (Integer i = 0; i < patternName.size(); i++) {
					dataResponse.put("Field-".concat(i.toString()), patternName.get(i));
				}
				response.setCode(400);
				response.setMessage(" Error in filling the form");
			} else if (!user.getPassword().equals(user.getConfirmPassword())) {
				dataResponse.put("errors", "incorrect Confirma_password");
				response.setCode(400);
				response.setMessage(" Incorrect Confirma_password");
			} else {
				User userCognito = service.FindByEmail(user.getEmail());
				if (userCognito.getIdUsername() != null) {
					if (userCognito.getStatus() == 2) {
						response.setCode(400);
						response.setMessage(
								"Email: " + user.getEmail().concat(" user with status 2 is blocked from the portal"));
					} else if (userCognito.getStatus() == 0) {
						response.setCode(400);
						response.setMessage(
								"Email: " + user.getEmail().concat(" user with status 0 is delete for the portal"));
					} else {
						response.setCode(400);
						response.setMessage("El correo "+user.getEmail()+" ya ha sido registrado anteriormente.");
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
							service.Create(user);

							dataResponse.put("UserId", user.getIdUsername());
							dataResponse.put("Name", user.getName());
							dataResponse.put("LastName", user.getLastName());
							dataResponse.put("Phone", user.getPhone());
							dataResponse.put("Email", user.getEmail());
							dataResponse.put("BirthDate", user.getBirthdate());
							dataResponse.put("Status", user.getStatus());
							dataResponse.put("Nickname", user.getNickname());

							response.setCode(200);
							response.setMessage("Successful User Creation");
						} else {
							response.setCode(400);
							response.setMessage("El correo "+user.getEmail()+" ya ha sido registrado anteriormente.");
						}

					} else {
						response.setCode(400);
						response.setMessage("Error in filling the form");
						response.setError("the field divace :" + user.getDevice() + " not correct");
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(500);
			response.setMessage("Error performing database insert");
			response.setError(e.getMessage());
		}
		return Helpers.ResponseClass(response.getCode(), dataResponse, response.getMessage(), response.getError());
	}

	/* Edit User */
	@RequestMapping(path = "/edit/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> Update(@RequestBody User user, @PathVariable String id) throws Exception {

		Map<String, Object> dataResponse = new HashMap<>();
		CognitoClient cognito = new CognitoClient();

		Response response = new Response();

		try {
			User clienteActual = service.FindById(id);


			if (clienteActual.getIdUsername() == null) {
				response.setCode(400);
				response.setMessage("Error: could not edit, user ID: " + id + " does not exist in the database!");

			} else

				if (!user.getEmail().equals(clienteActual.getEmail())) {

					if (Helpers.ValidEmailList(user.getEmail())) {
						response.setCode(400);
						response.setMessage("El correo "+user.getEmail()+" ya ha sido registrado anteriormente.");
						response.setError("");

					}else {
						SaveResponseEdit(user, id, dataResponse, cognito, response, clienteActual);
					}

				} else {
					SaveResponseEdit(user, id, dataResponse, cognito, response, clienteActual);
				}

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(500);
			response.setMessage("");
			response.setError(e.getMessage().toString());

		}
		return Helpers.ResponseClass(response.getCode(), dataResponse, response.getMessage(), response.getError());
	}

	private void SaveResponseEdit(User user, String id, Map<String, Object> dataResponse, CognitoClient cognito,
			Response response, User clienteActual) throws Exception {
		List<String> role = Helpers.getAuthorities(user.getDevice());
		user.setRoles(role);
		user.setStatus(clienteActual.getStatus());
		cognito.ChangeEmail(id, user.getEmail());
		user.setName(clienteActual.getName());
		user.setEmail(user.getEmail());
		user.setLastName(clienteActual.getLastName());
		service.Edit(user, id);

		dataResponse.put("Name", user.getName());
		dataResponse.put("LastName", user.getLastName());
		dataResponse.put("Phone", user.getPhone());
		dataResponse.put("Email", user.getEmail());
		dataResponse.put("Birthdate", user.getBirthdate());
		dataResponse.put("Images", clienteActual.getImages());
		dataResponse.put("Nickname", user.getNickname());
		dataResponse.put("Status", clienteActual.getStatus());

		response.setCode(200);
		response.setMessage("The client has been successfully updated!");
	}

	/*
	 * Send Email for change password NOTE: Missing mail delivery
	 */
	@RequestMapping(path = "/send-password", method = RequestMethod.POST)
	public Object send(@RequestBody final Request request) throws Exception {
		 System.out.println(request);
		Map<String, Object> dataResponse = new HashMap<>();
		Response response = new Response();
		try {
	        Object object = SecurityUtils.decrypt(request.getData(), User.class);
	        User data = (User) object;

			 System.out.println("object: "+data.getEmail());
			if ("" == null) {
				 object = errorParsingRequest();

			} else if (service.FindByEmail(data.getEmail()).getIdUsername() != null) {
				// User userBD = service.FindByEmail(user.getEmail());
				// System.out.println("Si encontro un usuario: "+userBD);
				dataResponse.put("email", "usuarios9@gmail.com");

				// Llamar lambda
				//				ResetPasswordModel resetPassword = new ResetPasswordModel();
				//				resetPassword.setEmail(userBD.getEmail());
				//				resetPassword.setUserName(userBD.getName());
				//				try {
				//					String result = LambdaInvoke.INSTANCE.invokeAsyncRawResult(
				//							"talentport-ws-email",
				//							resetPassword.toString());
				//				} catch (Exception e) {
				//					System.out.println("error al llamar lamda");
				//				}

				response.setCode(200);
				response.setMessage("The email has been sent to change the password");
			} else {
				response.setCode(400);
				response.setMessage("Usuario no registrado.");
				response.setError("Usuario no registrado.");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(500);
			response.setMessage("An error has occurred, please contact your administrator.");
		}

		return Helpers.ResponseClass(response.getCode(), dataResponse, response.getMessage(), response.getError());
	}

	/* Recovery User */
	@RequestMapping(path = "/recovery-password", method = RequestMethod.POST)
	public ResponseEntity<?> Recovery(@RequestBody User user, BindingResult result) throws Exception {


		Map<String, Object> dataResponse = new HashMap<>();
		CognitoClient cognito = new CognitoClient();

		Response response = new Response();

		try {
			User clienteActual = service.FindByEmail(user.getEmail());
			if (user.getEmail() == null && user.getPassword() == null && user.getConfirmPassword() == null) {
				response.setCode(400);
				response.setMessage("Email, Password and Confirm Password is not null");

			} else if (!user.getPassword().equals(user.getConfirmPassword())) {
				response.setCode(400);
				response.setMessage("The password is not the same as the confirm password");

			} else if (clienteActual.getIdUsername() == null) {
				response.setCode(400);
				response.setMessage(
						"Error: could not edit, user ID: " + user.getEmail() + " does not exist in the database!");
			} else {

				cognito.RecoveryPwsCognito(user.getEmail(), user.getConfirmPassword());

				dataResponse.put("Name", clienteActual.getName());
				dataResponse.put("LastName", clienteActual.getLastName());
				dataResponse.put("Phone", clienteActual.getPhone());
				dataResponse.put("Email", clienteActual.getEmail());
				dataResponse.put("Birthdate", clienteActual.getBirthdate());
				dataResponse.put("Images", clienteActual.getImages());
				dataResponse.put("Nickname", clienteActual.getNickname());
				dataResponse.put("Status", clienteActual.getStatus());

				response.setCode(200);
				response.setMessage("The password has been changed successfully, please log in again");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(500);
			response.setMessage("");
			response.setError(e.getMessage().toString());

		}
		return Helpers.ResponseClass(response.getCode(), dataResponse, response.getMessage(), response.getError());
	}

	/* Change password */
	@RequestMapping(path = "/change-password", method = RequestMethod.POST)
	public ResponseEntity<?> changePassword(@RequestBody User user) throws Exception {
		Map<String, Object> dataResponse = new HashMap<>();
		CognitoClient cognito = new CognitoClient();

		Response response = new Response();
		User userbd = new User();

		userbd = service.FindByEmail(user.getEmail());

		try {
		
			if (user.getEmail() == null && user.getPassword() == null && user.getConfirmPassword() == null
					&& user.getNewPassword() == null) {
				response.setCode(400);
				response.setMessage("Email, Password, Confirm Password and New Password is not null");

			} else if (!user.getNewPassword().equals(user.getConfirmPassword())) {
				response.setCode(400);
				response.setMessage("The password is not the same as the confirm password");

			} else if (user.getPassword().equals(user.getNewPassword())) {
				response.setCode(400);
				response.setMessage("The password you have must be different from the current one");

			} else if (userbd.getIdUsername() == null) {
				response.setCode(400);
				response.setMessage("Email not exist in DB");

			} else if (!pswEncoder.matches(user.getPassword(), userbd.getPassword())) {
				response.setCode(400);
				response.setMessage("The current password is incorrect");

			} else if (cognito.ChangePswCognito(user, userbd.getIdUsername())) {
				String encodedPassword = pswEncoder.encode(user.getNewPassword());

				service.EditPassword(encodedPassword, user.getId());
				dataResponse.put("email", user.getEmail());
				response.setCode(200);
				response.setMessage("The password has been changed successfully, please log in again");
			} else {
				response.setCode(400);
				response.setMessage("Email not exist");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(500);
			response.setMessage("An error has occurred, please contact your administrator.");
		}
		return Helpers.ResponseClass(response.getCode(), dataResponse, response.getMessage(), response.getError());
	}

	/* Delete User */
	@RequestMapping(path = "/delete/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> Delete(@PathVariable String id) throws Exception {

		Map<String, Object> dataResponse = new HashMap<>();
		Response response = new Response();

		try {

			User clienteActual = service.FindById(id);

			if (clienteActual.getIdUsername() == null) {
				response.setCode(400);
				response.setMessage("Error: could not edit, user ID: " + id + " does not exist in the database!");

			} else {
				service.Delete(id, clienteActual.getStatus());
				User statusActual = service.FindById(id);
				dataResponse.put("Name", clienteActual.getName());
				dataResponse.put("LastName", clienteActual.getLastName());
				dataResponse.put("Phone", clienteActual.getPhone());
				dataResponse.put("Email", clienteActual.getEmail());
				dataResponse.put("Birthdate", clienteActual.getBirthdate());
				dataResponse.put("Images", clienteActual.getImages());
				dataResponse.put("Nickname", clienteActual.getNickname());
				dataResponse.put("Status", statusActual.getStatus());

				response.setCode(200);
				response.setMessage("The client has been successfully delete!");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(500);
			response.setMessage("");
			response.setError(e.getMessage().toString());

		}
		return Helpers.ResponseClass(response.getCode(), dataResponse, response.getMessage(), response.getError());
	}

	/* Panel Administrator */
	/* Create User */
	@RequestMapping(path = "/admin/create", method = RequestMethod.POST)
	public ResponseEntity<?> CreateAdmin(@RequestBody User user) throws Exception {

		Map<String, Object> dataResponse = new HashMap<>();
		Token token = null;

		Response response = new Response();

		try {
			List<String> patternName = Validator.ValidatorCreateAdmin(user);

			if (patternName != null) {
				for (Integer i = 0; i < patternName.size(); i++) {
					dataResponse.put("Field-".concat(i.toString()), patternName.get(i));
				}
				response.setCode(400);
				response.setMessage(" Error in filling the form");
			} else {

				User userFind = service.FindByEmail(user.getEmail());
				if (userFind.getIdUsername() != null) {

					if (userFind.getStatus() == 2) {
						response.setCode(400);
						response.setMessage(
								"Email: " + user.getEmail().concat(" user with status 2 is blocked from the portal"));
					} else if (userFind.getStatus() == 0) {
						response.setCode(400);
						response.setMessage(
								"Email: " + user.getEmail().concat(" user with status 0 is delete for the portal"));
					} else {
						response.setCode(400);
						response.setMessage("El correo "+user.getEmail()+" ya ha sido registrado anteriormente.");
					}
				} else {

					List<String> role = Helpers.getAuthorities(user.getDevice());
					if (role.toString().substring(1, role.toString().length() - 1).toString() != null
							&& !role.toString().substring(1, role.toString().length() - 1).toString().isEmpty()) {

						user.setRoles(role);
						user.setPassword("Temp" + Helpers.NameUUID().substring(0, 4).replace(" ", "") + ""
								+ Helpers.generateRandomPassword().replace(" ", ""));
						user.setCogname(Helpers.NameUUID());
						user.setName(user.getName());
						token = cognitoService.getToken(user);
						if (token.getIdCognito() != null) {

							String encodedPassword = pswEncoder.encode(user.getPassword());
							user.setPassword(encodedPassword);
							user.setIdUsername(token.getIdCognito());
							user.setStatus(1);
							service.CreateAdmin(user);

							dataResponse.put("UserId", user.getIdUsername());
							dataResponse.put("Name", user.getName());
							dataResponse.put("LastName", user.getLastName());
							dataResponse.put("Phone", user.getPhone());
							dataResponse.put("Email", user.getEmail());
							dataResponse.put("BirthDate", user.getBirthdate());
							dataResponse.put("Status", user.getStatus());
							dataResponse.put("Nickname", user.getNickname());

							response.setCode(200);
							response.setMessage("Successful User Creation");
						} else {
							response.setCode(400);
							response.setMessage("Email: " + user.getEmail().concat(" No se pudo registrar en cognito"));
						}
					} else {
						response.setCode(400);
						response.setMessage("Error in filling the form");
						response.setError("the field divace :" + user.getDevice() + " not correct");
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(500);
			response.setMessage("Error performing database insert");
			response.setError(e.getMessage());
		}
		return Helpers.ResponseClass(response.getCode(), dataResponse, response.getMessage(), response.getError());
	}

	/* Edit User Admin */
	@RequestMapping(path = "/admin/edit/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> UpdateAdmin(@RequestBody User user, @PathVariable String id) throws Exception {

		Map<String, Object> dataResponse = new HashMap<>();
		CognitoClient cognito = new CognitoClient();
		Response response = new Response();
		try {
			User clienteActual = service.FindById(id);

			if (clienteActual.getIdUsername() == null) {
				response.setCode(400);
				response.setMessage("Error: could not edit, user ID: " + id + " does not exist in the database!");

			} else

				if (!user.getEmail().equals(clienteActual.getEmail())) {

					if (Helpers.ValidEmail(user.getEmail())) {
						response.setCode(400);
						response.setMessage("El correo "+user.getEmail()+" ya ha sido registrado anteriormente.");
						response.setError("");

					}else {
						SaveResponse(user, id, dataResponse, cognito, response, clienteActual);
					}

				} else {
					SaveResponse(user, id, dataResponse, cognito, response, clienteActual);
				}
		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(500);
			response.setMessage("");
			response.setError(e.getMessage().toString());

		}
		return Helpers.ResponseClass(response.getCode(), dataResponse, response.getMessage(), response.getError());
	}

	private void SaveResponse(User user, String id, Map<String, Object> dataResponse, CognitoClient cognito,
			Response response, User clienteActual) throws Exception {
		cognito.ChangeEmail(id, user.getEmail());
		List<String> role = Helpers.getAuthorities(user.getDevice());
		clienteActual.setRoles(role);
		clienteActual.setName(user.getName());
		clienteActual.setEmail(user.getEmail());
		service.Edit(clienteActual, id);

		dataResponse.put("Name", clienteActual.getName());
		dataResponse.put("LastName", user.getLastName());
		dataResponse.put("Phone", user.getPhone());
		dataResponse.put("Email", user.getEmail());
		dataResponse.put("Birthdate", user.getBirthdate());
		dataResponse.put("Images", clienteActual.getImages());
		dataResponse.put("Nickname", clienteActual.getNickname());
		dataResponse.put("Status", clienteActual.getStatus());

		response.setCode(200);
		response.setMessage("The client has been successfully updated!");
	}

	/* Edit User Admin */
	@RequestMapping(path = "/admin/list-admins", method = RequestMethod.GET)
	public ResponseEntity<?> ListAdmin() throws Exception {

		Map<String, Object> dataResponse = new HashMap<>();
		Response response = new Response();

		List<User> userList = service.FindByAmin();
		dataResponse.put("Users", userList);
		response.setCode(200);
		response.setMessage("The list admin user has been successfully!");

		return Helpers.ResponseClass(response.getCode(), dataResponse, response.getMessage(), response.getError());
	}

	@RequestMapping(path = "/admin/inactive/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> statusEdit(@PathVariable String id) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> dataUsers = new HashMap<>();
		String msg = "";
		if (id.equals(null) || id.trim().length() == 0) {
			msg = "The id are required";
			return Helpers.ResponseClass(400, dataUsers, msg, "");
		}

		try {
			User clienteActual = service.FindById(id);

			if (clienteActual == null) {
				msg = "User not found in database";
			}

			else {
				User userEdit = new User();
				userEdit.setName(clienteActual.getName());
				userEdit.setLastName(clienteActual.getLastName());
				userEdit.setPhone(clienteActual.getPhone());
				userEdit.setEmail(clienteActual.getEmail());
				userEdit.setBirthdate(clienteActual.getBirthdate());
				userEdit.setRoles(clienteActual.getRoles());
				userEdit.setStatus(clienteActual.getStatus() == 2 || clienteActual.getStatus() == 0 ? 1 : 2);

				service.Enabled(id, userEdit.getStatus());

				dataUsers.put("userId", userEdit.getId());
				dataUsers.put("name", userEdit.getName());
				dataUsers.put("lastname", userEdit.getLastName());
				dataUsers.put("nickname", userEdit.getNickname());
				dataUsers.put("email", userEdit.getEmail());
				dataUsers.put("status", userEdit.getStatus());
				response.put("user", dataUsers);

				msg = "User updated successfully";
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return Helpers.ResponseClass(500, null, "", "Internal server error contact");
		}

		return Helpers.ResponseClass(200, dataUsers, msg, "");
	}

	/* CODES */

	/* Code User */
	@RequestMapping(path = "/code/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> Code(@PathVariable String id) throws Exception {

		Map<String, Object> dataResponse = new HashMap<>();
		Response response = new Response();

		User user = new User();
		user.setIdUsername(id);
		user.setCodes(Helpers.getCode());
		String code = service.GetCode(user);

		dataResponse.put("Code", code);
		response.setCode(200);
		response.setMessage("Created Code");

		return Helpers.ResponseClass(response.getCode(), dataResponse, response.getMessage(), response.getError());
	}

	/* Shared Code User */
	@RequestMapping(path = "/code/shared/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> SharedCode(@RequestBody User users, @PathVariable String id) throws Exception {

		Map<String, Object> dataResponse = new HashMap<>();
		Response response = new Response();

		User user = new User();
		user.setIdUsername(id);
		// user.setCodes(Helpers.getCode());
		int code = service.SharedCode(user, users);

		dataResponse.put("Code", code);
		response.setCode(200);
		response.setMessage(
				code == 2 ? "Shared Created Code" : code == 0 ? "Code Not Found" : "Code is already registered");

		return Helpers.ResponseClass(response.getCode(), dataResponse, response.getMessage(), response.getError());
	}

	@RequestMapping(path = "/prf-image", method = RequestMethod.PUT)
	public ResponseEntity<?> UploadImage(@RequestBody ProductImg opj) throws IOException {
		Map<String, Object> dataUser = new HashMap<>();
		Response response = new Response();
		try {
			User clienteActual = service.FindById(opj.getId().replace(" ", ""));
			User clienteActual2 = clienteActual;
			if (clienteActual2 == null && clienteActual2.getId().isEmpty()) {

				response.setCode(400);
				response.setMessage(
						"Error: could not edit, user ID: " + opj.getId() + " does not exist in the database!");
			}
			if (opj.getImg() == null) {
				response.setCode(400);
				response.setMessage("Error uploading image!");
			} else {
				cognitoService.getDataImg(opj.getImg().replace(" ", ""), opj.getId().replace(" ", ""));
				clienteActual2.setImages("");
				dataUser.put("id", opj.getId());
				dataUser.put("name-image", opj.getImg());
				service.EditImage(clienteActual2, opj.getId());
				response.setCode(200);
				response.setMessage("Your file has been uploaded successfully!");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return Helpers.ResponseClass(500, null, "Internal server error contact", "");
		}
		return Helpers.ResponseClass(response.getCode(), dataUser, response.getMessage(), response.getError());
	}

	@RequestMapping(path = "/oauth", method = RequestMethod.POST)
	public ResponseEntity<?> getAuthenticationToken(@RequestBody ExternalOauhtModel data) {

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		Map<String, Object> dataUser = new HashMap<>();

		// Validar plataforma
		switch (data.getPlataform()) {
		case 1:
			// Google
			String urlTemplate = UriComponentsBuilder.fromHttpUrl("https://oauth2.googleapis.com/tokeninfo")
			.queryParam("id_token", "{id_token}").encode().toUriString();

			Map<String, String> params = new HashMap<>();
			params.put("id_token", data.getToken());

			RestTemplate restOperations = new RestTemplate();

			User userCognito = null;
			try {
				ResponseEntity<ResponseOauthGoogle> responseGoogle = restOperations.exchange(urlTemplate,
						HttpMethod.GET, entity, ResponseOauthGoogle.class, params);
				if (responseGoogle.getStatusCode().is2xxSuccessful()) {
					System.out.println("es valido " + responseGoogle.getBody().toString());
					String email = responseGoogle.getBody().getEmail();

					// Buscar en la BD si ya esta registrado
					userCognito = service.FindByEmail(email);
					if (userCognito.getIdUsername() != null) {
						System.out.println("Ya existe en la BD");
						userCognito = service.FindByEmail(email);
						dataUser.put("name", userCognito.getName());
						dataUser.put("lastName", userCognito.getLastName());
						dataUser.put("email", userCognito.getEmail());
						dataUser.put("birthdate", userCognito.getBirthdate());
						dataUser.put("idUsername", userCognito.getIdUsername());
						dataUser.put("status", userCognito.getStatus());
						dataUser.put("nickname", userCognito.getNickname());
						dataUser.put("step", (userCognito.getBirthdate() == null || userCognito.getPhone() == null) ? 1 : 2);
					} else {
						System.out.println("No existe en la BD. Va a registrarse");
						Token token = null;
						cognitoService = new ICognitoImplement();
						List<String> role = Helpers.getAuthorities(data.getDevice());
						if (role.toString().substring(1, role.toString().length() - 1).toString() != null
								&& !role.toString().substring(1, role.toString().length() - 1).toString().isEmpty()) {
							userCognito.setEmail(email);
							userCognito.setPassword("Ejemplo1%");
							userCognito.setRoles(role);
							userCognito.setCogname(Helpers.NameUUID());
							token = cognitoService.getToken(userCognito);

							if (token.getIdCognito() != null) {
								System.out.println("se creo en cpognito; " + email);
								userCognito.setIdUsername(token.getIdCognito());
								userCognito.setStatus(1);
								userCognito.setEmail(email);
								userCognito.setName(responseGoogle.getBody().getName());
								userCognito.setLastName(responseGoogle.getBody().getGiven_name());
								userCognito.setFacebookId(responseGoogle.getBody().toString());
								service.Create(userCognito);

								userCognito = service.FindByEmail(email);
								dataUser.put("name", userCognito.getName());
								dataUser.put("lastName", userCognito.getLastName());
								dataUser.put("email", userCognito.getEmail());
								dataUser.put("birthdate", userCognito.getBirthdate());
								dataUser.put("idUsername", userCognito.getIdUsername());
								dataUser.put("status", userCognito.getStatus());
								dataUser.put("nickname", userCognito.getNickname());
								dataUser.put("step", (userCognito.getBirthdate() == null || userCognito.getPhone() == null) ? 1 : 2);

							}
						}
					}
				}

				return Helpers.ResponseClass(200, dataUser, "Datos del usuario", null);
			} catch (Exception e) {
				e.printStackTrace();
				return Helpers.ResponseClass(412, dataUser, null, "Token invalido");
			}
		case 2:
			// Facebook
			return loginFacebook(data.getToken(), data.getDevice());
		case 3:
			// Apple
			return loginApple(data.getToken(), data.getDevice(), data.getEmail());
		}
		return null;
	}

	private ResponseEntity<?> loginApple(String token, String device, String email) {
		// Buscar si el id de apple o email
		Map<String, Object> dataUser = new HashMap<>();

		try {
			User userEmail = service.FindByEmail(email);
			User userApple = service.findByAppleId(token);

			if (userApple.getIdUsername() != null || userEmail.getIdUsername() != null) {
				System.out.println("No existe en la BD. Va a registrarse");
				// El usuario ya existe
				dataUser.put("name", userEmail.getName());
				dataUser.put("lastName", userEmail.getLastName());
				dataUser.put("email", userEmail.getEmail());
				dataUser.put("birthdate", userEmail.getBirthdate());
				dataUser.put("idUsername", userEmail.getIdUsername());
				dataUser.put("status", userEmail.getStatus());
				dataUser.put("nickname", userEmail.getNickname());
				dataUser.put("step", (userEmail.getBirthdate() == null || userEmail.getPhone() == null) ? 1 : 2);

				return Helpers.ResponseClass(200, dataUser, "Datos del usuario", null);
			}else {
				// Registrar usuario
				System.out.println("No existe en la BD. Va a registrarse");

				User userCognito = new User();
				Token tokenCg = null;
				cognitoService = new ICognitoImplement();

				List<String> role = Helpers.getAuthorities(device);
				if (role.toString().substring(1, role.toString().length() - 1).toString() != null
						&& !role.toString().substring(1, role.toString().length() - 1).toString().isEmpty()) {
					userCognito.setEmail(email);
					userCognito.setPassword("Ejemplo1%");
					userCognito.setRoles(role);
					userCognito.setCogname(Helpers.NameUUID());
					tokenCg = cognitoService.getToken(userCognito);

					if (tokenCg.getIdCognito() != null) {
						System.out.println("se creo en cpognito; " + email);
						userCognito.setIdUsername(tokenCg.getIdCognito());
						userCognito.setStatus(1);
						userCognito.setEmail(email);
						userCognito.setName("AppleName");
						userCognito.setLastName("");
						userCognito.setFacebookId("");
						service.Create(userCognito);

						userCognito = service.FindByEmail(email);
						dataUser.put("name", userCognito.getName());
						dataUser.put("lastName", userCognito.getLastName());
						dataUser.put("email", userCognito.getEmail());
						dataUser.put("birthdate", userCognito.getBirthdate());
						dataUser.put("idUsername", userCognito.getIdUsername());
						dataUser.put("status", userCognito.getStatus());
						dataUser.put("nickname", userCognito.getNickname());
						dataUser.put("step", (userCognito.getBirthdate() == null || userCognito.getPhone() == null) ? 1 : 2);
					}
				}
				return Helpers.ResponseClass(200, dataUser, "Datos del usuario", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Helpers.ResponseClass(200, dataUser, "Datos del usuario", null);
		}
	}

	public ResponseEntity<?> loginFacebook(String token, String device) {
		Map<String, Object> dataUser = new HashMap<>();
		User userCognito = null;

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlTemplate = UriComponentsBuilder.fromHttpUrl("https://graph.facebook.com/me")
				.queryParam("access_token", "{access_token}")
				.encode()
				.toUriString();

		Map<String, String> params = new HashMap<>();
		params.put("access_token", token);

		// Obtener id del usuario
		RestTemplate restOperations = new RestTemplate();
		ResponseEntity<ResponseOauthGoogle> responseFacebook =restOperations.exchange(
				urlTemplate, HttpMethod.GET, entity, ResponseOauthGoogle.class, params);

		// Obtener info del usuario
		urlTemplate = UriComponentsBuilder.fromHttpUrl("https://graph.facebook.com/"+responseFacebook.getBody().getId()+"")
				.queryParam("fields", "{fields}")
				.queryParam("access_token", "{access_token}")
				.encode()
				.toUriString();

		Map<String, String> paramsData = new HashMap<>();
		paramsData.put("fields", "id,name,email");
		paramsData.put("access_token", token);

		ResponseEntity<ResponseOauthGoogle> responseFacebookData =restOperations.exchange(
				urlTemplate, HttpMethod.GET, entity, ResponseOauthGoogle.class, paramsData);

		if(responseFacebook.getStatusCode().is2xxSuccessful()) {

			// Buscar si existe el usuario registrado
			try {
				System.out.println("id a  buscar: "+responseFacebook.getBody().getId());
				userCognito = service.findByFacebookId(responseFacebook.getBody().getId());

				if(responseFacebookData.getBody().getEmail() != null) {
					System.out.println("si tiene un correo asociado");
					// Si tiene un correo asociado
					User userEmail = service.FindByEmail(responseFacebookData.getBody().getEmail());

					if (userCognito.getIdUsername() != null || userEmail.getIdUsername() != null) {
						System.out.println("Ya existe en la BD");
						if (userCognito.getIdUsername() != null) {
							userCognito = service.findByFacebookId(responseFacebook.getBody().getId());
							dataUser.put("name", userCognito.getName());
							dataUser.put("lastName", userCognito.getLastName());
							dataUser.put("email", userCognito.getEmail());
							dataUser.put("birthdate", userCognito.getBirthdate());
							dataUser.put("idUsername", userCognito.getIdUsername());
							dataUser.put("status", userCognito.getStatus());
							dataUser.put("nickname", userCognito.getNickname());
							dataUser.put("step", (userCognito.getBirthdate() == null || userCognito.getPhone() == null) ? 1 : 2);
						}else {
							dataUser.put("name", userEmail.getName());
							dataUser.put("lastName", userEmail.getLastName());
							dataUser.put("email", userEmail.getEmail());
							dataUser.put("birthdate", userEmail.getBirthdate());
							dataUser.put("idUsername", userEmail.getIdUsername());
							dataUser.put("status", userEmail.getStatus());
							dataUser.put("nickname", userEmail.getNickname());
							dataUser.put("step", (userCognito.getBirthdate() == null || userCognito.getPhone() == null) ? 1 : 2);
						}

					}else {
						System.out.println("No existe en la BD. Va a registrarse");
						if (responseFacebookData.getStatusCode().is2xxSuccessful()) {
							System.out.println("email: "+responseFacebookData.getBody().getEmail());
						}

						Token tokenCg = null;
						List<String> role = Helpers.getAuthorities(device);
						if (role.toString().substring(1, role.toString().length() - 1).toString() != null
								&& !role.toString().substring(1, role.toString().length() - 1).toString().isEmpty()) {
							userCognito.setEmail(responseFacebookData.getBody().getEmail());
							userCognito.setPassword("Ejemplo1%");
							userCognito.setRoles(role);
							userCognito.setCogname(Helpers.NameUUID());
							tokenCg = cognitoService.getToken(userCognito);

							if (tokenCg.getIdCognito() != null) {
								System.out.println("se creo en cpognito; "+responseFacebookData.getBody().getEmail());
								userCognito.setIdUsername(tokenCg.getIdCognito());
								userCognito.setStatus(1);
								userCognito.setEmail(responseFacebookData.getBody().getEmail());
								userCognito.setName(responseFacebookData.getBody().getName());
								userCognito.setLastName(responseFacebookData.getBody().getGiven_name());
								userCognito.setFacebookId(responseFacebook.getBody().getId());
								service.createFB(userCognito);

								userCognito = service.findByFacebookId(responseFacebook.getBody().getId());
								dataUser.put("name", userCognito.getName());
								dataUser.put("lastName", userCognito.getLastName());
								dataUser.put("email", userCognito.getEmail());
								dataUser.put("birthdate", userCognito.getBirthdate());
								dataUser.put("idUsername", userCognito.getIdUsername());
								dataUser.put("status", userCognito.getStatus());
								dataUser.put("nickname", userCognito.getNickname());
								dataUser.put("step", (userCognito.getBirthdate() == null || userCognito.getPhone() == null) ? 1 : 2);

							}
						}
					}	
				}else {
					System.out.println("no tiene un correo asociado "+responseFacebook.getBody().toString());
					return Helpers.ResponseClass(412, dataUser, null, "No se puede registrar porque no existe un correo asociado a esta cuenta");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		return Helpers.ResponseClass(200, dataUser, "Datos del usuario", null);
	}

	@Override
	public HashMap healthCheck() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap getStatus(boolean withDB) {
		// TODO Auto-generated method stub
		return null;
	}
}


