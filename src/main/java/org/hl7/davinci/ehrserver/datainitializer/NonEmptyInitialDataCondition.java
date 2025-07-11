package org.hl7.davinci.ehrserver.datainitializer;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class NonEmptyInitialDataCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata metadata) {
      DataInitializerProperties properties = Binder.get(conditionContext.getEnvironment())
          .bind("", DataInitializerProperties.class)
          .orElse(new DataInitializerProperties());

      return properties != null && properties.getInitialData() != null && !properties.getInitialData().isEmpty();
    }
}