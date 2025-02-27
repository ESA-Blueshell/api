package net.blueshell.api.security;

import net.blueshell.api.base.AuthorizationBase;
import net.blueshell.api.base.BaseModelService;
import net.blueshell.api.base.BaseRepository;
import org.springframework.core.GenericTypeResolver;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class BasePermissionEvaluator<T, ID, S extends BaseModelService<T, ID, ?>> extends AuthorizationBase {

    protected final S service;
    protected final Map<String, BiFunction<T, Authentication, Boolean>> permissionsMap = new HashMap<>() {
    };
    private final Class<T> domainType;

    public BasePermissionEvaluator(S service) {
        this.service = service;
        this.domainType = determineDomainType();
        registerPermissions();
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

    abstract boolean canSee(T t, Authentication authentication);

    abstract boolean canEdit(T t, Authentication authentication);

    abstract boolean canDelete(T t, Authentication authentication);

    protected void registerPermissions() {
        permissionsMap.put("see", this::canSee);
        permissionsMap.put("edit", this::canEdit);
        permissionsMap.put("delete", this::canDelete);
    }

    boolean supports(Class<?> domainClass) {
        return domainType.isAssignableFrom(domainClass);
    }

    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || permission == null) {
            return false;
        }
        try {
            @SuppressWarnings("unchecked")
            T t = (T) targetDomainObject;
            BiFunction<T, Authentication, Boolean> permissionCheck = permissionsMap.get(permission.toString());
            if (permissionCheck == null) {
                return false;
            }
            return permissionCheck.apply(t, authentication);
        } catch (ClassCastException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean hasPermission(Authentication authentication, Serializable targetId, Object permission) {
        if (authentication == null || targetId == null || permission == null) {
            return false;
        }
        @SuppressWarnings("unchecked")
        ID id = (ID) targetId;
        T domainObject = service.findById(id);
        return hasPermission(authentication, domainObject, permission);
    }
}
