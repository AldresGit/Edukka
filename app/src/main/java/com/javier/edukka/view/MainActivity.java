package com.javier.edukka.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.javier.edukka.R;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.fragment.ButtonContentFragment;
import com.javier.edukka.fragment.CardContentFragment;
import com.javier.edukka.fragment.ListContentFragment;
import com.javier.edukka.fragment.TileContentFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabs;
    private TextView title;
    private ImageView imageView;
    private final int[] tabIcons = {
        R.drawable.ic_games_white_36dp,
        R.drawable.ic_group_white_36dp,
        R.drawable.ic_schedule_white_36dp,
        //-------------------
        R.drawable.baseline_leak_add_white_36
        //-------------------
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(UserSingleton.getInstance().getUserModel().getUsername());
        imageView = (ImageView) findViewById(R.id.profile_image);
        int resourceId = getResources().getIdentifier(UserSingleton.getInstance().getUserModel().getImage(), "drawable", getPackageName());
        imageView.setImageDrawable(getResources().getDrawable(resourceId));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabs.getTabAt(0).setIcon(tabIcons[0]);
        tabs.getTabAt(1).setIcon(tabIcons[1]);
        tabs.getTabAt(2).setIcon(tabIcons[2]);
        //-------------------------
        tabs.getTabAt(3).setIcon(tabIcons[3]);
        //-------------------------

        tabs.getTabAt(0).setCustomView(R.layout.customlab);
        tabs.getTabAt(1).setCustomView(R.layout.customlab);
        tabs.getTabAt(2).setCustomView(R.layout.customlab);
        //-------------------------
        tabs.getTabAt(3).setCustomView(R.layout.customlab);
        //-------------------------

        tabs.getTabAt(0).getIcon().setColorFilter(Color.WHITE,PorterDuff.Mode.SRC_IN);
        tabs.getTabAt(1).getIcon().setColorFilter(Color.LTGRAY,PorterDuff.Mode.SRC_IN);
        tabs.getTabAt(2).getIcon().setColorFilter(Color.LTGRAY,PorterDuff.Mode.SRC_IN);
        //-------------------------
        tabs.getTabAt(3).getIcon().setColorFilter(Color.LTGRAY,PorterDuff.Mode.SRC_IN);
        //-------------------------

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.WHITE,PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.LTGRAY,PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new TileContentFragment(), "Subjects");
        adapter.addFragment(new ListContentFragment(), "Class");
        adapter.addFragment(new CardContentFragment(), "Activity");
        adapter.addFragment(new ButtonContentFragment(), "Multiplayer");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }

    private static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        //private final List<String> mFragmentTitleList = new ArrayList<>();

        private Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            //mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                int idp = Integer.parseInt(UserSingleton.getInstance().getUserModel().getId());
                Intent intentp = new Intent(MainActivity.this, ProfileActivity.class);
                intentp.putExtra(ProfileActivity.EXTRA_POSITION, idp);
                startActivity(intentp);
                return true;
            case R.id.myclass:
                int idc = Integer.parseInt(UserSingleton.getInstance().getUserModel().getClassId());
                Intent intentc = new Intent(MainActivity.this, ClassActivity.class);
                intentc.putExtra(ClassActivity.EXTRA_POSITION, idc);
                startActivity(intentc);
                return true;
            case R.id.help:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://edukka.000webhostapp.com/"));
                startActivity(browserIntent);
                return true;
            case R.id.logout:
                UserSingleton.getInstance().setUserModel(null);
                finish();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        title.setText(UserSingleton.getInstance().getUserModel().getUsername());
        int resourceId = getResources().getIdentifier(UserSingleton.getInstance().getUserModel().getImage(), "drawable", getPackageName());
        imageView.setImageDrawable(getResources().getDrawable(resourceId));
    }
}
