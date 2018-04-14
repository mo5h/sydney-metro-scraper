package sydney_metro_rss

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.*;
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Scraper{
    @Bean
    fun init() = CommandLineRunner {};
}
fun main(args: Array<String>) {
    runApplication<Scraper>(*args)
}

