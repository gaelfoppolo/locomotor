package locomotor.components.network;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Thread;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import locomotor.components.MutableInteger;

import org.apache.tika.Tika;

/**
 * This class format and send the response to the client.
 */
public abstract class NetworkResponse {	

	/**
	 * The exchange.
	 */
	HttpExchange _exchange;

	/**
	 * Contains all the usefull error codes that the server can return in case of error.
	 */
	public enum ErrorCode {
		
		/**
		 * The server will not process the request due to a malformed request (parameters missing, ...).
		 */
		BAD_REQUEST(400),

		/**
		 * The user need to authenticate.
		 */
		UNAUTHORIZED_ACCESS(401),
		
		/**
		 * The user can not access this part.
		 */
		FORBIDDEN_ACCESS(403),
		
		/**
		 * The data was not found.
		 */
		NOT_FOUND(404),

		/**
		 * The request must specify its length.
		 */
		LENGTH_REQUIRED(411);

		/**
		 * The identifier.
		 */
		private final int _id;

		/**
		 * Constructs the object.
		 *
		 * @param      id    The identifier
		 */
		ErrorCode(int id) {
			_id = id;
		}

		/**
		 * Gets the value.
		 *
		 * @return     The value.
		 */
		public int getValue() {
			return _id;
		}
	}

	/**
	 * Constructs the object.
	 *
	 * @param      exchange  The exchange
	 */
	public NetworkResponse(HttpExchange exchange) {
		_exchange = exchange;
	}

	/**
	 * Send the client an error message. The message is formatted in JSON.
	 * @param httpError The HTTP error code to use
	 * @param message The message to send in the JSON object, describing the error to the user
	 * @param code The code to send alongside the message for the client to handle
	 */
	public void failure(ErrorCode httpError, String message, API.ErrorCode code) {
		JsonObject obj = Json.object().add("success", "false").add("message", message).add("code", code.getValue());
		MutableInteger length = new MutableInteger();
		ByteArrayOutputStream out = toByteArrayOutputStream(obj, length);
		sendAnswer(out, "application/json", httpError.getValue(), length.intValue());
	}

	/**
	 * Convert a JSON object to an ByteArrayOutputStream.
	 * @param obj The object to be converted
	 * @return A newly created ByteArrayOutputStream
	 */
	protected ByteArrayOutputStream toByteArrayOutputStream(JsonObject obj) {
		return toByteArrayOutputStream(obj, null);
	}

	/**
	 * Convert a JSON object to an ByteArrayOutputStream and stores the length of the generated object.
	 * @param obj The object to be converted
	 * @param length The object in which to store the length of the ByteArrayOutputStream
	 * @return A newly created ByteArrayOutputStream
	 */
	protected ByteArrayOutputStream toByteArrayOutputStream(JsonObject obj, MutableInteger length) {
		String json = obj.toString();
		return toByteArrayOutputStream(json, length);
	}

	/**
	 * Convert a String object to an ByteArrayOutputStream.
	 * @param string The string to be converted
	 * @return A newly created ByteArrayOutputStream
	 */
	protected ByteArrayOutputStream toByteArrayOutputStream(String string) {
		return toByteArrayOutputStream(string, null);
	}

	/**
	 * Convert a String object to an ByteArrayOutputStream.
	 * @param string The string to be converted
	 * @param length The object in which to store the length of the ByteArrayOutputStream
	 * @return A newly created ByteArrayOutputStream
	 */
	protected ByteArrayOutputStream toByteArrayOutputStream(String string, MutableInteger length) {
		byte[] data = string.getBytes(StandardCharsets.UTF_8);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
		baos.write(data, 0, data.length);
		if(length != null) {
			length.set(data.length);
		}
		return baos;
	}


	/**
	 * Send the response to the client.
	 * @param out The content of the message to send
	 * @param contentType The MIME type of the content sent
	 * @param code The HTTP status code to send
	 * @param length The length of the message sent. If unknown, can be set to 0
	 */
	protected void sendAnswer(ByteArrayOutputStream out, String contentType, int code, int length) {
		Headers headers = _exchange.getResponseHeaders();
		if(contentType != null) {
			headers.set("Content-Type", contentType);
		}

		try {
			_exchange.sendResponseHeaders(code, length);
			OutputStream bodyResponse = _exchange.getResponseBody();
			out.writeTo(bodyResponse);
			bodyResponse.close();
		}
		catch(IOException exception) {
			System.out.println("Error : unable to send the response to the user");
			System.out.println(exception);
			System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
		}
	}

	/**
	* Send a file as a response to the client.
	* @param out The file to be sent
	* @param code The HTTP status code to send
	*/
	protected void sendAnswer(File out, int code) {
		Headers headers = _exchange.getResponseHeaders();
		String contentType = "";

		try {
			Tika tika = new Tika();
			contentType = tika.detect(out);
			headers.set("Content-Type", contentType);
		}
		catch(Exception exception) {
			headers.set("Content-Type", "application/octet-stream");
		}

		try {
			_exchange.sendResponseHeaders(code, out.length());
			OutputStream bodyResponse = _exchange.getResponseBody();
			Files.copy(out.toPath(), bodyResponse);
			bodyResponse.close();
		}
		catch(IOException exception) {
			System.out.println("Error : unable to send the response to the user");
		}
	}
}