package v1.author;

import javax.persistence.*;

/**
 * Data returned from the database
 */
@Entity
@Table(name = "authors")
public class AuthorData {

    public AuthorData() {
    }

    public AuthorData(String name, String blurb) {
        this.name = name;
        this.blurb = blurb;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String name;
    public String blurb;
}
