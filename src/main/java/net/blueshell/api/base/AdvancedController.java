package net.blueshell.api.base;

public abstract class AdvancedController<S, AM, SM> extends AuthorizationBase {

    protected final S service;
    protected final AM advancedMapper;
    protected final SM simpleMapper;

    public AdvancedController(S service, AM advancedMapper, SM simpleMapper) {
        this.service = service;
        this.advancedMapper = advancedMapper;
        this.simpleMapper = simpleMapper;
    }
}
