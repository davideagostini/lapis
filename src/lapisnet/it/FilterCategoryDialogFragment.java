package lapisnet.it;

import java.util.ArrayList;
import java.util.List;

import lapisnet.it.adapter.FilterCategoryListAdapter;
import lapisnet.it.classes.Category;
import lapisnet.it.utility.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class FilterCategoryDialogFragment extends DialogFragment {

	public static final String FILTER_CATEGORY = "cat";
	public static final String FILTER_TITLE_CATEGORY = "title_cat";
	private JSONParser jsonParser = new JSONParser();
	private ArrayList<Category> mCategoryList;
	private JSONArray mItem = null;
	private String mErr = null;
	private FilterCategoryListAdapter mAdapter;
	private Spinner mSpinCategory;
	protected ProgressBar mProgressBar;
	private String mSelectIdCat, mSelectTitleCat;

	public static interface OnFilterCategoryListener {
		public abstract void onCompleteCategory(String idCat, String tCat);
	}

	private OnFilterCategoryListener fListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.fListener = (OnFilterCategoryListener) activity;
	}

	@Override
	public void onDetach() {
		this.fListener = null;
		super.onDetach();
	}

	public static FilterCategoryDialogFragment newInstance() {
		Bundle args = new Bundle();
		FilterCategoryDialogFragment fragment = new FilterCategoryDialogFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	/** questo metodo invia la categoria selezionata al fragment o all'activity dal quale è stato chiamato il dialog */
	private void sendResult(int resultCode) {
		//if (getTargetFragment() == null) 
			//  return;

		Intent i = new Intent();
		i.putExtra(FILTER_CATEGORY, mSelectIdCat);
		i.putExtra(FILTER_TITLE_CATEGORY, mSelectTitleCat);

		if(getTargetFragment() != null)
			getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
		else
			this.fListener.onCompleteCategory(mSelectIdCat, mSelectTitleCat);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		View v = getActivity().getLayoutInflater()
				.inflate(R.layout.filter_layout, null);

		mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		mSpinCategory = (Spinner) v.findViewById(R.id.spinner3);
		
		TextView tvTitle = (TextView) v.findViewById(R.id.titolo_dialog);
		tvTitle.setText(R.string.select_category);

		mProgressBar.setVisibility(View.VISIBLE);
		new LoadFilterCategory().execute();

		return new AlertDialog.Builder(getActivity())
		.setView(v)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				sendResult(Activity.RESULT_OK);
			}
		})
		.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.create();
	}



	class LoadFilterCategory extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
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

				} 
				else {
					mErr = "Nessuna informazione disponibile";
				}

			} 
			catch (JSONException e) {
				mErr = "Impossibile scaricare le liste. Riprova più tardi.";
			}

			return null;
		}

		protected void onPostExecute(String file_url) {

			if(mProgressBar.getVisibility()==0)
				mProgressBar.setVisibility(View.INVISIBLE);

			if(mErr!=null) {
				//alert.showAlertDialog(getActivity(), "Errore", err, false);
			}
			else {
				mSpinCategory.setVisibility(View.VISIBLE);
				mAdapter = new FilterCategoryListAdapter(getActivity(), mCategoryList);
				mSpinCategory.setAdapter(mAdapter);
				mSpinCategory.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						int index = arg0.getSelectedItemPosition();
						mSelectIdCat = mCategoryList.get(index).getId().toString();
						mSelectTitleCat = mCategoryList.get(index).getTitle().toString();
						getArguments().putString("FILTER_CATEGORY", mSelectIdCat);
						getArguments().putString("FILTER_TITLE_CATEGORY", mSelectTitleCat);
					}

					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
			}
		}
	}
}