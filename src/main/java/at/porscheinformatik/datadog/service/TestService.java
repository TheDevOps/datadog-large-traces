package at.porscheinformatik.datadog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;

@Service
public class TestService
{
    private static final Logger LOG = LoggerFactory.getLogger(TestService.class);

    // no special OTEL annotation here
    public void receiveMessage(ServiceBusReceivedMessageContext msg)
    {
        LOG.info("Received message: {}", msg.getMessage().getBody());
        doSimpleRestCall();
    }

    public void doSomethingChildSpan()
    {
        LOG.info("do something in child span");
        doSimpleRestCall();
    }

    public void doSomethingNewSpanAnnotation()
    {
        LOG.info("do something in new trace span, or at least should imo but doesn't work");
        doSimpleRestCall();
    }

    private void doSimpleRestCall()
    {
        RestTemplate template = new RestTemplate();
        String response =
            template.getForObject("http://localhost:4444/datadog-large-traces/actuator/health", String.class);
        LOG.info("Response from rest call: {}", response);
    }
}
