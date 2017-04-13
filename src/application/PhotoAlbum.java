package application;
	
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import javafx.scene.input.MouseEvent;

import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import javafx.scene.image.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

/**
 * This class is responsible for the PhotoAlbum object and can store the various other objects in their correct locations.
 * 
 * @author Aaron Hu
 * @author Nasrelden Elsakr
 * 
 */
public class PhotoAlbum extends Application implements Serializable{
	
	// TODO fix serializable implementation
	// date formats
	final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	final DateFormat dFNT = new SimpleDateFormat("MM/dd/yyyy");
	
	// storage locations
	public static final String storeDir = "Photos94";
	public static final String storeFile = "users.dat";
	
	// stored lists
	ListView<User> list = new ListView<User>();
	ObservableList<User> users = FXCollections.observableArrayList();
	
	// override method
	public void start(Stage primaryStage) {
		try {
			BorderPane bpLogin = loginBP(primaryStage);
			
			Scene scene = new Scene(bpLogin, 700, 700);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setResizable(false);
			primaryStage.setTitle("Photo Album");
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}	

	/**
	 * This method is responsible for the login page that the user is first greeted with.
	 *
	 * @param firstStage the login pane schematics
	 * @return the login border pane
	 */
	private BorderPane loginBP(Stage firstStage){
		BorderPane bpLogin = new BorderPane();

		// top area with "Login" text
		StackPane spTop = new StackPane();
		Text login = new Text("Photo Album v.1.0.0");
		login.setFont(Font.font(40));
		StackPane.setAlignment(login, Pos.CENTER);
		spTop.setPadding(new Insets(100, 20, 20, 20));
		spTop.getChildren().add(login);
		bpLogin.setTop(spTop);
		
		// area with "Username" label
		StackPane spLeft = new StackPane();
		Text username = new Text("Username:");
		username.setFont(Font.font(18));
		StackPane.setAlignment(username, Pos.CENTER);
		spLeft.setPadding(new Insets(20, 20, 20, 160));
		spLeft.getChildren().add(username);
		bpLogin.setLeft(spLeft);
		
		// text field to enter username
		StackPane spRight = new StackPane();
		TextField usernameField = new TextField();
		usernameField.setPrefWidth(300);
		spRight.setPadding(new Insets(20, 100, 20, 20));
		spRight.getChildren().add(usernameField);
		StackPane.setAlignment(usernameField, Pos.CENTER);
		bpLogin.setRight(spRight);
		
		// login button
		StackPane spBottom = new StackPane();
		Button loginButton = new Button("Enter");
		loginButton.setFont(Font.font(15));
		StackPane.setAlignment(loginButton, Pos.CENTER);
		spBottom.setPadding(new Insets(20, 20, 180, 20));
		spBottom.getChildren().add(loginButton);
		bpLogin.setBottom(spBottom);
		
		// login button actions
		loginButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				Stage secondStage = new Stage();
				int index = 1000000000;
				for (int i = 0; i < users.size(); i++){
					if(users.get(i).name.equals(usernameField.getText())){
						index = i;
					}
				}
		// transition to admin screen
				if (usernameField.getText().equals("admin")){
					BorderPane bpAdmin = AdminBP(secondStage);
					Scene admin = new Scene(bpAdmin, 700, 700);
					admin.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					secondStage.setScene(admin);
					firstStage.close();
					secondStage.setResizable(false);
					secondStage.show();
				}
		// transitions to user albums
				else if (index < 1000000000){
					BorderPane bpAlbum = AlbumsBP(secondStage, users.get(index));
					Scene albums = new Scene(bpAlbum, 900, 700);
					albums.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					secondStage.setScene(albums);
					firstStage.close();
					secondStage.setResizable(false);
					secondStage.show();
				}
		// error if the user is not valid
				else if (index == 1000000000){
					Alert userNotFound = new Alert(AlertType.ERROR);
					userNotFound.initOwner(firstStage);
					userNotFound.setTitle("Something went wrong!");
					userNotFound.setHeaderText("Specified user is not in the list.");
					userNotFound.setContentText("Enter a registered username.");
					userNotFound.showAndWait();
				}
			}
		});
		
		return bpLogin;
	}

	/**
	 * This class is the design for the album borderpane.
	 *
	 * @param secondStage 	the design specifications of the Album BorderPane
	 * @param user 			the specific user that is currently logged in
	 * @return the border pane
	 */
	private BorderPane AlbumsBP(Stage secondStage, User user){

		BorderPane albums = new BorderPane();
		albums.setPadding(new Insets(20, 20, 20, 20));
		

		// shows all the user's albums when albums button pressed
		user.albumList.setPadding(new Insets(20, 20, 20, 20));
		user.albumList.setPrefWidth(500);
		user.albumList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		albums.setLeft(user.albumList);
		
		// all other button actions
		GridPane gpButtons = new GridPane();
		gpButtons.setPadding(new Insets(70, 40, 10, 10));
		gpButtons.setVgap(20);
		gpButtons.setHgap(10);
		gpButtons.setAlignment(Pos.CENTER);
		
		// create album button
		Button createAlbum = new Button("Create");
		createAlbum.setPrefWidth(60);
		gpButtons.add(createAlbum, 0, 0);
		
		// move photo when double clicked
		user.albumList.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent click){
				if (click.getClickCount() == 2){
					if (user.userAlbums.size() == 0){
						Alert albumEmpty = new Alert(AlertType.ERROR);
						albumEmpty.initOwner(secondStage);
						albumEmpty.setTitle("Something went wrong!");
						albumEmpty.setHeaderText("No Present Album");
						albumEmpty.setContentText("No Album Selected or Album Empty.");
						albumEmpty.showAndWait();
					}
					else{
						Album currentAlbum = user.albumList.getSelectionModel().getSelectedItem();
						secondStage.close();
						Stage primary = new Stage();
						BorderPane bpPhoto = PictureBP(primary, currentAlbum.photoList, currentAlbum.userPhotos, user.albumList, user, currentAlbum);
						currentAlbum.photoList.setCellFactory(listView -> new ListCell<Photo>() {
							@Override
							public void updateItem(Photo pic, boolean empty){
								super.updateItem(pic, empty);
								if (empty) {
									setText(null);
									setGraphic(null);
								}
								else {
									setText(pic.name + "\n" + pic.caption);
									pic.image.setPreserveRatio(true);
									pic.image.setFitWidth(100);
									pic.image.setFitHeight(100);
									setGraphic(pic.image);
								}
							}
						});
						Scene pic = new Scene(bpPhoto, 700, 700);
						pic.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
						primary.setScene(pic);
						primary.setResizable(false);
						primary.show();
					}
				}
			}
		});
		
		// creates an empty new album when the button is pressed
		createAlbum.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				ListView<Photo> photoList = new ListView<Photo>();
				ObservableList<Photo> userPhotos = FXCollections.observableArrayList();
				Album al = new Album("New Album", photoList, userPhotos, null, null);
				user.userAlbums.add(al);
				user.albumList.setItems(user.userAlbums);
				user.albumList.setCellFactory(listView -> new ListCell<Album>() {
					@Override
					public void updateItem(Album alb, boolean empty){
						super.updateItem(alb, empty);
		                if (empty) {
		                    setText(null);
		                    setGraphic(null);
		                } 
		                else {
		                	Date d = new Date(findOldestDate(photoList, userPhotos));
		                	String d1 = dFNT.format(d);
		                	Date dN = new Date(findNewestDate(photoList, userPhotos));
		                	String d2 = dFNT.format(dN);
		                	String dF = dateFormat.format(d);
		                    setText(alb.name + "\n" + alb.userPhotos.size() + " Photos" + "\n" + "Oldest Photo : " + alb.oldestDate + "\n" + "Date Range: " + alb.dateRange);
		                }
					}
		        });
				int index = user.userAlbums.indexOf(al);
				user.albumList.getSelectionModel().select(index);
			}
		});
		
		// rename button
		Button renameAlbum = new Button("Rename");
		renameAlbum.setPrefWidth(60);
		gpButtons.add(renameAlbum, 0, 1);
		
		// transitions to a new screen that prompts the user for a new name for the album
		renameAlbum.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				Stage secondStage = new Stage();
				BorderPane bpRename = new BorderPane();
				Scene rn = new Scene(bpRename, 500, 300);
				rn.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				secondStage.setScene(rn);
				secondStage.setResizable(false);
				secondStage.show();
				
				// top pane
				StackPane spTop = new StackPane();
				Text rename = new Text("Enter new album name:");
				rename.setFont(Font.font(18));
				StackPane.setAlignment(rename, Pos.CENTER);
				spTop.setPadding(new Insets(100, 20, 20, 20));
				spTop.getChildren().add(rename);
				bpRename.setTop(spTop);
				
				// textbox and buttons at the bottom
				GridPane gpBot = new GridPane();
				gpBot.setPadding(new Insets(20, 20, 70, 125));
				gpBot.setVgap(20);
				gpBot.setHgap(20);
				TextField renameField = new TextField();
				renameField.setPrefWidth(100);
				gpBot.add(renameField, 1, 0);
				Button renameButton = new Button("Rename");
				renameButton.setPrefWidth(70);
				gpBot.add(renameButton, 0, 1);
				
				renameButton.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent e){
						if (user.userAlbums.size() == 0){
							Alert albumEmpty = new Alert(AlertType.ERROR);
							albumEmpty.initOwner(secondStage);
							albumEmpty.setTitle("Something went wrong!");
							albumEmpty.setHeaderText("No Present Album");
							albumEmpty.setContentText("No Album Selected or Album Empty.");
							albumEmpty.showAndWait();
						}
						else{
							user.albumList.getSelectionModel().getSelectedItem().name = renameField.getText();
							user.albumList.setCellFactory(listView -> new ListCell<Album>() {
								@Override
								public void updateItem(Album alb, boolean empty){
									super.updateItem(alb, empty);
					                if (empty) {
					                    setText(null);
					                    setGraphic(null);
					                } 
					                else {
					                	Date d = new Date(findOldestDate(user.albumList.getSelectionModel().getSelectedItem().photoList, user.albumList.getSelectionModel().getSelectedItem().userPhotos));
					                	String d1 = dFNT.format(d);
					                	Date dN = new Date(findNewestDate(user.albumList.getSelectionModel().getSelectedItem().photoList, user.albumList.getSelectionModel().getSelectedItem().userPhotos));
					                	String d2 = dFNT.format(dN);
					                	String dF = dateFormat.format(d);
					                    setText(alb.name + "\n" + alb.userPhotos.size() + " Photos" + "\n" + "Oldest Photo : " + alb.oldestDate + "\n" + "Date Range: " + alb.dateRange);
					                }
								}
					        });
						}
						secondStage.close();
					}
				});
				
				Button cancelButton = new Button("Cancel");
				cancelButton.setPrefWidth(70);
				gpBot.add(cancelButton, 2, 1);
				
				cancelButton.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent e){
						secondStage.close();
					}
				});
				
				bpRename.setBottom(gpBot);
			}
		});
		
		// delete button
		Button deleteAlbum = new Button("Delete");
		deleteAlbum.setPrefWidth(60);
		gpButtons.add(deleteAlbum, 0, 2);
		
		// delete button actions
		deleteAlbum.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				if(user.userAlbums.size() == 0){
					Alert albumEmpty = new Alert(AlertType.ERROR);
					albumEmpty.initOwner(secondStage);
					albumEmpty.setTitle("Something went wrong!");
					albumEmpty.setHeaderText("No Present Album");
					albumEmpty.setContentText("No Album Selected or Album Empty.");
					albumEmpty.showAndWait();
				}
				else if(user.userAlbums.size()-1 == user.albumList.getSelectionModel().getSelectedIndex()){
					user.userAlbums.remove(user.albumList.getSelectionModel().getSelectedIndex());
					user.albumList.getSelectionModel().select(user.userAlbums.size()-1);
				}
				else if(user.userAlbums.size() > 2){
					user.albumList.getSelectionModel().selectNext();
					user.userAlbums.remove(user.albumList.getSelectionModel().getSelectedIndex()-1);
				}
				else if(user.userAlbums.size() == 2 || user.userAlbums.size() == 1){
					user.userAlbums.remove(user.albumList.getSelectionModel().getSelectedIndex());
					user.albumList.getSelectionModel().select(0);
				}
			}
		});
		
		// logout handling
		Button logout = new Button("Logout");
		logout.setPrefWidth(80);
		gpButtons.add(logout, 2, 0);
		logoutButton(logout, secondStage);
		
		// safe quit handling
		Button safeQuit = new Button("Safe Quit");
		safeQuit.setPrefWidth(80);
		gpButtons.add(safeQuit, 2, 1);
		safeQuitButton(safeQuit, secondStage);
		
		Text dateRange = new Text("Date Range");
		gpButtons.add(dateRange, 0, 3);
		DatePicker drFrom = new DatePicker();
		drFrom.setPrefWidth(110);
		gpButtons.add(drFrom, 0, 4);
		DatePicker drTo = new DatePicker();
		drTo.setPrefWidth(110);
		gpButtons.add(drTo, 2, 4);
		Text type = new Text("Type");
		gpButtons.add(type, 0, 5);
		Text tag = new Text("Tag");
		gpButtons.add(tag, 2, 5);
		TextField typeField = new TextField();
		typeField.setPrefWidth(70);
		gpButtons.add(typeField, 0, 6);
		Text to = new Text("to");
		gpButtons.add(to, 1, 4);
		TextField tagField = new TextField();
		tagField.setPrefWidth(70);
		gpButtons.add(tagField, 2, 6);
		
		Button search = new Button ("Search");
		gpButtons.add(search, 3, 4);
		Button search2 = new Button("Search");
		gpButtons.add(search2, 3, 6);
		
		search.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				if (drFrom.getValue() == null || drTo.getValue() == null){
					Alert invalid = new Alert(AlertType.ERROR);
					invalid.setTitle("Something went wrong!");
					invalid.setHeaderText("Invalid format");
					invalid.setContentText("Field(s) are empty, please fill in all fields.");
					invalid.showAndWait();
				}
				else if (drFrom.getValue().compareTo(drTo.getValue()) > 0){
					Alert invalid = new Alert(AlertType.ERROR);
					invalid.setTitle("Something went wrong!");
					invalid.setHeaderText("Invalid format");
					invalid.setContentText("The From value can not be greater than the To Value.");
					invalid.showAndWait();
				}
				else if (drFrom.getValue().compareTo(drTo.getValue()) == 0){
					Alert invalid = new Alert(AlertType.ERROR);
					invalid.setTitle("Something went wrong!");
					invalid.setHeaderText("Invalid format");
					invalid.setContentText("Dates Cannot be Equal.");
					invalid.showAndWait();
				}
				else{
					secondStage.close();
					Stage primary = new Stage();
					ListView<Photo> listPhotos = new ListView<Photo>();
					ObservableList<Photo> userPhotos = FXCollections.observableArrayList();
					for (int i = 0; i < user.userAlbums.size(); i++){
						Album searchAlbum = user.userAlbums.get(i);
						for (int j = 0; j < searchAlbum.userPhotos.size(); j++){
							Photo searchPhoto = searchAlbum.userPhotos.get(j);
							java.util.Date dTo = java.sql.Date.valueOf(drTo.getValue());
							java.util.Date dFrom = java.sql.Date.valueOf(drFrom.getValue());
							Date value = new Date(searchPhoto.dateModified);
							if (value.compareTo(dFrom) > 0 && value.compareTo(dTo) < 0){
								userPhotos.add(searchPhoto);
							}
						}
					}
					listPhotos.setItems(userPhotos);
					BorderPane searchBP = searchResultsBP(primary, listPhotos, userPhotos, user);
					Scene searchScene = new Scene(searchBP, 900, 700);
					searchScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					primary.setScene(searchScene);
					primary.setResizable(false);
					primary.show();
				}
			}
		});
		
		search2.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				if (typeField.getText().trim().isEmpty() || tagField.getText().trim().isEmpty()){
					Alert fieldEmpty = new Alert(AlertType.ERROR);
					fieldEmpty.setTitle("Something went wrong!");
					fieldEmpty.setHeaderText("Field is empty.");
					fieldEmpty.setContentText("Fill in both fields.");
					fieldEmpty.showAndWait();
				}
				else{
					secondStage.close();
					Stage primary = new Stage();
					ListView<Photo> listPhotos = new ListView<Photo>();
					ObservableList<Photo> userPhotos = FXCollections.observableArrayList();
					for (int i = 0; i < user.userAlbums.size(); i++){
						Album searchAlbum = user.userAlbums.get(i);
						for (int j = 0; j < searchAlbum.userPhotos.size(); j++){
							Photo searchPhoto = searchAlbum.userPhotos.get(j);
							for (int k = 0; k < searchPhoto.types.size(); k++){
								if (searchPhoto.types.get(k).equals(typeField.getText()) && searchPhoto.tags.get(k).equals(tagField.getText())){
									userPhotos.add(searchPhoto);
								}
							}
						}
					}
					listPhotos.setItems(userPhotos);
					BorderPane searchBP = searchResultsBP(primary, listPhotos, userPhotos, user);
					Scene searchScene = new Scene(searchBP, 900, 700);
					searchScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					primary.setScene(searchScene);
					primary.setResizable(false);
					primary.show();
				}
			}
		});
		
		albums.setRight(gpButtons);		
		return albums;
	}
	
	/**
	 * This class is responsible for the admin borderpane.
	 *
	 * @param secondStage Admin borderpane schematics
	 * @return border pane
	 */
	private BorderPane AdminBP(Stage secondStage){
		BorderPane admin = new BorderPane();
		
		// text and ListView
		GridPane gpLeft = new GridPane();
		gpLeft.setPadding(new Insets(50, 50, 50, 50));
		
		StackPane sPaneUsers = new StackPane();
		sPaneUsers.setPadding(new Insets(30, 30, 30, 30));
		Text user = new Text("User List");
		user.setFont(Font.font(25));
		StackPane.setAlignment(user, Pos.CENTER);
		sPaneUsers.getChildren().add(user);
		gpLeft.add(sPaneUsers, 0, 0);
		
		list.setPrefWidth(350);
		list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		gpLeft.add(list, 0, 1);
		
		admin.setLeft(gpLeft);
		
		// buttons
		GridPane gpRight = new GridPane();
		gpRight.setPadding(new Insets(70, 50, 20, 20));
		gpRight.setVgap(20);
		
		TextField userField = new TextField();
		userField.setPrefWidth(150);
		gpRight.add(userField, 0, 0);
		
		Button addUser = new Button("Add User");
		addUser.setPrefWidth(100);
		gpRight.add(addUser, 0, 1);
		
		addUser.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				int index = 1000000000;
				for (int i = 0; i < users.size(); i++){
					if(users.get(i).name.equals(userField.getText())){
						index = i;
					}
				}
				if (userField.getText().equals("")){
					Alert fieldEmpty = new Alert(AlertType.ERROR);
					fieldEmpty.initOwner(secondStage);
					fieldEmpty.setTitle("Something went wrong!");
					fieldEmpty.setHeaderText("Field is empty.");
					fieldEmpty.setContentText("Enter a username into the field.");
					fieldEmpty.showAndWait();
				}
				else if (index == 1000000000){
					User newUser = new User(userField.getText(), new ListView<Album>(), FXCollections.observableArrayList());
					users.add(newUser);
					list.setItems(users);
					list.getSelectionModel().select(newUser);
					userField.setText("");
				}
				else if (index < 1000000000){
					Alert userExists = new Alert(AlertType.ERROR);
					userExists.initOwner(secondStage);
					userExists.setTitle("Something went wrong!");
					userExists.setHeaderText("User already exists.");
					userExists.setContentText("Enter in a different username.");
					userExists.showAndWait();
				}
			}
		});
		
		Button deleteUser = new Button("Delete User");
		deleteUser.setPrefWidth(100);
		gpRight.add(deleteUser, 0, 2);
		
		deleteUser.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				if (users.size() > 0){
					Alert delConfirm = new Alert(AlertType.CONFIRMATION);
					delConfirm.initOwner(secondStage);
					delConfirm.setTitle("Confirm?");
					delConfirm.setHeaderText("Confirm deleting this user's profile?");
					delConfirm.setContentText("This may delete all of their content!");
					delConfirm.showAndWait();
					if(users.size()-1 == list.getSelectionModel().getSelectedIndex()){
						users.remove(list.getSelectionModel().getSelectedIndex());
						list.getSelectionModel().select(users.size()-1);
					}
					else if(users.size() > 2){
						list.getSelectionModel().selectNext();
						users.remove(list.getSelectionModel().getSelectedIndex()-1);
					}
					else if(users.size() == 2 || users.size() == 1){
						users.remove(list.getSelectionModel().getSelectedIndex());
						list.getSelectionModel().select(0);
					}
				}
				else if(users.size() == 0){
					Alert nothingSelected = new Alert(AlertType.ERROR);
					nothingSelected.initOwner(secondStage);
					nothingSelected.setTitle("Something went wrong!");
					nothingSelected.setHeaderText("No user selected.");
					nothingSelected.setContentText("Please select a user.");
					nothingSelected.showAndWait();
				}
			}
		});
		
		// logout handling
		Button logout = new Button("Logout");
		logout.setPrefWidth(100);
		gpRight.add(logout, 0, 8);
		logoutButton(logout, secondStage);
		
		// safe quit handling
		Button safeQuit = new Button("Safe Quit");
		safeQuit.setPrefWidth(100);
		gpRight.add(safeQuit, 0, 9);
		safeQuitButton(safeQuit, secondStage);
		
		admin.setRight(gpRight);	
		return admin;
	}

	/**
	 * This class is responsible for the picture borderpane.
	 *
	 * @param secondStage 	picture borderpane schematics
	 * @param photoList 	ListView of photos
	 * @param userPhotos 	observableList of photos
	 * @param albumList 	album list from the previous screen
	 * @param user 			currently logged in user
	 * @param alb 			album that the picture is in
	 * @return border pane
	 */
	private BorderPane PictureBP(Stage secondStage, ListView<Photo> photoList, ObservableList<Photo> userPhotos, ListView<Album> albumList, User user, Album alb){
		BorderPane bpPic = new BorderPane();
		bpPic.setPadding(new Insets(20, 20, 20, 20));
		
		// left
		photoList.setPadding(new Insets(20, 20, 20, 20));
		photoList.setPrefWidth(500);
		photoList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		bpPic.setLeft(photoList);
		
		// right
		GridPane gpRight = new GridPane();
		gpRight.setPadding(new Insets(70, 50, 20, 20));
		gpRight.setVgap(35);
		
		// action when a photo is clicked
		photoList.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent click){
				if (click.getClickCount() == 2){
					if (userPhotos.size() == 0){
						Alert albumEmpty = new Alert(AlertType.ERROR);
						albumEmpty.initOwner(secondStage);
						albumEmpty.setTitle("Something went wrong!");
						albumEmpty.setHeaderText("No Present Photo");
						albumEmpty.setContentText("No Photo Selected or Album Empty.");
						albumEmpty.showAndWait();
					}
					else{
						Photo currentPhoto = photoList.getSelectionModel().getSelectedItem();
						secondStage.close();
						Stage primary = new Stage();
						BorderPane bpClicked = picClickedBP(primary, currentPhoto, user, userPhotos, photoList, albumList, alb);
						Scene pic = new Scene(bpClicked, 1000, 900);
						pic.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
						primary.setScene(pic);
						primary.setResizable(false);
						primary.show();
					}
				}
			}
		});
		
		// add photo button
		Button addPhoto = new Button("Add Photo");
		addPhoto.setPrefWidth(100);
		gpRight.add(addPhoto, 0, 0);
		addPhoto.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				FileChooser fChooser = new FileChooser();
				fChooser.setTitle("Open Photo");
				fChooser.getExtensionFilters().addAll(new ExtensionFilter("*.tif", "*.jpg", "*.png"));
				File photo = fChooser.showOpenDialog(secondStage);
				ImageView iv = new ImageView();
				iv.setFitHeight(100);
				iv.setFitWidth(100);
				BufferedImage bufferedImage;
				Image img;
				try{
					bufferedImage = ImageIO.read(photo);
					img = SwingFXUtils.toFXImage(bufferedImage, null);
					iv.setImage(img);
				}
				catch (IOException ex) {
	            }
				if (photo != null){
					ArrayList<String> tags = new ArrayList<String>();
					ArrayList<String> types = new ArrayList<String>();
					Photo chosenPhoto = new Photo(photo, iv, photo.lastModified(), photo.getName(), "", tags, types, 0);
					userPhotos.add(chosenPhoto);
					Date d = new Date(findOldestDate(photoList, userPhotos));
					String dF = dateFormat.format(d);
					alb.oldestDate = dF;
					Date dN = new Date(findNewestDate(photoList, userPhotos));
					String d1 = dFNT.format(d) + " - " + dFNT.format(dN);
					alb.dateRange = d1;
					
					photoList.setItems(userPhotos);
					photoList.setCellFactory(listView -> new ListCell<Photo>() {
						@Override
						public void updateItem(Photo pic, boolean empty){
							super.updateItem(pic, empty);
							if (empty) {
								setText(null);
								setGraphic(null);
							}
							else {
								setText(pic.name + "\n" + pic.caption);
								pic.image.setPreserveRatio(true);
								pic.image.setFitWidth(100);
								pic.image.setFitHeight(100);
								setGraphic(pic.image);
							}
						}
					});
					int index = userPhotos.indexOf(chosenPhoto);
					photoList.getSelectionModel().select(index);
				}
				albumList.setCellFactory(listView -> new ListCell<Album>() {
					@Override
					public void updateItem(Album alb, boolean empty){
						super.updateItem(alb, empty);
		                if (empty) {
		                    setText(null);
		                    setGraphic(null);
		                } 
		                else {
		                	Date d = new Date(findOldestDate(photoList, userPhotos));
		                	String d1 = dFNT.format(d);
		                	Date dN = new Date(findNewestDate(photoList, userPhotos));
		                	String d2 = dFNT.format(dN);
		                	String dF = dateFormat.format(d);
		                    setText(alb.name + "\n" + alb.userPhotos.size() + " Photos" + "\n" + "Oldest Photo : " + alb.oldestDate + "\n" + "Date Range: " + alb.dateRange);
		                }
					}
		        });
			}
		});
		
		// remove photo button
		Button removePhoto = new Button("Remove Photo");
		removePhoto.setPrefWidth(100);
		gpRight.add(removePhoto, 0, 1);
		removePhoto.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				if(userPhotos.size() == 0 || photoList.getSelectionModel().getSelectedItem() == null){
					Alert noPhoto = new Alert(AlertType.ERROR);
					noPhoto.initOwner(secondStage);
					noPhoto.setTitle("Something went wrong!");
					noPhoto.setHeaderText("No Present Photo");
					noPhoto.setContentText("No Photo Selected or does not exist.");
					noPhoto.showAndWait();
				}
				else if(userPhotos.size()-1 == photoList.getSelectionModel().getSelectedIndex()){
					userPhotos.remove(photoList.getSelectionModel().getSelectedIndex());
					photoList.getSelectionModel().select(userPhotos.size()-1);
				}
				else if(userPhotos.size() > 2){
					photoList.getSelectionModel().selectNext();
					userPhotos.remove(photoList.getSelectionModel().getSelectedIndex()-1);
				}
				else if(userPhotos.size() == 2 || userPhotos.size() == 1){
					userPhotos.remove(photoList.getSelectionModel().getSelectedIndex());
					photoList.getSelectionModel().select(0);
				}
				
				if (!(userPhotos.size() == 0)){
            		Date d = new Date(findOldestDate(photoList, userPhotos));
                	String d1 = dFNT.format(d);
                	Date dN = new Date(findNewestDate(photoList, userPhotos));
                	String d2 = dFNT.format(dN);
                	String dF = dateFormat.format(d);
                	alb.oldestDate = dF;
                	alb.dateRange = d1 + " - " + d2;
            	}
            	else{
            		alb.oldestDate = null;
            		alb.dateRange = null;
            	}
				albumList.setCellFactory(listView -> new ListCell<Album>() {
					@Override
					public void updateItem(Album album, boolean empty){
						super.updateItem(album, empty);
		                if (empty) {
		                    setText(null);
		                    setGraphic(null);
		                } 
		                else {
		                	setText(album.name + "\n" + album.userPhotos.size() + " Photos" + "\n" + "Oldest Photo : " + album.oldestDate + "\n" + "Date Range: " + album.dateRange);
		                }
					}
		        });
			}
		});
		
		// add caption button
		Button caption = new Button("Add Caption");
		caption.setPrefWidth(100);
		gpRight.add(caption, 0, 2);
		caption.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				if (userPhotos.size() == 0){
					Alert noPhoto = new Alert(AlertType.ERROR);
					noPhoto.initOwner(secondStage);
					noPhoto.setTitle("Something went wrong!");
					noPhoto.setHeaderText("No Present Photo");
					noPhoto.setContentText("No Photo Selected or Photo Empty.");
					noPhoto.showAndWait();
				}
				else if(photoList.getSelectionModel().getSelectedItem().caption.equals("")){
					Stage secondStage = new Stage();
					BorderPane bpAddCap = new BorderPane();
					Scene aC = new Scene(bpAddCap, 500, 300);
					aC.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					secondStage.setScene(aC);
					secondStage.setResizable(false);
					secondStage.show();
							
					// top
					StackPane spTop = new StackPane();
					Text add = new Text("Enter picture caption.");
					add.setFont(Font.font(18));
					StackPane.setAlignment(add, Pos.CENTER);
					spTop.setPadding(new Insets(100, 20, 20, 20));
					spTop.getChildren().add(add);
					bpAddCap.setTop(spTop);
							
					// bottom
					GridPane gpBot = new GridPane();
					gpBot.setPadding(new Insets(20, 20, 70, 75));
					gpBot.setVgap(20);
					gpBot.setHgap(20);
					
					TextField addField = new TextField();
					addField.setPrefWidth(100);
					gpBot.add(addField, 1, 0);				
					Button addButton = new Button("Add Caption");
					addButton.setPrefWidth(100);
					gpBot.add(addButton, 0, 1);						
					addButton.setOnAction(new EventHandler<ActionEvent>(){
						public void handle(ActionEvent e){
							photoList.getSelectionModel().getSelectedItem().caption = addField.getText();
							photoList.setCellFactory(listView -> new ListCell<Photo>() {
								@Override
								public void updateItem(Photo pho, boolean empty){
									super.updateItem(pho, empty);
									if (empty) {
										setText(null);
									    setGraphic(null);
									} 
									else {
									    setText(pho.name + "\n" + pho.caption); 
									    pho.image.setPreserveRatio(true);
									    pho.image.setFitWidth(100);
										pho.image.setFitHeight(100);
										setGraphic(pho.image);
									}
									secondStage.close();
								}	
							});
						}
					});
				
					Button cancelButton = new Button("Cancel");
					cancelButton.setPrefWidth(100);
					gpBot.add(cancelButton, 2, 1);							
					cancelButton.setOnAction(new EventHandler<ActionEvent>(){
						public void handle(ActionEvent e){
							secondStage.close();
						}
					});
							
					bpAddCap.setBottom(gpBot);
				}
			}
		});
		
		// edit caption button
		Button editCaption = new Button("Edit Caption");
		editCaption.setPrefWidth(100);
		gpRight.add(editCaption, 0, 3);
		editCaption.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				if (userPhotos.size() == 0){
					Alert noPhoto = new Alert(AlertType.ERROR);
					noPhoto.initOwner(secondStage);
					noPhoto.setTitle("Something went wrong!");
					noPhoto.setHeaderText("No Present Photo");
					noPhoto.setContentText("No Photo Selected or Photo Empty.");
					noPhoto.showAndWait();
				}
				else if (!photoList.getSelectionModel().getSelectedItem().caption.equals("")){
					Stage secondStage = new Stage();
					BorderPane bpEditCap = new BorderPane();
					Scene eC = new Scene(bpEditCap, 500, 300);
					eC.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					secondStage.setScene(eC);
					secondStage.setResizable(false);
					secondStage.show();
							
					// top
					StackPane spTop = new StackPane();
					Text edit = new Text("Enter new caption.");
					edit.setFont(Font.font(18));
					StackPane.setAlignment(edit, Pos.CENTER);
					spTop.setPadding(new Insets(100, 20, 20, 20));
					spTop.getChildren().add(edit);
					bpEditCap.setTop(spTop);
							
					// bottom
					GridPane gpBot = new GridPane();
					gpBot.setPadding(new Insets(20, 20, 70, 75));
					gpBot.setVgap(20);
					gpBot.setHgap(20);
					
					TextField editField = new TextField();
					editField.setPrefWidth(100);
					gpBot.add(editField, 1, 0);
					
					Button editButton = new Button("Edit Caption");
					editButton.setPrefWidth(100);
					gpBot.add(editButton, 0, 1);
							
					editButton.setOnAction(new EventHandler<ActionEvent>(){
						public void handle(ActionEvent e){
							photoList.getSelectionModel().getSelectedItem().caption = editField.getText();
							photoList.setCellFactory(listView -> new ListCell<Photo>() {
								@Override
								public void updateItem(Photo pho, boolean empty){
									super.updateItem(pho, empty);
									if (empty) {
										setText(null);
									    setGraphic(null);
									} 
									else {
									    setText(pho.name + "\n" + pho.caption);
									    pho.image.setPreserveRatio(true);
									    pho.image.setFitWidth(100);
										pho.image.setFitHeight(100);
										setGraphic(pho.image);
									}
									secondStage.close();
								}	
							});
						}
					});
				
					Button cancelButton = new Button("Cancel");
					cancelButton.setPrefWidth(100);
					gpBot.add(cancelButton, 2, 1);
							
					cancelButton.setOnAction(new EventHandler<ActionEvent>(){
						public void handle(ActionEvent e){
							secondStage.close();
						}
					});
							
					bpEditCap.setBottom(gpBot);
				}
			}
		});
		
		// back button
		Button back = new Button("Back");
		back.setPrefWidth(100);
		gpRight.add(back, 0, 6);
		
		back.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				secondStage.close();
				Stage primary = new Stage();
				BorderPane backScreen = AlbumsBP(primary, user);
				Scene backScene = new Scene(backScreen, 900, 700);
				backScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primary.setScene(backScene);
				primary.setResizable(false);
				primary.show();
			}
		});
		
		// logout handling
		Button logout = new Button("Logout");
		logout.setPrefWidth(100);
		gpRight.add(logout, 0, 10);
		logoutButton(logout, secondStage);
		
		// safe quit handling
		Button safeQuit = new Button("Safe Quit");
		safeQuit.setPrefWidth(100);
		gpRight.add(safeQuit, 0, 11);
		safeQuitButton(safeQuit, secondStage);
		
		bpPic.setRight(gpRight);		
		return bpPic;
	}
	
	/**
	 * This class is responsible for the query results page.
	 *
	 * @param secondStage 	the design specifications of the search results borderpane
	 * @param listPhotos 	the listview of Photos that the user can see
	 * @param userPhotos 	the ObservableList of photos that the application keeps track of
	 * @param user 			the user that is currently logged in
	 * @return the border pane
	 */
	private BorderPane searchResultsBP(Stage secondStage, ListView<Photo> listPhotos, ObservableList<Photo> userPhotos, User user){
		BorderPane sResults = new BorderPane();
		sResults.setPadding(new Insets(20, 20, 20, 20));
		
		// top
		StackPane spTop = new StackPane();
		Text SR = new Text("Results");
		SR.setFont(Font.font(30));
		StackPane.setAlignment(SR, Pos.CENTER);
		spTop.setPadding(new Insets(40, 20, 20, 20));
		spTop.getChildren().add(SR);
		sResults.setTop(spTop);
		
		// left
		GridPane gpLeft = new GridPane();
		gpLeft.setPadding(new Insets(20, 20, 20, 20));
		gpLeft.setVgap(20);
		
		Button logout = new Button("Logout");
		logout.setPrefWidth(100);
		gpLeft.add(logout, 0, 15);
		
		logout.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				logoutButton(logout, secondStage);
			}
		});
		
		Button safeQuit = new Button("Safe Quit");
		safeQuit.setPrefWidth(100);
		gpLeft.add(safeQuit, 0, 16);
		
		safeQuit.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				safeQuitButton(safeQuit, secondStage);
			}
		});
		
		sResults.setLeft(gpLeft);

		Button back = new Button("Back to Albums");
		back.setPrefWidth(100);
		gpLeft.add(back, 0, 0);
		sResults.setLeft(gpLeft);
		
		back.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				secondStage.close();
				Stage primary = new Stage();
				BorderPane backScreen = AlbumsBP(primary, user);
				Scene backScene = new Scene(backScreen, 900, 700);
				backScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primary.setScene(backScene);
				primary.setResizable(false);
				primary.show();
			}
		});
		
		// middle
		listPhotos.setPadding(new Insets(20, 20, 20, 20));
		listPhotos.setPrefWidth(400);
		listPhotos.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		listPhotos.setCellFactory(listView -> new ListCell<Photo>() {
			@Override
			public void updateItem(Photo pho, boolean empty){
				super.updateItem(pho, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } 
                else {
                	setText(pho.name + "\n" + pho.caption);
                	pho.image.setPreserveRatio(true);
				    pho.image.setFitWidth(100);
					pho.image.setFitHeight(100);
					setGraphic(pho.image);
                }
			}
        });
		sResults.setCenter(listPhotos);
		
		// bottom
		StackPane spBot = new StackPane();
		Button createAlbum = new Button("Create Album");
		createAlbum.setPrefWidth(100);
		StackPane.setAlignment(createAlbum, Pos.CENTER);
		spBot.setPadding(new Insets(20, 20, 40, 20));
		spBot.getChildren().add(createAlbum);
		sResults.setBottom(spBot);
		
		createAlbum.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){ 
				if (userPhotos.size() != 0){
					Date d = new Date(findOldestDate(listPhotos, userPhotos));
                	String d1 = dFNT.format(d);
                	Date dN = new Date(findNewestDate(listPhotos, userPhotos));
                	String d2 = dFNT.format(dN);
                	String dF = dateFormat.format(d);
                   
					Album album = new Album("New Album", listPhotos, userPhotos, dF, d1 + " - " + d2);
					user.userAlbums.add(album);
					user.albumList.setCellFactory(listView -> new ListCell<Album>() {
						@Override
						public void updateItem(Album alb, boolean empty){
							super.updateItem(alb, empty);
			                if (empty) {
			                    setText(null);
			                    setGraphic(null);
			                } 
			                else {
			                	setText(alb.name + "\n" + alb.userPhotos.size() + " Photos" + "\n" + "Oldest Photo : " + alb.oldestDate + "\n" + "Date Range: " + alb.dateRange);
			                }
						}
			        });
					secondStage.close();
					Stage primary = new Stage();
					BorderPane bpAlbum = AlbumsBP(primary, user);
					Scene alb = new Scene(bpAlbum, 1000, 900);
					alb.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					primary.setScene(alb);
					primary.setResizable(false);
					primary.show();
				}
				else{
					Alert pEmpty = new Alert(AlertType.ERROR);
					pEmpty.setHeaderText("No Photos in the List");
					pEmpty.setTitle("Something went wrong!");
					pEmpty.setContentText("No search resultings matching query.");
					pEmpty.showAndWait();
				}
			}
		});	
		return sResults;
	}
	
	/**
	 * This class is responsible for when a picture has been clicked.
	 *
	 * @param secondStage 	clicked picture borderpane schematics
	 * @param pic 			currently selected picture
	 * @param user 			currently logged in user
	 * @param userPhotos 	ObservableList
	 * @param photoList 	ListView
	 * @param albumList 	album list
	 * @param album 		album that the picture is inside of
	 * @return border pane
	 */
	private BorderPane picClickedBP(Stage secondStage, Photo pic, User user, ObservableList<Photo> userPhotos, ListView<Photo> photoList, ListView<Album> albumList, Album album){
		BorderPane picBP = new BorderPane();
		
		// center
		StackPane spCenter = new StackPane();
		pic.image.setPreserveRatio(true);
		pic.image.setFitWidth(500);
		pic.image.setFitHeight(500);
		StackPane.setAlignment(pic.image, Pos.CENTER);
		spCenter.getChildren().add(pic.image);
		picBP.setCenter(spCenter);
		
		// top
		GridPane gpTop = new GridPane();
		gpTop.setPadding(new Insets(20, 20, 20, 50));
		gpTop.setHgap(20);
		gpTop.setVgap(20);
		
		Button backScreen = new Button("Return to Albums");
		backScreen.setPrefWidth(150);
		gpTop.add(backScreen, 0, 0);
		
		backScreen.setOnAction(new EventHandler <ActionEvent>(){
			public void handle(ActionEvent e){
				secondStage.close();
				Stage primary = new Stage();
				BorderPane prevS = AlbumsBP(primary, user);
				Scene backScene = new Scene(prevS, 900, 700);
				backScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primary.setScene(backScene);
				primary.setResizable(false);
				primary.show();
			}
		});
		
		Text Caption = new Text("Caption: ");
		Caption.setFont(Font.font(20));
		gpTop.add(Caption, 0, 1);
		
		Text caption = new Text();
		caption.setText(pic.caption);
		caption.setFont(Font.font(20));
		gpTop.add(caption, 1, 1);
		
		Text dateTime = new Text("Date/Time: ");
		dateTime.setFont(Font.font(20));
		gpTop.add(dateTime, 0, 2);
		
		Text dT = new Text("");
		Date d = new Date(pic.dateModified);
    	String dF = dateFormat.format(d);
		dT.setText(dF);
		dT.setFont(Font.font(20));
		gpTop.add(dT, 1, 2);
		
		Text SetTag = new Text("Type/Tag: ");
		SetTag.setFont(Font.font(20));
		gpTop.add(SetTag, 0, 3);
		
		Text setTag = new Text("");
		setTag.setFont(Font.font(20));
		for (int i = 0; i < pic.tags.size(); i++){
			setTag.setText(setTag.getText() + pic.types.get(i) + ": " + pic.tags.get(i) + "\n");
		}
		gpTop.add(setTag, 1, 3);
		
		picBP.setTop(gpTop);
		
		// bottom
		GridPane gpBot = new GridPane();
		gpBot.setHgap(30);
		gpBot.setVgap(20);
		gpBot.setPadding(new Insets(20, 20, 50, 125));
		
		// add tag button
		Button addTag = new Button("Add Tag");
		addTag.setPrefWidth(100);
		gpBot.add(addTag, 0, 1);
		addTag.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				Stage secondStage = new Stage();
				BorderPane bpAddTag = new BorderPane();
				Scene aT = new Scene(bpAddTag, 500, 300);
				aT.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				secondStage.setScene(aT);
				secondStage.setResizable(false);
				secondStage.show();
							
				// top
				StackPane spTop = new StackPane();
				Text add = new Text("Tag");
				add.setFont(Font.font(18));
				StackPane.setAlignment(add, Pos.CENTER);
				spTop.setPadding(new Insets(100, 20, 20, 20));
				spTop.getChildren().add(add);
				bpAddTag.setTop(spTop);
							
				// bottom
				GridPane gpBot = new GridPane();
				gpBot.setPadding(new Insets(20, 20, 70, 75));
				gpBot.setVgap(20);
				gpBot.setHgap(20);				
				Text type = new Text("Type: ");
				gpBot.add(type, 0, 0);			
				Text tag = new Text("Tag: ");
				gpBot.add(tag, 1, 0);			
				TextField addTypeField = new TextField();
				addTypeField.setPrefWidth(100);
				gpBot.add(addTypeField, 0, 1);				
				TextField addTagField = new TextField();
				addTagField.setPrefWidth(100);
				gpBot.add(addTagField, 1, 1);			
				Button addButton = new Button("Add Tag");
				addButton.setPrefWidth(100);
				gpBot.add(addButton, 3, 0);
			
				addButton.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent e){
						if (pic.tags.size() == 0){
							pic.tags.add(addTagField.getText());
							pic.types.add(addTypeField.getText());
							setTag.setText(addTypeField.getText() + ": " + addTagField.getText());
							secondStage.close();
						}
						else if (pic.tags.contains(addTagField.getText()) && pic.types.contains(addTypeField.getText())){
							Alert tagExists = new Alert(AlertType.ERROR);
							tagExists.initOwner(secondStage);
							tagExists.setTitle("Something went wrong!");
							tagExists.setHeaderText("Tag Already Exists");
							tagExists.setContentText("Enter another tag.");
							tagExists.showAndWait();
						}
						else{
							pic.tags.add(addTagField.getText());
							pic.types.add(addTypeField.getText());
							setTag.setText(setTag.getText() + "\n" + addTypeField.getText() + ": " + addTagField.getText());
							secondStage.close();
						}
					}
				});		
				Button cancelButton = new Button("Cancel");
				cancelButton.setPrefWidth(100);
				gpBot.add(cancelButton, 3, 1);					
				cancelButton.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent e){
						secondStage.close();
					}
				});
						
				bpAddTag.setBottom(gpBot);
			}
		});
		
		// delete tag button
		Button deleteTag = new Button("Delete Tag");
		deleteTag.setPrefWidth(100);
		gpBot.add(deleteTag, 1, 1);
		deleteTag.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				Stage secondStage = new Stage();
				BorderPane bpList = new BorderPane();
				Scene dT = new Scene(bpList, 300, 500);
				dT.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				secondStage.setScene(dT);
				secondStage.setResizable(false);
				secondStage.show();
				
				// top
				ListView<String> TTList = new ListView<String>();
				ObservableList<String> typeTag = FXCollections.observableArrayList();
				for (int i = 0; i < pic.tags.size(); i++){
					typeTag.add(pic.types.get(i) + ": " + pic.tags.get(i));
				}
				TTList.setPadding(new Insets(20, 20, 20, 20));
				TTList.setPrefSize(300, 400);
				TTList.setItems(typeTag);
				TTList.getSelectionModel().selectFirst();			
				bpList.setTop(TTList);
				
				// bottom
				GridPane gpButtons = new GridPane();
				gpButtons.setPadding(new Insets(20, 30, 20, 30));
				gpButtons.setHgap(40);				
				Button delete = new Button("Delete");
				delete.setPrefWidth(100);
				gpButtons.add(delete, 0, 0);				
				delete.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent e){
						if(typeTag.size() == 0){
							Alert noTags = new Alert(AlertType.ERROR);
							noTags.initOwner(secondStage);
							noTags.setTitle("Something went wrong!");
							noTags.setHeaderText("No Present Tags");
							noTags.setContentText("No Tag Present.");
							noTags.showAndWait();
						}
						else if(typeTag.size()-1 == TTList.getSelectionModel().getSelectedIndex()){
							typeTag.remove(TTList.getSelectionModel().getSelectedIndex());
							TTList.getSelectionModel().select(typeTag.size()-1);
						}
						else if(typeTag.size() > 2){
							TTList.getSelectionModel().selectNext();
							typeTag.remove(TTList.getSelectionModel().getSelectedIndex()-1);
						}
						else if(typeTag.size() == 2 || typeTag.size() == 1){
							typeTag.remove(TTList.getSelectionModel().getSelectedIndex());
							TTList.getSelectionModel().select(0);
						}
					}
				});				
				Button cancel = new Button("Cancel");
				cancel.setPrefWidth(100);
				gpButtons.add(cancel, 1, 0);				
				cancel.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent e){
						secondStage.close();
					}
				});
				
				bpList.setBottom(gpButtons);
			}
		});
		
		// copy button
		Button copy = new Button("Copy");
		copy.setPrefWidth(100);
		gpBot.add(copy, 4, 1);
		copy.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				Stage secondStage = new Stage();
				BorderPane bpCopy = new BorderPane();
				Scene cT = new Scene(bpCopy, 300, 500);
				cT.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				secondStage.setScene(cT);
				secondStage.setResizable(false);
				secondStage.show();
				
				// top
				user.albumList.setPadding(new Insets(20, 20, 20, 20));
				user.albumList.setPrefSize(300, 400);
				user.albumList.getSelectionModel().selectFirst();
				
				bpCopy.setTop(user.albumList);
				
				// bottom
				GridPane gpButtons = new GridPane();
				gpButtons.setPadding(new Insets(20, 30, 20, 30));
				gpButtons.setHgap(40);
				
				// copy button
				Button copy = new Button("Copy");
				copy.setPrefWidth(100);
				gpButtons.add(copy, 0, 0);
				copy.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent e){
						if (user.userAlbums.size() == 0){
							Alert albumEmpty = new Alert(AlertType.ERROR);
							albumEmpty.initOwner(secondStage);
							albumEmpty.setTitle("Something went wrong!");
							albumEmpty.setHeaderText("No Present Photo");
							albumEmpty.setContentText("No Photo Selected or Album Empty.");
							albumEmpty.showAndWait();
						}
						else if (user.albumList.getSelectionModel().getSelectedItem().userPhotos.contains(pic)){
							Alert picExists = new Alert(AlertType.ERROR);
							picExists.initOwner(secondStage);
							picExists.setTitle("Something went wrong!");
							picExists.setHeaderText("Picture Exists in this Album");
							picExists.setContentText("Select a different Album.");
							picExists.showAndWait();
						}
						else{
							user.albumList.getSelectionModel().getSelectedItem().userPhotos.add(pic);
							Date d = new Date(findOldestDate(user.albumList.getSelectionModel().getSelectedItem().photoList, user.albumList.getSelectionModel().getSelectedItem().userPhotos));
		                	String d1 = dFNT.format(d);
		                	Date dN = new Date(findNewestDate(user.albumList.getSelectionModel().getSelectedItem().photoList, user.albumList.getSelectionModel().getSelectedItem().userPhotos));
		                	String d2 = dFNT.format(dN);
		                	String dF = dateFormat.format(d);
		                    user.albumList.getSelectionModel().getSelectedItem().oldestDate = dF;
		                    user.albumList.getSelectionModel().getSelectedItem().dateRange = d1 + " - " + d2;
		                	
							user.albumList.setCellFactory(listView -> new ListCell<Album>() {
								@Override
								public void updateItem(Album alb, boolean empty){
									super.updateItem(alb, empty);
									if (empty) {
										setText(null);
									    setGraphic(null);
									} 
									else {
					                	setText(alb.name + "\n" + alb.userPhotos.size() + " Photos" + "\n" + "Oldest Photo : " + alb.oldestDate + "\n" + "Date Range: " + alb.dateRange);
					                }
								}	
							});
						
							user.albumList.getSelectionModel().getSelectedItem().photoList.setItems(user.albumList.getSelectionModel().getSelectedItem().userPhotos);
							user.albumList.getSelectionModel().getSelectedItem().photoList.setCellFactory(listView -> new ListCell<Photo>(){
								@Override
								public void updateItem(Photo pic, boolean empty){
									super.updateItem(pic, empty);
									if (empty){
										setText(null);
										setGraphic(null);
									}
									else{
										setText(pic.name + "\n" + pic.caption);
										pic.image.setPreserveRatio(true);
										pic.image.setFitWidth(100);
										pic.image.setFitHeight(100);
										setGraphic(pic.image);
									}
								}
							});
							secondStage.close();
						}
					}
				});
				
				// cancel button
				Button cancel = new Button("Cancel");
				cancel.setPrefWidth(100);
				gpButtons.add(cancel, 1, 0);
				cancel.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent e){
						secondStage.close();
					}
				});
				
				bpCopy.setBottom(gpButtons);
			}
		});
		
		// move button
		Button move = new Button("Move");
		move.setPrefWidth(100);
		gpBot.add(move, 5, 1);
		move.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				Stage thirdStage = new Stage();
				BorderPane bpMove = new BorderPane();
				Scene mT = new Scene(bpMove, 300, 500);
				mT.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				thirdStage.setScene(mT);
				thirdStage.setResizable(false);
				thirdStage.show();
				
				// top
				user.albumList.setPadding(new Insets(20, 20, 20, 20));
				user.albumList.setPrefSize(300, 400);
				user.albumList.getSelectionModel().selectFirst();
				
				bpMove.setTop(user.albumList);
				
				// bottom
				GridPane gpButtons = new GridPane();
				gpButtons.setPadding(new Insets(20, 30, 20, 30));
				gpButtons.setHgap(40);
				
				// move button
				Button move = new Button("Move");
				move.setPrefWidth(100);
				gpButtons.add(move, 0, 0);
				move.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent e){
						if (user.userAlbums.size() == 0){
							Alert albumEmpty = new Alert(AlertType.ERROR);
							albumEmpty.initOwner(secondStage);
							albumEmpty.setTitle("Something went wrong!");
							albumEmpty.setHeaderText("No Present Photo");
							albumEmpty.setContentText("No Photo Selected or Album Empty.");
							albumEmpty.showAndWait();
						}
						else if (user.albumList.getSelectionModel().getSelectedItem().userPhotos.contains(pic)){
							Alert picExists = new Alert(AlertType.ERROR);
							picExists.initOwner(secondStage);
							picExists.setTitle("Something went wrong!");
							picExists.setHeaderText("Picture Exists in this Album");
							picExists.setContentText("Select a different Album.");
							picExists.showAndWait();
						}
						else{
							user.albumList.getSelectionModel().getSelectedItem().userPhotos.add(pic);
							userPhotos.remove(pic);
							secondStage.close();
							thirdStage.close();
							Stage primary = new Stage();
							BorderPane prevS = AlbumsBP(primary, user);
							Scene backScene = new Scene(prevS, 900, 700);
							backScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
							primary.setScene(backScene);
							primary.setResizable(false);
							primary.show();

		                	if (!(userPhotos.size() == 0)){
		                		Date d = new Date(findOldestDate(photoList, userPhotos));
			                	String d1 = dFNT.format(d);
			                	Date dN = new Date(findNewestDate(photoList, userPhotos));
			                	String d2 = dFNT.format(dN);
			                	String dF = dateFormat.format(d);
			                	album.oldestDate = dF;
			                	album.dateRange = d1 + " - " + d2;
		                	}
		                	else{
		                		album.oldestDate = null;
		                		album.dateRange = null;
		                	}
							
							albumList.setCellFactory(listView -> new ListCell<Album>() {
								@Override
								public void updateItem(Album alb, boolean empty){
									super.updateItem(alb, empty);
									if (empty) {
										setText(null);
									    setGraphic(null);
									} 
									else {
					                    setText(alb.name + "\n" + alb.userPhotos.size() + " Photos" + "\n" + "Oldest Photo : " + alb.oldestDate + "\n" + "Date Range: " + alb.dateRange);
					                }
								}	
							});
						
							photoList.setItems(userPhotos);
							photoList.setCellFactory(listView -> new ListCell<Photo>(){
								@Override
								public void updateItem(Photo pic, boolean empty){
									super.updateItem(pic, empty);
									if (empty){
										setText(null);
										setGraphic(null);
									}
									else{
										setText(pic.name + "\n" + pic.caption);
										pic.image.setPreserveRatio(true);
										pic.image.setFitWidth(100);
										pic.image.setFitHeight(100);
										setGraphic(pic.image);
									}
								}
							});
							
		                	Date d = new Date(findOldestDate(user.albumList.getSelectionModel().getSelectedItem().photoList, user.albumList.getSelectionModel().getSelectedItem().userPhotos));
		                	String d1 = dFNT.format(d);
		                	Date dN = new Date(findNewestDate(user.albumList.getSelectionModel().getSelectedItem().photoList, user.albumList.getSelectionModel().getSelectedItem().userPhotos));
		                	String d2 = dFNT.format(dN);
		                	String dF = dateFormat.format(d);
							user.albumList.getSelectionModel().getSelectedItem().oldestDate = dF;
		                    user.albumList.getSelectionModel().getSelectedItem().dateRange = d1 + " - " + d2;
		                    
							user.albumList.setCellFactory(listView -> new ListCell<Album>() {
								@Override
								public void updateItem(Album alb, boolean empty){
									super.updateItem(alb, empty);
									if (empty) {
										setText(null);
									    setGraphic(null);
									} 
									else {
					                    setText(alb.name + "\n" + alb.userPhotos.size() + " Photos" + "\n" + "Oldest Photo : " + alb.oldestDate + "\n" + "Date Range: " + alb.dateRange);
					                }
								}	
							});
						
							user.albumList.getSelectionModel().getSelectedItem().photoList.setItems(user.albumList.getSelectionModel().getSelectedItem().userPhotos);
							user.albumList.getSelectionModel().getSelectedItem().photoList.setCellFactory(listView -> new ListCell<Photo>(){
								@Override
								public void updateItem(Photo pic, boolean empty){
									super.updateItem(pic, empty);
									if (empty){
										setText(null);
										setGraphic(null);
									}
									else{
										setText(pic.name + "\n" + pic.caption);
										pic.image.setPreserveRatio(true);
										pic.image.setFitWidth(100);
										pic.image.setFitHeight(100);
										setGraphic(pic.image);
									}
								}
							});
						}
					}
				});
				
				// cancel button
				Button cancel = new Button("Cancel");
				cancel.setPrefWidth(100);
				gpButtons.add(cancel, 1, 0);
				cancel.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent e){
						secondStage.close();
					}
				});
				
				bpMove.setBottom(gpButtons);
			}
		});

		// logout handling
		Button logout = new Button("Logout");
		logout.setPrefWidth(100);
		gpBot.add(logout, 2, 2);
		logoutButton(logout, secondStage);
		
		// safe quit handling
		Button safeQuit = new Button("Safe Quit");
		safeQuit.setPrefWidth(100);
		gpBot.add(safeQuit, 3, 2);
		safeQuitButton(safeQuit, secondStage);
		
		pic.selectedIndex = photoList.getSelectionModel().getSelectedIndex();		
		Button back = new Button("Previous");
		back.setPrefWidth(100);
		gpBot.add(back, 2, 0);		
		back.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				if (pic.selectedIndex == 0){
					Alert LastPhoto = new Alert(AlertType.ERROR);
					LastPhoto.setTitle("Something went wrong!");
					LastPhoto.setHeaderText("No Photo before this.");
					LastPhoto.showAndWait();
				}
				else{
					int priorPhotoIndex = pic.selectedIndex-1;
					pic.selectedIndex--;
					Photo priorPhoto = userPhotos.get(priorPhotoIndex);
					
					priorPhoto.image.setPreserveRatio(true);
					priorPhoto.image.setFitWidth(500);
					priorPhoto.image.setFitHeight(500);
					
					picBP.setCenter(priorPhoto.image);
					
					caption.setText(priorPhoto.caption);
					
					Date d = new Date(priorPhoto.dateModified);
			    	String dF = dateFormat.format(d);
					dT.setText(dF);
					
					picBP.setTop(gpTop);
					
					for (int i = 0; i < priorPhoto.tags.size(); i++){
						setTag.setText(setTag.getText() + priorPhoto.types.get(i) + ": " + priorPhoto.tags.get(i) + "\n");
					}
				}
			}
		});
		
		Button next = new Button("Next");
		next.setPrefWidth(100);
		gpBot.add(next, 3, 0);		
		next.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				if (pic.selectedIndex == userPhotos.size()-1){
					Alert LastPhoto = new Alert(AlertType.ERROR);
					LastPhoto.setTitle("Something went wrong!");
					LastPhoto.setHeaderText("No Photo After");
					LastPhoto.showAndWait();
				}
				else{
					
					int nextPhotoIndex = pic.selectedIndex+1;
					pic.selectedIndex = nextPhotoIndex;
					Photo nextPhoto = userPhotos.get(nextPhotoIndex);			
					
					nextPhoto.image.setPreserveRatio(true);
					nextPhoto.image.setFitWidth(500);
					nextPhoto.image.setFitHeight(500);
					
					picBP.setCenter(nextPhoto.image);
					
					caption.setText(nextPhoto.caption);
					
					Date d = new Date(nextPhoto.dateModified);
			    	String dF = dateFormat.format(d);
					dT.setText(dF);
					
					picBP.setTop(gpTop);
					
					for (int i = 0; i < nextPhoto.tags.size(); i++){
						setTag.setText(setTag.getText() + nextPhoto.types.get(i) + ": " + nextPhoto.tags.get(i) + "\n");
					}
				}
			}
		});		
		picBP.setBottom(gpBot);		
		return picBP;
	}
		
	/**
	 * This class is responsible for the logout button and its functions.
	 *
	 * @param logout		logout button 
	 * @param secondStage 	logout button schematics
	 */
	private void logoutButton(Button logout, Stage secondStage){
		logout.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				secondStage.close();
				Stage primaryStage = new Stage();
				BorderPane bpLogin = loginBP(primaryStage);
				Scene scene = new Scene(bpLogin, 700, 700);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.setResizable(false);
				primaryStage.show();
			}
		});
	}

	/**
	 * This class is responsible for the safe quit button and its functions.
	 *
	 * @param safequit 		safe quit Button
	 * @param secondStage 	safe quit button schematics
	 */
	private void safeQuitButton(Button safequit, Stage secondStage){
		safequit.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				secondStage.close();
			}
		});
	}

	/**
	 * This class is responsible for tracking the oldest date of an album.
	 *
	 * @param photoList 	photo list 
	 * @param userPhotos 	user photos 
	 * @return date modified of the photo
	 */
	private long findOldestDate(ListView<Photo> photoList, ObservableList<Photo> userPhotos){
		if (userPhotos.size() == 0){
			return 0L;
		}
		else{
			long oldestDate = userPhotos.get(0).file.lastModified();
			for (int i = 1; i < userPhotos.size(); i++){
				if (Long.valueOf(oldestDate).compareTo(userPhotos.get(i).dateModified) > 0){
					oldestDate = userPhotos.get(i).dateModified;
				}
			}
			return oldestDate;
		}
	}
	
	/**
	 * This class is responsible for tracking the newest date of an album.
	 *
	 * @param photoList 	photo list
	 * @param userPhotos 	user photos
	 * @return date modified of the photo
	 */
	private long findNewestDate(ListView<Photo> photoList, ObservableList<Photo> userPhotos){
		if (userPhotos.size() == 0){
			return 0L;
		}
		else{
			long newestDate = userPhotos.get(0).file.lastModified();
			for (int i = 1; i < userPhotos.size(); i++){
				if (Long.valueOf(newestDate).compareTo(userPhotos.get(i).dateModified) < 0){
					newestDate = userPhotos.get(i).dateModified;
				}
			}
			return newestDate;
		}
	}
	
	/**
	 * This class is the main method that will launch the application.
	 *
	 * @param args arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
