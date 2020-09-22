package net.rezxis.mchosting.returns;

import java.util.Date;
import java.util.HashMap;

public class StatisticsReturn {

	public HashMap<Date,String> players;
	public HashMap<Date,String> servers;
	
	public StatisticsReturn(HashMap<Date,String> p, HashMap<Date,String> s) {
		this.players = p;
		this.servers = s;
	}
}
