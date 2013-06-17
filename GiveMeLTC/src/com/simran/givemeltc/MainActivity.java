package com.simran.givemeltc;

import java.sql.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.simran.givemeltc.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.simran.givemeltc.adapters.MinerListViewAdapter;
import com.simran.givemeltc.controls.HomeTutorialDialog;
import com.simran.givemeltc.interfaces.RefreshListener;
import com.simran.givemeltc.managers.MinerManager;
import com.simran.givemeltc.managers.PrefManager;
import com.simran.givemeltc.models.Miner;
import com.simran.givemeltc.tasks.GetMinerDataTask;

@SuppressLint("NewApi")
public class MainActivity extends CustomSlidingActivity {

	TextView mUsername, mRewards, mRoundEstimate, mHashrate, mPayoutHistory, mRoundShares, mTimestamp, mLastUpdate;
	TextView mError;

	ViewPager vpWorkers;
	ListView lvWorkers;

	MinerListViewAdapter mLVAdapter;

	RefreshListener refreshListener;
	
	PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setSlidingActionBarEnabled(true);

		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
		
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				new GetDataTask().execute();
			}
		});

		mScrollView = mPullRefreshScrollView.getRefreshableView();
		
		
		mUsername = (TextView) findViewById(R.id.tv_username);
		mRewards = (TextView) findViewById(R.id.tv_confirmed_rewards);
		mRoundEstimate = (TextView) findViewById(R.id.tv_round_estimate);
		mHashrate = (TextView) findViewById(R.id.tv_total_hashrate);
		mPayoutHistory = (TextView) findViewById(R.id.tv_payout_history);
		mRoundShares = (TextView) findViewById(R.id.tv_round_shares);
		mLastUpdate = (TextView) findViewById(R.id.tv_last_update);
		mError = (TextView) findViewById(R.id.tv_error);

		
		/*
		 * Initialize
		 */
		setUpTutorial();
		setUpSlidingDrawer();
		setUpListeners();
		updateView();	
	}

	@SuppressLint("NewApi")
	public void aboutDialog()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Simran's GMLTC App - Alpha");
		alert.setMessage("Credits:\nApp Base - nninchuu\nIcon - doublejdesign.co.uk\n\nDonation Address:\nLRgbgTa3XNQSEUhnwC6Ye2vjiCV2CNRpib");


		alert.setPositiveButton("Copy Address", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  // Do something with value!
			 ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			        //manager.setText( showTextParam.getText() );
			        ClipData clip = ClipData.newPlainText(null, "LRgbgTa3XNQSEUhnwC6Ye2vjiCV2CNRpib" );
			        manager.setPrimaryClip(clip);
			        // Show a message:
			        Toast.makeText(getBaseContext(), "Address in clipboard", Toast.LENGTH_SHORT).show();
		  }
		});

		alert.show();
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here
			new GetMinerDataTask(getBaseContext(), refreshListener).execute();
			
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshScrollView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (PrefManager.getAPIKey(this).isEmpty()) {
			mError.setVisibility(View.VISIBLE);
			mError.setText("ERROR: Empty API Key, enter one in settings!");
			askAPIKey();
			
		} else {
			updateView();
			mError.setVisibility(View.GONE);

		}
	}
	
	public void askAPIKey()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("API Key Required");
		alert.setMessage("Please enter your GMLTC API Key!");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Editable value = input.getText();
		  	String APIKey = value.toString();
			PrefManager.setAPIKey(getApplicationContext(), input.getText().toString());
			Toast.makeText(getApplicationContext(), "Saved your API key", Toast.LENGTH_LONG).show();
			updateView();
			mError.setVisibility(View.GONE);
		  // Do something with value!
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// This uses the imported MenuItem from ActionBarSherlock
		Intent intent = null;

			if (item.getItemId() == android.R.id.home)
			{
				toggle();
			}
			/*if (item.getItemId() == R.id.action_refresh)
			{
				updateView();
			}*/
			if (item.getItemId() == R.id.action_settings)
			{
				intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
			}
			if (item.getItemId() == R.id.action_about)
			{
				aboutDialog();
			}
		return true;
	}

	/**
	 * Updating view with new data
	 */
	public void updateView() {
		new GetMinerDataTask(this, refreshListener).execute();
	}

	/**
	 * Setting up listeners
	 */
	private void setUpListeners() {
		refreshListener = new RefreshListener() {

			@Override
			public void onRefresh() {
				Miner mMiner = new Miner();
				mMiner = MinerManager.getInstance().miner;

				mUsername.setText(mMiner.username);
				mRewards.setText(String.valueOf(mMiner.confirmed_rewards) + " LTC");
				mRoundEstimate.setText(String.valueOf(mMiner.round_estimate) + " LTC");
				mHashrate.setText(String.valueOf(mMiner.total_hashrate) + " kH/s");
				mPayoutHistory.setText(String.valueOf(mMiner.payout_history) + " LTC");
				mRoundShares.setText(String.valueOf(mMiner.round_shares));

				mLVAdapter.notifyDataSetChanged();

				long dtMili = System.currentTimeMillis();
				Date d = new Date(dtMili);
				CharSequence s = DateFormat.format("hh:mm:ss, EEEE, MMMM d, yyyy ", d.getTime());
				// textView is the TextView view that should display it
				mLastUpdate.setText(s);

			}
		};
	}

	private void setUpSlidingDrawer() {
		setBehindContentView(R.layout.slidingdrawer_workers);
		getSlidingMenu().setMode(SlidingMenu.LEFT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
		getSlidingMenu().setFadeDegree(0.35f);

		lvWorkers = (ListView) getSlidingMenu().findViewById(R.id.lv_workers);
		mLVAdapter = new MinerListViewAdapter(this);
		lvWorkers.setAdapter(mLVAdapter);
	}
	
	private void setUpTutorial()
	{
		if (!PrefManager.getSeenHomeTutorial(this))
		{
			HomeTutorialDialog dialog = new HomeTutorialDialog(this);
			dialog.show();
		}
	}
}
