package com.appskimo.app.japanese.ui.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.Dictionary;
import com.appskimo.app.japanese.domain.DictionaryWord;
import com.appskimo.app.japanese.domain.SupportLanguage;
import com.appskimo.app.japanese.domain.Word;
import com.appskimo.app.japanese.event.OpenPronounDialog;
import com.appskimo.app.japanese.event.RefreshCardWord;
import com.appskimo.app.japanese.event.RefreshListWord;
import com.appskimo.app.japanese.service.MiscService;
import com.appskimo.app.japanese.service.PrefsService_;
import com.appskimo.app.japanese.service.WordService;
import com.appskimo.app.japanese.ui.dialog.StrokeAnimationTuneDialog;
import com.appskimo.app.japanese.ui.dialog.StrokeAnimationTuneDialog_;
import com.eftimoff.androipathview.PathView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.larvalabs.svgandroid.SVGParser;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionRes;
import org.androidannotations.annotations.res.IntegerRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

@EViewGroup(R.layout.view_writing_pad)
public class WritingPadView extends RelativeLayout {
    @ViewById(R.id.guideView) PathView guideView;
    @ViewById(R.id.strokeView) PathView strokeView;
    @ViewById(R.id.strokeOrderView) StrokeOrderView strokeOrderView;
    @ViewById(R.id.signaturePad) SignaturePad signaturePad;
    
    @ViewById(R.id.writingSettings) FloatingActionsMenu writingSettings;
    @ViewById(R.id.showNote) FloatingActionButton showNoteButton;
    @ViewById(R.id.showGuideLine) FloatingActionButton showGuideLineButton;
    @ViewById(R.id.showStrokeOrder) FloatingActionButton showStrokeOrderButton;

    @ViewById(R.id.noteView) View noteView;
    @ViewById(R.id.onlyPronounView) TextView onlyPronounView;
    @ViewById(R.id.pronounView) TextView pronounView;
    @ViewById(R.id.meaningView) TextView meaningView;
    @ViewById(R.id.pronounMeaningView) View pronounMeaningView;
    @ViewById(R.id.check) ImageView check;

    @Bean MiscService miscService;
    @Bean WordService wordService;
    @Pref PrefsService_ prefs;
    @SystemService WindowManager windowManager;
    @IntegerRes(R.integer.max_line_width) int maxLineWidth;
    @IntegerRes(R.integer.min_line_width) int minLineWidth;
    @ColorRes(R.color.pink) int pink;
    @ColorRes(R.color.grey_light) int greyLight;
    @DimensionRes(R.dimen.stroke_guide_top_spacing2) float strokeGuideTopSpacing;

    private static final int lineWidthFactor = 5;
    private int currentWordPosition;
    private final Matrix guideScaleMatrix = new Matrix();
    private final Matrix strokeScaleMatrix = new Matrix();
    private final Matrix translateMatrix = new Matrix();

    private StrokeAnimationTuneDialog strokeAnimationTuneDialog = StrokeAnimationTuneDialog_.builder().build();

    private static final int STROKE_BASE_DURATION = 100;
    private ObjectAnimator strokeAnimator;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    private final RectF rectF = new RectF(0, 0, 0, 0);
    private List<DictionaryWord> words = new ArrayList<>();
    private boolean showGuideLine = true;
    private boolean showNote = true;
    private boolean showStrokeOrder = true;

    private FragmentManager fragmentManager;


    public WritingPadView(Context context) {
        super(context);
    }

    public WritingPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterInject
    void afterInject() {
        populateWords();
    }

    @AfterViews
    void afterViews() {
        initGuideView();
    }

    @UiThread(delay = 500)
    void initGuideView() {
        guideView.setPathWidth(prefs.guideLineStrokeWidth().get());

        float guideScaleUnit = wordService.getGuideScaleUnit();
        float density = wordService.getDensity();
        guideScaleMatrix.setScale(guideScaleUnit, guideScaleUnit);
        strokeScaleMatrix.setScale(density, density);

        setPathTo();
    }

    @Click(R.id.stroke)
    void onClickStroke() {
        if (strokeAnimator != null) {
            strokeAnimator.cancel();
            strokeAnimator = null;
        }

        List<String> pathArrayList = getPathList();
        if (pathArrayList != null) {
            int duration = prefs.strokeSpeed().getOr(3) * STROKE_BASE_DURATION * pathArrayList.size();
            strokeAnimator = strokeView.getPathAnimator().duration(duration).build();
            strokeAnimator.setRepeatCount(prefs.strokeRepeatCount().getOr(2));
            strokeAnimator.setInterpolator(decelerateInterpolator);
            strokeAnimator.start();
        }
    }

