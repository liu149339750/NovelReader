package com.lw.ui.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.justwayward.reader.view.loadding.CustomDialog;
import com.justwayward.reader.view.readview.BaseReadView;
import com.justwayward.reader.view.readview.Constant;
import com.justwayward.reader.view.readview.OnReadStateChangeListener;
import com.justwayward.reader.view.readview.OverlappedWidget;
import com.justwayward.reader.view.readview.PageWidget;
import com.justwayward.reader.view.readview.ScreenUtils;
import com.justwayward.reader.view.readview.SettingManager;
import com.justwayward.reader.view.readview.SharedPreferencesUtil;
import com.lw.db.DBUtil;
import com.lw.novel.common.Util;
import com.lw.novelreader.BookShelftManager;
import com.lw.novelreader.DownloadService;
import com.lw.novelreader.DownloadTask;
import com.lw.novelreader.R;
import com.lw.presenter.ChapterContentPresenter;
import com.lw.ttzw.NovelManager;
import com.lw.ui.fragment.IChapterContentView;
import com.mingle.widget.LoadingView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class NovelReadActivity extends Activity implements IChapterContentView {

	private BaseReadView mPageWidget;
	private LoadingView mLoading;
	private ViewGroup flReadWidget;

	private ChapterContentPresenter mPresenter;

	private IntentFilter intentFilter = new IntentFilter();
	private Receiver receiver = new Receiver();

	
	 private CustomDialog dialog;
	 
	 private static final String TAG = "NovelReadActivity";
	 
	 private int bookId = 0;
	 private boolean isInBookShelft;
	 
	 private View decodeView;
	 
	 private View topView;
	 private View buttomView;
	 
	 
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
				&& Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		setContentView(R.layout.chapter_content);
		bookId = NovelManager.getInstance().getCurrentNovel().getId();
		isInBookShelft = BookShelftManager.instance().isInbookShelft(bookId);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			finish();
			return;
		}
		Util.hideNavigationBar(this);
		flReadWidget = (ViewGroup) findViewById(R.id.page);
		topView = findViewById(R.id.topview);
		buttomView = findViewById(R.id.buttomview);
		mLoading = (LoadingView) findViewById(R.id.loadView);
		mLoading.setLoadingText("downloading");
		mPresenter = new ChapterContentPresenter(this);
//		mPresenter.prepareChapterContent(NovelManager.getInstance().getChapterId());

		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		intentFilter.addAction(Intent.ACTION_TIME_TICK);

		if(NovelManager.getInstance().getChapterSize() == 0) {
			NovelManager.getInstance().setChaper(DBUtil.queryNovelChapterList(bookId));
		}
		initPagerWidget(bookId +"");
		
		DBUtil.updateReadtime(bookId);
		
		decodeView = getWindow().getDecorView();
		
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topView.getLayoutParams();
        params.topMargin = ScreenUtils.getStatusBarHeight(this) ;
        topView.setLayoutParams(params);
        
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(NovelManager.getInstance().getCurrentNovel().getName());
	}

	private void initPagerWidget(String bookid) {
		if (SharedPreferencesUtil.getInstance().getInt(Constant.FLIP_STYLE, 0) == 0) {
			Log.v(TAG, "new widget");
			mPageWidget = new PageWidget(this, bookid, NovelManager.getInstance().getChaper(), new ReadListener());
		} else {
			mPageWidget = new OverlappedWidget(this, bookid, NovelManager.getInstance().getChaper(), new ReadListener());
		}
		if (SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false)) {
			mPageWidget.setTextColor(ContextCompat.getColor(this, R.color.chapter_content_night),
					ContextCompat.getColor(this, R.color.chapter_title_night));
		}
		flReadWidget.removeAllViews();
		flReadWidget.addView(mPageWidget);
		
		if(NovelManager.getInstance().getChapterId() > 1)
			mPageWidget.setCurrentChapter(NovelManager.getInstance().getChapterId());
		mPageWidget.init(SettingManager.getInstance().getReadTheme());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(receiver, intentFilter);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
	}
	
	
	@Override
	public void showLoading() {
		// TODO Auto-generated method stub
//		getDialog().show();
		flReadWidget.setVisibility(View.GONE);
		mLoading.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideLoading() {
		// TODO Auto-generated method stub
//		getDialog().hide();
		flReadWidget.setVisibility(View.VISIBLE);
		mLoading.setVisibility(View.GONE);
	}

	@Override
	public void showChapterContent(int c) {
		Log.v(TAG, "showCurrent::mPageWidget.isPrepared = " + mPageWidget.isPrepared);
		int curTheme = SettingManager.getInstance().getReadTheme();
		if (!mPageWidget.isPrepared) {
			mPageWidget.setCurrentChapter(c);
			mPageWidget.init(curTheme);
		} else {
//			mPageWidget.jumpToChapter(c);
			mPageWidget.openLoadedChapter(c);
		}
	}
	
	@Override
	public void onLoadFail(int chapter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareNext(String path) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preparaPrev(String path) {
		// TODO Auto-generated method stub

	}

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

	class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mPageWidget != null) {
				if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
					int level = intent.getIntExtra("level", 0);
					mPageWidget.setBattery(100 - level);
				} else if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
					mPageWidget.setTime(sdf.format(new Date()));
				}
			}
		}
	}

	class ReadListener implements OnReadStateChangeListener {

		@Override
		public void onChapterChanged(int chapter) {
			// TODO Auto-generated method stub
			System.out.println("onChapterChanged chapter =" + chapter);
			NovelManager.getInstance().setChapterId(chapter);
			DownloadService.addToDownload(new DownloadTask(NovelManager.getInstance().getCurrentNovel(), chapter, 5));
			if(isInBookShelft) {
				int c = DBUtil.setCurrentReadChapterPosition(bookId, chapter);
			}
		}

		@Override
		public void onPageChanged(int chapter, int page) {
			// TODO Auto-generated method stub
			System.out.println("page=" + page);
		}

		@Override
		public void onLoadChapterFailure(int chapter) {
			System.out.println("onLoadChapterFailure chapter="+chapter);
			mPresenter.prepareChapterContent(chapter);
		}

		@Override
		public void onCenterClick() {
			// TODO Auto-generated method stub
			System.out.println("onCenterClick");
			toggleReadBar();
		}

		@Override
		public void onFlip() {
			// TODO Auto-generated method stub

		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			toggleReadBar();
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
    public CustomDialog getDialog() {
        if (dialog == null) {
            dialog = CustomDialog.instance(this);
            dialog.setCancelable(true);
            dialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
        }
        return dialog;
    }
    
    public void hideDialog() {
        if (dialog != null)
            dialog.hide();
    }

    public void showDialog() {
        getDialog().show();
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
//			Util.hideNavigationBar(this);
			hideReadBar();
		}
	}
	
    private synchronized void toggleReadBar() { // 切换工具栏 隐藏/显示 状态
        if (isVisible(topView)) {
            hideReadBar();
        } else {
            showReadBar();
        }
    }
    
    private synchronized void hideReadBar() {
    	gone(topView,buttomView);
//        hideStatusBar();
//        decodeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    	Util.hideNavigationBar(this);
    }

    private synchronized void showReadBar() { // 显示工具栏
    	visible(topView,buttomView);
        showStatusBar();
        decodeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    
    protected void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);

    }

    protected void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);

    }

    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }

    }
    
    protected boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }
		
	
	public static void startNovelReadActivity(Context context,int chapter) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("chapter", chapter);
		intent.putExtras(bundle);
		intent.setClass(context, NovelReadActivity.class);
		context.startActivity(intent);
	}
}