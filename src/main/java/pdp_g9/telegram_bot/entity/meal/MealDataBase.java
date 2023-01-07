package pdp_g9.telegram_bot.entity.meal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;
import pdp_g9.telegram_bot.entity.base.BaseDataBase;
import pdp_g9.telegram_bot.entity.category.CategoryDataBase;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Configuration
public class MealDataBase extends BaseDataBase {

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    private byte[] photoByte;
    private String photoName;
    private  String videoUrl;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryDataBase categoryDataBase;

}
