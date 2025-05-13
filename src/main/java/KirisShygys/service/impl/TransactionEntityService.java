package KirisShygys.service.impl;

import KirisShygys.exception.NotFoundException;
import KirisShygys.repository.UserRepository;
import KirisShygys.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public abstract class TransactionEntityService<T, ID> extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionEntityService.class);
    protected final JpaRepository<T, ID> repository;
    private final String entityName;

    public TransactionEntityService(JpaRepository<T, ID> repository, String entityName, UserRepository userRepository, JwtUtil jwtUtil) {
        super(userRepository, jwtUtil);
        this.repository = repository;
        this.entityName = entityName;
    }

    public List<T> getAll(String token) {
        getAuthenticatedUser(token);
        logger.info("Fetching all {}", entityName);
        List<T> all = repository.findAll();
        return all.stream().filter(entity -> {
            try {
                var field = entity.getClass().getDeclaredField("isDeleted");
                field.setAccessible(true);
                Object value = field.get(entity);
                return value instanceof Boolean && !((Boolean) value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return true;
            }
        }).toList();
    }

    public T getById(String token, ID id) {
        getAuthenticatedUser(token);
        logger.info("Fetching {} with ID {}", entityName, id);
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName + " with ID " + id + " not found"));
    }

    @Transactional
    public T create(String token, T entity) {
        getAuthenticatedUser(token);
        logger.info("Creating new {}", entityName);
        return repository.save(entity);
    }

    @Transactional
    public T update(String token, ID id, T updatedEntity) {
        getAuthenticatedUser(token);
        if (!repository.existsById(id)) {
            throw new NotFoundException(entityName + " with ID " + id + " not found");
        }
        logger.info("Updating {} with ID {}", entityName, id);
        return repository.save(updatedEntity);
    }

    @Transactional
    public void delete(String token, ID id) {
        getAuthenticatedUser(token);
        T entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName + " with ID " + id + " not found"));
        try {
            var field = entity.getClass().getDeclaredField("isDeleted");
            field.setAccessible(true);
            field.set(entity, true);
            logger.info("Soft deleted {} with ID {}", entityName, id);
            repository.save(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.warn("{} does not support soft delete. Falling back to hard delete.", entityName);
            repository.deleteById(id);
        }
    }
}
