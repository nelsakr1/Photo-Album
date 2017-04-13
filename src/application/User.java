package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import java.io.*;
import java.util.ArrayList;

/**
 * This class is responsible for the user object and can hold the names and albums of a specific user.
 * 
 * @author Aaron Hu
 * @author Nasrelden Elsakr
 * 
 */
public class User implements Serializable{

	// various user properties
	String name;
	ListView<Album> albumList = new ListView<Album>();
	ObservableList<Album> userAlbums = FXCollections.observableArrayList();
	ArrayList<Album> albums = new ArrayList<Album>();
	
	/**
	 * Constructor that creates an instance of a new User.
	 *
	 * @param name  			name of the User
	 * @param listView 			ListView of Albums
	 * @param observableList 	ObservableList of Albums
	 */	
	public User (String name, ListView<Album> listView, ObservableList<Album> observableList){
		this.name = name;
		this.albumList = listView;
		this.userAlbums = observableList;
	}
	
	/**
	 * No-arg constructor that creates an instance of a new User.
	 */
	public User(){}

	public String toString(){
		return this.name;
	}
	
}
