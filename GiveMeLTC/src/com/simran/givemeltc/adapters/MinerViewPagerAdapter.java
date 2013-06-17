package com.simran.givemeltc.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simran.givemeltc.R;
import com.simran.givemeltc.managers.MinerManager;
import com.simran.givemeltc.models.Worker;

public class MinerViewPagerAdapter extends PagerAdapter {

	Context mContext;

	public MinerViewPagerAdapter(Context context) {
		this.mContext = context;

	}

	@Override
	public int getCount() {
		// Log.w("GIVEMELTC", "Size: " +
		// MinerManager.getInstance().miner.workers.size());
		return MinerManager.getInstance().miner.workers.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Worker worker = MinerManager.getInstance().miner.workers.get(position);
		View view = null;

		view = inflater.inflate(R.layout.worker_item, null);
		TextView mName = (TextView) view.findViewById(R.id.tv_worker_name);
		TextView mAlive = (TextView) view.findViewById(R.id.tv_worker_alive);
		TextView mHashRate = (TextView) view.findViewById(R.id.tv_worker_hashrate);
		TextView mTimestamp = (TextView) view.findViewById(R.id.tv_worker_lastshare_timestamp);

		mName.setText(worker.name);

		if (worker.alive == 1) {
			String aliveText = "Alive";
			mAlive.setTextColor(mContext.getResources().getColor(R.color.worker_alive));
			mAlive.setText(aliveText);
		} else {
			String aliveText = "Dead";
			mAlive.setTextColor(mContext.getResources().getColor(R.color.worker_dead));
			mAlive.setText(aliveText);
		}

		mHashRate.setText(worker.hashrate + " kH/s");

		if (worker.last_share_timestamp == 0) {
			mTimestamp.setText("Undefined");
		} else {
			mTimestamp.setText(DateFormat.format("dd/MM/yyyy hh:mm:ssaa", worker.last_share_timestamp * 1000L));

		}

		container.addView(view);
		return view;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public float getPageWidth(int position) {
		return (0.4f);
	}
}
