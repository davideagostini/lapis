package lapisnet.it;

import java.util.ArrayList;
import lapisnet.it.adapter.FilterProvinceListAdapter;
import lapisnet.it.classes.Category;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class FilterProvinceDialogFragment extends DialogFragment {

	public static final String FILTER_PROVINCE = "prov";
	public static final String FILTER_TITLE_PROVINCE = "title_prov";
	private ArrayList<Category> mProvinceList;
	private FilterProvinceListAdapter mAdapter;
	private Spinner mSpinProvince;
	private String mSelectIdProvince, mSelectTitleProvince;

	static String[] sProvince = {"Agrigento", "Caltanissetta", "Catania", "Enna", "Messina", "Palermo", "Ragusa", "Siracusa", "Trapani"};
	static int [] sIdProvince = {1, 2, 3, 4, 5, 6, 7, 8, 9};

	public static interface OnFilterProvinceListener {
		public abstract void onCompleteProvince(Intent i);
	}

	private OnFilterProvinceListener fpListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.fpListener = (OnFilterProvinceListener) activity;
	}

	@Override
	public void onDetach() {
		this.fpListener = null;
		super.onDetach();
	}

	public static FilterProvinceDialogFragment newInstance() {
		Bundle args = new Bundle();
		FilterProvinceDialogFragment fragment = new FilterProvinceDialogFragment();
		fragment.setArguments(args);
		return fragment;
	}

	private void sendResult(int resultCode) {
		Intent i = new Intent();
		i.putExtra(FILTER_PROVINCE, mSelectIdProvince);
		i.putExtra(FILTER_TITLE_PROVINCE, mSelectTitleProvince);

		if(getTargetFragment() != null)
			getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
		else
			this.fpListener.onCompleteProvince(i);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		View v = getActivity().getLayoutInflater()
				.inflate(R.layout.filter_layout, null);

		TextView tvTitle = (TextView) v.findViewById(R.id.titolo_dialog);
		tvTitle.setText(R.string.select_province);

		mProvinceList = new ArrayList<Category>();

		for(int i=0; i<sIdProvince.length; i++) {
			String id = String.valueOf(sIdProvince[i]);
			String titolo = sProvince[i];

			Category item = new Category(id, titolo);
			mProvinceList.add(item);
		}

		mSpinProvince = (Spinner) v.findViewById(R.id.spinner3);

		mSpinProvince.setVisibility(View.VISIBLE);
		mAdapter = new FilterProvinceListAdapter(getActivity(), mProvinceList);
		mSpinProvince.setAdapter(mAdapter);
		mSpinProvince.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				int index = arg0.getSelectedItemPosition();
				mSelectIdProvince = mProvinceList.get(index).getId().toString();
				mSelectTitleProvince = mProvinceList.get(index).getTitle().toString();
				getArguments().putString("FILTER_CATEGORY", mSelectIdProvince);
				getArguments().putString("FILTER_TITLE_CATEGORY", mSelectTitleProvince);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

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
}