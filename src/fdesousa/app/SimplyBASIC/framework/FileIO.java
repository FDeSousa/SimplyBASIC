package fdesousa.app.SimplyBASIC.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.res.AssetManager;
import android.os.Environment;

/**
 * Simplistic handler class for controlling the file-accessing mechanism.
 * @author Filipe De Sousa
 * @version 0.1
 */
public class FileIO {
	AssetManager assets;
	String externalStoragePath;

	/**
	 * Constructor requires an AssetManager instance for access to the app assets
	 * and a String containing folder name where the app will read from/write to for
	 * user's own files
	 * @param assets - an instance of AssetManager to use for reading assets, can be null
	 * @param folderName - the folder to use for user's own written programs
	 */
	public FileIO(AssetManager assets, String folderName) {
		this.assets = assets;
		this.externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() 
			+ File.separator + folderName + File.separator;
	}

	/**
	 * Method for retrieving an InputStream to read in a game asset from the APK file.
	 * @param fileName - the name of the file to input from
	 * @return InputStream for the file defined with fileName
	 * @throws IOException - thrown if any error occurs
	 */
	public InputStream readAsset(String fileName) throws IOException {
		return assets.open(fileName);
	}

	/**
	 * Method for retrieving an InputStream to read in a file from the SD card.
	 * @param fileName - the name of the file to input from
	 * @return InputStream for the file defined with fileName
	 * @throws IOException - thrown if any error occurs
	 */
	public InputStream readFile(String fileName) throws IOException {
		return new FileInputStream(externalStoragePath + fileName);
	}

	/**
	 * Method for retrieving an OutputStream to write to a file on the SD card.
	 * @param fileName - the name of the file to output to
	 * @return OutputStream for the file defined with fileName
	 * @throws IOException - thrown if any error occurs
	 */
	public OutputStream writeFile(String fileName) throws IOException {
		return new FileOutputStream(externalStoragePath + fileName);
	}
}
