package net.blueshell.api.security.base;

import net.blueshell.api.base.AuthorizationBase;
import net.blueshell.api.base.BaseModelService;
import org.springframework.core.GenericTypeResolver;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class BasePermissionEvaluator<T, ID, S extends BaseModelService<T, ID, ?>> extends AuthorizationBase {

    protected final S service;
    protected final Map<String, BiFunction<T, Authentication, Boolean>> permissionsMap = new HashMap<>() {
    };
    public final Class<T> domainType;

    public BasePermissionEvaluator(S service) {
        this.service = service;
        this.domainType = determineDomainType();
    }

    private Class<T> determineDomainType() {
        Class<?>[] resolvedTypes = GenericTypeResolver.resolveTypeArguments(getClass(), BasePermissionEvaluator.class);
        if (resolvedTypes == null || resolvedTypes.length < 1) {
            throw new IllegalStateException("Unable to determine domain type for " + getClass().getName());
        }
        @SuppressWarnings("unchecked")
        Class<T> castedType = (Class<T>) resolvedTypes[0];
        return castedType;
    }

    boolean supports(Class<?> domainClass) {
        return domainType.isAssignableFrom(domainClass);
    }

    public abstract boolean hasPermission(Authentication authentication, Object targetDomainObject, String string);
    public abstract boolean hasPermissionId(Authentication authentication, Object targetId, String string);
}
