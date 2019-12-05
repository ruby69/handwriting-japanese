package com.appskimo.app.japanese.ui.view;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.GameRecord;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.DecimalFormat;

@EViewGroup(R.layout.view_game_record_item)
public class GameRecordItemView extends RelativeLayout {
    @ViewById(R.id.rankView) TextView rankView;
    @ViewById(R.id.comboCount) TextView comboCount;
    @ViewById(R.id.scoreView) TextView scoreView;
    @ViewById(R.id.dictionaryTitle) TextView dictionaryTitle;
    @ViewById(R.id.timeRecord) TextView timeRecord;
    @ViewById(R.id.registDateView) TextView registDateView;
    @StringRes(R.string.label_game_combo1) String comboLabel;
    private static final DecimalFormat scoreFormat = new DecimalFormat("#,###");
    private static final FastDateFormat df = FastDateFormat.getInstance();

    public GameRecordItemView(Context context) {
        super(context);
    }

    public void setRecord(int no, GameRecord gameRecord) {
        rankView.setText(String.valueOf(no + 1));
        scoreView.setText(scoreFormat.format(gameRecord.getScore()));
        dictionaryTitle.setText(gameRecord.getDictionary().getName());
        comboCount.setText(String.format(comboLabel, gameRecord.getMaxComboCount()));
        timeRecord.setText(getRecordTime(gameRecord.getRecordTime()));
        registDateView.setText(df.format(gameRecord.getRegistTime()));
    }

    public String getRecordTime(long elapsedMillis) {
        String sec = String.valueOf(elapsedMillis / 1000);
        String msec = String.format("%03d sec", elapsedMillis % 1000);
        return sec.concat(".").concat(msec);
    }
}