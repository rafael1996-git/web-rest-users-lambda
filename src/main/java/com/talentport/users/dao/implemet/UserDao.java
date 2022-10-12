package com.talentport.users.dao.implemet;



import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.talentport.core.db.oracle.OracleDBPool;
import com.talentport.users.controller.UsersController;
import com.talentport.users.dao.IUserDao;
import com.talentport.users.dto.User;
import com.talentport.users.security.Credentials;
import com.talentport.users.utils.Constantes;

import oracle.jdbc.OracleTypes;

@Repository
public class UserDao implements IUserDao { 
	private static final Logger log = LoggerFactory.getLogger(UsersController.class);

	/* Create user 
	 * 
	 * 1) 	ID COGNITO
	 * 2) 	NAME
	 * 3) 	LASTNAME
	 * 4) 	PHONE
	 * 5)	NICKNAME
	 * 6)	EMAIL
	 * 7) 	BIRTHDATE
	 * 8)	IMAGE
	 * 9)	ROLES
	 * 
	 * 10) CURSOR
	 * */
	@Override
	public boolean Create(User user) throws Exception {
		Connection conn =null;
		CallableStatement ps =null;
		try {
			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSUARIOS(?, ?, ?, ?, ?, ?, ?, ?, '1', ?, ?, 'Insert', ?) }");

			/* IN */
			ps.setString(1, user.getIdUsername());
			ps.setString(2, user.getName());
			ps.setString(3, user.getLastName());
			ps.setString(4, user.getPhone());
			ps.setString(5, null);
			ps.setString(6, user.getEmail());
			ps.setString(7, user.getBirthdate());
			ps.setString(8, null);
			ps.setString(9, user.getRoles().get(0).toString());
			ps.setString(10, user.getPassword());
			/* OUT */
			ps.registerOutParameter(11, OracleTypes.CURSOR);
			ps.execute();
			conn.commit();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	/* Edit user 
	 * 
	 * 1) 	ID COGNITO
	 * 2) 	NAME
	 * 3) 	LASTNAME
	 * 4) 	PHONE
	 * 5)	NICKNAME
	 * 6)	EMAIL
	 * 7) 	BIRTHDATE
	 * 8)	IMAGE
	 * 
	 * 9) CURSOR
	 * */
	@Override
	public boolean Edit(User user, String id) throws Exception {
		Connection conn =null;
		CallableStatement ps =null;
		try {
			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSUARIOS(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, 'Update', ?) }");
			/* IN */
			ps.setString(1, id);
			ps.setString(2, user.getName());
			ps.setString(3, user.getLastName());
			ps.setString(4, user.getPhone());
			ps.setString(5, user.getNickname());
			ps.setString(6, user.getEmail());
			ps.setString(7, user.getBirthdate());
			ps.setString(8,null);	
			ps.setString(9, String.valueOf(user.getStatus()).toString());
			ps.setString(10, user.getRoles().get(0));
			ps.setString(11, null);
			/* OUT */
			ps.registerOutParameter(12, OracleTypes.CURSOR);


			ps.execute();
			conn.commit();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/* Edit user 
	 * 
	 * 1) 	ID COGNITO
	 * 2)	IMAGE
	 * 3) CURSOR
	 * */
	@Override
	public boolean EditImage(User user, String id) throws Exception {
		Connection conn =null;
		CallableStatement ps =null;
		try {
			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSERIMAGE(?,?,?) }");
			/* IN */
			log.info(id);
			ps.setString(1, id);
			ps.setString(2,user.getImages());	
			/* OUT */
			ps.registerOutParameter(3, OracleTypes.CURSOR);
			ps.execute();
			conn.commit();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/* Edit user 
	 * 
	 * 1) 	ID COGNITO
	 * 2) 	NAME
	 * 3) 	LASTNAME
	 * 4) 	PHONE
	 * 5)	NICKNAME
	 * 6) 	BIRTHDATE
	 * 7)	IMAGE
	 * 8)	STATUS
	 * 
	 * 8) CURSOR
	 * */
	@Override
	public boolean Delete(String id, int status) throws Exception {
		Connection conn =null;
		CallableStatement ps =null;
		try {
			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSUARIOS(?, ?, ?, ?, ?, ?, ?, ?, ?,?, NULL, 'Delete', ?) }");

			/* IN */
			ps.setString(1, id);
			ps.setString(2, null);
			ps.setString(3, null);
			ps.setString(4, null);
			ps.setString(5, null);
			ps.setString(6, null);
			ps.setString(7, null);
			ps.setString(8, null);
			ps.setString(9, (status == 1 || status == 2) ? "0" : "1");
			ps.setString(10, null);

			/* OUT */
			ps.registerOutParameter(11, OracleTypes.CURSOR);

			ps.execute();
			conn.commit();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/* ENABLE user 
	 * 
	 * 1) 	ID COGNITO
	 * 2) 	NAME
	 * 3) 	LASTNAME
	 * 4) 	PHONE
	 * 5)	NICKNAME
	 * 6) 	BIRTHDATE
	 * 7)	IMAGE
	 * 8)	STATUS
	 * 
	 * 8) CURSOR
	 * */
	@Override
	public boolean Enabled(String id, int status) throws Exception {
		Connection conn =null;
		CallableStatement ps =null;
		try {
			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSUARIOS(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL,?, 'UpdateInactive', ?) }");

			/* IN */
			ps.setString(1, id);
			ps.setString(2, null);
			ps.setString(3, null);
			ps.setString(4, null);
			ps.setString(5, null);
			ps.setString(6, null);
			ps.setString(7, null);
			ps.setString(8, null);
			ps.setString(9, status+"");
			ps.setString(10, null);

			/* OUT */
			ps.registerOutParameter(11, OracleTypes.CURSOR);

			ps.execute();
			conn.commit();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	/* Find uuid */
	@Override
	public User FindById(String uuid) throws Exception {
		User userResponse = new User();
		Connection conn =null;
		CallableStatement ps =null;
		try {

			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSUARIOS(?, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,NULL, 'SelectOne', ?) }");

			/* IN */
			ps.setString(1, uuid);

			/* OUT */
			ps.registerOutParameter(2, OracleTypes.CURSOR); 

			ps.execute();

			ResultSet resultSet = (ResultSet) ps.getObject(2);
			while (resultSet.next()) {
				//CognitoClient cognito = new CognitoClient();
				//User usrcongnito = cognito.GetInfoIdCognito(uuid);
				userResponse.setIdUsername(resultSet.getString("FCIDUSUARIOMODIFICO"));
				userResponse.setStatus(Integer.parseInt(resultSet.getString("FIACTIVO")));
				//userResponse.setEmail(resultSet.getString("ACTIVO"));
				userResponse.setName(resultSet.getString("FCNOMBRES"));
				userResponse.setLastName(resultSet.getString("FCAPELLIDOSPATERNO"));
				userResponse.setPhone(resultSet.getString("FCTELEFONO"));
				userResponse.setBirthdate(resultSet.getString("FDFECHACUMPLEANOS"));
				userResponse.setImages(resultSet.getString("FCIMPERFIL"));
				userResponse.setNickname(resultSet.getString("FCSOBRENOMBRE"));
				userResponse.setEmail(resultSet.getString("FCEMAIL"));

				List<String> rolList = new ArrayList<>();
				String rol = resultSet.getString("FCROLES"); 
				rolList.add(rol);
				userResponse.setRoles(rolList);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return userResponse;
	}

	/* Find email */
	@Override
	public User FindByEmail(String email) throws Exception {
		User userResponse = new User();
		Connection conn =null;
		CallableStatement ps =null;
		try {

			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSUARIOS(NULL, NULL, NULL, NULL, NULL, ?, NULL, NULL, NULL, NULL,NULL, 'SelectOneEmail',?) }");

			/* IN */
			ps.setString(1, email);

			/* OUT */
			ps.registerOutParameter(2, OracleTypes.CURSOR); 

			ps.execute();
			conn.commit();

			ResultSet resultSet = (ResultSet) ps.getObject(2);
			while (resultSet.next()) {

				userResponse.setIdUsername(resultSet.getString("FCIDUSUARIOMODIFICO"));
				userResponse.setStatus(Integer.parseInt(resultSet.getString("FIACTIVO")));
				//userResponse.setEmail(resultSet.getString("ACTIVO"));
				userResponse.setName(resultSet.getString("FCNOMBRES"));
				userResponse.setLastName(resultSet.getString("FCAPELLIDOSPATERNO"));
				userResponse.setPhone(resultSet.getString("FCTELEFONO"));
				userResponse.setBirthdate(resultSet.getString("FDFECHACUMPLEANOS"));
				userResponse.setImages(resultSet.getString("FCIMPERFIL"));
				userResponse.setNickname(resultSet.getString("FCSOBRENOMBRE"));
				userResponse.setEmail(resultSet.getString("FCEMAIL"));
				userResponse.setPassword(resultSet.getString("FCPASSWORD"));
				List<String> rolList = new ArrayList<>();
				String rol = resultSet.getString("FCROLES"); 
				rolList.add(rol);
				userResponse.setRoles(rolList);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return userResponse;
	}

	/* Find admin*/
	@Override
	public List<User> FindByAmin() throws Exception {
		List<User> userList = new ArrayList<User>();
		Connection conn =null;
		CallableStatement ps =null;
		try {

			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSUARIOS(NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,NULL, 'SelectAdmin',?) }");

			/* IN */

			/* OUT */
			ps.registerOutParameter(1, OracleTypes.CURSOR); 

			ps.execute();
			ResultSet resultSet = (ResultSet) ps.getObject(1);
			while (resultSet.next()) {


				User userResponse = new User();
				userResponse.setIdUsername(resultSet.getString("FCIDUSUARIOMODIFICO"));
				userResponse.setStatus(Integer.parseInt(resultSet.getString("FIACTIVO")));
				//userResponse.setEmail(resultSet.getString("ACTIVO"));
				userResponse.setName(resultSet.getString("FCNOMBRES"));
				userResponse.setLastName(resultSet.getString("FCAPELLIDOSPATERNO"));
				userResponse.setPhone(resultSet.getString("FCTELEFONO"));
				userResponse.setBirthdate(resultSet.getString("FDFECHACUMPLEANOS"));
				userResponse.setImages(resultSet.getString("FCIMPERFIL"));
				userResponse.setNickname(resultSet.getString("FCSOBRENOMBRE"));
				userResponse.setEmail(resultSet.getString("FCEMAIL"));
				List<String> rolList = new ArrayList<>();
				String rol = resultSet.getString("FCROLES"); 
				rolList.add(rol);
				userResponse.setRoles(rolList);

				userList.add(userResponse);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return userList;
	}
	/* Find admin*/
	@Override
	public List<User> FindByUser() throws Exception {
		List<User> userList = new ArrayList<User>();
		Connection conn =null;
		CallableStatement ps =null;
		try {

			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSUARIOS(NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,NULL, 'Select',?) }");

			/* IN */

			/* OUT */
			ps.registerOutParameter(1, OracleTypes.CURSOR); 

			ps.execute();
			ResultSet resultSet = (ResultSet) ps.getObject(1);
			while (resultSet.next()) {


				User userResponse = new User();
				userResponse.setIdUsername(resultSet.getString("FCIDUSUARIOMODIFICO"));
				userResponse.setStatus(Integer.parseInt(resultSet.getString("FIACTIVO")));
				//userResponse.setEmail(resultSet.getString("ACTIVO"));
				userResponse.setName(resultSet.getString("FCNOMBRES"));
				userResponse.setLastName(resultSet.getString("FCAPELLIDOSPATERNO"));
				userResponse.setPhone(resultSet.getString("FCTELEFONO"));
				userResponse.setBirthdate(resultSet.getString("FDFECHACUMPLEANOS"));
				userResponse.setImages(resultSet.getString("FCIMPERFIL"));
				userResponse.setNickname(resultSet.getString("FCSOBRENOMBRE"));
				userResponse.setEmail(resultSet.getString("FCEMAIL"));
				List<String> rolList = new ArrayList<>();
				String rol = resultSet.getString("FCROLES"); 
				rolList.add(rol);
				userResponse.setRoles(rolList);

				userList.add(userResponse);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return userList;
	}


	/* Create user 
	 * 
	 * 1) 	ID COGNITO
	 * 2) 	NAME
	 * 3) 	LASTNAME
	 * 4) 	PHONE
	 * 5)	NICKNAME
	 * 6) 	BIRTHDATE
	 * 7)	IMAGE
	 * 8)	ROLES
	 * 
	 * 8) CURSOR
	 * */
	@Override
	public boolean CreateAdmin(User user) throws Exception {
		Connection conn =null;
		CallableStatement ps =null;
		try {
			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSUARIOS(?, ?, ?, ?, ?, ?, ?, ?, '1', ?,?, 'Insert', ?) }");

			/* IN */
			ps.setString(1, user.getIdUsername());
			ps.setString(2, user.getName());
			ps.setString(3, user.getLastName());
			ps.setString(4, user.getPhone());
			ps.setString(5, user.getNickname());
			ps.setString(6, user.getEmail());
			ps.setString(7, user.getBirthdate());
			ps.setString(8, null);
			ps.setString(9, user.getRoles().get(0).toString());
			ps.setString(10, user.getPassword());
			/* OUT */
			ps.registerOutParameter(11, OracleTypes.CURSOR);

			ps.execute();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/* Code user 
	 * 
	 * 1) 	ID COGNITO
	 * 2) 	CODE
	 * 
	 * 3) CURSOR
	 * */
	@Override
	public String GetCode(User user) throws Exception {
		String codeResponse = "";
		Connection conn =null;
		CallableStatement ps =null;
		try {
			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPGETCODE(?, ?, ?) }");

			/* IN */
			ps.setString(1, user.getIdUsername());
			ps.setString(2, user.getCodes());

			/* OUT */
			ps.registerOutParameter(3, OracleTypes.CURSOR);

			ps.execute();
			ResultSet resultSet = (ResultSet) ps.getObject(3);
			while (resultSet.next()) {
				codeResponse = resultSet.getString("FCCODE");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return codeResponse;
	}

	/* Sahred code user 
	 * 
	 * 1) 	ID COGNITO
	 * 2) 	CODE
	 * 
	 * 3) CURSOR
	 * */
	@Override

	public int SharedCode(User user, User users) throws Exception {
		int response = -1;
		Connection conn =null;
		CallableStatement ps =null;

		try {
			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPSHAREDCODE(?, ?, ?) }");

			/* IN */
			ps.setString(1, user.getIdUsername());
			ps.setString(2, users.getCodes());

			/* OUT */
			ps.registerOutParameter(3, OracleTypes.CURSOR);
			ps.execute();

			ResultSet resultSet = (ResultSet) ps.getObject(3);

			while (resultSet.next()) { 
				if(resultSet.getString(1).equals("0") || resultSet.getString(1).equals("1"))
				{
					response = Integer.parseInt(resultSet.getString(1));
				}
				else
				{
					response = 2;
				}
			}

			conn.commit();
			conn.close();

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	
	@Override
	public boolean EditPassword(String pass, String id) throws Exception {
		Connection conn =null;
		CallableStatement ps =null;
		try {
			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSUARIOS(?, ?, ?, ?, ?, ?, ?, ?, '1', ?,?, 'UpdatePassword', ?) }");
			/* IN */
			ps.setString(1, id);
			ps.setString(2, null);
			ps.setString(3, null);
			ps.setString(4, null);
			ps.setString(5, null);
			ps.setString(6, null);
			ps.setString(7, null);
			ps.setString(8,null);	
			ps.setString(9, null);
			ps.setString(10,  pass);
			/* OUT */
			ps.registerOutParameter(11, OracleTypes.CURSOR);
			ps.execute();
			conn.commit();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public User findByFacebookId(String facebookId) throws Exception {

		User userResponse = new User();
		Connection conn =null;
		CallableStatement ps =null;
		try {

			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPFINDUSERBYFACEBOOKID(?, ?) }");
			//ps = conn.prepareCall("{ CALL TALENTPORTTST.PAUSERCURD.SPFINDUSERBYFACEBOOKID(?, ?) }");

			/* IN */
			ps.setString(1, facebookId);

			/* OUT */
			ps.registerOutParameter(2, OracleTypes.CURSOR); 

			ps.execute();
			conn.commit();

			ResultSet resultSet = (ResultSet) ps.getObject(2);
			while (resultSet.next()) {

				userResponse.setIdUsername(resultSet.getString("FCIDUSUARIOMODIFICO"));
				userResponse.setStatus(Integer.parseInt(resultSet.getString("FIACTIVO")));
				//userResponse.setEmail(resultSet.getString("ACTIVO"));
				userResponse.setName(resultSet.getString("FCNOMBRES"));
				userResponse.setLastName(resultSet.getString("FCAPELLIDOSPATERNO"));
				userResponse.setPhone(resultSet.getString("FCTELEFONO"));
				userResponse.setBirthdate(resultSet.getString("FDFECHACUMPLEANOS"));
				userResponse.setNickname(resultSet.getString("FCSOBRENOMBRE"));
				userResponse.setEmail(resultSet.getString("FCEMAIL"));
				List<String> rolList = new ArrayList<>();
				String rol = resultSet.getString("FCROLES"); 
				rolList.add(rol);
				userResponse.setRoles(rolList);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return userResponse;
	}

	@Override
	public boolean createFB(User user) throws Exception {
		Connection conn =null;
		CallableStatement ps =null;
		try {
			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSUARIOFB(?, ?, ?, ?, ?, ?, ?, ?, '1', ?, ?, ?) }");
			//ps = conn.prepareCall("{ CALL TALENTPORTTST.PAUSERCURD.SPUSUARIOFB(?, ?, ?, ?, ?, ?, ?, ?, '1', ?, ?, ?) }");

			/* IN */
			ps.setString(1, user.getIdUsername());
			ps.setString(2, user.getName());
			ps.setString(3, user.getLastName());
			ps.setString(4, user.getPhone());
			ps.setString(5, null);
			ps.setString(6, user.getEmail());
			ps.setString(7, user.getBirthdate());
			ps.setString(8, null);
			ps.setString(9, user.getRoles().get(0).toString());
			ps.setString(10, user.getFacebookId());
			/* OUT */
			ps.registerOutParameter(11, OracleTypes.CURSOR);
			ps.execute();
			conn.commit();

			System.out.println("SE registro en BD");
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	 static {
	        try {
	            if (Credentials.  DB_CONFIG == null) {
	                throw new com.talentport.users.utils.Exceptions.TradeException(
	                        Constantes.FAILED_GET_DB_CONFIG,
	                        Constantes.CODIGO_FAILED_GET_DB_CONFIG
	                );
	            }
	          OracleDBPool.initSingletonConnectionCredentials("jdbc:oracle:thin:@TALENTPORT-DEV-ORACLE.CQOKQWDUHZKE.US-EAST-1.RDS.AMAZONAWS.COM:1521:TALENTDV", "TALENTPORTDEVPCK",
	          "GBtOTjN2AO");
//	            OracleDBPool.initSingletonConnectionCredentials(
//	                    Credentials.DB_CONFIG.getUrl(),
//	                    Credentials.DB_CONFIG.getUser(),
//	                    Credentials.DB_CONFIG.getPass()
//	            );
	        } catch (Exception e) {
	        	log.error(e.getMessage());
	        }
	    }


	@Override
	public User findByAppleId(String appleId) throws Exception {

		User userResponse = new User();
		Connection conn =null;
		CallableStatement ps =null;
		try {

			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPFINDUSERBYAPPLEID(?, ?) }");
			//ps = conn.prepareCall("{ CALL TALENTPORTTST.PAUSERCURD.SPFINDUSERBYAPPLEID(?, ?) }");

			/* IN */
			ps.setString(1, appleId);

			/* OUT */
			ps.registerOutParameter(2, OracleTypes.CURSOR); 

			ps.execute();
			conn.commit();

			ResultSet resultSet = (ResultSet) ps.getObject(2);
			while (resultSet.next()) {

				userResponse.setIdUsername(resultSet.getString("FCIDUSUARIOMODIFICO"));
				userResponse.setStatus(Integer.parseInt(resultSet.getString("FIACTIVO")));
				//userResponse.setEmail(resultSet.getString("ACTIVO"));
				userResponse.setName(resultSet.getString("FCNOMBRES"));
				userResponse.setLastName(resultSet.getString("FCAPELLIDOSPATERNO"));
				userResponse.setPhone(resultSet.getString("FCTELEFONO"));
				userResponse.setBirthdate(resultSet.getString("FDFECHACUMPLEANOS"));
				userResponse.setNickname(resultSet.getString("FCSOBRENOMBRE"));
				userResponse.setEmail(resultSet.getString("FCEMAIL"));
				List<String> rolList = new ArrayList<>();
				String rol = resultSet.getString("FCROLES"); 
				rolList.add(rol);
				userResponse.setRoles(rolList);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return userResponse;
	}

	@Override
	public boolean createApple(User user) throws Exception {
		Connection conn =null;
		CallableStatement ps =null;
		try {
			conn = OracleDBPool.getSingletonConnectionJDBC();
			ps = conn.prepareCall("{ CALL TALENTPORT.PAUSERCURD.SPUSUARIOAPPLE(?, ?, ?, ?, ?, ?, ?, ?, '1', ?, ?, ?) }");
			//ps = conn.prepareCall("{ CALL TALENTPORTTST.PAUSERCURD.SPUSUARIOAPPLE(?, ?, ?, ?, ?, ?, ?, ?, '1', ?, ?, ?) }");

			/* IN */
			ps.setString(1, user.getIdUsername());
			ps.setString(2, user.getName());
			ps.setString(3, user.getLastName());
			ps.setString(4, user.getPhone());
			ps.setString(5, null);
			ps.setString(6, user.getEmail());
			ps.setString(7, user.getBirthdate());
			ps.setString(8, null);
			ps.setString(9, user.getRoles().get(0).toString());
			ps.setString(10, user.getFacebookId());
			/* OUT */
			ps.registerOutParameter(11, OracleTypes.CURSOR);
			ps.execute();
			conn.commit();

			System.out.println("SE registro en BD");
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally{
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
