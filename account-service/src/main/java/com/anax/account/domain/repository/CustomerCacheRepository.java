package com.anax.account.domain.repository;

import com.anax.account.domain.model.CachedCustomer;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerCacheRepository extends ReactiveCrudRepository<CachedCustomer, Long> {

	@Modifying
	@Query("INSERT INTO customer_cache (id, name) VALUES (:id, :name) " +
			"ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name")
	Mono<Integer> upsertName(@Param("id") Long id, @Param("name") String name);

    @Modifying
    @Query("INSERT INTO customer_cache (id, name, gender, age, identification, address, phone, status) " +
	    "VALUES (:id, :name, :gender, :age, :identification, :address, :phone, :status) " +
	    "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, gender = EXCLUDED.gender, age = EXCLUDED.age, " +
	    "identification = EXCLUDED.identification, address = EXCLUDED.address, phone = EXCLUDED.phone, " +
	    "status = EXCLUDED.status")
    Mono<Integer> upsert(@Param("id") Long id,
			 @Param("name") String name,
			 @Param("gender") String gender,
			 @Param("age") Integer age,
			 @Param("identification") String identification,
			 @Param("address") String address,
			 @Param("phone") String phone,
			 @Param("status") Boolean status);
}