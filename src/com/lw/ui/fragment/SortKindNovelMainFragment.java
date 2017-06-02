package com.lw.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.lw.novel.utils.Util;
import com.lw.novelreader.R;
import com.lw.ttzw.DataQueryManager;
import com.viewpagerindicator.TitlePageIndicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SortKindNovelMainFragment extends BaseFragment{

	
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
			System.out.println("in android studio,the mPager is not null,but in eclipse,it is null,why?");
			mPager = (ViewPager) view.findViewById(R.id.viewpager);
			mPageIndicator = (TitlePageIndicator) view.findViewById(R.id.titles);
		}
		mPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
		List<Pair<String, String>> data = DataQueryManager.instance().getSortKindUrlPairs();
		for(int i=0;i<data.size();i++) {
			Pair<String, String>p = data.get(i);
			mPagerAdapter.add(SortNovelListFragment.newInstance(p.first,p.second,R.layout.sort_kind_novel_item));
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
