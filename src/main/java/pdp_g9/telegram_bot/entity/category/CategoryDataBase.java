package pdp_g9.telegram_bot.entity.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pdp_g9.telegram_bot.entity.base.BaseDataBase;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class CategoryDataBase extends BaseDataBase {
    @JoinColumn(
            name = "category_id"
    )
    private int parentId;


}
