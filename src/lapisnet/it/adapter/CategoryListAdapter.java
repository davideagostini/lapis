package lapisnet.it.adapter;

import java.util.ArrayList;
import lapisnet.it.classes.Category;

import lapisnet.it.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CategoryListAdapter extends ArrayAdapter<Category> {
	private Context activity;
	private ArrayList<Category> data;

	public CategoryListAdapter(Context a, ArrayList<Category> d) {
		super(a, R.layout.list_item_category, d);
		activity = a;
		data = d;
	}
	
	static class ViewHolder {
		public TextView id;
		public TextView title;
		public TextView item_icon;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.list_item_category, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.id = (TextView) vi.findViewById(R.id.cat_id);
			viewHolder.title = (TextView) vi.findViewById(R.id.cat_title);
			viewHolder.item_icon = (TextView) vi.findViewById(R.id.item_icon);
			vi.setTag(viewHolder);		
		}
		
		ViewHolder holder = (ViewHolder) vi.getTag();
		final Category item = data.get(position);
		holder.id.setText(item.getId());
		holder.title.setText(item.getTitle());
		holder.item_icon.setText(item.getTitle().substring(0, 1));

		return vi;
	}
}