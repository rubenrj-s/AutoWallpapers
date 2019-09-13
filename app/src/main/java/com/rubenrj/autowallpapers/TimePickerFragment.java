package com.rubenrj.autowallpapers;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private int tvId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = getArguments().getInt("hour");
        int minute = getArguments().getInt("min");
        tvId = getArguments().getInt("tv");

        return new TimePickerDialog(getActivity(), this, hour, minute,
                true);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView tv = getActivity().findViewById(tvId);
        tv.setText(String.format("%1$s:%2$s",turnTwo(hourOfDay),turnTwo(minute)));
    }

    /**
     * Turn a only character (number) on two character
     * @param number
     * @return
     */
    private String turnTwo(int number){
        String twoNumbers = Integer.toString(number);
        if(twoNumbers.length() == 1){
            twoNumbers = "0" + twoNumbers;
        }
        return twoNumbers;
    }
}
