package urlshortener.team.message;

public class SuggestRequestMessage {

	private String id;

	public SuggestRequestMessage() {
	}
	
	public SuggestRequestMessage(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
}
