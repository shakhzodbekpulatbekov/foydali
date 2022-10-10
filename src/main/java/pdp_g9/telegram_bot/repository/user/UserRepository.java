package pdp_g9.telegram_bot.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pdp_g9.telegram_bot.entity.user.UserDataBase;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDataBase, Integer> {
//    List<UserDataBase> findByChadId(Long chatId);
    Optional<UserDataBase> findByChadId(Long chatId);

}
