package v1.author;

/**
 * Resource for the API.  This is a presentation class for frontend work.
 */
public class AuthorResource {
    private String id;
    private String link;
    private String name;
    private String blurb;

    public AuthorResource() {
    }

    public AuthorResource(String id, String link, String title, String blurb) {
        this.id = id;
        this.link = link;
        this.name = title;
        this.blurb = blurb;
    }

    public AuthorResource(AuthorData data, String link) {
        this.id = data.id.toString();
        this.link = link;
        this.name = data.name;
        this.blurb = data.blurb;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public String getBlurb() {
        return blurb;
    }

}
