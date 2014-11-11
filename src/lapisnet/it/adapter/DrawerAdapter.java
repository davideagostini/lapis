package lapisnet.it.adapter;

import lapisnet.it.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerAdapter extends ArrayAdapter<ListNavigationItemModel>{

	public DrawerAdapter(Context context) {
	    super(context, 0);
	}

	public void addHeader(int title) {
	    add(new ListNavigationItemModel(title, -1, true));
	}

	public void addItem(int title, int icon) {
	    add(new ListNavigationItemModel(title, icon, false));
	}

	public void addItem(ListNavigationItemModel itemModel) {
	    add(itemModel);
	}

	@Override
	public int getViewTypeCount() {
	    return 2; 
	}

	@Override
	public int getItemViewType(int position) {
	    return getItem(position).isHeader ? 0 : 1;
	}

	@Override
	public boolean isEnabled(int position) {
	    return !getItem(position).isHeader;
	}

	public static class ViewHolder {
	    public final TextView textHolder;
	    public final ImageView imageHolder;

	    public ViewHolder(TextView text1, ImageView image1) {
	        this.textHolder = text1;
	        this.imageHolder = image1;
	    }
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ListNavigationItemModel item = getItem(position);
	    ViewHolder holder = null;
	    View view = convertView;

	    if (view == null) {
	        int layout = R.layout.list_item_drawer;
	        if (item.isHeader)
	        	layout = R.layout.ns_menu_row_header;

	        view = LayoutInflater.from(getContext()).inflate(layout, null);

	        TextView text1 = (TextView) view.findViewById(R.id.nd_title);
	        ImageView image1 = (ImageView) view.findViewById(R.id.nd_thumb_image);
	        view.setTag(new ViewHolder(text1, image1));
	    }

	    if (holder == null && view != null) {
	        Object tag = view.getTag();
	        if (tag instanceof ViewHolder) {
	            holder = (ViewHolder) tag;
	        }
	    }

	    if (item != null && holder != null) {
	        if (holder.textHolder != null)
	            holder.textHolder.setText(item.title);

	        if (holder.imageHolder != null) {
	            if (item.iconRes > 0) {
	                holder.imageHolder.setVisibility(View.VISIBLE);
	                holder.imageHolder.setImageResource(item.iconRes);
	            } else {
	                holder.imageHolder.setVisibility(View.GONE);
	            }
	        }
	    }

	    return view;
	}
	
}
