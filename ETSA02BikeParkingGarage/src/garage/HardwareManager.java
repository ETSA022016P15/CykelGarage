package garage;
import hardware_interfaces.*;
import hardware_testdrivers.*;

public class HardwareManager {
	BarcodeScanner entryScanner;
	
	public HardwareManager() {
		entryScanner = new BarcodeScannerTestDriver("Titel", 0, 0);
		entryScanner.registerObserver(new EntryBarcodeObserver());
	}
	
	private class EntryBarcodeObserver implements BarcodeObserver {
		@Override
		public void handleBarcode(String s) {
			//Kod för vad som ska hända när en streckkod skannas
		}
	}
}