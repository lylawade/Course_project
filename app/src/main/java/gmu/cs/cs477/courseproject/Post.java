package gmu.cs.cs477.courseproject;

import java.util.Date;

public class Post {
    private long post_ID;
    private String text;
    private Date timestamp;

    public Post(long post_ID, String text, Date timestamp) {
        this.post_ID = post_ID;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public long getPost_ID() {
        return post_ID;
    }

    public void setPost_ID(long post_ID) {
        this.post_ID = post_ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
