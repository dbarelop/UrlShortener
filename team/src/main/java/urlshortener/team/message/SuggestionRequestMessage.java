package urlshortener.team.message;

public class SuggestionRequestMessage {

	private String shortName;

	public SuggestionRequestMessage() {
	}
	
	public SuggestionRequestMessage(String shortName) {
		this.shortName = shortName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	
	
}
