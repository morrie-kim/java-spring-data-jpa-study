package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

/**
 * Created by morrie kim on 2020/12/09.
 */
public interface TeamRepository extends JpaRepository<Team, Long> {
}
