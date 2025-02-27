package net.blueshell.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Component
public class CompositePermissionEvaluator implements PermissionEvaluator {

    private final List<BasePermissionEvaluator<?, ?, ?>> evaluators;

    @Autowired
    public CompositePermissionEvaluator(List<BasePermissionEvaluator<?, ?, ?>> evaluators) {
        this.evaluators = evaluators;
    }

    private Optional<BasePermissionEvaluator<?, ?, ?>> findEvaluator(Class<?> domainClass) {
        return evaluators.stream()
                .filter(evaluator -> evaluator.supports(domainClass))
                .findFirst();
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject == null || permission == null) {
            return false;
        }

        return findEvaluator(targetDomainObject.getClass())
                .map(evaluator -> evaluator.hasPermission(authentication, targetDomainObject, permission))
                .orElse(false);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (targetId == null || targetType == null || permission == null) {
            return false;
        }

        try {
            Class<?> domainClass = Class.forName(targetType);
            return findEvaluator(domainClass)
                    .map(evaluator -> evaluator.hasPermission(authentication, targetId, permission))
                    .orElse(false);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

