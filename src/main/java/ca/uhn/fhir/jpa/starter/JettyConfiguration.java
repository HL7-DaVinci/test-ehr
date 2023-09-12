package ca.uhn.fhir.jpa.starter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.BufferOverflowException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.ByteBufferOutputStream;
import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.util.StringUtil;


import org.eclipse.jetty.server.Server;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JettyConfiguration
            implements WebServerFactoryCustomizer<JettyServletWebServerFactory> {

    @Override
    public void customize(JettyServletWebServerFactory factory) {
        JettyServerCustomizer customizer = new JettyServerCustomizer() {
            @Override
            public void customize(Server server) {
                server.setErrorHandler(new CustomJettyErrorHandler());
            }
        };
        factory.addServerCustomizers(customizer);
    }


    public class CustomJettyErrorHandler extends ErrorHandler {

        @Override
        public void handle(String target, Request baseRequest,
                           HttpServletRequest request,
                           HttpServletResponse response)
                throws IOException, ServletException {


            try {
                // Get error message, sanitize it, just in case.
                String message = StringUtil.sanitizeXmlString(
                        (String) request.getAttribute(Dispatcher.ERROR_MESSAGE)
                );

                // Get error code that will returned
                int code = response.getStatus();

                var charset = StandardCharsets.UTF_8;

                // Get writer used
                var buffer = baseRequest.getResponse().getHttpOutput().getBuffer();
                var out = new ByteBufferOutputStream(buffer);
                var writer = new PrintWriter(new OutputStreamWriter(out, charset));

                // Set content type, encoding and write response
                response.setContentType(MimeTypes.Type.TEXT_PLAIN.asString());
                response.setCharacterEncoding(charset.name());
                writer.print("HTTP ERROR ");
                writer.print(code);
                writer.print("\nMessage: ");
                writer.print(message);
                writer.print("\nURI: ");
                writer.print(request.getRequestURI());


                writer.flush();
            } catch (BufferOverflowException e) {
                baseRequest.getResponse().resetContent();
            }

            baseRequest.getHttpChannel().sendResponseAndComplete();
        }
    }
}