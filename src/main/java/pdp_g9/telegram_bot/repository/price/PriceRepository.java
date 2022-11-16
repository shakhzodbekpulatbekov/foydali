package pdp_g9.telegram_bot.repository.price;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pdp_g9.telegram_bot.entity.price.PriceEntity;

@Repository
public interface PriceRepository extends JpaRepository<PriceEntity, Integer> {
}
