package KirisShygys.service;

import java.util.List;

public interface BaseEntityService<T> {
    List<T> getAll(String token);
    T create(String token, T entity);
    T update(String token, Long id, T updatedEntity);
    void delete(String token, Long id);
}
