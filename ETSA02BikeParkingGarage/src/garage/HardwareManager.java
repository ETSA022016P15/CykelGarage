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
	
	private class pinTerminalObserver implements PincodeObserver {
		ArrayList<Character> list = new ArrayList<Character>();
		Long startTime = null;
		
		@Override
		public void handleCharacter(char c) {
			list.add(c);
	
			if(startTime == null){
				startTime = System.currentTimeMillis();
			}
			
			if(System.currentTimeMillis() - startTime > 3000){
				terminal.lightLED(0, 1);
				list.clear();
				startTime = null;
			}
			if(c == '#'){
				terminal.lightLED(0, 1);
				list.clear();
				startTime = null;
			}
			
			if(c == '*'){
				list.remove(list.size()-1);
				if(list.size() >= 1){
					list.remove(list.size()-1);
				}else{
					terminal.lightLED(0, 1);
				}
			}
			
			startTime = System.currentTimeMillis();
			if(list.size() == 5){
				//metod för att kolla pincode
				System.out.println(list);
				list.clear();
				terminal.lightLED(1, 2);
				startTime = null;
			}
			
		}
	}
}
