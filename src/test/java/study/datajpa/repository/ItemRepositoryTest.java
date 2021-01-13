package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

/**
 * Created by morrie kim on 2020/12/15.
 */
@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

//    @Test
//    public void save(){
//        Item item = new Item();
//        itemRepository.save(item);
//    }

    @Test
    public void save(){
        Item item = new Item("A");
        itemRepository.save(item);
    }
}