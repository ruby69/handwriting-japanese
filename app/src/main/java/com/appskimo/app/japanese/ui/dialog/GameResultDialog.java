package com.appskimo.app.japanese.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.event.GameNextAction;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;

@EFragment
public class GameResultDialog extends CommonDialog {
    public static final String TAG = "GameResultDialog";

    private TextView scoreView;
    private TextView recordView;
    private TextView comboCountView;
    @StringRes(R.string.label_game_combo) String comboLabel;

    public static final DecimalFormat scoreFormat = new DecimalFormat("#,###");

    private long score;
    private String recordValue;
    private int comboCountValue;

    private View.OnClickListener mainListener = v -> {
        EventBus.getDefault().post(new GameNextAction(GameNextAction.Kind.MAIN));
        dismiss();
    };

    private View.OnClickListener retryListener = v -> {
        EventBus.getDefault().post(new GameNextAction(GameNextAction.Kind.PLAY));
        dismiss();
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        var builder = new AlertDialog.Builder(getActivity());
        var inflater = getActivity().getLayoutInflater();
        builder.setView(init(inflater.inflate(R.layout.dialog_game_result, null)));
        return builder.create();
    }

    private View init(View view) {
        scoreView = view.findViewById(R.id.scoreView);
        recordView = view.findViewById(R.id.recordView);
        comboCountView = view.findViewById(R.id.comboCountView);

        view.findViewById(R.id.moveToMain).setOnClickListener(mainListener);
        view.findViewById(R.id.retry).setOnClickListener(retryListener);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        var dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            scoreView.setText(scoreFormat.format(score));
            recordView.setText(recordValue);
            comboCountView.setText(String.format(comboLabel, comboCountValue));
        }
    }

    public GameResultDialog setRecord(long score, long elapsedMillis, int count) {
        var sec = String.valueOf(elapsedMillis / 1000);
        var msec = String.format("%03d sec", elapsedMillis % 1000);

        this.score = score;
        this.recordValue = sec.concat(".").concat(msec);
        this.comboCountValue = count;

        return this;
    }
}