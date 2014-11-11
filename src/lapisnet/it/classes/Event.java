package lapisnet.it.classes;

import java.io.Serializable;

public class Event implements Serializable{

	private static final long serialVersionUID = -4912226994021191930L;
	private String id;
	private String title;
	private String type;
	private String day;
	private String month;
	private String luogo;
	private String cat;
	private String thumb;

	public Event(String id, String title, String type, String day, String month, String luogo, String cat, String thumb) {
		this.id = id;
		this.title = title;
		this.type = type;
		this.day = day;
		this.month = month;
		this.luogo = luogo;
		this.cat = cat;
		this.thumb = thumb;
	}

	public Event(String id, String title, String type, String day, String month, String luogo, String cat) {
		this.id = id;
		this.title = title;
		this.type = type;
		this.day = day;
		this.month = month;
		this.luogo = luogo;
		this.cat = cat;
	}

	public Event(String id, String title, String day, String month, String luogo, String cat) {
		this.id = id;
		this.title = title;
		this.day = day;
		this.month = month;
		this.luogo = luogo;
		this.cat = cat;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLuogo() {
		return luogo;
	}

	public void setLuogo(String luogo) {
		this.luogo = luogo;
	}

	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		this.cat = cat;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
}
