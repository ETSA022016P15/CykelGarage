package garage;

import java.util.Optional;
import java.util.Set;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class BicycleGarageApplication extends Application {
	private BorderPane root;
	private TextArea textArea;
	private UserAndBikeHandler userAndBikeHandler;

	@Override
	public void start(Stage stage) {
		HardwareManager hardware = new HardwareManager();
		userAndBikeHandler = new UserAndBikeHandler(); // här får vi sen lägga
														// till att den ska läsa
														// in från en fil och en
														// databas

		// ...
		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setPrefColumnCount(50);
		textArea.setPrefRowCount(20);
		root = new BorderPane();
		root.setTop(makeMenu());
		root.setCenter(textArea);
		root.setBottom(buttons());
		Scene scene = new Scene(root, 400, 400);
		stage.setScene(scene);
		stage.setTitle("Title");
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private HBox buttons() {
		Button quit = new Button("Quit");
		quit.setOnAction(event -> {
			Platform.exit();
		});
		HBox temp = new HBox();
		temp.getChildren().add(quit);
		return temp;
	}

	private MenuBar makeMenu() {
		final Menu menuEdit = new Menu("Edit");
		final MenuItem menuAddUser = new MenuItem("Add user");
		menuAddUser.setOnAction(e -> addUser());
		final MenuItem menuAddBike = new MenuItem("Add bike");
		menuAddBike.setOnAction(e -> addBike());
		final MenuItem menuRemoveUser = new MenuItem("Remove user");
		menuRemoveUser.setOnAction(e -> removeUser());
		final MenuItem menuRemoveBike = new MenuItem("Remove bike");
		menuRemoveBike.setOnAction(e -> removeBike());
		menuEdit.getItems().addAll(menuAddUser, menuAddBike, menuRemoveUser, menuRemoveBike);

		final Menu menuFind = new Menu("Find");
		final MenuItem menuFindUser = new MenuItem("Find bikes");
		menuFindUser.setOnAction(e -> findBikes());
		final MenuItem menuFindBike = new MenuItem("Find user");
		menuFindBike.setOnAction(e -> findUser());
		menuFind.getItems().addAll(menuFindUser, menuFindBike);

		final Menu menuView = new Menu("View");
		final MenuItem menuShowAll = new MenuItem("Show all");
		menuShowAll.setOnAction(e -> showAll());
		final MenuItem menuShowLog = new MenuItem("Show log");
		menuShowLog.setOnAction(e -> showLog());
		menuView.getItems().addAll(menuShowLog);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(menuEdit, menuFind, menuView);
		return menuBar;
	}

	private void addUser() {
		String[] labels = new String[2];
		labels[0] = "Name";
		labels[1] = "ID";
		Optional<String[]> result = twoInputsDialog("Add user",
				"Enter the name and ID of the person you would like to add.", labels);
		if (result.isPresent() && result.get().length != 0) {
			String[] toBeAdded = result.get();
			if (toBeAdded.length == 1) {
				textArea.setText("Please enter an ID along with the name.");
			} else {
				if (toBeAdded[0].equals("")) {
					textArea.setText("Please enter a name along with the ID");
				} else {
					int PIN = userAndBikeHandler.put(toBeAdded[0], toBeAdded[1]);
					if (PIN > 0) {
						textArea.setText(toBeAdded[0] + ", ID: " + toBeAdded[1] + ", has been added and been assigned the PIN code " + PIN);
					} else {
						textArea.setText(toBeAdded[0] + ", ID: " + toBeAdded[1] + ", already exists in the registry.");
					}
				}
			}
		}
	}

	private void addBike() {
		String[] labels = new String[2];
		labels[0] = "Name";
		labels[1] = "ID";
		Optional<String[]> result = twoInputsDialog("Add bike",
				"Enter the name and ID of the person you would like to add a bike to.", labels);
		if (result.isPresent() && result.get().length != 0) {
			String[] inputUser = result.get();
			if (inputUser.length == 1) {
				textArea.setText("Please enter a number along with the name.");
			} else {
				if (inputUser[0].equals("")) {
					textArea.setText("Please enter a name along with the number");
				} else {
					User user = new User(inputUser[0], inputUser[1]);
					if (userAndBikeHandler.containsUser(user)) {
						Optional<String> result2 = oneInputDialog("Add bike",
								"Enter the frame number of the bike you would like to add.", "Frame number");
						if (result2.isPresent() && result2.get().length() != 0) {
							String frameNbr = result2.get();
							boolean added = userAndBikeHandler.addBike(user, frameNbr);
							if (added) {
								textArea.setText(
										"The bike with the frame number " + frameNbr + " has been added to the user "
												+ user.getName() + ", ID: " + user.getId() + ".");
							} else {
								if (userAndBikeHandler.checkIfFull(user)) {
									textArea.setText("The user " + user.getName() + ", ID: " + user.getId()
											+ ", already has the maximum amount of registered bikes.");
								} else {
									textArea.setText(
											"The bike with the frame number " + frameNbr + " is already registered to "
													+ user.getName() + ", ID: " + user.getId() + ".");
								}
							}
						} else {
							textArea.setText("Please enter a frame number.");
						}
					} else {
						textArea.setText(inputUser[0] + ", ID: " + inputUser[1] + ", does not exist in the registry.");
						return;
					}
				}
			}
		}
	}

	private void removeUser() {
		String[] labels = new String[2];
		labels[0] = "Name";
		labels[1] = "ID";
		Optional<String[]> result = twoInputsDialog("Remove user",
				"Enter the name and ID of the person you would like to remove.", labels);
		if (result.isPresent() && result.get().length != 0) {
			String[] inputUser = result.get();
			if (inputUser.length == 1) {
				textArea.setText("Please enter an ID along with the name.");
			} else {
				if (inputUser[0].equals("")) {
					textArea.setText("Please enter a name along with the ID");
				} else {
					boolean removed = userAndBikeHandler.removeUser(inputUser[0], inputUser[1]);
					if (removed) {
						textArea.setText(inputUser[0] + ", ID: " + inputUser[1] + ", has been removed.");
					} else {
						textArea.setText(inputUser[0] + ", ID: " + inputUser[1] + ", does not exist in the registry.");
					}
				}
			}
		}
	}

	private void removeBike() {
		String[] labels = new String[2];
		labels[0] = "Name";
		labels[1] = "ID";
		Optional<String[]> result = twoInputsDialog("Remove bike",
				"Enter the name and ID of the person you would like to remove a bike from.", labels);
		if (result.isPresent() && result.get().length != 0) {
			String[] inputUser = result.get();
			if (inputUser.length == 1) {
				textArea.setText("Please enter an ID along with the name.");
			} else {
				if (inputUser[0].equals("")) {
					textArea.setText("Please enter a name along with the ID");
				} else {
					User user = new User(inputUser[0], inputUser[1]);
					if (userAndBikeHandler.containsUser(user)) {
						Optional<String> result2 = oneInputDialog("Remove bike",
								"Enter the frame number of the bike you would like to remove.", "Frame number");
						if (result2.isPresent() && result2.get().length() != 0) {
							String frameNbr = result2.get();
							boolean removed = userAndBikeHandler.removeBike(user, frameNbr);
							if (removed) {
								textArea.setText("The bike with the frame number " + frameNbr
										+ " has been removed from the user " + user.getName() + ", ID: " + user.getId()
										+ ".");
							} else {
								textArea.setText("The bike with the frame number " + frameNbr
										+ " was not found in the register belonging to " + user.getName() + ", ID: "
										+ user.getId() + ".");
							}
						} else {
							textArea.setText("Please enter a frame number.");
						}
					} else {
						textArea.setText(inputUser[0] + ", ID: " + inputUser[1] + ", does not exist in the registry.");
						return;
					}
				}
			}
		}
	}

	private void findBikes() {
		String[] labels = new String[2];
		labels[0] = "Name";
		labels[1] = "ID";
		Optional<String[]> result = twoInputsDialog("Find bikes",
				"Enter the name and ID of the person whose bikes you would like to see.", labels);
		if (result.isPresent() && result.get().length != 0) {
			String[] inputUser = result.get();
			User user = new User(inputUser[0], inputUser[1]);
			Set<Bike> bikes = userAndBikeHandler.getBikes(user);
			if (!bikes.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				sb.append("The following bikes belong to " + inputUser[0] + ", ID: " + inputUser[1] + ":");
				for (Bike bike : bikes) {
					sb.append("\n" + "Frame number: " + bike.getFrameNbr() + ", Barcode: " + bike.getBarcode());
				}
				textArea.setText(sb.toString());
			} else {
				textArea.setText(
						"The contact " + inputUser[0] + ", ID: " + inputUser[1] + ", was not found in the phone book.");
			}
		}
	}

	private void findUser() {
		Optional<String> result = oneInputDialog("Find user",
				"Enter the barcode bike that you would like to see who it belongs to.", "Barcode");
		if (result.isPresent() && result.get().length() != 0) {
			String barcode = result.get();
			User owner = userAndBikeHandler.findOwner(barcode);
			if (owner != null) {
				textArea.setText("The bike with barcode " + barcode + " belongs to: " + owner.getName() + ", ID: "
						+ owner.getId());
			} else {
				textArea.setText("The barcode " + barcode + " was not found in the registry.");
			}
		}
	}

	private Object showAll() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object showLog() {
		// TODO Auto-generated method stub
		return null;
	}

	private Optional<String> oneInputDialog(String title, String headerText, String label) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(title);
		dialog.setHeaderText(headerText);
		dialog.setContentText(label + ':');
		return dialog.showAndWait();
	}

	private Optional<String[]> twoInputsDialog(String title, String headerText, String[] labels) {
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(headerText);
		dialog.setResizable(true);
		Label label1 = new Label(labels[0] + ':');
		Label label2 = new Label(labels[1] + ':');
		TextField tf1 = new TextField();
		TextField tf2 = new TextField();
		GridPane grid = new GridPane();
		grid.add(label1, 1, 1);
		grid.add(tf1, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(tf2, 2, 2);
		dialog.getDialogPane().setContent(grid);
		ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel, buttonTypeOk);
		dialog.setResultConverter(new Callback<ButtonType, String>() {
			@Override
			public String call(ButtonType b) {
				String inputs = null;
				if (b == buttonTypeOk) {
					inputs = tf1.getText() + ":" + tf2.getText();
				}
				return inputs;
			}
		});
		tf1.requestFocus();
		Optional<String> result = dialog.showAndWait();
		String[] input = null;
		if (result.isPresent()) {
			input = result.get().split(":");
		}
		return Optional.ofNullable(input);
	}

}
