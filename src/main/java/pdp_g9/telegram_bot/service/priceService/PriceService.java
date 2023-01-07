package pdp_g9.telegram_bot.service.priceService;

import org.springframework.stereotype.Service;
import pdp_g9.telegram_bot.entity.price.PriceEntity;
import pdp_g9.telegram_bot.repository.price.PriceRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
@Service
public class PriceService {
    final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public void WriteToFile(){
        List<PriceEntity> all = priceRepository.findAll();
        byte[] demBytes = all.get(0).getPhotoByte();

        File outputFile = new File("root/lorettouz/files/price.jpg");

        FileOutputStream outputStream;

        {
            try {
                outputStream = new FileOutputStream(outputFile);
                outputStream.write(demBytes);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
