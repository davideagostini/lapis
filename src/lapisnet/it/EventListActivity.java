package lapisnet.it;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lapisnet.it.FilterDateDialogFragment.OnFilterDateListener;
import lapisnet.it.FilterProvinceDialogFragment.OnFilterProvinceListener;
import lapisnet.it.adapter.EventListAdapter;
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
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class EventListActivity extends ActionBarActivity implements OnFilterDateListener, OnFilterProvinceListener {

	static final String DIALOG_DATE = "filter_date";
	static final String DIALOG_PROVINCE = "filter_province";
	static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	private ConnectionDetector mCd;
	private JSONParser jsonParser = new JSONParser();
	private ArrayList<Event> mEventList;
	private JSONArray mItem = null;
	private String mTitolo = "";
	private TextView mTvListTitle, mTvEmptyText;
	private ListView mList;
	private EventListAdapter mAdapter;
	private String mErr = null;
	protected ProgressBar mProgressBar;
	private int mPaginazione = 1;
	private String mTotPag;
	private String mSelectDate, mSelectCategory, mSelectProvince = null;
	private MyBroadcastReceiver mConnectionReceiver;
	private Button mBFilterDate, mBFilterProvince;
	private LinearLayout mLlFilter;


	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.event_list_activity);
		overridePendingTransition(0, 0);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(false);

		mConnectionReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(ACTION);
		this.registerReceiver(mConnectionReceiver, filter);

		Intent i = getIntent();
		Bundle bundle = i.getExtras();
		if(bundle != null) {
			mTitolo = i.getStringExtra("cat_title");
			mSelectCategory = i.getStringExtra("cat_id");	
		}

		mCd = new ConnectionDetector(EventListActivity.this);
		mEventList = new ArrayList<Event>();

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mLlFilter = (LinearLayout) findViewById(R.id.filter);
		listenerFilterDateButton();
		listenerFilterProvinceButton();

		mTvListTitle = (TextView) findViewById(R.id.title_list);
		mTvListTitle.setText(mTitolo);
		mList = (ListView) findViewById(android.R.id.list);
		mList.setOnScrollListener(new EndlessScrollListener());

		if(savedInstanceState!= null) {
			mEventList = ((ArrayList<Event>) savedInstanceState.getSerializable("eventList"));
			mAdapter = new EventListAdapter(EventListActivity.this, mEventList);
			mList.setAdapter(mAdapter);				
			mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
					if (mCd.isConnectingToInternet()) {
						Intent i = new Intent(EventListActivity.this, EventDetailActivity.class);
						String event_id = ((TextView) view.findViewById(R.id.event_id)).getText().toString();
						i.putExtra("event_id", event_id);
						startActivity(i);
					}
				}
			});
		}
		else {
			mProgressBar.setVisibility(View.VISIBLE);
			new LoadEvent().execute(
					String.valueOf(mPaginazione),
					mSelectProvince,
					mSelectDate,
					mSelectCategory
					);
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("eventList", mEventList);
	}

	/** interroga il server il quale restituisce la lista degli eventi in base ai parametri passati  */
	class LoadEvent extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			//Log.d("PAGINAZIONE VALE ---->", "PAGINAZIONE VALE -----> "+args[0]);
			//Log.d("PROVINCIA VALE ---->", "PROVINCIA VALE -----> "+args[1]);
			//Log.d("DATA VALE ---->", "DATA VALE -----> "+args[2]);
			//Log.d("CAT VALE ---->", "CAT VALE -----> "+args[3]);

			params.add(new BasicNameValuePair("pag", args[0]));

			if(args[1] != null)
				params.add(new BasicNameValuePair("p", args[1]));

			if(args[2] != null)
				params.add(new BasicNameValuePair("d", args[2]));

			if(args[3] != null)
				params.add(new BasicNameValuePair("c", args[3]));

			String json = jsonParser.makeHttpRequest(Constant.URL_EVENTI, "GET",params);

			try {
				mItem = new JSONArray(json);

				if (mItem != null) {
					for (int i = 0; i < mItem.length(); i++) {
						JSONObject c = mItem.getJSONObject(i);

						if(c.has("num_pages")) mTotPag = c.getString("num_pages");
						else {

							String id = c.getString(Constant.EVENT_ID);
							String titolo = c.getString(Constant.EVENT_TITLE);
							String luogo = c.getString(Constant.EVENT_LUOGO);
							String type = c.getString(Constant.EVENT_TYPE);
							String day = c.getString(Constant.EVENT_DAY);
							String month = c.getString(Constant.EVENT_MONTH);
							String cat = c.getString(Constant.EVENT_CAT);

							Event item = new Event(id, titolo, type, day, month, luogo, cat);
							mEventList.add(item);
						}

						if (isCancelled()) break;	
					}
				} else {
					mErr = "Nessuna informazione disponibile";
				}

			} catch (JSONException e) {
				mErr = "Impossibile scaricare le liste. Riprova pi tardi.";
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			//if(mProgressBar.getVisibility()==0)
			mProgressBar.setVisibility(View.INVISIBLE);
			setProgressBarIndeterminateVisibility(false);

			if(mErr!=null) {
				//alert.showAlertDialog(getActivity(), "Errore", err, false);
			} else {
				if(mEventList.isEmpty()) {
					mList.setVisibility(View.GONE);
					mTvEmptyText = (TextView) findViewById(android.R.id.empty);
					mTvEmptyText.setVisibility(View.VISIBLE);					
				} else {

					if(mList.getVisibility() == View.GONE) {
						mList.setVisibility(View.VISIBLE);
						mTvEmptyText.setVisibility(View.GONE);
					}

					mList.setScrollingCacheEnabled(false);
					int currentPosition = mList.getFirstVisiblePosition();
					mAdapter = new EventListAdapter(EventListActivity.this, mEventList);
					mList.setAdapter(mAdapter);
					mList.setSelection(currentPosition);

					mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
							if (mCd.isConnectingToInternet()) {
								Intent i = new Intent(EventListActivity.this, EventDetailActivity.class);
								String event_id = ((TextView) view.findViewById(R.id.event_id)).getText().toString();
								i.putExtra("event_id", event_id);
								startActivity(i);
							}
						}
					});
				}
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
		inflater.inflate(R.menu.province_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(0, 0);
			return true;
		case R.id.action_province:
			showDialogProvince();
			return true;
		case R.id.action_date:
			showDialogDate();
			return true;
		case R.id.action_refresh:
			if(mCd.isConnectingToInternet())
				startSearch();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/** visualizza il dialog per selezionare la provincia su cui effettuare la ricerca */
	public void showDialogProvince() {
		FragmentManager fm = getSupportFragmentManager();
		FilterProvinceDialogFragment filter = FilterProvinceDialogFragment.newInstance();
		filter.show(fm, DIALOG_PROVINCE);
	}

	/** visualizza il dialog per selezionare la data su cui effettuare la ricerca */
	private void showDialogDate() {
		FragmentManager fm = getSupportFragmentManager();		
		FilterDateDialogFragment date = FilterDateDialogFragment.newInstance(new Date());
		date.show(fm, DIALOG_DATE);
	}

	@Override
	public void onCompleteDate(Date date) { 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd", Locale.ITALY);
		mSelectDate = sdf.format(date);

		SimpleDateFormat data_formato_italiano = new SimpleDateFormat("dd-M-yyyy", Locale.ITALY);
		String data_italiana = data_formato_italiano.format(date);

		//Log.d("LA DATA SCELTA -----> ", "LA DATA SCELTA -----> "+mSelectDate);
		startSearch();
		showFilterDate(true, data_italiana);
	}

	@Override
	public void onCompleteProvince(Intent i) {
		String title = i.getStringExtra(FilterProvinceDialogFragment.FILTER_TITLE_PROVINCE);
		mSelectProvince = i.getStringExtra(FilterProvinceDialogFragment.FILTER_PROVINCE);
		startSearch();
		//Log.d("LA PROVINCIA SCELTA -----> ", "LA PROVINCIA SCELTA -----> "+mSelectProvince + " - "+title);

		showFilterProvince(true, title);
	}

	/** richiama l'AsyncTask per effettuare la ricerca con i nuovi parametri selezionati */
	public void startSearch() {
		mPaginazione = 1;
		mEventList = new ArrayList<Event>();
		if(!mEventList.isEmpty())
			mAdapter.clear();
		new LoadEvent().execute(
				String.valueOf(mPaginazione),
				mSelectProvince,
				mSelectDate,
				mSelectCategory
				);
	}

	/** mostra il filtro data selezionato */
	public void showFilterDate(boolean set, String text) {
		if(set) {			
			mBFilterDate.setText(text);		
			if(!isOneFilterVisible(mBFilterProvince))
				showFilterLayout(true);
		}
		else {
			if(!isOneFilterVisible(mBFilterProvince))
				showFilterLayout(false);
		}
		mBFilterDate.setVisibility(set ? View.VISIBLE : View.GONE);
	}

	/** mostra il filtro provincia selezionato */
	public void showFilterProvince(boolean set, String text) {
		if(set) {
			mBFilterProvince.setText(text);
			if(!isOneFilterVisible(mBFilterDate))
				showFilterLayout(true);
		}
		else {
			if(!isOneFilterVisible(mBFilterDate))
				showFilterLayout(false);
		}
		mBFilterProvince.setVisibility(set ? View.VISIBLE : View.GONE);
	}

	/** ritorna un booleano a seconda se un filtro  visibile sulla view */
	public boolean isOneFilterVisible(TextView t) {
		if (t.getVisibility() == View.VISIBLE)
			return true;
		else
			return false;
	}

	/** mostra o nasconde il layout che contiene i filtri  */
	public void showFilterLayout(boolean set) {
		mLlFilter.setVisibility(set ? View.VISIBLE : View.GONE);
	}

	public void listenerFilterDateButton() {
		mBFilterDate = (Button)findViewById(R.id.filter_date);
		mBFilterDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showFilterDate(false, null);
				mSelectDate = null;
				startSearch();
			}
		});
	}

	public void listenerFilterProvinceButton() {
		mBFilterProvince = (Button)findViewById(R.id.filter_province);
		mBFilterProvince.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showFilterProvince(false, null);
				mSelectProvince = null;
				startSearch();
			}
		});
	}

	/** mostra lo stato della connessione */
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

	public class EndlessScrollListener implements OnScrollListener {

		private int visibleTreshold = 1;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { }

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(scrollState == SCROLL_STATE_IDLE) {
				if(mList.getLastVisiblePosition() >= mList.getCount() - visibleTreshold) {
					if(mPaginazione<Integer.parseInt(mTotPag) && mCd.isConnectingToInternet()) {
						mPaginazione++;
						new LoadEvent().execute(
								String.valueOf(mPaginazione),
								mSelectProvince,
								mSelectDate,
								mSelectCategory);
					}
				}
			}
		}
	}
}