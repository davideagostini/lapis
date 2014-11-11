package lapisnet.it;

import java.util.ArrayList;
import java.util.List;

import lapisnet.it.adapter.CategoryListAdapter;
import lapisnet.it.classes.Category;
import lapisnet.it.utility.ConnectionDetector;
import lapisnet.it.utility.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoryListFragment extends Fragment  {

	private ConnectionDetector mCd;
	private JSONParser jsonParser = new JSONParser();
	private ArrayList<Category> mCategoryList;
	private JSONArray mItem = null;
	private RelativeLayout mRl;
	private String mTitolo = "";
	private TextView mListTitle;
	private ListView mList;
	private CategoryListAdapter mAdapter;
	private String mErr = null;
	protected ProgressBar mProgressBar;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);

		@SuppressWarnings("unused")
		Bundle bundle = this.getArguments();
		if(getArguments()!=null) {
			mTitolo = getArguments().getString("titolo");
		}

		mCd = new ConnectionDetector(getActivity());
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mRl = (RelativeLayout)inflater.inflate(R.layout.category_list, container, false);
		mProgressBar = (ProgressBar) mRl.findViewById(R.id.progressBar);
		mListTitle = (TextView) mRl.findViewById(R.id.title_list);
		mListTitle.setText(mTitolo);
		mList = (ListView) mRl.findViewById(android.R.id.list);

		if(savedInstanceState!= null) {
			mCategoryList = ((ArrayList<Category>) savedInstanceState.getSerializable("categoryList"));
			mAdapter = new CategoryListAdapter(getActivity(), mCategoryList);
			mList.setAdapter(mAdapter);				
			mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
					if (mCd.isConnectingToInternet()) {
						Intent i = new Intent(getActivity(), EventListActivity.class);
						String cat_id = ((TextView) view.findViewById(R.id.cat_id)).getText().toString();
						String cat_title = ((TextView) view.findViewById(R.id.cat_title)).getText().toString();
						i.putExtra("cat_id", cat_id);
						i.putExtra("cat_title", cat_title);
						startActivity(i);
					}
				}
			});
		}
		else {
			mProgressBar.setVisibility(View.VISIBLE);
			new LoadCategory().execute();
		}


		return mRl;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("categoryList", mCategoryList);
	}

	/**
	 * 
	 * interroga il server il quale risponde con la lista delle categorie disponibili
	 *
	 */
	class LoadCategory extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getActivity().setProgressBarIndeterminateVisibility(true);

		}
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			String json = jsonParser.makeHttpRequest(Constant.URL_SEZIONI, "GET",params);

			try {
				mItem = new JSONArray(json);
				mCategoryList = new ArrayList<Category>();

				if (mItem != null) {
					for (int i = 0; i < mItem.length(); i++) {
						JSONObject c = mItem.getJSONObject(i);

						String id = c.getString(Constant.CAT_ID);
						String titolo = c.getString(Constant.CAT_TITLE);

						Category item = new Category(id, titolo);
						mCategoryList.add(item);
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
			if(mProgressBar.getVisibility()==0)
				mProgressBar.setVisibility(View.INVISIBLE);
			getActivity().setProgressBarIndeterminateVisibility(false);

			if(mErr!=null) {
				//alert.showAlertDialog(getActivity(), "Errore", err, false);
			} else {
				mList.setScrollingCacheEnabled(false);
				mAdapter = new CategoryListAdapter(getActivity(), mCategoryList);
				mList.setAdapter(mAdapter);

				mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
						if (mCd.isConnectingToInternet()) {
							Intent i = new Intent(getActivity(), EventListActivity.class);
							String cat_id = ((TextView) view.findViewById(R.id.cat_id)).getText().toString();
							String cat_title = ((TextView) view.findViewById(R.id.cat_title)).getText().toString();
							i.putExtra("cat_id", cat_id);
							i.putExtra("cat_title", cat_title);
							startActivity(i);
						}
					}
				});
			}
		}
	}
}