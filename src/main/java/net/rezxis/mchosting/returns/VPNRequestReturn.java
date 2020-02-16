package net.rezxis.mchosting.returns;

import lombok.Getter;

@Getter
public class VPNRequestReturn {

	private int code;
	private String message;
	
	public VPNRequestReturn(int c, String m) {
		this.code = c;
		this.message = m;
	}
}
