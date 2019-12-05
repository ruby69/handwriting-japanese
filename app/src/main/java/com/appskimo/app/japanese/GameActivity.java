package com.appskimo.app.japanese;

import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.appskimo.app.japanese.service.GameService;
import com.appskimo.app.japanese.service.MiscService;
import com.appskimo.app.japanese.service.PrefsService_;
import com.appskimo.app.japanese.service.WordService;
import com.appskimo.app.japanese.ui.frags.GameHistoryFragment;
import com.appskimo.app.japanese.ui.frags.GameHistoryFragment_;
import com.appskimo.app.japanese.ui.frags.GameMenuFragment;
import com.appskimo.app.japanese.ui.frags.GameMenuFragment_;
import com.appskimo.app.japanese.ui.frags.GamePlayFragment;
import com.appskimo.app.japanese.ui.frags.GamePlayFragment_;
import com.google.android.gms.ads.AdView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

@Fullscreen
@EActivity(R.layout.activity_game)
public class GameActivity extends AppCompatActivity {
    @ViewById(R.id.viewPager) ViewPager viewPager;
    @ViewById(R.id.adBanner) AdView adBanner;

    @Pref PrefsService_ prefs;

    @Bean WordService vocabService;
    @Bean MiscService miscService;
    @Bean GameService gameService;

    private PagerAdapter pagerAdapter;

    private GameMenuFragment gameMenuFragment = GameMenuFragment_.builder().build();
    private GamePlayFragment gamePlayFragment = GamePlayFragment_.builder().build();
    private GameHistoryFragment gameHistoryFragment = GameHistoryFragment_.builder().build();

    private DialogInterface.OnClickListener quitListener = (dialog, i) -> {
        gamePlayFragment.stopSession();
        viewPager.setCurrentItem(0, true);
        dialog.dismiss();
    };
    private DialogInterface.OnClickListener resumeListener = (dialog, i) -> {
        gamePlayFragment.resumeSession();
        dialog.dismiss();
    };

    @AfterInject
    void afterInject() {
        miscService.applyFontScale(this);
    }

    @AfterViews
    void afterViews() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        miscService.loadBannerAdView(adBanner);
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> items = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            items.add(gameMenuFragment);
            items.add(new Fragment());
            items.add(gamePlayFragment);
            items.add(new Fragment());
            items.add(gameHistoryFragment);
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        Fragment fragment = pagerAdapter.getItem(viewPager.getCurrentItem());
        if (fragment instanceof GamePlayFragment) {
            if (gameService.isReady()) {
                gameService.cancelSession();
                viewPager.setCurrentItem(0, true);

            } else if (gameService.isPlaying()) {
                gamePlayFragment.pauseSession();
                miscService.showAdDialog(this, R.string.label_game_pause, R.string.label_game_quit, quitListener, R.string.label_game_resume, resumeListener);
            }
        } else if (fragment instanceof GameHistoryFragment) {
            viewPager.setCurrentItem(0, false);

        } else {
            miscService.showAdDialog(this, R.string.label_game_finish, (dialog, i) -> finish());
        }
    }
}
