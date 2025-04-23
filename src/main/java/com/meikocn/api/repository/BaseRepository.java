package com.meikocn.api.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<M, ID>
    extends EntityGraphJpaRepository<M, ID>, EntityGraphQuerydslPredicateExecutor<M> {}
