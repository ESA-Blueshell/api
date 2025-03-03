package net.blueshell.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import net.blueshell.api.model.*;
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
        System.out.println("find evaluator for " + domainClass);
        return evaluators.stream()
                .filter(evaluator -> evaluator.supports(domainClass))
                .findFirst();
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject == null || permission == null) return false;
        Class<?> targetClass = ClassUtils.getUserClass(targetDomainObject.getClass());
        return findEvaluator(targetClass)
                .map(evaluator -> evaluator.hasPermission(authentication, targetDomainObject, permission.toString()))
                .orElse(false);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (targetId == null || targetType == null || permission == null) return false;
        try {
            String fullClassName = "net.blueshell.api.model." + targetType;
            Class<?> domainClass = Class.forName(fullClassName);
            return findEvaluator(domainClass)
                    .map(evaluator -> evaluator.hasPermissionId(authentication, targetId, permission.toString()))
                    .orElse(false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}

