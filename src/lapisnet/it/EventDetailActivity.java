package lapisnet.it;

import java.util.ArrayList;
import java.util.List;
import lapisnet.it.adapter.EventListSuggestedAdapter;
import lapisnet.it.classes.Event;
import lapisnet.it.utility.ConnectionDetector;
import lapisnet.it.utility.JSONParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EventDetailActivity extends ActionBarActivity {

	static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	private ConnectionDetector mCd;
	protected ProgressBar mProgressBar;
	private JSONParser jsonParser = new JSONParser();
	private JSONArray mItem = null;
	private String event_title, event_sub_title, event_cat, event_when, event_when_hour, event_where, event_price, event_desc, event_url;
	private TextView tv_event_title, tv_event_sub_title, tv_event_cat, tv_event_when, tv_event_when_hour, tv_event_where, tv_event_price, tv_event_desc, tv_event_url;
	private String mErr, mEventId = null;
	private ShareActionProvider mShareActionProvider;
	private MyBroadcastReceiver mConnectionReceiver;
	private ListView mList;
	private EventListSuggestedAdapter mAdapter;
	private ArrayList<Event> mListEvent;
	private View mHeaderView;
	private LayoutInflater mLi;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		overridePendingTransition(0, 0);
		setContentView(R.layout.detail_view);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(false);

		mConnectionReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(ACTION);
		this.registerReceiver(mConnectionReceiver, filter);

		mCd = new ConnectionDetector(getApplicationContext());
		mListEvent = new ArrayList<Event>();
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

		mLi = LayoutInflater.from(EventDetailActivity.this);
		mList = (ListView) findViewById(android.R.id.list);
		mHeaderView = mLi.inflate(R.layout.event_detail, null);
		mList.addHeaderView(mHeaderView);

		Intent i = getIntent();
		mEventId = i.getStringExtra("event_id");

		tv_event_title = (TextView) findViewById(R.id.title_event);
		tv_event_sub_title = (TextView) findViewById(R.id.event_sub_title);
		tv_event_cat = (TextView) findViewById(R.id.res_cat);
		tv_event_when = (TextView) findViewById(R.id.res_when);
		tv_event_when_hour = (TextView) findViewById(R.id.res_when_hour);
		tv_event_where = (TextView) findViewById(R.id.res_where);
		tv_event_price = (TextView) findViewById(R.id.res_price);
		tv_event_desc = (TextView) findViewById(R.id.res_desc);
		tv_event_url = (TextView) findViewById(R.id.event_url);

		if(savedInstanceState != null) {
			mListEvent = ((ArrayList<Event>) savedInstanceState.getSerializable("eventList"));
			mList.setScrollingCacheEnabled(false);
			mAdapter = new EventListSuggestedAdapter(EventDetailActivity.this, mListEvent);
			mList.setAdapter(mAdapter);

			mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
					if(arg2 != 0) {
						if (mCd.isConnectingToInternet()) {
							Intent i = new Intent(EventDetailActivity.this, EventDetailActivity.class);
							i.putExtra("event_id", mListEvent.get(arg2-1).getId());
							startActivity(i);
						}
					}
				}
			});
			event_title = savedInstanceState.getString("event_title");
			event_sub_title = savedInstanceState.getString("event_sub_title");
			event_cat = savedInstanceState.getString("event_cat");
			event_when = savedInstanceState.getString("event_when"); 
			event_when_hour = savedInstanceState.getString("event_when_hour"); 
			event_where = savedInstanceState.getString("event_where");
			event_price = savedInstanceState.getString("event_price");
			event_desc = savedInstanceState.getString("event_desc");
			event_url = savedInstanceState.getString("event_url");

			tv_event_title.setText(event_title);
			if(!event_sub_title.equalsIgnoreCase("")) {			
				tv_event_sub_title.setText(event_sub_title);
				showSubTitle(true);
			}
			tv_event_cat.setText(event_cat);
			tv_event_when.setText(event_when);
			tv_event_when_hour.setText(event_when_hour);
			tv_event_where.setText(event_where);
			tv_event_price.setText(event_price);
			tv_event_desc.setText(event_desc);
			tv_event_url.setText(event_url);
		}
		else {
			mProgressBar.setVisibility(View.VISIBLE);
			new LoadEventDetail().execute();
		}
	}

	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter(ACTION);
		this.registerReceiver(mConnectionReceiver, filter);
		super.onResume();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(mConnectionReceiver);
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("event_title", event_title);
		outState.putString("event_sub_title", event_sub_title);
		outState.putString("event_cat", event_cat);
		outState.putString("event_when", event_when);
		outState.putString("event_when_hour", event_when_hour);
		outState.putString("event_where", event_where);
		outState.putString("event_price", event_price);
		outState.putString("event_desc", event_desc);
		outState.putString("event_url", event_url);
		outState.putSerializable("eventList", mListEvent);
	}

	class LoadEventDetail extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", mEventId));

			String json = jsonParser
					.makeHttpRequest(Constant.URL_DETTAGLI_EVENTO, "GET", params);

			try {

				mItem = new JSONArray(json);
				//Log.d("LENGTH","LENGTH"+mItem.length());
				if (mItem != null) {
					for (int i = 0; i < mItem.length(); i++) {
						JSONObject jObj = mItem.getJSONObject(i);
						switch(i) {
						case 0:
							event_title = jObj.getString(Constant.DET_TITLE);
							event_sub_title = jObj.getString(Constant.DET_SUB_TITLE);
							event_cat = jObj.getString(Constant.DET_CAT);
							event_when = jObj.getString(Constant.DET_WHEN);
							event_when_hour = jObj.getString(Constant.DET_WHEN_HOUR);
							event_where = jObj.getString(Constant.DET_WHERE);
							event_price = jObj.getString(Constant.DET_PRICE);
							event_desc = jObj.getString(Constant.DET_DESC);
							event_url = jObj.getString(Constant.DET_URL);
							break;
						default:
							String mSId = jObj.getString(Constant.ES_ID);
							String mSDay = jObj.getString(Constant.ES_DAY);
							String mSMonth = jObj.getString(Constant.ES_MONTH);
							String mSCat = jObj.getString(Constant.ES_CAT);
							String mSTitle = jObj.getString(Constant.ES_TITOLO);
							String mSWhere = jObj.getString(Constant.ES_LUOGO);

							Event event = new Event(mSId, mSTitle, mSDay, mSMonth, mSWhere, mSCat);
							mListEvent.add(event);
							break;
						}
					}
				} else 
					mErr = "Nessuna informazione disponibile";

			} catch (JSONException e) {
				mErr = "Impossibile scaricare le notizie. Riprova pi tardi.";
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			mProgressBar.setVisibility(View.INVISIBLE);
			setProgressBarIndeterminateVisibility(false);
			mShareActionProvider.setShareIntent(getDefaultIntent());

			if(mErr!=null) {
				//alert.showAlertDialog(ArticleDetail.this, "Errore di connessione", err, false);
			}
			else {
				tv_event_title.setText(event_title);
				if(!event_sub_title.equalsIgnoreCase("")) {			
					tv_event_sub_title.setText(event_sub_title);
					showSubTitle(true);
				}
				tv_event_cat.setText(event_cat);
				tv_event_when.setText(event_when);
				
				if(!event_when_hour.equalsIgnoreCase("")) {
					tv_event_when_hour.setText(event_when_hour);
					showHour(true);
				}
				
				tv_event_where.setText(event_where);
				tv_event_price.setText(event_price);
				tv_event_desc.setText(event_desc);
				tv_event_url.setText(event_url);
				
				if(mListEvent.size() > 0)
					showSuggestedEvent(true);

				mList.setScrollingCacheEnabled(false);
				mAdapter = new EventListSuggestedAdapter(EventDetailActivity.this, mListEvent);
				mList.setAdapter(mAdapter);

				mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
						if(arg2 != 0) {
							if (mCd.isConnectingToInternet()) {
								Intent i = new Intent(EventDetailActivity.this, EventDetailActivity.class);
								i.putExtra("event_id", mListEvent.get(arg2-1).getId());
								startActivity(i);
							}
						}
					}
				});

			}
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(0, 0);
		//overridePendingTransition(R.animator.push_right_in, R.animator.push_right_out);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.refresh_menu, menu);
		inflater.inflate(R.menu.main, menu);
		inflater.inflate(R.menu.share_menu, menu);        
		MenuItem item = menu.findItem(R.id.action_date);
		item.setVisible(false);

		MenuItem shareItem = menu.findItem(R.id.action_share);
		mShareActionProvider = (ShareActionProvider)
				MenuItemCompat.getActionProvider(shareItem);
		mShareActionProvider.setShareIntent(getDefaultIntent());

		return super.onCreateOptionsMenu(menu);
	}
	
	

	private Intent getDefaultIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(android.content.Intent.EXTRA_TEXT, event_title+", "
				+event_when
				+" ore "+event_when_hour
				+" "+event_where+" "
				+event_url+" #lapisnet");
		return intent;
	}
	


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(0, 0);
			//overridePendingTransition(R.animator.push_right_in, R.animator.push_right_out);
			return true;
			case R.id.action_refresh:
			if(mCd.isConnectingToInternet())
				new LoadEventDetail().execute();
			return true;
			//case ADD_TEATRO:
			//return true;
		case R.id.action_share:
			if(mShareActionProvider != null) {
				mShareActionProvider.setShareIntent(getDefaultIntent());
				//Log.d("SONO QUI","SONO QUI");
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showSubTitle(boolean set) {
		findViewById(R.id.event_sub_title).setVisibility(set ? View.VISIBLE : View.GONE);
	}
	
	public void showHour(boolean set) {
		findViewById(R.id.layout_when_hour).setVisibility(set ? View.VISIBLE : View.GONE);
	}
	
	public void showSuggestedEvent(boolean set) {
		findViewById(R.id.title_event_suggest).setVisibility(set ? View.VISIBLE : View.GONE);
	}

	public void showStatusConnection(boolean a) {
		((TextView)findViewById(R.id.statusConnection)).setVisibility(!a ? View.GONE : View.VISIBLE);
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean a = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);
			showStatusConnection(a);
		}
	}; 



}
