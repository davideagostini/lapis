package lapisnet.it.adapter;

import java.util.ArrayList;
import lapisnet.it.R;
import lapisnet.it.classes.Event;
import lapisnet.it.utility.ImageLoader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventListHomeAdapter extends ArrayAdapter<Event> {
	private Context activity;
	private ArrayList<Event> data;
	public ImageLoader imageLoader;


	public EventListHomeAdapter(Context a, ArrayList<Event> d) {
		super(a, R.layout.list_item_event_home, d);
		activity = a;
		data = d;
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	static class ViewHolder {
		public TextView id;
		public TextView title;
		public TextView date;
		public TextView luogo;
		public TextView category;
		public ImageView thumb_image;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.list_item_event_home, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.id = (TextView) vi.findViewById(R.id.event_id);
			viewHolder.title = (TextView) vi.findViewById(R.id.event_title);
			viewHolder.date = (TextView) vi.findViewById(R.id.event_date);
			viewHolder.luogo = (TextView) vi.findViewById(R.id.event_luogo);
			viewHolder.category = (TextView) vi.findViewById(R.id.event_category);
			viewHolder.thumb_image = (ImageView) vi.findViewById(R.id.event_thumb_image);

			vi.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) vi.getTag();
		final Event event = data.get(position);

		holder.id.setText(event.getId());
		holder.title.setText(event.getTitle());
		if(event.getType().equalsIgnoreCase("1"))
			holder.date.setText(event.getDay()+" "+event.getMonth());
		else if(event.getType().equalsIgnoreCase("2"))
			holder.date.setText(event.getDay()+" "+event.getMonth());
		else
			holder.date.setText(event.getDay()+" - "+event.getMonth());
		holder.luogo.setText(event.getLuogo());
		holder.category.setText(event.getCat());
		imageLoader.DisplayImage(event.getThumb(), holder.thumb_image);

		return vi;
	}



}