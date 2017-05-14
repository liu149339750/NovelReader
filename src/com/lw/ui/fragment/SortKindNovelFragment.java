package com.lw.ui.fragment;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.lw.novelreader.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class SortKindNovelFragment extends BaseFragment {

	@Bind(R.id.webview)
	WebView mWebView;

	TextView mProgress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.online_layout, null);
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
		mWebView = (WebView) view.findViewById(R.id.webview);
		mProgress = (TextView) view.findViewById(R.id.progress);
		mWebView.loadUrl("http://m.ttzw.com/class/0/1.html");
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				System.out.println("url=" + url);
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				mProgress.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mProgress.setVisibility(View.GONE);
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				if (url.startsWith("http://m.ttzw")) {
					super.onLoadResource(view, url);
					System.out.println(url);
				}
			}

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view,
					String url) {
				url = url.toLowerCase();
				if (!url.startsWith("http://m.ttzw") && !url.startsWith("http://www.ttzw") && !url.startsWith("http://zhannei")) {
					return new WebResourceResponse(null, null, null);
				} else {
					return super.shouldInterceptRequest(view, url);
				}
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				mProgress.setText("" + newProgress + "%");
			}
		});

	}

	@Override
	protected boolean onBackPress() {
		System.out.println("onBackPress");
		if (mWebView.canGoBack()) {
			System.out.println("goback");
			mWebView.goBack();
			return true;
		}
		return super.onBackPress();
	}
}
