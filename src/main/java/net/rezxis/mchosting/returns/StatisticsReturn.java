package net.rezxis.mchosting.returns;

import java.util.Date;
import java.util.HashMap;

public class StatisticsReturn {

	public HashMap<Date,Integer> players;
	public HashMap<Date,Integer> servers;
	
	public StatisticsReturn(HashMap<Date,Integer> p, HashMap<Date,Integer> s) {
		this.players = p;
		this.servers = s;
	}
}
