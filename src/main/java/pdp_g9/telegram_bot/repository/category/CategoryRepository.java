package pdp_g9.telegram_bot.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pdp_g9.telegram_bot.entity.category.CategoryDataBase;

public interface CategoryRepository extends JpaRepository<CategoryDataBase, Integer> {
    void deleteById(Integer id);

}
