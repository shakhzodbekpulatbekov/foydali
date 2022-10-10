package pdp_g9.telegram_bot.repository.meal;

import org.springframework.data.jpa.repository.JpaRepository;
import pdp_g9.telegram_bot.entity.meal.MealDataBase;

import java.util.List;

public interface MealRepository extends JpaRepository<MealDataBase, Integer> {
    List<MealDataBase> findByCategoryDataBase_Id (Integer categoryId);
}
