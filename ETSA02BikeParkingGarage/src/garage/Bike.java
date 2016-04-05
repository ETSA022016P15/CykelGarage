package garage;

public class Bike {
	private String barcode;
	private User owner;
	private String frameNbr;
	
	public Bike(User owner, String barcode, String frameNbr) {
		this.owner = owner;
		this.barcode = barcode;
		this.frameNbr = frameNbr;
	}
	
	public String getBarcode() {
		return barcode;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public String getFrameNbr() {
		return frameNbr;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Bike) {
			return ((Bike) obj).frameNbr.equals(frameNbr) && ((Bike) obj).barcode.equals(barcode);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return frameNbr.hashCode();
	}
}
