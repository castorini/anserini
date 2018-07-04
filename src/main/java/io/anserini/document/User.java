package io.anserini.document;

public class User {
  protected String screen_name;
  protected String name;
  protected String profile_image_url;
  protected long id;
  protected int followers_count;
  protected int friends_count;
  protected int statuses_count;

  public String getScreen_name() {
    return screen_name;
  }

  public String getName() {
    return name;
  }

  public String getProfile_image_url() {
    return profile_image_url;
  }

  public long getId() {
    return id;
  }

  public int getFollowers_count() {
    return followers_count;
  }

  public int getFriends_count() {
    return friends_count;
  }

  public int getStatuses_count() {
    return statuses_count;
  }
}