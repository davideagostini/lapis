package lapisnet.it.adapter;

public class ListNavigationItemModel {

	public int title;
	public int iconRes;
	public boolean isHeader;

	public ListNavigationItemModel(int title, int iconRes, boolean header) {
		this.title = title;
		this.iconRes = iconRes;
		this.isHeader = header;
	}

	public ListNavigationItemModel(int title, int iconRes) {
		this(title, iconRes, false);
	}
}
