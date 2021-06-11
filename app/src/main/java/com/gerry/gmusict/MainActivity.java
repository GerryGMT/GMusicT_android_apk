package com.gerry.gmusict;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gerry.gmusict.databinding.ActivityMainBinding;
import com.gerry.gmusict.menu.AlbumFragment;
import com.gerry.gmusict.menu.ArtistFragment;
import com.gerry.gmusict.menu.PlaylistFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        listView = findViewById(R.id.listView);
        runtimePermission();

        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    //Permission to access storage
    private void runtimePermission()
    {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                displaySong();
            }


            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    //To scroll through the fragments which are the tabs
    private void setUpWithViewPager(ViewPager viewPager){
        MainActivity.SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PlaylistFragment(),"Playlist");
        adapter.addFragment(new AlbumFragment(),"Album");
        adapter.addFragment(new ArtistFragment(),"Artist");
        viewPager.setAdapter(adapter);
    }
    private static class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mfragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager){super(manager);}

        @NonNull
        @Override
        public Fragment getItem(int position){return mfragmentList.get(position);}

        @Override
        public int getCount(){return mfragmentList.size();}

        public void addFragment(Fragment fragment, String title){
            mfragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position){return mFragmentTitleList.get(position);}

    }

    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        assert files != null;
        for (File singleFile : files){
            if (singleFile.isDirectory() && !singleFile.isHidden()){
                arrayList.addAll(findSong(singleFile));
            }
            else {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav") || singleFile.getName().endsWith(".wav")|| singleFile.getName().endsWith(".w"))
                {
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }
    public void displaySong()
    {
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];
        for (int i=0;i<mySongs.size();i++)
        {
            items[i]= mySongs.get(i).getName().replace(".mp3","").replace(".wav","");
        }

        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);
    }
    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"InflateParams", "ViewHolder"}) View view = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView txtSong = view.findViewById(R.id.txtSong);
            txtSong.setSelected(true);
            txtSong.setText(items[position]);
            return view;
        }
    }
}