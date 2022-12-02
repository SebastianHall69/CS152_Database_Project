public class User {
	private int _userid;
	private double _latitude;
	private double _longitude;
	private String _name;
	private String _type;
	
	public User() {
		_userid = -1;
		_latitude = -1;
		_longitude = -1;
		_name = null;
		_type = null;
	}

	void setUserid(int userid) {
		this._userid = userid;
	}

	void setLatitude(double latitude) {
		this._latitude = latitude;
	}

	void setLongitude(double longitude) {
		this._longitude = longitude;
	}

	void setName(String name) {
		this._name = name;
	}

	void setType(String type) {
		this._type = type;
	}

	int userid() {
		return this._userid;
	}

	double latitude() {
		return this._latitude;
	}

	double longitude() {
		return this._longitude;
	}

	String name() {
		return this._name;
	}

	String type() {
		return this._type;
	}
}
