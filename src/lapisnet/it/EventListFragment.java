package lapisnet.it;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lapisnet.it.adapter.EventListAdapter;
import lapisnet.it.classes.Event;
import lapisnet.it.utility.ConnectionDetector;
import lapisnet.it.utility.JSONParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class EventListFragment extends Fragment {

	private static final String DIALOG_DATE = "filter_date";
	private static final String DIALOG_CATEGORY = "filter_category";
	private ConnectionDetector mCd;
	private JSONParser jsonParser = new JSONParser();
	private ArrayList<Event> mEventList;
	private JSONArray mItem = null;
	private ListView mList;
	private EventListAdapter mAdapter;
	private RelativeLayout mRl;
	private String mTitolo = "";
	private TextView mTvListTitle, mTvEmptyText;
	private Button mBFilterDate, mBFilterCategory;
	private String mErr = null;	
	protected ProgressBar mProgressBar;
	private int mPaginazione = 1;
	private String mSelectDate, mSelectCategory, mSelectProvince = null;
	private static String mTotPag;
	private LinearLayout mLlFilter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		@SuppressWarnings("unused")
		Bundle bundle = this.getArguments();
		if(getArguments()!=null) {
			mTitolo = getArguments().getString("titolo");
			mSelectProvince = getArguments().getString("provincia");
		}
		mCd = new ConnectionDetector(getActivity());
		mEventList = new ArrayList<Event>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mRl = (RelativeLayout)inflater.inflate(R.layout.event_list, container, false);

		mProgressBar = (ProgressBar) mRl.findViewById(R.id.progressBar);
		mLlFilter = (LinearLayout) mRl.findViewById(R.id.filter);
		listenerFilterDateButton();
		listenerFilterCategoryButton();

		mTvListTitle = (TextView) mRl.findViewById(R.id.title_list);
		mTvListTitle.setText(mTitolo);
		mList = (ListView) mRl.findViewById(android.R.id.list);
		mList.setOnScrollListener(new EndlessScrollListener());


		if(savedInstanceState!= null) {
			mEventList = ((ArrayList<Event>) savedInstanceState.getSerializable("eventList"));
			mPaginazione = (Integer) savedInstanceState.getSerializable("paginazione");
			mSelectProvince = (String) savedInstanceState.getSerializable("provincia");
			mSelectDate = (String) savedInstanceState.getSerializable("data");
			mAdapter = new EventListAdapter(getActivity(), mEventList);
			mList.setAdapter(mAdapter);				
			mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
					if (mCd.isConnectingToInternet()) {
						Intent i = new Intent(getActivity(), EventDetailActivity.class);
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

		return mRl;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("eventList", mEventList);
		outState.putSerializable("paginazione", mPaginazione);
		outState.putSerializable("provincia", mSelectProvince);
		outState.putSerializable("data", mSelectDate);
	}

	/** interroga il server il quale restituisce la lista degli eventi in base ai parametri passati  */
	class LoadEvent extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getActivity().setProgressBarIndeterminateVisibility(true);
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

			String json = jsonParser.makeHttpRequest(Constant.URL_EVENTI, "GET", params);

			//Log.d("JSON---->","JSON----->"+json);

			try {
				mItem = new JSONArray(json);

				if (mItem != null) {
					for (int i = 0; i < mItem.length(); i++) {
						JSONObject c = mItem.getJSONObject(i);

						if(c.has("num_pages"))
							mTotPag = c.getString("num_pages");

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
				}
				else {
					mErr = "Nessuna informazione disponibile";
					//Log.d("NESSUNA INFORMAZIONE DISPONIBILE","NESSUNA INFORMAZIONE DISPONIBILE");
				}

			} 
			catch (JSONException e) {
				mErr = "Impossibile scaricare le liste. Riprova più tardi.";
				//Log.d("IMPOSSIBILE SCARICARE","IMPOSSIBILE SCARICARE");
			}

			return null;
		}

		protected void onPostExecute(String file_url) {

			if(mProgressBar.getVisibility()==0)
				mProgressBar.setVisibility(View.INVISIBLE);
			getActivity().setProgressBarIndeterminateVisibility(false);

			if(mErr!=null) {
				//alert.showAlertDialog(getActivity(), "Errore", err, false);
			}
			else {
				if(mEventList.isEmpty()) {
					mList.setVisibility(View.GONE);
					mTvEmptyText = (TextView) mRl.findViewById(android.R.id.empty);
					mTvEmptyText.setVisibility(View.VISIBLE);					
				}
				else {
					if(mList.getVisibility() == View.GONE) {
						mList.setVisibility(View.VISIBLE);
						mTvEmptyText.setVisibility(View.GONE);
					}

					mList.setScrollingCacheEnabled(false);
					int currentPosition = mList.getFirstVisiblePosition();
					mAdapter = new EventListAdapter(getActivity(), mEventList);
					mList.setAdapter(mAdapter);
					mList.setSelection(currentPosition);

					mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
							if (mCd.isConnectingToInternet()) {
								Intent i = new Intent(getActivity(), EventDetailActivity.class);
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

	/** visualizza il dialog per selezionare la categoria su cui effettuare la ricerca */
	public void showDialogCategory() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FilterCategoryDialogFragment filter = FilterCategoryDialogFragment.newInstance();
		filter.setTargetFragment(EventListFragment.this, 1);
		filter.show(fm, DIALOG_CATEGORY);
	}

	/** visualizza il dialog per selezionare la data su cui effettuare la ricerca */
	private void showDialogDate() {
		FragmentManager fm = getActivity().getSupportFragmentManager();		
		FilterDateDialogFragment date = FilterDateDialogFragment.newInstance(new Date());
		date.setTargetFragment(EventListFragment.this, 0);
		date.show(fm, DIALOG_DATE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) return;
		if (requestCode == 0) {
			Date date = (Date)data.getSerializableExtra(FilterDateDialogFragment.EXTRA_DATE);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd", Locale.ITALY);
			mSelectDate = sdf.format(date);

			SimpleDateFormat data_formato_italiano = new SimpleDateFormat("dd-M-yyyy", Locale.ITALY);
			String data_italiana = data_formato_italiano.format(date);

			//Log.d("LA DATA SCELTA -----> ", "LA DATA SCELTA -----> "+mSelectDate);
			startSearch();
			showFilterDate(true, data_italiana);
		}
		if(requestCode == 1) {
			mSelectCategory = data.getStringExtra(FilterCategoryDialogFragment.FILTER_CATEGORY);
			String title = data.getStringExtra(FilterCategoryDialogFragment.FILTER_TITLE_CATEGORY);
			//Log.d("LA CATEGORIA SCELTA -----> ", "LA CATEGORIA SCELTA -----> "+mSelectCategory);
			startSearch();
			showFilterCategory(true, title);

		}
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
				mSelectCategory);
	}

	/** mostra il filtro data selezionato */
	public void showFilterDate(boolean set, String text) {
		if(set) {			
			mBFilterDate.setText(text);		
			if(!isOneFilterVisible(mBFilterCategory))
				showFilterLayout(true);
		}
		else {
			if(!isOneFilterVisible(mBFilterCategory))
				showFilterLayout(false);
		}
		mBFilterDate.setVisibility(set ? View.VISIBLE : View.GONE);
	}

	/** mostra il filtro categoria selezionato */
	public void showFilterCategory(boolean set, String text) {
		if(set) {
			mBFilterCategory.setText(text);
			if(!isOneFilterVisible(mBFilterDate))
				showFilterLayout(true);
		}
		else {
			if(!isOneFilterVisible(mBFilterDate))
				showFilterLayout(false);
		}
		mBFilterCategory.setVisibility(set ? View.VISIBLE : View.GONE);
	}

	/** ritorna un booleano a seconda se un filtro è visibile sulla view */
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
		mBFilterDate = (Button)mRl.findViewById(R.id.filter_date);
		mBFilterDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showFilterDate(false, null);
				mSelectDate = null;
				startSearch();
			}
		});
	}

	public void listenerFilterCategoryButton() {
		mBFilterCategory = (Button)mRl.findViewById(R.id.filter_category);
		mBFilterCategory.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showFilterCategory(false, null); 
				mSelectCategory = null;
				startSearch();
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh_menu, menu);
		inflater.inflate(R.menu.main, menu);
		inflater.inflate(R.menu.category_menu, menu);

		/*SubMenu subMenu = (SubMenu) menu.getItem(0).getSubMenu();
        for(int i=0; i<sLoadCategory.getCategoryList().size(); i++)
        	subMenu.add(R.id.action_categories,
        			Integer.parseInt(sLoadCategory.getCategoryList().get(i).getId()),
        			0,
        			sLoadCategory.getCategoryList().get(i).getTitle());*/
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_categories:
			showDialogCategory();
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