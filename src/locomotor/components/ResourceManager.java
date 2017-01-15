package locomotor.components;

import java.io.File;

public class ResourceManager {
	protected static ResourceManager _instance = null;
	protected String _baseURL;

	protected ResourceManager() {
		_baseURL = "resources/";
	}

	public static synchronized ResourceManager getInstance() {
		if(_instance == null) {
			_instance = new ResourceManager();
		}

		return _instance;
	}

	/**
	* Returns the last modified timestamp of the file. Used as a version number.
	* @param resource The pathname of the resource needed (ex. "images/cat.png")
	* @return The version number if the file exists or 0 otherwise.
	*/
	public long getVersion(String resource) {
		File file = new File(_baseURL + resource);

		// if the file does not exist, abort
		if(!file.exists() || file.isDirectory()) {
			return 0;
		}
		
		return file.lastModified();
	}

	/**
	* Returns the file requested if it exists.
	* @param resource The pathname of the resource needed (ex. "images/cat.png")
	* @return The file if it is found, null otherwise.
	*/
	public File getResource(String resource) {
		File file = new File(_baseURL + resource);

		if(!file.exists() || file.isDirectory()) {
			return null;
		}

		return file;
	}

	/**
	* Check wether the resource exists or not.
	* @param resource The pathname of the resource needed (ex. "images/cat.png")
	* @return true if the file exists, false otherwise
	*/
	public boolean exists(String resource) {
		File file = new File(_baseURL + resource);

		return file.exists() && !file.isDirectory();
	}
}