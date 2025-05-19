package at.porscheinformatik.datadog.service;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;

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

    // basic @WithSpan OTEL annotation here without any special configuration
    @WithSpan
    public void doSomethingChildSpan()
    {
        LOG.info("do something in child span");
        doSimpleRestCall();
    }

    // @WithSpan annotation here but with inheritContext = false to hopefully start a new trace, but doesn't work
    @WithSpan(inheritContext = false)
    public void doSomethingNewSpanAnnotation()
    {
        LOG.info("do something in new trace span, or at least should imo but doesn't work");
        doSimpleRestCall();
    }

    // big code side span creation using OTEL to start a new trace that actually works but is annoying to use
    public void doSomethingNewSpanCode()
    {
        try
        {
            tryDoInNewTrace(() ->
            {
                LOG.info("do something in new trace span");
                doSimpleRestCall();
            },
                "TestServiceImpl.doSomethingNewSpanCode");
        }
        catch (Exception e)
        {
            LOG.error("Error while executing in new trace", e);
        }
    }

    private void doSimpleRestCall()
    {
        RestTemplate template = new RestTemplate();
        String response =
            template.getForObject("http://localhost:4444/datadog-large-traces/status/health", String.class);
        LOG.info("Response from rest call: {}", response);
    }

    private static void tryDoInNewTrace(Runnable runnable, String spanName) throws Exception
    {
        tryDoInNewTrace(() ->
        {
            runnable.run();
            return null;
        }, spanName);
    }

    private static <V> V tryDoInNewTrace(Callable<V> callable, String spanName) throws Exception
    {
        // some sanity checks if tracer setup is actually done
        TracerProvider tracerProvider = GlobalOpenTelemetry.getTracerProvider();
        if (tracerProvider == null)
        {
            return callable.call();
        }
        Tracer tracer = tracerProvider.get("datadog"); // seems like this can be pretty much anything? weird
        if (tracer == null)
        {
            return callable.call();
        }
        SpanBuilder spanBuilder = tracer.spanBuilder(spanName);
        spanBuilder.setNoParent(); // important to start a new trace
        Span span = spanBuilder.startSpan();
        try (Scope scope = span.makeCurrent())
        {
            return callable.call();
        }
        catch (Exception e)
        {
            span.recordException(e);
            throw e;
        }
        finally
        {
            span.end();
        }
    }
}
