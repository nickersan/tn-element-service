package com.tn.element.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;

import com.tn.element.domain.Element;
import com.tn.query.QueryParser;
import com.tn.query.jpa.AbstractQueryableRepository;

public class ElementRepositoryImpl extends AbstractQueryableRepository<Element>
{
  public ElementRepositoryImpl(EntityManager entityManager, CriteriaQuery<Element> criteriaQuery, QueryParser<Predicate> queryParser)
  {
    super(
      entityManager,
      criteriaQuery,
      queryParser
    );
  }
}
