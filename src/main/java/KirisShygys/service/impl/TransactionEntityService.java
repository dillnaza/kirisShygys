package KirisShygys.service.impl;

import KirisShygys.entity.User;
import KirisShygys.exception.NotFoundException;
import KirisShygys.exception.UnauthorizedException;
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
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Invalid or missing token");
        }
        getAuthenticatedUser(token);
        logger.info("Fetching all {}", entityName);
        return repository.findAll();
    }

    public T getById(String token, ID id) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Invalid or missing token");
        }
        getAuthenticatedUser(token);
        logger.info("Fetching {} with ID {}", entityName, id);
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName + " with ID " + id + " not found"));
    }

    @Transactional
    public T create(String token, T entity) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Invalid or missing token");
        }
        getAuthenticatedUser(token);
        logger.info("Creating new {}", entityName);
        return repository.save(entity);
    }

    @Transactional
    public T update(String token, ID id, T updatedEntity) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Invalid or missing token");
        }
        getAuthenticatedUser(token);
        logger.info("Updating {} with ID {}", entityName, id);
        return repository.save(updatedEntity);
    }

    @Transactional
    public void delete(String token, ID id) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Invalid or missing token");
        }
        getAuthenticatedUser(token);
        if (!repository.existsById(id)) {
            throw new NotFoundException(entityName + " with ID " + id + " not found");
        }
        logger.info("Deleting {} with ID {}", entityName, id);
        repository.deleteById(id);
    }
}
