package application;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;

/**
 * This class is responsible for the album object and can hold the name, list of photos, and dates of the photos.
 * 
 * @author Aaron Hu
 * @author Nasrelden Elsakr
 * 
 */
public class Album {
	
	// various album properties
	String name;
	ListView<Photo> photoList;
	ObservableList<Photo> userPhotos;
	String oldestDate;
	String dateRange;
	
	/**
	 * Constructor that creates an instance of a new Album.
	 *
	 * @param name  			name of the album
	 * @param listView 			list of photos in a ListView.
	 * @param observableList 	list of photos in an ObservableList.
	 * @param oldestDate 		date of the oldest photo in the album
	 * @param dateRange 		date range of the oldest and newest photos in the album
	 */
	Album(String name, ListView<Photo> listView, ObservableList<Photo> observableList, String oldestDate, String dateRange){
		this.name = name;
		this.photoList = listView;
		this.userPhotos = observableList;
		this.oldestDate = oldestDate;
		this.dateRange = dateRange;
	}
}