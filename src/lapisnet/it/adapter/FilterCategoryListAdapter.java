package lapisnet.it.adapter;

import java.util.ArrayList;
import lapisnet.it.classes.Category;
import lapisnet.it.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FilterCategoryListAdapter extends BaseAdapter {

	private Context activity;
	private ArrayList<Category> data;

	public FilterCategoryListAdapter(Context a, ArrayList<Category> d) {
		activity = a;
		data = d;
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.filter_category_item, null);
		}

		TextView id = (TextView) vi.findViewById(R.id.cat_id);
		TextView titolo = (TextView) vi.findViewById(R.id.cat_title);
		TextView icon = (TextView) vi.findViewById(R.id.cat_icon);

		final Category item = data.get(position);
		id.setText(item.getId());
		titolo.setText(item.getTitle());
		icon.setText(item.getTitle().substring(0, 1));

		return vi;
	}
}