package com.handmark.pulltorefresh.library;

import com.handmark.pulltorefresh.library.PullToRefreshListView.InternalListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView.InternalListViewSDK9;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class PullToRefreshAndLoadMoreListView extends PullToRefreshListView{

	private LayoutInflater mInflater;
	// footer view
	private RelativeLayout mFooterView;
	// private TextView mLabLoadMore;
	private ProgressBar mProgressBarLoadMore;
	
	// Listener to process load more items when user reaches the end of the list
	private OnLoadMoreListener mOnLoadMoreListener;
	// To know if the list is loading more items
	private boolean mIsLoadingMore = false;
	private int mCurrentScrollState;
		
	public PullToRefreshAndLoadMoreListView(Context context) {
		super(context);
	}
	public PullToRefreshAndLoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	@Override
	protected ListView createListView(Context context, AttributeSet attrs) {
		final ListView lv;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			lv = new InternalListViewSDK9(context, attrs);
		} else {
			lv = new InternalListView(context, attrs);
		}
		
		
		mFooterView = new RelativeLayout(context);
		mProgressBarLoadMore = new ProgressBar(context);
		mProgressBarLoadMore.setLayoutParams(new LayoutParams(70, 70));
		mProgressBarLoadMore.setIndeterminateDrawable(
				context.getResources().getDrawable(R.drawable.progress_indeterminate_horizontal_holo));
		mProgressBarLoadMore.setVisibility(View.GONE);
		mFooterView.addView(mProgressBarLoadMore);
		lv.addFooterView(mFooterView);
		return lv;
	}
	
	/**
	 * Register a callback to be invoked when this list reaches the end (last
	 * item be visible)
	 * 
	 * @param onLoadMoreListener
	 *            The callback to run.
	 */

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		mOnLoadMoreListener = onLoadMoreListener;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		
		if (mOnLoadMoreListener != null) {
			if (visibleItemCount == totalItemCount) {
				mProgressBarLoadMore.setVisibility(View.GONE);
				// mLabLoadMore.setVisibility(View.GONE);
				return;
			}

			boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
			if (DEBUG) {
				Log.i("PTRAndLoadMore", "loadMore:" + loadMore + "..mIsLoadingMore:" + mIsLoadingMore 
						+ "..mCurrentScrollState:" + mCurrentScrollState);
				
			}
			if (!mIsLoadingMore && loadMore
					&& mCurrentScrollState != SCROLL_STATE_IDLE) {
				mProgressBarLoadMore.setVisibility(View.VISIBLE);
				// mLabLoadMore.setVisibility(View.VISIBLE);
				mIsLoadingMore = true;
				onLoadMore();
			}

		}
	};
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		super.onScrollStateChanged(view, scrollState);
		mCurrentScrollState = scrollState;
	}
	
	public void onLoadMore() {
		if (DEBUG) 
			Log.d("PullToRefreshAndLoadMoreListView", "onLoadMore");
		if (mOnLoadMoreListener != null) {
			mOnLoadMoreListener.onLoadMore();
		}
	}

	/**
	 * Notify the loading more operation has finished
	 */
	public void onLoadMoreComplete() {
		if (DEBUG) 
			Log.d("PullToRefreshAndLoadMoreListView", "onLoadMoreComplete");
		mIsLoadingMore = false;
		mProgressBarLoadMore.setVisibility(View.GONE);
	}
	/**
	 * Interface definition for a callback to be invoked when list reaches the
	 * last item (the user load more items in the list)
	 */
	public interface OnLoadMoreListener {
		/**
		 * Called when the list reaches the last item (the last item is visible
		 * to the user)
		 */
		public void onLoadMore();
	}
	
}
