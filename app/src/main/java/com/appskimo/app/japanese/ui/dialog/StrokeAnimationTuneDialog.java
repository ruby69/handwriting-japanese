package com.appskimo.app.japanese.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.service.PrefsService_;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

@EFragment
public class StrokeAnimationTuneDialog extends CommonDialog {
    public static final String TAG = "StrokeAnimationTuneDialog";
    @Pref PrefsService_ prefs;

    private AbstractWheel repeatCount;
    private AbstractWheel strokeSpeed;

    private DialogInterface.OnClickListener confirmListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            prefs.strokeRepeatCount().put(repeatCount.getCurrentItem());
            prefs.strokeSpeed().put(strokeSpeed.getCurrentItem() + 1);
            dismiss();
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(init(inflater.inflate(R.layout.dialog_tune_animation, null)));
        builder.setPositiveButton(R.string.label_confirm, confirmListener);
        builder.setTitle(R.string.label_stroke_animation);
        return builder.create();
    }

    private View init(View view) {
        repeatCount = (AbstractWheel) view.findViewById(R.id.repeatCount);
        strokeSpeed = (AbstractWheel) view.findViewById(R.id.strokeSpeed);

        repeatCount.setViewAdapter(new NumericWheelAdapter(getActivity(), 1, 10, "%02d"));
        strokeSpeed.setViewAdapter(new NumericWheelAdapter(getActivity(), 1, 10, "%02d"));
        repeatCount.setCurrentItem(prefs.strokeRepeatCount().get());
        strokeSpeed.setCurrentItem(prefs.strokeSpeed().get() - 1);

        return view;
    }
}