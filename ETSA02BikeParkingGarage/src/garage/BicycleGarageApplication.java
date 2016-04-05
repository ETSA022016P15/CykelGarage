package garage;

import javafx.application.Application;
import javafx.stage.Stage;

public class BicycleGarageApplication extends Application {
	@Override
	public void start(Stage stage) {
		
		HardwareManager hardware = new HardwareManager();
		
		//...
		
//		Scene scene = new Scene(...);
//		stage.setScene(scene);
		stage.setTitle("Title");
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
