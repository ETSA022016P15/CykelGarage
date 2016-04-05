package garage;

public class User {
	private String name;
	private String id;
	private int pin;
	
	public User(String name, String id) {
		this.name = name;
		this.id = id;
	}
	
	public void setPin(int pin) {
		this.pin = pin;
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			return ((User) obj).name.equals(name) && ((User) obj).id.equals(id);
		} else {
			return false;
		}
	}
	
	public int getPin() {
		return pin;
	}
	
	public int hashCode() {
		return id.hashCode();
	}
}
