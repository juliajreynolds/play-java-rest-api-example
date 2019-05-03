package v1.post;

import v1.author.AuthorData;

import javax.persistence.*;

/**
 * Data returned from the database
 */
@Entity
@Table(name = "posts")
public class PostData {

    public PostData() {
    }

    public PostData(String title, String body) {
        this.title = title;
        this.body = body;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String title;
    public String body;

    @ManyToOne
    public AuthorData authorData;
}
