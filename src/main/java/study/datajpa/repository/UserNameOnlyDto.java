package study.datajpa.repository;

/**
 * Created by morrie kim on 2020/12/18.
 */
public class UserNameOnlyDto {
    private final String userName;

    public UserNameOnlyDto(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
