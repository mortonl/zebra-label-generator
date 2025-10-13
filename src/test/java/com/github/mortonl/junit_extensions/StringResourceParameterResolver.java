package com.github.mortonl.junit_extensions;


import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public class StringResourceParameterResolver implements ParameterResolver
{

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
    {
        return parameterContext.isAnnotated(StringFileResource.class) &&
            parameterContext.getParameter()
                            .getType()
                            .equals(String.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
    {
        String              location    = parameterContext.findAnnotation(StringFileResource.class)
                                                          .get()
                                                          .value();
        java.io.InputStream inputStream = getClass().getClassLoader()
                                                    .getResourceAsStream(location);

        if (inputStream == null) {
            throw new RuntimeException("Resource not found: " + location);
        }

        try {
            return new String(inputStream.readAllBytes());
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (java.io.IOException e) {
                // Ignore close exception
            }
        }
    }
}
