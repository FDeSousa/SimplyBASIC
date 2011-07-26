package fdesousa.app.SimplyBASIC.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

/**
 * Simplistic handler class for controlling the file-accessing mechanism.
 * @author Filipe De Sousa
 * @version 0.1
 */
public class FileIO {
	//	For convenience, hold the file extension to use for BASIC
	/**
	 * The file extension for BASIC program files
	 */
	public static String BASIC_FILE_EXTENSION = ".bas";
	
	String externalStoragePath;	//	Path of the folder we want to use in ext storage
	File dir;	//	File instance of the folder we'll be reading from/writing to
	FilenameFilter fileFilter;	//	Instance of the FilenameFilter we'll be using

	/**
	 * Constructor requires a String containing folder name where the app will
	 * read from/write to for user's own BASIC program files
	 * @param foldername - the folder to use for user's own written programs
	 */
	public FileIO(String foldername) {
		//	Ask the environment to construct the path to the external storage directory
		this.externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + foldername + File.separator;
		//	Create a new File instance with the created external storage directory
		this.dir = new File(externalStoragePath);
		//	Create a FilenameFilter that only accepts files with names ending ".bas" 
		this.fileFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return (filename.endsWith(FileIO.BASIC_FILE_EXTENSION));
			}
		};
	}

	/**
	 * Method for retrieving an InputStream to read in a file from the SD card.
	 * @param filename - the name of the file to input from
	 * @return InputStream for the file defined with fileName
	 * @throws IOException - thrown if any error occurs
	 */
	public InputStream readFile(String filename) throws IOException {
		return new FileInputStream(externalStoragePath + filename + BASIC_FILE_EXTENSION);
	}

	/**
	 * Method for retrieving an OutputStream to write to a file on the SD card.
	 * @param filename - the name of the file to output to
	 * @return OutputStream for the file defined with fileName
	 * @throws IOException - thrown if any error occurs
	 */
	public OutputStream writeFile(String filename) throws IOException {
		return new FileOutputStream(externalStoragePath + filename + BASIC_FILE_EXTENSION);
	}
	
	/**
	 * Convenience method to handle the deletion of a file
	 * @param filename the name of the file we want deleted
	 * @throws IOException if something should fail, we'll throw this
	 */
	public void deleteFile(String filename) throws IOException {
		new File(externalStoragePath + filename + BASIC_FILE_EXTENSION).delete();
	}

	/**
	 * Convenience method to list the number and names of all the relevant files
	 * contained in our program's folder in the device's external storage
	 * @return String containing the number of files and name of each file, one per line
	 * @throws IOException - if the file is not readable will throw an IOException
	 */
	public String folderListing() throws IOException {
		StringBuffer sb = new StringBuffer();
		File[] dirList = dir.listFiles(fileFilter);

		if (dirList != null){
			sb.append("THERE ARE ");
			sb.append(dirList.length);
			sb.append(" FILES IN PROGRAMS DIRECTORY\n\t");

			for (int i = 0; i < dirList.length; i++){
				sb.append(dirList[i].getName().toUpperCase());
				sb.append("\n\t");
			}
		}
		sb.append("\nEND OF DIRECTORY LISTING\n");
		return sb.toString();
	}
}
