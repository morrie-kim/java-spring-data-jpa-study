package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Item;

/**
 * Created by morrie kim on 2020/12/15.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
}
