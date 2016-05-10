package webApps;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.File;


@SpringBootApplication
public class Application implements CommandLineRunner{
    public static String ROOT = "upload-dir";

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

    @Override
    public void run(String... strings) throws Exception {
        //create folder for uploaded files
        new File(ROOT).mkdir();
    }
}
