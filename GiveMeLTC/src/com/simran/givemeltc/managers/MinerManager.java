package com.simran.givemeltc.managers;

import java.util.ArrayList;
import java.util.List;

import com.simran.givemeltc.models.Miner;
import com.simran.givemeltc.models.Worker;


public class MinerManager {
	private static MinerManager _instance;

	public Miner miner = new Miner();

	public static MinerManager getInstance() {
		if (_instance == null)
			_instance = new MinerManager();

		return _instance;
	}
	
	public void setMiner(Miner miner) {
		this.miner = miner;
	}

	public Miner getMiner() {
		return miner;
	}
	
	public List<Worker> getWorkers()
	{
		ArrayList<Worker> result = new ArrayList<Worker>();
		
		for (Worker worker : miner.workers)
		{
			result.add(worker);
		}
		
		return result;
	}
	
}