    @Click(R.id.clear)
    void onClickClear() {
        strokeView.setPercentage(0F);
        signaturePad.clear();
    }

    @Click(R.id.hear)
    void onClickHear() {
        Word word = dictionaryWord.getWord();
        if(word.getWordUid() < 141) {
            miscService.speech(word.getWord());
        } else {
            EventBus.getDefault().post(new OpenPronounDialog(word));
        }
    }

    @Click(R.id.prev)
    void onClickPrev() {
        applyPosition(currentWordPosition > 0 ? currentWordPosition - 1 : words.size() - 1);
    }

    @Click(R.id.next)
    void onClickNext() {
        applyPosition(currentWordPosition < words.size() - 1 ? currentWordPosition + 1 : 0);
    }

    @Click(R.id.random)
    void onClickRandom() {
        int position = currentWordPosition;
        int wordSize = words.size();
        do {
            position = ((int) (Math.random() * 100)) % wordSize;
        } while (position == currentWordPosition);

        applyPosition(position);
    }

    private DictionaryWord dictionaryWord;
    public void applyPosition(int position) {
        clear();
        cancelStroke();
        if (position > -1 && words.size() > 0) {
            currentWordPosition = position;
            if (signaturePad != null) {
                signaturePad.clear();
            }

            setPathTo();
            if (!showGuideLine) {
                clearGuide();
            }

            dictionaryWord = words.get(currentWordPosition);
            Word word = dictionaryWord.getWord();
            String pronoun = word.getPronunciation();
            if(word.getWordUid() < 141) {
                onlyPronounView.setVisibility(View.VISIBLE);
                pronounMeaningView.setVisibility(View.GONE);
                onlyPronounView.setText(pronoun);

            } else {
                onlyPronounView.setVisibility(View.GONE);
                pronounMeaningView.setVisibility(View.VISIBLE);
                populatePronoun(pronoun);
                SupportLanguage supportLanguage = SupportLanguage.valueOf(prefs.userLanguage().getOr("en"));
                meaningView.setText(word.getMeaningForCard(supportLanguage, prefs.userCountry().getOr("US")));
            }

            pinning();

            checkAd();
        }
    }

    private int touchCount = 0;
    private int checkCount = 4;
    private void checkAd() {
        if (countForAd()) {
            miscService.showAdDialog(getActivity(), false, R.string.label_continue, (dialog, i) -> {});
        }
    }

    private boolean countForAd() {
        boolean b = touchCount++ % checkCount == 0 && touchCount > 1;
        if (b) {
            touchCount = 0;
            checkCount++;
        }
        return b;
    }

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    private void populatePronoun(String pronouns) {
        StringBuilder sb = new StringBuilder();
        String[] arr = pronouns.split(", ");
        int count = arr.length > 3 ? 3 : arr.length;
        for(int i = 0; i<count; i++) {
            sb.append(arr[i].split("\\|")[0]).append((i < count - 1) ? ", " : "");
        }
        pronounView.setText(sb.toString());
    }

