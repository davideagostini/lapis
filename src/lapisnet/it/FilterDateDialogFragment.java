package lapisnet.it;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.DatePicker.OnDateChangedListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class FilterDateDialogFragment extends DialogFragment {
	
	public static final String EXTRA_DATE = "event_data";
    private Date mDate;
    
    public static interface OnFilterDateListener {
        public abstract void onCompleteDate(Date date);
    }

    private OnFilterDateListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mListener = (OnFilterDateListener) activity;
    }
    
    @Override
    public void onDetach() {
        this.mListener = null;
        super.onDetach();
    }
    
    public static FilterDateDialogFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);
        
        FilterDateDialogFragment fragment = new FilterDateDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private void sendResult(int resultCode) {
        //if (getTargetFragment() == null) 
          //  return;

        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);

        if(getTargetFragment() != null)
        	getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
        else
        	this.mListener.onCompleteDate(mDate);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

    	mDate = (Date)getArguments().getSerializable(EXTRA_DATE);
    	
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = getActivity().getLayoutInflater()
            .inflate(R.layout.date_picker_view, null);

		TextView tvTitle = (TextView) v.findViewById(R.id.titolo_dialog);
		tvTitle.setText(R.string.select_date);
        
        DatePicker datePicker = (DatePicker)v.findViewById(R.id.date_picker);
        datePicker.init(year, month, day, new OnDateChangedListener() {
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                mDate = new GregorianCalendar(year, month, day).getTime();
                getArguments().putSerializable(EXTRA_DATE, mDate);
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