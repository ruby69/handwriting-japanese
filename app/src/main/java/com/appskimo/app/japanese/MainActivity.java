package com.appskimo.app.japanese;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.appskimo.app.japanese.domain.Dictionary;
import com.appskimo.app.japanese.domain.SupportLanguage;
import com.appskimo.app.japanese.event.ChangeFontScale;
import com.appskimo.app.japanese.event.Link;
import com.appskimo.app.japanese.event.SelectLanguage;
import com.appskimo.app.japanese.event.SelectWord;
import com.appskimo.app.japanese.service.MiscService;
import com.appskimo.app.japanese.service.PrefsService_;
import com.appskimo.app.japanese.service.WordService;
import com.appskimo.app.japanese.support.EventBusObserver;
import com.appskimo.app.japanese.ui.dialog.LanguageDialog;
import com.appskimo.app.japanese.ui.dialog.LanguageDialog_;
import com.appskimo.app.japanese.ui.dialog.LinkDialog;
import com.appskimo.app.japanese.ui.frags.CardFragment_;
import com.appskimo.app.japanese.ui.frags.ListFragment_;
import com.appskimo.app.japanese.ui.view.FontScaleView_;
import com.appskimo.app.japanese.ui.view.WritingPadView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @ViewById(R.id.appBarLayout) AppBarLayout appBarLayout;
    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.drawerLayout) DrawerLayout drawerLayout;
    @ViewById(R.id.navigationView) NavigationView navigationView;
    @ViewById(R.id.progressView) View progressView;
    private Spinner spinner;

    @ViewById(R.id.mainViewPager) ViewPager mainViewPager;
    @ViewById(R.id.writingPad) WritingPadView writingPad;

    @Pref PrefsService_ prefs;
    @Bean MiscService miscService;
    @Bean WordService wordService;
    @SystemService AudioManager audioManager;

    private List<Dictionary> dictionaries;
    private PagerAdapter pagerAdapter;
    private LinkDialog linkDialog = new LinkDialog();
    private LanguageDialog languageDialog = LanguageDialog_.builder().build();
    private BottomSheetBehavior bottomSheetBehavior;

    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!BuildConfig.DEBUG) {
            FirebaseAnalytics.getInstance(this);
        }
        getLifecycle().addObserver(new EventBusObserver.AtCreateDestroy(this));
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @AfterInject
    void afterInject() {
        miscService.applyFontScale(this);
        dictionaries = wordService.getDictionaries();
    }

    @AfterViews
    void afterViews() {
        initNavigationDrawer();
        mainViewPager.setAdapter(pagerAdapter);
        initBottomSheet();
    }

    private void initNavigationDrawer() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        var toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        var headerLayout = navigationView.getHeaderView(0);
        TextView language = headerLayout.findViewById(R.id.language);
        language.setText(SupportLanguage.valueOf(prefs.userLanguage().get()).getDisplayName());
        language.setOnClickListener(v -> languageDialog.show(getSupportFragmentManager(), LanguageDialog.TAG));
    }

    private void initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(writingPad);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        writingPad.setFragmentManager(getSupportFragmentManager());
    }

    public void setVisibleWritingPad(boolean visible) {
        appBarLayout.setExpanded(!visible);
        bottomSheetBehavior.setState(visible ? BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        initSpinner(menu);
        initSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initSpinner(Menu menu) {
        dictionaries = wordService.getDictionaries();
        var dictionaryTitles = new String[dictionaries.size()];
        for(int i = 0; i<dictionaries.size(); i++) {
            dictionaryTitles[i] = dictionaries.get(i).getName();
        }

        var adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dictionaryTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) menu.findItem(R.id.spinner).getActionView();
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(spinnerSelectedListener);
        spinner.setSelection(1);
    }

    private AdapterView.OnItemSelectedListener spinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        int count = 0;

        @Override
        public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
            if (count++ > 2) {
                miscService.showDialog(MainActivity.this, R.string.label_continue, (dialog, i) -> {});
            }

            wordService.selectDictionary(position);
            writingPad.cancelStroke();
            writingPad.refreshWords();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    private void initSearchView(Menu menu) {
        var searchItem = menu.findItem(R.id.search);
        var spinnerItem = menu.findItem(R.id.spinner);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                spinnerItem.setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                    setVisibleWritingPad(false);
                    return false;

                } else {
                    spinnerItem.setVisible(true);
                    currentQuery = "";
                    wordService.selectPrevDictionary();
                    writingPad.refreshWords();
                    return true;
                }
            }
        });

        var searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setVisibleWritingPad(false);
                if (query != null && query.trim().length() > 0 && !currentQuery.equals(query)) {
                    find(query);
                    currentQuery = query;
                    searchView.clearFocus();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setVisibleWritingPad(false);
                return false;
            }
        });
    }

    public void find(String text) {
        wordService.find(text);
        writingPad.refreshWords();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuList) {
            mainViewPager.setCurrentItem(0, Boolean.FALSE);
            drawerLayout.closeDrawer(GravityCompat.START);
            setVisibleWritingPad(false);
        } else if (id == R.id.menuFlashCard) {
            mainViewPager.setCurrentItem(1, Boolean.FALSE);
            drawerLayout.closeDrawer(GravityCompat.START);
            setVisibleWritingPad(false);
        } else if (id == R.id.menuGames) {
            GameActivity_.intent(this).flags(PendingIntent.FLAG_IMMUTABLE).start();
        } else if (id == R.id.ttsLink) {
            onEvent(new Link("https://www.greenbot.com/article/2105862/how-to-get-started-with-google-text-to-speech.html"));
        } else if (id == R.id.fontScale) {
            fontScaleDialog = new AlertDialog.Builder(this).setTitle(R.string.label_font_scale).setView(FontScaleView_.build(this)).create();
            fontScaleDialog.show();
        }
        return true;
    }

    private AlertDialog fontScaleDialog;

    @Subscribe
    public void onEvent(ChangeFontScale event) {
        if (fontScaleDialog != null && fontScaleDialog.isShowing()) {
            fontScaleDialog.dismiss();
        }
        LauncherActivity_.intent(this).start();
        finish();
    }

    @Subscribe
    public void onEvent(Link event) {
        linkDialog.setUrl(event.getUrl()).show(getSupportFragmentManager(), LinkDialog.TAG);
    }

    @Subscribe
    public void onEvent(SelectLanguage event) {
        var supportLanguage = event.getSupportLanguage();
        var current = prefs.userLanguage().get();
        if (SupportLanguage.valueOf(current) != supportLanguage) {
            prefs.userLanguage().put(supportLanguage.getCode());
            var intent = new Intent(getApplicationContext(), LauncherActivity_.class);
            intent.putExtra("isRestart", Boolean.TRUE);
            startActivity(intent);
            finish();
        }
    }

    @Subscribe
    public void onEvent(SelectWord event) {
        setVisibleWritingPad(true);
        writingPad.applyPosition(event.getPosition());
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> items = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            items.add(ListFragment_.builder().build());
            items.add(CardFragment_.builder().build());
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

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED){
            setVisibleWritingPad(false);
            return;
        }

        miscService.showDialog(this, R.string.label_finish, (dialog, i) -> finish());
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

}
