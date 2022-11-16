package pdp_g9.telegram_bot.entity.price;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;
import pdp_g9.telegram_bot.entity.base.BaseDataBase;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Configuration
public class PriceEntity extends BaseDataBase {
    private byte[] photoByte;
}
