// TODO Cleanup and Commenting here

package model;

import java.util.ArrayList;

public class Outfit {

    private final int id;
    private String nickname;
    private final ArrayList<Integer> apparelIDList;

    public Outfit(int id, String nickname, ArrayList<Integer> apparelMembers) {
        this.id = id;
        this.nickname = nickname;
        this.apparelIDList = apparelMembers;
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

    public ArrayList<Integer> getApparelIDList(){
        return apparelIDList;
    }
    public void setApparelIDList(int[] newIDList) {
        apparelIDList.clear();
        for (int i : newIDList) {
            apparelIDList.add(i);
        }
    }

    public boolean apparelExistsInOutfit (int apparelID) {
        for (Integer i : apparelIDList) {
            if (i == apparelID) {
                return true;
            }
        }

        return false;
    }

}
