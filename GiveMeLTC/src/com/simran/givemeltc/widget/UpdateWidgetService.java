package com.simran.givemeltc.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.simran.givemeltc.R;
import com.simran.givemeltc.interfaces.RefreshListener;
import com.simran.givemeltc.managers.MinerManager;
import com.simran.givemeltc.models.Miner;
import com.simran.givemeltc.tasks.GetMinerDataTask;

public class UpdateWidgetService extends Service {
	private static final String LOG = "GIVEMELTC";
	Miner mMiner = new Miner();
	RefreshListener refreshListener;
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(LOG, "UpdateWidgetService: onStart() called");
		// Create some random data
		selfUpdate(intent);
		widgetClicked(intent);
		
		// stopSelf();
	}
	
	public void selfUpdate(Intent intent)
	{
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
		int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		
		for (int widgetId : allWidgetIds) {
		final RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);

		
		mMiner = MinerManager.getInstance().miner;
		remoteViews.setTextViewText(R.id.widget_hashrate, mMiner.total_hashrate + " kH/s");
		remoteViews.setTextViewText(R.id.widget_round_estimate, mMiner.round_estimate + " LTC");
		remoteViews.setTextViewText(R.id.widget_confirmed_rewards, mMiner.confirmed_rewards + " LTC");


		new GetMinerDataTask(getApplicationContext(), refreshListener).execute();
		
		appWidgetManager.updateAppWidget(widgetId, remoteViews);


		}
	}
	
	public void widgetClicked(Intent intent)
	{
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
		int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		Log.w(LOG, "From Intent: " + String.valueOf(allWidgetIds.length));

		if (allWidgetIds.length == 0) {
			return;
		}

		for (int widgetId : allWidgetIds) {

			final RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);

			refreshListener = new RefreshListener() {
				@Override
				public void onRefresh() {
					Log.w(LOG, "onRefresh() called");
					mMiner = MinerManager.getInstance().miner;
				}
			};

			remoteViews.setTextViewText(R.id.widget_hashrate, mMiner.total_hashrate + " kH/s");
			remoteViews.setTextViewText(R.id.widget_round_estimate, mMiner.round_estimate + " LTC");
			remoteViews.setTextViewText(R.id.widget_confirmed_rewards, mMiner.confirmed_rewards + " LTC");


			new GetMinerDataTask(getApplicationContext(), refreshListener).execute();

			// Register an onClickListener
			Intent clickIntent = new Intent(this.getApplicationContext(), MyWidgetProvider.class);

			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);

			Toast.makeText(getApplicationContext(), "GiveMeLTC: Updated stats", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}