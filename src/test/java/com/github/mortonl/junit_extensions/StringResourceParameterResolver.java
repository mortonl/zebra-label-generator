package com.github.mortonl.junit_extensions;


import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.IOException;
import java.io.InputStream;

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
        String location = parameterContext.findAnnotation(StringFileResource.class)
                                          .get()
                                          .value();
        InputStream inputStream = getClass()
            .getClassLoader()
            .getResourceAsStream(location);

        if (inputStream == null) {
            throw new RuntimeException("Resource not found: " + location);
        }

        try {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Ignore close exception
            }
        }
    }
}
