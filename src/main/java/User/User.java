package User;

public class User {
    public final String fullName;
    public final int sex;

    public User(String fullName, int sex) {
        this.fullName = fullName;
        this.sex = sex;
    }

    @Override
    public String toString() {
        var label = "";
        if (sex == 1)
            label = "Женщина";
        else if (sex == 2)
            label = "Мужчина";
        return fullName + " : " + label;
    }
}
