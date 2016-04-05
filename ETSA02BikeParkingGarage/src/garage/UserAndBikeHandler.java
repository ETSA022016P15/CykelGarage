package garage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class UserAndBikeHandler {
	private Map<User, Set<Bike>> map;
	private Set<Integer> barcodes;

	public UserAndBikeHandler() {
		map = new HashMap<User, Set<Bike>>();
		barcodes = new HashSet<Integer>();
	}

	public boolean put(String name, String id) {
		User user = new User(name, id);
		if (map.containsKey(user)) {
			return false;
		} else {
			Set<Bike> bikes = new HashSet<Bike>();
			map.put(user, bikes);
			return true;
		}
	}

	public boolean addBike(User user, String frameNbr) {
		Set<Bike> set = map.get(user);
		if (set.size() > 2) {
			return false;
		}
		for (Bike bike : set) {
			if (bike.getFrameNbr().equals(frameNbr) && bike.getOwner().equals(user)) {
				return false;
			}
		}
		Random rand = new Random();
		int code = rand.nextInt(100000);
		while (barcodes.contains(code)) {
			code = rand.nextInt(100000);
		}
		int zeroes = code;
		StringBuilder sb = new StringBuilder();
		while (zeroes < 10000) {
			sb.append(0);
			zeroes *= 10;
		}
		sb.append(code);
		String barcode = sb.toString();
		Bike bike = new Bike(user, barcode, frameNbr);
		map.get(user).add(bike);
		return true;
	}

	public boolean removeUser(String name, String id) {
		User user = new User(name, id);
		Set<Bike> bikes = map.remove(user);
		if (bikes == null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean removeBike(User user, String frameNbr) {
		Set<Bike> set = map.get(user);
		for (Bike bike : set) {
			if (bike.getFrameNbr().equals(frameNbr)) {
				barcodes.remove(Integer.parseInt(bike.getBarcode()));
				set.remove(bike);
				return true;
			}
		}
		return false;
	}

	public User findOwner(String barcode) {
		for (Set<Bike> set : map.values()) {
			for (Bike bike : set) {
				if (bike.getBarcode().equals(barcode)) {
					return bike.getOwner();
				}
			}
		}
		return null;
	}

	public Set<Bike> getBikes(User user) {
		return map.get(user);
	}

	public boolean containsUser(User user) {
		return map.containsKey(user);
	}

	public boolean checkIfFull(User user) {
		Set<Bike> set = map.get(user);
		if (set.size() > 3) {
			return false;
		}
		return true;
	}
}