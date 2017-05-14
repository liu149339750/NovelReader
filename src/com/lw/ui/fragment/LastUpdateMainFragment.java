package com.lw.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.lw.novel.common.Util;
import com.lw.novelreader.R;
import com.lw.ttzw.TTZWManager;
import com.viewpagerindicator.TitlePageIndicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LastUpdateMainFragment extends Fragment{

	@Bind(R.id.viewpager)
	 ViewPager mPager;
	@Bind(R.id.titles)
	TitlePageIndicator mPageIndicator;
	
	private MyPagerAdapter mPagerAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.lastupdate_main, null);
		ButterKnife.bind(this, view);
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if(mPager == null) {
			System.out.println("null");
			mPager = (ViewPager) view.findViewById(R.id.viewpager);
			mPageIndicator = (TitlePageIndicator) view.findViewById(R.id.titles);
		}
		mPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
		String kinds[] = getResources().getStringArray(R.array.novel_kinds);
		String urls[] = getResources().getStringArray(R.array.urls);
		for(int i=0;i<kinds.length;i++) {
			mPagerAdapter.add(LastNovelListFragment.newInstance(kinds[i],urls[i]));
		}
		mPager.setAdapter(mPagerAdapter);
		mPageIndicator.setViewPager(mPager);
		Util.setIndicatorTheme(getActivity(), mPageIndicator);
		
	}
	
	static class MyPagerAdapter extends FragmentPagerAdapter {

		
		private List<Fragment> mFragments;
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragments = new ArrayList<Fragment>();
		}
		
		public void add(Fragment f) {
			mFragments.add(f);
		}

		@Override
		public Fragment getItem(int arg0) {
			return mFragments.get(arg0);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return mFragments.get(position).getArguments().getString("title");
		}
	}
	
}
