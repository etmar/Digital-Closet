package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Controller handles all manipulation of persistent data within the application,
 * including the creation, editing, and deletion of data with the attached SQLite Database
 */
public class Controller {

    public static Profile selectedProfile;

    //PROFILE METHODS

    /**
      * Returns an ArrayList containing any existing Profiles in the Closet Database.
      * @return the ArrayList of all existing Profiles in the SQL Database
     */
    public static ArrayList<Profile> getAllProfiles() {
        String sql = "SELECT profile_nickname FROM Profile";
        ArrayList<Profile> profileNicknames = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {
                profileNicknames.add(new Profile(rs.getString("profile_nickname")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return profileNicknames;
    }

    /**
     * Writes a profile to the Closet Database using the nickname provided by the user.
     * @param profileNickname the nickname of the profile to be written to the database
     */
    public static void addProfile(String profileNickname) {
        String sql = "INSERT INTO Profile(profile_nickname) VALUES(?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profileNickname);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Edits an existing profile in the Closet Database, changing its nickname.
     * @param oldNickname the previous nickname of the Profile being edited
     * @param newNickname the new nickname of the Profile being edited
     */
    public static void editProfile(String oldNickname, String newNickname) {
        String sql = "UPDATE Profile SET profile_nickname = ? WHERE profile_nickname = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newNickname);
            pstmt.setString(2, oldNickname);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Removes an existing profile from the Closet Database, also deleting any Apparel or Outfit items
     * associated with the Profile being removed.
     * @param profileNickname the nickname of the profile being removed
     */
    public static void deleteProfile(String profileNickname) {


        for (Apparel a : getAllApparelByProfile(profileNickname).values()) {
            deleteApparel(a.getId(), a.getNickname());
        }

        String sql = "DELETE FROM Profile WHERE profile_nickname = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profileNickname);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    //APPAREL METHODS

    /**
     * Returns a HashMap of all Apparel items associated with the given Profile nickname.
     * @param profileNickname the nickname of the Profile
     * @return a HashMap of Apparel items
     */
    public static HashMap<Integer, Apparel> getAllApparelByProfile(String profileNickname) {
        String sql = "SELECT apparel_id, apparel_nickname, body_part FROM Apparel WHERE associated_profile = ?";
        HashMap<Integer, Apparel> apparelList = new HashMap<>();
        try (Connection conn = connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){
            pstmt.setString(1, profileNickname);
            ResultSet rs = pstmt.executeQuery();
            int currentApparelID;
            while (rs.next()) {
                currentApparelID = rs.getInt("apparel_id");
                apparelList.put(currentApparelID, new Apparel(currentApparelID,
                        rs.getString("apparel_nickname"),
                        rs.getString("body_part")));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return apparelList;
    }

    /**
     * Writes a new Apparel item to the Closet Database while passing the Profile it's associated with,
     * the Apparel's nickname, the body part associated with the clothing, and saves a user provided image
     * to a directory for later viewing.
     * @param associatedProfile the Profile to which the Apparel item is associated
     * @param apparelNickname the nickname of the Apparel item
     * @param bodyPart the part of the body on which the Apparel would be worn
     * @param apparelImage the image of the Apparel item
     */
    public static void addApparel(String associatedProfile, String apparelNickname, String bodyPart, File apparelImage) {
        int largestID = 0;

        String sql = "INSERT INTO Apparel(associated_profile, apparel_nickname, body_part) VALUES(?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, associatedProfile);
            pstmt.setString(2, apparelNickname);
            pstmt.setString(3, bodyPart);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        sql = "SELECT MAX(apparel_id) AS LatestID FROM Apparel";
        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            largestID = rs.getInt("LatestID");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
            if (Files.probeContentType(Paths.get(apparelImage.getAbsolutePath())).equals("image/jpeg")) {

                Files.copy(Paths.get(apparelImage.getAbsolutePath()), Paths.get("./images/" + largestID + apparelNickname + ".jpg"), StandardCopyOption.REPLACE_EXISTING);

                // Image Resizing and Compression
                File newImageFile = new File("./images/" + largestID + apparelNickname + ".jpg");

                BufferedImage image = ImageIO.read(newImageFile);
                Image resultingImage = image.getScaledInstance(300, 300, java.awt.Image.SCALE_SMOOTH);
                BufferedImage outputImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
                outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

                ImageIO.write(outputImage, "jpg", newImageFile);

                image.flush();
                resultingImage.flush();
                outputImage.flush();


                Files.copy(Paths.get(newImageFile.getAbsolutePath()), Paths.get("./images/" + largestID + apparelNickname + ".jpg"), StandardCopyOption.REPLACE_EXISTING);

            }
            else {

                Files.copy(Paths.get(apparelImage.getAbsolutePath()), Paths.get("./images/" + largestID + apparelNickname + ".jpg"), StandardCopyOption.REPLACE_EXISTING);

                // Image Resizing and Compression
                File newImageFile = new File("./images/" + largestID + apparelNickname + ".png");

                BufferedImage image = ImageIO.read(newImageFile);
                Image resultingImage = image.getScaledInstance(300, 300, java.awt.Image.SCALE_SMOOTH);
                BufferedImage outputImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
                outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

                ImageIO.write(outputImage, "png", newImageFile);

                image.flush();
                resultingImage.flush();
                outputImage.flush();

                Files.copy(Paths.get(newImageFile.getAbsolutePath()), Paths.get("./images/" + largestID + apparelNickname + ".png"), StandardCopyOption.REPLACE_EXISTING);

            }

        } catch (IOException ignored) {}
    }

    /**
     * Edits an existing Apparel item's nickname in the Closet Database, changing its nickname and the name
     * of any images associated with the Apparel item.
     * @param apparelID the ID of the Apparel item being edited
     * @param oldNickname the previous nickname of the Apparel item being edited
     * @param newNickname the new nickname of the Apparel item being edited
     */
    public static void editApparelNickname(int apparelID, String oldNickname, String newNickname) {
        try {
            if (Files.exists(Path.of("./images/" + apparelID + oldNickname + ".jpg"))) {
                Files.copy(Path.of("./images/" + apparelID + oldNickname + ".jpg"), Paths.get("./images/" + apparelID + newNickname + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
                Files.deleteIfExists(Path.of("./images/" + apparelID + oldNickname + ".jpg"));
            } else {
                Files.copy(Path.of("./images/" + apparelID + oldNickname + ".png"), Paths.get("./images/" + apparelID + newNickname + ".png"), StandardCopyOption.REPLACE_EXISTING);
                Files.deleteIfExists(Path.of("./images/" + apparelID + oldNickname + ".png"));
            }
        } catch (IOException  ignored) {}

        String sql = "UPDATE Apparel SET apparel_nickname = ? WHERE apparel_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newNickname);
            pstmt.setInt(2, apparelID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Edits an existing Apparel item's assigned body part in the Closet Database.
     * @param apparelID the ID of the Apparel item being edited
     * @param newBodyPart the new body part of the Apparel item being edited
     */
    public static void editApparelBodyPart(int apparelID, String newBodyPart) {
        String sql = "UPDATE Apparel SET body_part = ? WHERE apparel_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newBodyPart);
            pstmt.setInt(2, apparelID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Edits an existing Apparel item's associated image.
     * @param apparelID the ID of the Apparel item being edited
     * @param apparelNickname the nickname of the Apparel item being edited
     * @param newImage the replacement image for the Apparel item
     */
    public static void editApparelImage(int apparelID, String apparelNickname, File newImage) {
        if (newImage != null) {
            try {
                Files.deleteIfExists(Paths.get("./images/" + apparelID + apparelNickname + ".jpg"));
                Files.deleteIfExists(Paths.get("./images/" + apparelID + apparelNickname + ".png"));

                if (Files.probeContentType(Paths.get(newImage.getAbsolutePath())).equals("image/jpeg"))
                    Files.copy(Paths.get(newImage.getAbsolutePath()), Paths.get("./images/" + apparelID + apparelNickname + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
                else
                    Files.copy(Paths.get(newImage.getAbsolutePath()), Paths.get("./images/" + apparelID + apparelNickname + ".png"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {}
        }
    }

    /**
     * Removes an existing profile from the Closet Database.
     * @param apparelID the ID of the Apparel item being deleted
     */
    public static void deleteApparel(int apparelID, String apparelNickname) {
        String sql = "DELETE FROM Apparel WHERE apparel_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, apparelID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try{
            Files.deleteIfExists(Paths.get("./images/" + apparelID + apparelNickname + ".jpg"));
            Files.deleteIfExists(Paths.get("./images/" + apparelID + apparelNickname + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    //OUTFIT METHODS

    /**
     * Returns an ArrayList of all Outfits associated with the given Profile nickname.
     * @param profileNickname the nickname of the Profile
     * @return a HashMap of Apparel items
     */
    public static ArrayList<Outfit> getAllOutfitByProfile(String profileNickname) {
        String sql = "SELECT * FROM Outfit WHERE associated_profile = ?";
        ArrayList<Outfit> outfitList = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){
            pstmt.setString(1, profileNickname);
            ResultSet rs = pstmt.executeQuery();
            ArrayList<Integer> currentOutfitApparelIDList = new ArrayList<>();
            while (rs.next()) {

                currentOutfitApparelIDList.add(rs.getInt("outer_top_apparel_id"));
                currentOutfitApparelIDList.add(rs.getInt("inner_top_apparel_id"));
                currentOutfitApparelIDList.add(rs.getInt("bottom_apparel_id"));
                currentOutfitApparelIDList.add(rs.getInt("footwear_apparel_id"));
                currentOutfitApparelIDList.add(rs.getInt("accessory_apparel_id_1"));
                currentOutfitApparelIDList.add(rs.getInt("accessory_apparel_id_2"));
                currentOutfitApparelIDList.add(rs.getInt("accessory_apparel_id_3"));
                currentOutfitApparelIDList.add(rs.getInt("accessory_apparel_id_4"));
                currentOutfitApparelIDList.add(rs.getInt("accessory_apparel_id_5"));

                outfitList.add(new Outfit(rs.getInt("outfit_id"),
                        rs.getString("outfit_nickname"),
                        new ArrayList<>(currentOutfitApparelIDList)));

                currentOutfitApparelIDList.clear();

            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return outfitList;
    }

    /**
     * Writes an empty Outfit to the Closet Database.
     * @param outfitNickname the nickname of the Outfit
     * @param associatedProfile the Profile to which the Outfit is associated
     */
    public static void createOutfit(String outfitNickname, String associatedProfile) {
        String sql = "INSERT INTO Outfit(outfit_nickname, associated_profile) VALUES(?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, outfitNickname);
            pstmt.setString(2, associatedProfile);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Edits the Nickname of an Outfit in the Closet Database.
     * @param outfitID the ID of the Outfit being edited
     * @param newNickname the new Nickname of the Outfit being edited
     */
    public static void editOutfitNickname(int outfitID, String newNickname) {
        String sql = "UPDATE Outfit SET outfit_nickname = ? WHERE outfit_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newNickname);
            pstmt.setInt(2, outfitID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Edits a slot determined by <code>slotToChange</code> within an existing Outfit in the Closet Database.
     * @param outfitID the ID of the Outfit being edited
     * @param slotToChange the slot to be changed within the Outfit
     * @param associatedApparelID the ID of the Apparel item to be assigned to the Outfit slot
     */
    public static void editOutfitSlot(int outfitID, String slotToChange, int associatedApparelID) {
        String sql = "UPDATE Outfit SET "+slotToChange+" = ? WHERE outfit_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, associatedApparelID);
            pstmt.setInt(2, outfitID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sets the Outfit slot indicated by <code>slotToEmpty</code> to NULL
     * @param outfitID the ID of the Outfit being edited
     * @param slotToEmpty the slot to be emptied within the Outfit
     */
    public static void removeSlotFromOutfit(int outfitID, String slotToEmpty) {
        String sql = "UPDATE Outfit SET " + slotToEmpty + " = NULL WHERE outfit_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, outfitID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Removes an existing Outfit from the Closet Database.
     * @param outfitID the ID of the Outfit being deleted
     */
    public static void deleteOutfit(int outfitID) {
        String sql = "DELETE FROM Outfit WHERE outfit_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, outfitID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Serves as the global connection logic for every SQL method
     *
     * @return the connection information including the path of the database being used by the application
     */
    private static Connection connect() {

        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite::resource:Closet Database.db");
            conn.prepareStatement("PRAGMA foreign_keys=ON");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}