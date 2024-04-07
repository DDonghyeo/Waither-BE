package com.waither.notiservice.redis;

import com.waither.notiservice.domain.FireBaseToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FireTokenBaseRepository extends CrudRepository<FireBaseToken, Long> {
}
