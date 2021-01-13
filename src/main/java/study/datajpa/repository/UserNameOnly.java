package study.datajpa.repository;

/**
 * Created by morrie kim on 2020/12/18.
 */

public interface UserNameOnly {
    //@Value("#{target.userName + ' ' + target.age}") // open projections
    String getUserName();
}
