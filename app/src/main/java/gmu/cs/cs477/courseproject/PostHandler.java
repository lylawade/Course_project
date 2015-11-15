package gmu.cs.cs477.courseproject;


import java.util.ArrayList;

public interface PostHandler {
    public ArrayList<Post> getPosts();
    public void addPost(String post);
}
