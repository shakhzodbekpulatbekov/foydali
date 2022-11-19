package pdp_g9.telegram_bot.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import pdp_g9.telegram_bot.entity.location.LocationEntity;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity,Integer> {
    List<LocationEntity> findByAddress(String address);

    void deleteAllByAddress(String address);
}
