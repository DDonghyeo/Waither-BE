package com.waither.notiservice.redis;

import com.waither.notiservice.domain.FireBaseToken;
import org.springframework.data.repository.CrudRepository;

public interface FireTokenBaseRepository extends CrudRepository<FireBaseToken, Long> {
}
