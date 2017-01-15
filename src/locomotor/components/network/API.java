package locomotor.components.network;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import java.io.File;
import java.lang.InterruptedException;
import java.lang.Thread;

import locomotor.components.Pair;
import locomotor.components.logging.ErrorHandler;
import locomotor.components.logging.Logging;
import locomotor.core.DBH;
import locomotor.core.jwt.JWTH;
import locomotor.core.CoreResourceManager;

/**
 * Network interface shown to the client.
 */
public class API {

	/**
	* Adds all the endpoints defined in this method to a NetworkHandler.
	* @param nh The NetworkHandler to which the endpoint will be attached
	*/
	public static void createHooks(NetworkHandler nh) {
		nh.createEndpoint("/api/user/auth", new IEndpointHandler() {
			public void handle(NetworkData data, NetworkResponseFactory response) {
				
				if(!data.isValid()) {
					response.getJsonContext().failure(NetworkResponse.ErrorCode.BAD_REQUEST, 
						"The request must be in POST format to be read by the server");
					return;
				}			
				
				// all parameter, ok
				if((data.isDefined("username") && data.isDefined("password")) || (data.isDefined("token"))) {
					
					JWTH jwt = JWTH.getInstance();
					if (data.isDefined("username") && data.isDefined("password")) {
						
						// check user exist and good password
						String username = data.getAsString("username");
						String password = data.getAsString("password");
						Pair<String,Boolean> claims = DBH.getInstance().authUser(username, password);

						// check error
						if (claims == null) {
							Pair<String, Logging> log = ErrorHandler.getInstance().pop();
							response.getJsonContext().failure(NetworkResponse.ErrorCode.UNAUTHORIZED_ACCESS, 
								log.getRight().toString());
							return;
						}
						String longToken = jwt.createLongToken(claims.getLeft(), claims.getRight());
						String shortToken = jwt.createShortToken(claims.getLeft(), claims.getRight());

						response.getJsonContext().success(Json.object()
							.add("short-term-token", shortToken)
							.add("long-term-token", longToken));
					}
					else {
						
						// auth with token
						String longToken = data.getAsString("token");
						Pair<String,Boolean> claims = jwt.checkToken(longToken);
						
						// check error
						if (claims == null) {
							Pair<String, Logging> log = ErrorHandler.getInstance().pop();
							response.getJsonContext().failure(NetworkResponse.ErrorCode.UNAUTHORIZED_ACCESS, 
								log.getRight().toString());
							return;
						}

						// check if the user still exist in the database
						// before create the token
						if(!DBH.getInstance().usernameAlreadyTaken(claims.getLeft())) {
							response.getJsonContext().failure(NetworkResponse.ErrorCode.UNAUTHORIZED_ACCESS, 
								"The token is no longer valid");
							return;
						}

						String shortToken = jwt.createShortToken(claims.getLeft(), claims.getRight());
						response.getJsonContext().success(Json.object()
							.add("short-term-token", shortToken));
						
					}

				}
				else { // error

					String errorMessage = "";
					if((!data.isDefined("username") && data.isDefined("password"))
						|| (data.isDefined("username") && !data.isDefined("password"))
						|| (!data.isDefined("username") && !data.isDefined("password"))) {
						// missing username or password
						errorMessage = "At least, one of the following parameter is missing: `username`,"
							+ " `password`. Both of them are mandatory for this request.";
					}
					else if (!data.isDefined("token")) {
						// missing token
						errorMessage = "The following parameter is missing: `token`."
							+ " It is mandatory for this request.";
					}
					response.getJsonContext().failure(NetworkResponse.ErrorCode.BAD_REQUEST, errorMessage);
				}
			}
		});

		nh.createEndpoint("/api/user/register", new IEndpointHandler() {
			public void handle(NetworkData data, NetworkResponseFactory response) {
				
				if(!data.isValid()) {
					response.getJsonContext().failure(NetworkResponse.ErrorCode.BAD_REQUEST,
						"La requête doit être au format POST pour être lue par le serveur");
					return;
				}			
				
				// all parameter, ok
				if((data.isDefined("username") && data.isDefined("password"))) {
					
					JWTH jwt = JWTH.getInstance();						
					// check user exist
					String username = data.getAsString("username");
					String password = data.getAsString("password");
					Pair<String,Boolean> claims = DBH.getInstance().registerUser(username, password);
					
					// check error
					if (claims == null) {
						Pair<String, Logging> log = ErrorHandler.getInstance().pop();
						response.getJsonContext().failure(NetworkResponse.ErrorCode.UNAUTHORIZED_ACCESS,
							log.getRight().toString());
						return;
					}
					
					String shortToken = jwt.createShortToken(claims.getLeft(), claims.getRight());
					String longToken = jwt.createLongToken(claims.getLeft(), claims.getRight());

					response.getJsonContext().success(Json.object()
						.add("short-term-token", shortToken)
						.add("long-term-token", longToken));

				}
				else { // error

					String errorMessage = "At least, one of the following parameter is missing:"
						+ "`username`, `password`. Both of them are mandatory for this request.";
					response.getJsonContext().failure(NetworkResponse.ErrorCode.BAD_REQUEST, errorMessage);
				}
			}
		});

		nh.createEndpoint("/api/img/version", new IEndpointHandler() {
			public void handle(NetworkData data, NetworkResponseFactory response) {			
				if(!data.isValid()) {
					response.getJsonContext().failure(NetworkResponse.ErrorCode.BAD_REQUEST,
						"La requête doit être au format POST pour être lue par le serveur");
					return;
				}			
				
				// all parameter, ok
				if(data.isDefined("id")) {
					String id = data.getAsString("id");
					CoreResourceManager crm = CoreResourceManager.getInstance();
					if(!crm.exists(id)) {
						System.out.println("le fichier demandé n'existe pas");
						response.getJsonContext().failure(NetworkResponse.ErrorCode.NOT_FOUND, "The image requested does not exist");
					}
					else {
						response.getJsonContext().success(Json.object().add("version", crm.getVersion(id)));
					}
				}
				else { // error
					String errorMessage = "The parameter `id` is missing to complete this request.";
					response.getJsonContext().failure(NetworkResponse.ErrorCode.BAD_REQUEST, errorMessage);
				}
			}
		});

		nh.createEndpoint("/img/get", new IEndpointHandler() {
			public void handle(NetworkData data, NetworkResponseFactory response) {
				if(data.isValid()) {
					if(!data.isDefined("name")) {
						response.getJsonContext().failure(NetworkResponse.ErrorCode.BAD_REQUEST, 
							"Le paramètre `name` n'a pas été trouvé. Il est obligatoire pour cette requête");
						return;
					}
				}
				else {
					response.getJsonContext().failure(NetworkResponse.ErrorCode.BAD_REQUEST, 
						"La requête doit être au format POST pour être lue par le serveur");
					return;
				}

				File file = new File("resources/core/images/" + data.getAsString("name"));
				System.out.print("Fichier demandé : " + data.getAsString("name"));

				if(file.exists() && !file.isDirectory()) {
					System.out.println(", le fichier existe");
					response.getBinaryContext().success(file);
				}
				else {
					System.out.println(", le fichier n'existe pas");
					response.getJsonContext().failure(NetworkResponse.ErrorCode.NOT_FOUND, 
						"L'image demandée n'a pas pu être trouvée : `" 
						+ "resources/core/images/" + data.getAsString("name") + "`");
				}
			}
		});
	}
}