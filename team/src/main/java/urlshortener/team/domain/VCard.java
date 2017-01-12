package urlshortener.team.domain;

public class VCard {

    private String name;
    private String surname;
    private String organization;
    private String telephone;
    private String email;
    private String url;

    public VCard() {
    }

    public VCard(String name, String surname, String organization, String telephone, String email, String url) {
        this.name = name;
        this.surname = surname;
        this.organization = organization;
        this.telephone = telephone;
        this.email = email;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlEncodedParameters() {
        String res = "";
        res += "vcardname=" + getName();
        res += (getSurname() != null ? "&vcardsurname=" + getSurname() : "");
        res += (getOrganization() != null ? "&vcardorganization=" + getOrganization() : "");
        res += (getTelephone() != null ? "&vcardtelephone=" + getTelephone() : "");
        res += (getEmail() != null ? "&vcardemail=" + getEmail() : "");
        return res;
    }
}
