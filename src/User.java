// Represents a User entity
public class User {
    private int userID;
    private int age;
    private String gender;
    private String name;

    // Constructor
    public User(int userID, int age, String gender, String name) {
        this.userID = userID;
        this.age = age;
        this.gender = gender;
        this.name = name;
    }

    // Getters (add setters if needed later)
    public int getUserID() {
        return userID;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User [userID=" + userID + ", age=" + age + ", gender=" + gender + ", name=" + name + "]";
    }
}