    @UiThread
    void pinning() {
        if (wordService.pinned(dictionaryWord)) {
            check.setColorFilter(pink, PorterDuff.Mode.SRC_ATOP);
        } else {
            check.setColorFilter(greyLight, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Click({R.id.checkLayer, R.id.check})
    void onClickCheck() {
        wordService.pin(dictionaryWord);
        pinning();
        EventBus.getDefault().post(new RefreshListWord(currentWordPosition));
        EventBus.getDefault().post(new RefreshCardWord());
    }

    @Click(R.id.showNote)
    void onClickShowNote() {
        showNote = !showNote;
        if (showNote) {
            noteView.setVisibility(View.VISIBLE);
            showNoteButton.setColorNormalResId(R.color.pink_light);
        } else {
            noteView.setVisibility(View.INVISIBLE);
            showNoteButton.setColorNormalResId(R.color.white);
        }
    }

    @Click(R.id.showGuideLine)
    void onClickShowGuideLine() {
        cancelStroke();
        showGuideLine = !showGuideLine;
        if (showGuideLine) {
            setPathTo();
            showGuideLineButton.setColorNormalResId(R.color.pink_light);
        } else {
            clearGuide();
            showGuideLineButton.setColorNormalResId(R.color.white);
        }
    }

    @Click(R.id.showStrokeOrder)
    void onClickShowStrokeOrder() {
        showStrokeOrder = !showStrokeOrder;
        if (showStrokeOrder) {
            strokeOrderView.setVisibility(View.VISIBLE);
            showStrokeOrderButton.setColorNormalResId(R.color.pink_light);
        } else {
            showStrokeOrderButton.setColorNormalResId(R.color.white);
            strokeOrderView.setVisibility(View.INVISIBLE);
        }
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Click(R.id.tunePlay)
    void onClickTunePlay() {
        cancelStroke();
        strokeAnimationTuneDialog.show(fragmentManager, StrokeAnimationTuneDialog.TAG);
        writingSettings.collapse();
    }

    private float adjustLineWidth(float lineWidth) {
        lineWidth += lineWidthFactor;
        if (lineWidth > maxLineWidth) {
            lineWidth = minLineWidth;
        }
        return lineWidth;
    }

    private List<String> getPathList() {
        try {
            return words.get(currentWordPosition).getWord().getPathList();
        } catch (Exception e) {
            return null;
        }
    }

    private List<Word.OrderPoint> getOrderList() {
        try {
            return words.get(currentWordPosition).getWord().getOrderList();
        } catch (Exception e) {
            return null;
        }
    }

    private void clear() {
        signaturePad.clear();
        guideView.clear();
        strokeView.clear();
    }

    private void clearGuide() {
        signaturePad.clear();
        guideView.clear();
    }

    private void setPathTo() {
        guideView.clear();
        strokeView.clear();

        List<String> pathStrings = getPathList();
        if (pathStrings != null) {
            computeBounds(pathStrings, guideScaleMatrix);   // must call at first.
            drawGuideStroke(pathStrings);                   // must call at second.
            drawGuideStrokeOrder(getOrderList());           // must call at 3rd.
            drawStroke(pathStrings);                        // must call finally.
        }
    }

    private void drawGuideStroke(List<String> pathStrings) {
        guideView.setPaths(getTransformedPaths(pathStrings, guideScaleMatrix, Boolean.TRUE));
        guideView.setFillAfter(false);
        guideView.setPercentage(1.0F);
    }

    private void drawGuideStrokeOrder(List<Word.OrderPoint> list) {
        float translateX = (strokeOrderView.getWidth() / 2F) - rectF.centerX();
        float translateY = (strokeOrderView.getHeight() / 2F) - rectF.centerY();
        translateMatrix.setTranslate(translateX, translateY + strokeGuideTopSpacing);
        strokeOrderView.drawOrder(list, guideScaleMatrix, translateMatrix);
    }

    private void drawStroke(List<String> pathStrings) {
        strokeView.setPaths(getTransformedPaths(pathStrings, strokeScaleMatrix, Boolean.FALSE));
        strokeView.setFillAfter(false);
    }

    private void computeBounds(List<String> pathStrings, Matrix scaleMatrix) {
        Path tempPath = new Path();
        for (String pathString : pathStrings) {
            Path path = SVGParser.parsePath(pathString);
            path.transform(scaleMatrix);
            tempPath.addPath(path);
        }
        tempPath.computeBounds(rectF, true);
    }

    private List<Path> getTransformedPaths(List<String> pathStrings, Matrix scaleMatrix, boolean center) {
        float translateX = (guideView.getWidth() / 2F) - rectF.centerX();
        float translateY = (guideView.getHeight() / 2F) - rectF.centerY();
        List<Path> list = new ArrayList<>();
        for (String pathString : pathStrings) {
            Path path = SVGParser.parsePath(pathString);
            path.transform(scaleMatrix);
            if (center) {
                translateMatrix.setTranslate(translateX, translateY);
                path.transform(translateMatrix);
            }
            list.add(path);
        }

        return list;
    }

    public void populateWords() {
        Dictionary dictionary = wordService.getSelectedDictionary();
        if (dictionary != null && dictionary.getDictionaryWords() != null) {
            words = new ArrayList<>(dictionary.getDictionaryWords());
        }
    }

    public void refreshWords() {
        populateWords();
        signaturePad.clear();
        applyPosition(0);
    }

    public void cancelStroke() {
        if (strokeAnimator != null) {
            strokeAnimator.cancel();
        }
    }
}