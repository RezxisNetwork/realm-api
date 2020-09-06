package net.rezxis.mchosting.returns;

import java.util.UUID;

public class VoteRankingReturn {

	public VoteRankingReturn(UUID uuid, int total, int streak) {
		this.uuid = uuid;
		this.total = total;
		this.streak = streak;
	}
	
	public UUID uuid;
	public int total;
	public int streak;
}
