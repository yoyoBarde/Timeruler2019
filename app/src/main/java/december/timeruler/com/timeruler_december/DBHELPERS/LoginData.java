package december.timeruler.com.timeruler_december.DBHELPERS;


public class LoginData {

    public String userName=" ";
    public String userPass=" ";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public LoginData(String userName, String userPass) {
        this.userName = userName;
        this.userPass = userPass;
    }
}

