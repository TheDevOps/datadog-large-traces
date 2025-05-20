package at.porscheinformatik.datadog.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;

import at.porscheinformatik.datadog.service.TestService;

@Configuration
@EnableScheduling
@EnableAspectJAutoProxy
@ComponentScan(basePackageClasses = TestService.class)
public class TestConfiguration
{
    private static final Logger LOG = LoggerFactory.getLogger(TestConfiguration.class);

    @Autowired
    private TestService testService;

    @Scheduled(initialDelay = 30000)
    public void messagingTraceTest()
    {
        LOG.info("Messaging trace test started");
        ServiceBusClientBuilder serviceBusClientBuilder =
            new ServiceBusClientBuilder().connectionString(
                "Endpoint=sb://our-company.servicebus.windows.net/;SharedAccessKeyName=READ_WRITE;SharedAccessKey=thekey;EntityPath=test-topic");
        final ServiceBusClientBuilder.ServiceBusProcessorClientBuilder builder =
            serviceBusClientBuilder
                .retryOptions(new AmqpRetryOptions().setTryTimeout(Duration.of(5, ChronoUnit.MINUTES)))
                .processor()
                .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
                .topicName("test-topic")
                .subscriptionName("SUBSCRIPTION")
                .prefetchCount(0)
                .maxConcurrentCalls(5)
                .maxAutoLockRenewDuration(Duration.ofMinutes(5))
                .processMessage(this::receiveMessage)
                .processError(
                    errorContext -> LOG.error("Error occurred in message processing", errorContext.getException()));
        builder.buildProcessorClient().start();
    }

    private void receiveMessage(ServiceBusReceivedMessageContext msg)
    {
        testService.receiveMessage(msg);
        testService.doSomethingChildSpan();
        testService.doSomethingNewSpanAnnotation();
    }
}
