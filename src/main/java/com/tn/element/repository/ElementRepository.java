package com.tn.element.repository;

import org.springframework.data.repository.CrudRepository;

import com.tn.element.domain.Element;
import com.tn.query.jpa.QueryableRepository;

public interface ElementRepository extends CrudRepository<Element, Long>, QueryableRepository<Element> {}