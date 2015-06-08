package info.todowonders.utils;

/**
 * Created by ptanwar on 08/06/15.
 */
public class MyProfile
{
    public static String name;
    public static String profileImg;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        MyProfile.name = name;
    }

    public static String getProfileImg() {
        return profileImg;
    }

    public static void setProfileImg(String profileImg) {
        MyProfile.profileImg = profileImg;
    }
}
