package com.lw.ui.activity;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.bumptech.glide.Glide;
import com.lw.adapter.SourceDrawerAdpater;
import com.lw.novelreader.R;
import com.lw.ttzw.DataInterface;
import com.lw.ttzw.SourceSelector;
import com.lw.ui.fragment.BookShelftFragment;
import com.lw.ui.fragment.LastUpdateMainFragment;
import com.lw.ui.fragment.SortKindNovelMainFragment;
import com.lw.ui.fragment.WebNovelFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

	private Fragment mShelft;
	private Fragment mLastUpdate;
	private Fragment mOnlineKind;
	private Fragment mSortKindNovel;
	
	private Fragment mCurrentFragment;
	
	@Bind(R.id.drawer_layout)
	DrawerLayout mDrawer;
	@Bind(R.id.start_drawer)
	ListView mDrawerList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		System.out.println(Glide.getPhotoCacheDir(this).getPath());
//		mLastUpdate = new LastNovelListFragment();
		mLastUpdate = new LastUpdateMainFragment();
		mLastUpdate.setArguments(new Bundle());
		mShelft = new BookShelftFragment();
		mShelft.setArguments(new Bundle());
		mOnlineKind = new WebNovelFragment();
		mSortKindNovel = new SortKindNovelMainFragment();
		if (savedInstanceState == null) {
			mCurrentFragment = mShelft;
			getSupportFragmentManager().beginTransaction().add(R.id.container, mShelft).commit();
		}
		
		findViewById(R.id.shelft).setOnClickListener(this);
		findViewById(R.id.books).setOnClickListener(this);
		findViewById(R.id.online_novels).setOnClickListener(this);
		findViewById(R.id.sort_kind_novel).setOnClickListener(this);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.inflateMenu(R.menu.toobar_menu);
		
		mDrawerList.setAdapter(new SourceDrawerAdpater(this));
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DataInterface di = (DataInterface) parent.getItemAtPosition(position);
				SourceSelector.setDefaultSource(di);
				Toast.makeText(MainActivity.this, getString(R.string.change_source_to, di.getTag()), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.toobar_menu , menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		System.out.println("onOptionsItemSelected");
		switch (id) {
		case R.id.search:
			SearchActvity.startSearchActivity(this);
			break;
		case R.id.setting:
		    SettingActivity.startSettingActivity();
		    break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(mCurrentFragment instanceof OnKeyListener) {
			boolean b = ((OnKeyListener) mCurrentFragment).onKey(null, keyCode, event);
			if(b)
				return b;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction().hide(mCurrentFragment);
		switch (id) {
		case R.id.shelft:
			mCurrentFragment = mShelft;
			fm.beginTransaction().replace(R.id.container, mCurrentFragment).commit();
			break;
		case R.id.books:
//			mCurrentFragment = mLastUpdate;
			mCurrentFragment = new LastUpdateMainFragment();
			fm.beginTransaction().replace(R.id.container, mCurrentFragment).commit();
			break;
		case R.id.online_novels:
			mCurrentFragment = mOnlineKind;
			fm.beginTransaction().replace(R.id.container, mCurrentFragment).commit();
			break;
		case R.id.sort_kind_novel:
			mCurrentFragment = mSortKindNovel;
			fm.beginTransaction().replace(R.id.container, mCurrentFragment).commit();
			break;
		default:
			break;
		}
	}
}
