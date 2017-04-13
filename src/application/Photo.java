package application;

import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Calendar;
import java.io.*;

/**
 * This class is responsible for the photo object and can hold the image, name, caption, and tags of the picture.
 * 
 * @author Aaron Hu
 * @author Nasrelden Elsakr
 * 
 */
public class Photo implements Serializable{
	
	// file location
	File file;
	
	// various image properties
	ImageView image;
	long dateModified;
	String name;
	String caption;
	ArrayList<String> tags;
	ArrayList<String> types;
	
	// keeps track of position in array
	int selectedIndex;
	
	/**
	 * Constructor that creates a an instance of a new Photo.
	 *
	 * @param file 			file location of the Photo
	 * @param imageVIew 	image
	 * @param dateModified 	date the Photo was taken
	 * @param name 			name of the Photo
	 * @param caption 		caption of the Photo
	 * @param tags 			tags of photo
	 * @param types 		types of Photo
	 * @param selectedIndex index number
	 */
	Photo(File file, ImageView imageView, long dateModified, String name, String caption, ArrayList<String> tags, ArrayList<String> types, int selectedIndex){
		this.file = file;
		this.image = imageView;
		this.dateModified = dateModified;
		this.name = name;
		this.caption = caption;
		this.tags = tags;
		this.types = types;
		this.selectedIndex = selectedIndex;
	}
}
