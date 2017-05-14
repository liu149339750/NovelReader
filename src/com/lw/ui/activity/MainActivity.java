package com.lw.ui.activity;

import com.bumptech.glide.Glide;
import com.lw.novelreader.R;
import com.lw.ui.fragment.BookShelftFragment;
import com.lw.ui.fragment.LastUpdateMainFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

	private Fragment mShelft;
	private Fragment mLastUpdate;
	
	private Fragment mCurrentFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println(Glide.getPhotoCacheDir(this).getPath());
//		mLastUpdate = new LastNovelListFragment();
		mLastUpdate = new LastUpdateMainFragment();
		mLastUpdate.setArguments(new Bundle());
		mShelft = new BookShelftFragment();
		mShelft.setArguments(new Bundle());
		if (savedInstanceState == null) {
			mCurrentFragment = mShelft;
			getSupportFragmentManager().beginTransaction().add(R.id.container, mShelft).commit();
		}
		
		findViewById(R.id.shelft).setOnClickListener(this);
		findViewById(R.id.books).setOnClickListener(this);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.inflateMenu(R.menu.toobar_menu);
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

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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

		default:
			break;
		}
	}
}
