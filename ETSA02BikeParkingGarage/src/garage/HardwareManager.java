package garage;
import hardware_interfaces.*;
import hardware_testdrivers.*;

public class HardwareManager {
	BarcodeScanner entryScanner;
	BarcodeScanner exitScanner;
	PincodeTerminal terminal;
	
	
	public HardwareManager() {
		entryScanner = new BarcodeScannerTestDriver("Entry scanner", 20, 50);
		entryScanner.registerObserver(new EntryBarcodeObserver());
		exitScanner = new BarcodeScannerTestDriver("Exit scanner", 20, 400);
		exitScanner.registerObserver(new ExitBarcodeObserver());
		terminal = new PincodeTerminalTestDriver("Pincode terminal", 20, 175);
		terminal.registerObserver(new PincodeTerminalObserver());
	}
	
	private class EntryBarcodeObserver implements BarcodeObserver {
		@Override
		public void handleBarcode(String s) {
			//Kod för vad som ska hända när en streckkod skannas
		}
	}
	
	private class ExitBarcodeObserver implements BarcodeObserver {
		@Override
		public void handleBarcode(String s) {
			//Kod för vad som ska hända när en streckkod skannas
		}
	}
	
	private class PincodeTerminalObserver implements PincodeObserver {
		@Override
		public void handleCharacter(char c) {
			// TODO Auto-generated method stub
			
		}
	}
}