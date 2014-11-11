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

public class EventListAdapter extends ArrayAdapter<Event> {
	private Context activity;
	private ArrayList<Event> data;

	public EventListAdapter(Context a, ArrayList<Event> d) {
		super(a, R.layout.list_item_event, d);
		activity = a;
		data = d;
	}

	static class ViewHolder {
		public TextView day;
		public TextView month;
		public TextView day2;
		public TextView month2;
		public TextView day3;
		public TextView month3;
		public TextView id;
		public TextView title;
		public TextView luogo;
		public TextView category;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.list_item_event, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.id = (TextView) vi.findViewById(R.id.event_id);
			viewHolder.title = (TextView) vi.findViewById(R.id.event_title);
			viewHolder.luogo = (TextView) vi.findViewById(R.id.event_luogo);
			viewHolder.category = (TextView) vi.findViewById(R.id.event_category);
			viewHolder.day = (TextView) vi.findViewById(R.id.day);
			viewHolder.month = (TextView) vi.findViewById(R.id.month);
			viewHolder.day2 = (TextView) vi.findViewById(R.id.day2);
			viewHolder.month2 = (TextView) vi.findViewById(R.id.month2);
			viewHolder.day3 = (TextView) vi.findViewById(R.id.day3);
			viewHolder.month3 = (TextView) vi.findViewById(R.id.month3);
			vi.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) vi.getTag();
		final Event event = data.get(position);

		holder.id.setText(event.getId());
		holder.title.setText(event.getTitle());
		holder.luogo.setText(event.getLuogo());
		holder.category.setText(event.getCat());
		
		if(event.getType().equalsIgnoreCase("1"))  {
			holder.day.setVisibility(View.VISIBLE);
			holder.month.setVisibility(View.VISIBLE);			
			holder.day.setText(event.getDay());
			holder.month.setText(event.getMonth());
			
			holder.day2.setVisibility(View.GONE);
			holder.month2.setVisibility(View.GONE);
			holder.day3.setVisibility(View.GONE);
			holder.month3.setVisibility(View.GONE);
		} 
		else if(event.getType().equalsIgnoreCase("2")) {
			holder.day2.setVisibility(View.VISIBLE);
			holder.month2.setVisibility(View.VISIBLE);	
			holder.day2.setText(event.getDay());
			holder.month2.setText(event.getMonth());
			
			holder.day.setVisibility(View.GONE);
			holder.month.setVisibility(View.GONE);
			holder.day3.setVisibility(View.GONE);
			holder.month3.setVisibility(View.GONE);
		}
		else {
			holder.day3.setVisibility(View.VISIBLE);
			holder.month3.setVisibility(View.VISIBLE);	
			holder.day3.setText(event.getDay());
			holder.month3.setText(event.getMonth());
			
			holder.day.setVisibility(View.GONE);
			holder.month.setVisibility(View.GONE);
			holder.day2.setVisibility(View.GONE);
			holder.month2.setVisibility(View.GONE);
		}

		return vi;
	}

}


