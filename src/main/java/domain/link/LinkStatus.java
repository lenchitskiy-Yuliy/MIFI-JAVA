package domain.link;

public enum LinkStatus {
  ACTIVE("активна"),
  EXHAUSTED("лимит переходов исчерпан"),
  EXPIRED("срок жизни истёк");

  private final String title;

  LinkStatus(String title) {
    this.title = title;
  }

  public String title() {
    return title;
  }
}
