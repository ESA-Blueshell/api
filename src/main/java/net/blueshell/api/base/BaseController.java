package net.blueshell.api.base;

public abstract class BaseController<S, M> extends AuthorizationBase {

    protected final S service;
    protected final M mapper;

    public BaseController(S service, M mapper) {
        this.service = service;
        this.mapper = mapper;
    }
}
