package model;

public class Apparel {

    private final int id;
    private String nickname;
    private String bodyPart;

    public Apparel(int id, String nickname, String bodyPart) {
        this.id = id;
        this.nickname = nickname;
        this.bodyPart = bodyPart;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBodyPart() {
        return bodyPart;
    }
    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

}
