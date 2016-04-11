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

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		HardwareManager hardware = new HardwareManager();

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

	public void stop() {

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

	private MenuBar makeMenu() throws Exception {
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
		final MenuItem menuFindBikes = new MenuItem("Find bikes");
		menuFindBikes.setOnAction(e -> findBikes());
		final MenuItem menuFindOwner = new MenuItem("Find owner");
		menuFindOwner.setOnAction(e -> findOwner());
		final MenuItem menuFindBarcode = new MenuItem("Find barcode");
		menuFindBarcode.setOnAction(e -> findBarcode());
		menuFind.getItems().addAll(menuFindBikes, menuFindOwner, menuFindBarcode);

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
					int PIN = DatabaseInterface.put(toBeAdded[0], toBeAdded[1]);
					if (PIN > 0) {
						textArea.setText(toBeAdded[0] + ", ID: " + toBeAdded[1]
								+ ", has been added and been assigned the PIN code " + PIN);
					} else if (PIN == 0) {
						textArea.setText(toBeAdded[0] + ", ID: " + toBeAdded[1] + ", already exists in the registry.");
					} else {
						textArea.setText("There was an error handling the request, please try again.");
					}
				}
			}
		}
	}

	private void addBike() {
		String[] labels = new String[3];
		labels[0] = "Name";
		labels[1] = "ID";
		labels[2] = "Frame number";
		Optional<String[]> result = threeInputsDialog("Add bike",
				"Enter the name and ID of the person you would like to add a bike to, as well as the frame number of the bike.",
				labels);
		if (result.isPresent() && result.get().length != 0) {
			String[] toBeAdded = result.get();
			if (toBeAdded.length != 3) {
				textArea.setText("Please fill in all the fields.");
			} else {
				int added = DatabaseInterface.addBike(toBeAdded[0], toBeAdded[1], toBeAdded[2]);
				if (added > 3) {
					textArea.setText("The bike with the frame number " + toBeAdded[2] + " has been added to the user "
							+ toBeAdded[0] + ", ID: " + toBeAdded[1] + ".");
				} else if (added == 3) {
					textArea.setText("The bike with the frame number " + toBeAdded[2]
							+ " is already registered to another user.");
				} else if (added == 2) {
					textArea.setText(
							"The user " + toBeAdded[0] + ", ID: " + toBeAdded[1] + ", was not found in the registry.");
				} else if (added == 1) {
					textArea.setText("The user " + toBeAdded[0] + ", ID: " + toBeAdded[1]
							+ ", already has the maximum amount of registered bikes.");
				} else if (added == 0) {
					textArea.setText("The bike with the frame number " + toBeAdded[2] + " is already registered to "
							+ toBeAdded[0] + ", ID: " + toBeAdded[1] + ".");
				} else {
					textArea.setText("There was an error handling the request, please try again.");
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
					int removed = DatabaseInterface.removeUser(inputUser[0], inputUser[1]);
					if (removed == 0) {
						textArea.setText("The user " + inputUser[0] + ", ID: " + inputUser[1]
								+ ", does not exist in the registry.");
					} else if (removed > 0) {
						textArea.setText("The user " + inputUser[0] + ", ID: " + inputUser[1] + ", was removed.");
					} else {
						textArea.setText("There was an error handling the request, please try again.");
					}
				}
			}
		}
	}

	private void removeBike() {
		String[] labels = new String[3];
		labels[0] = "Name";
		labels[1] = "ID";
		labels[2] = "Frame number";
		Optional<String[]> result = threeInputsDialog("Remove bike",
				"Enter the name and ID of the person you would like to add a bike to, as well as the frame number of the bike.",
				labels);
		if (result.isPresent() && result.get().length != 0) {
			String[] toBeRemoved = result.get();
			if (toBeRemoved.length != 3) {
				textArea.setText("Please fill in all the fields.");
			} else {
				int removed = DatabaseInterface.removeBike(toBeRemoved[0], toBeRemoved[1], toBeRemoved[2]);
				if (removed > 1) {
					textArea.setText("The bike with frame number " + toBeRemoved[2] + " has been removed from the user "
							+ toBeRemoved[0] + ", ID: " + toBeRemoved[1] + ".");
				} else if (removed == 1) {
					textArea.setText("The user " + toBeRemoved[0] + ", ID: " + toBeRemoved[1]
							+ ", was not found in the registry.");
				} else if (removed == 0) {
					textArea.setText(
							"The bike with frame number " + toBeRemoved[2] + " was not found registered to the user "
									+ toBeRemoved[0] + ", ID: " + toBeRemoved[1] + ".");
				} else {
					textArea.setText("There was an error handling the request, please try again.");
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
			Set<String> bikes = DatabaseInterface.getBikes(inputUser[0], inputUser[1]);
			if (bikes == null) {
				textArea.setText(
						"The contact " + inputUser[0] + ", ID: " + inputUser[1] + ", was not found in the registry.");
			} else if (!bikes.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				sb.append("The following bikes belong to " + inputUser[0] + ", ID: " + inputUser[1] + ":");
				for (String bike : bikes) {
					sb.append("\n" + bike);
				}
				textArea.setText(sb.toString());
			} else {
				textArea.setText("There are no registered bikes to the user " + inputUser[0] + ", ID: " + inputUser[1] + ".");
			}
		}
	}

	private void findOwner() {
		Optional<String> result = oneInputDialog("Find owner",
				"Enter the frame number of the bike that you would like to see who it belongs to.", "Frame number");
		if (result.isPresent() && result.get().length() != 0) {
			String frameNbr = result.get();
			String[] owner = DatabaseInterface.findOwner(frameNbr);
			if (owner != null) {
				textArea.setText("The bike with frame number " + frameNbr + " belongs to: " + owner[0] + ", ID: "
						+ owner[1]);
			} else {
				textArea.setText("The frame number " + frameNbr + " was not found in the registry.");
			}
		}
	}
	
	private void findBarcode() {
		Optional<String> result = oneInputDialog("Find barcode",
				"Enter the frame number of the bike whose barcode you would like to see.", "Frame number");
		if (result.isPresent() && result.get().length() != 0) {
			String frameNbr = result.get();
			String barcode = DatabaseInterface.findBarcode(frameNbr);
			if (barcode != null) {
				textArea.setText("The bike with frame number " + frameNbr + " has the barcode " + barcode);
			} else {
				textArea.setText("The frame number " + frameNbr + " was not found in the registry.");
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

	private Optional<String[]> threeInputsDialog(String title, String headerText, String[] labels) {
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(headerText);
		dialog.setResizable(true);
		Label label1 = new Label(labels[0] + ':');
		Label label2 = new Label(labels[1] + ':');
		Label label3 = new Label(labels[2] + ':');
		TextField tf1 = new TextField();
		TextField tf2 = new TextField();
		TextField tf3 = new TextField();
		GridPane grid = new GridPane();
		grid.add(label1, 1, 1);
		grid.add(tf1, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(tf2, 2, 2);
		grid.add(label3, 1, 3);
		grid.add(tf3, 2, 3);
		dialog.getDialogPane().setContent(grid);
		ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel, buttonTypeOk);
		dialog.setResultConverter(new Callback<ButtonType, String>() {
			@Override
			public String call(ButtonType b) {
				String inputs = null;
				if (b == buttonTypeOk) {
					inputs = tf1.getText() + ":" + tf2.getText() + ":" + tf3.getText();
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
