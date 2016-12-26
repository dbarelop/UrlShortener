package urlshortener.team.message;

public class MetricsRequestMessage {
    private String hash;
    private String startDate;
    private String endDate;

    public MetricsRequestMessage() {
    }

    public MetricsRequestMessage(String hash, String startDate, String endDate) {
        this.hash = hash;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
