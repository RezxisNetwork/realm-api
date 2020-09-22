package net.rezxis.mchosting.returns;

import java.util.List;
import java.util.Date;
//import java.util.HashMap;

public class StatisticsReturn {

	//public HashMap<Date,String> players;
	//public HashMap<Date,String> servers;
	
	//public StatisticsReturn(HashMap<Date,String> p, HashMap<Date,String> s) {
	//	this.players = p;
	//	this.servers = s;
	//}
	
	public List<Date> playersDate;
	public List<Integer> playersData;
	public List<Date> serversDate;
	public List<Integer> serversData;
	
	public StatisticsReturn(List<Date> pe, List<Integer> pd, List<Date> se, List<Integer> sd) {
		this.playersData = pd;
		this.playersDate = pe;
		this.serversData = sd;
		this.serversDate = se;
	}
}
