package at.porscheinformatik.datadog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

import at.porscheinformatik.datadog.config.TestConfiguration;

@EnableAutoConfiguration
@Import({
    TestConfiguration.class
})
public class LargeTraceApplication
{
    /**
     * The main method
     *
     * @param args .
     * @throws Exception .
     */
    public static void main(final String[] args)
    {
        SpringApplication.run(LargeTraceApplication.class, args);
    }
}
