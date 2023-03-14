package study.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRepositoryTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired ItemRepository itemRepository;

    @Test
    public void save(){
        Item item = new Item();
        itemRepository.save(item);
    }
}