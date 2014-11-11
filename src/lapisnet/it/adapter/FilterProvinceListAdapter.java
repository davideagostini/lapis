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

public class FilterProvinceListAdapter extends BaseAdapter {

	private Context activity;
	private ArrayList<Category> data;

	public FilterProvinceListAdapter(Context a, ArrayList<Category> d) {
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
			vi = inflater.inflate(R.layout.filter_province_item, null);
		}

		TextView id = (TextView) vi.findViewById(R.id.province_id);
		TextView titolo = (TextView) vi.findViewById(R.id.province_title);
		TextView icon = (TextView) vi.findViewById(R.id.province_icon);

		final Category item = data.get(position);
		id.setText(item.getId());
		titolo.setText(item.getTitle());
		setIconProvince(icon, item.getId());

		return vi;
	}
	
	
	public void setIconProvince(TextView t, String id) {
		
		switch(Integer.parseInt(id)) {
		case 1:
			t.setText("AG");
			break;
		case 2:
			t.setText("CL");
			break;
		case 3:
			t.setText("CT");
			break;
		case 4:
			t.setText("EN");
			break;
		case 5:
			t.setText("ME");
			break;
		case 6:
			t.setText("PA");
			break;
		case 7:
			t.setText("RG");
			break;
		case 8:
			t.setText("SR");
			break;
		case 9:
			t.setText("TP");
			break;	
		}
	}
}