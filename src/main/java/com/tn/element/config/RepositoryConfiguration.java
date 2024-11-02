package com.tn.element.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tn.element.domain.Element;
import com.tn.element.repository.ElementRepositoryImpl;
import com.tn.query.DefaultQueryParser;
import com.tn.query.ValueMappers;
import com.tn.query.jpa.JpaPredicateFactory;
import com.tn.query.jpa.NameMappings;

@Configuration
class RepositoryConfiguration
{
  @Bean
  ElementRepositoryImpl elementRepositoryImpl(EntityManager entityManager)
  {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Element> criteriaQuery = criteriaBuilder.createQuery(Element.class);

    return new ElementRepositoryImpl(
      entityManager,
      criteriaQuery,
      new DefaultQueryParser<>(
        new JpaPredicateFactory(
          entityManager.getCriteriaBuilder(),
          NameMappings.forFields(Element.class, criteriaQuery)
        ),
        ValueMappers.forFields(Element.class)
      )
    );
  }
}
