package lapisnet.it.adapter;

import java.util.ArrayList;
import lapisnet.it.R;
import lapisnet.it.classes.Event;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventListSuggestedAdapter extends ArrayAdapter<Event> {
	private Context activity;
	private ArrayList<Event> data;

	public EventListSuggestedAdapter(Context a, ArrayList<Event> d) {
		super(a, R.layout.list_item_event_suggested, d);
		activity = a;
		data = d;
	}

	static class ViewHolder {
		public TextView id;
		public TextView title;
		public TextView day;
		public TextView month;
		public TextView luogo;
		public TextView category;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.list_item_event_suggested, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.id = (TextView) vi.findViewById(R.id.id_sugg);
			viewHolder.title = (TextView) vi.findViewById(R.id.title_sugg);
			viewHolder.day = (TextView) vi.findViewById(R.id.day_sugg);
			viewHolder.month = (TextView) vi.findViewById(R.id.month_sugg);
			viewHolder.luogo = (TextView) vi.findViewById(R.id.luogo_sugg);
			viewHolder.category = (TextView) vi.findViewById(R.id.category_sugg);

			vi.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) vi.getTag();
		final Event event = data.get(position);

		holder.id.setText(event.getId());
		holder.title.setText(event.getTitle());
		holder.day.setText(event.getDay());
		holder.month.setText(event.getMonth());
		holder.luogo.setText(event.getLuogo());
		holder.category.setText(event.getCat());

		return vi;
	}
}