package net.blueshell.api.base;

import net.blueshell.api.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class BaseModelService<T, ID, R extends BaseRepository<T,ID>> extends AuthorizationBase {

    protected R repository;
    public BaseModelService(R repository) {
        this.repository = repository;
    }

    @Transactional
    public T create(T entity) {
        preCreate(entity);
        T savedEntity = repository.save(entity);
        postCreate(savedEntity);
        return savedEntity;
    }

    @Transactional
    public T update(T entity) {
        preUpdate(entity);
        ID id = extractId(entity);
        if (id != null && !repository.existsById(id)) {
            throw new RuntimeException("Entity not found with id: " + id);
        }
        T savedEntity = repository.save(entity);
        postUpdate(savedEntity);
        return savedEntity;
    }

    @Transactional
    public List<T> updateAll(List<T> entities) {
        return entities.stream().map(this::update).toList();
    }


    protected void preCreate(T entity) {
    }

    protected void postCreate(T entity) {
    }

    protected void preUpdate(T entity) {
    }

    protected void postUpdate(T entity) {
    }

    /**
     * Fetch an entity by its ID
     */
    @Transactional(readOnly = true)
    public T findById(ID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
    }

    /**
     * Delete an entity by its ID
     */
    @Transactional
    public void deleteById(ID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Entity not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public List<T> findAllById(List<ID> ids) {
        return repository.findAllById(ids);
    }

    /**
     * Retrieve all entities
     */
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }
    /**
     * Retrieve paginated entities
     */
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Retrieve entities based on a Specification (filtering).
     * Note: This requires the repository to implement JpaSpecificationExecutor.
     */
    @Transactional(readOnly = true)
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        if (repository != null) {
            return repository.findAll(spec, pageable);
        } else {
            throw new UnsupportedOperationException("Repository does not support Specifications");
        }
    }

    /**
     * Utility method to extract the ID from an entity.
     * <p>
     * Concrete classes should override this if they know how to extract the ID from entity T,
     * or you may introduce a common interface for entities that provides a getId() method.
     * </p>
     */
    protected abstract ID extractId(T entity);
}
