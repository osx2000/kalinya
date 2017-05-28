package com.kalinya.optimization.examples;

import com.github.rcaller.rstuff.ROutputParser;
import com.kalinya.util.ToStringBuilder;

public class RDetails {
	private String version;
	private String nickname;
	private String platform;
	private String os;
	private String system;
	
	public RDetails(ROutputParser parser) {
		version = parser.getAsStringArray("rVersion")[0];
		nickname = parser.getAsStringArray("rNickname")[0];
	}

	public String toString() {
		return new ToStringBuilder(this)
				.append("Version", version)
				.append("Nickname", nickname)
				.build();
	}
}
