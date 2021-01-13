package study.datajpa.repository;

/**
 * Created by morrie kim on 2020/12/18.
 */
public interface NestedClosedProjections {
    String getUserName();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }
}
