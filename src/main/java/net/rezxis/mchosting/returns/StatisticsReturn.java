package net.rezxis.mchosting.returns;

import java.util.Date;

import net.rezxis.mchosting.StatisticsLogging.ProcessedData;

public class StatisticsReturn {

	public ProcessedData players;
	public ProcessedData servers;
	
	public StatisticsReturn(ProcessedData p, ProcessedData s) {
		this.players = p;
		this.servers = s;
	}
}
