package com.tn.element.repository;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;

import com.tn.element.domain.Element;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ElementRepositoryIntegrationTest
{
  private static final String OWNER_ID = "OWN1";
  private static final Element ELEMENT = new Element(OWNER_ID, "TEST", "Root");

  @Autowired
  private ElementRepository elementRepository;

  @Test
  @Order(1)
  @Rollback(false)
  void shouldSave()
  {
    var element = elementRepository.save(ELEMENT);
    assertElement(element);
    assertNotNull(element.id());
//    assertNotNull(element.created());
  }

  @Test
  @Order(2)
  void shouldRead()
  {
    var element = StreamSupport.stream(elementRepository.findAll().spliterator(), false).findFirst().orElseThrow(AssertionFailedError::new);
    assertElement(element);
    assertNotNull(element.created());
  }

  @Test
  @Order(3)
  void shouldReadById()
  {
    var element = elementRepository.findById(1L).orElseThrow(AssertionFailedError::new);
    assertElement(element);
    assertNotNull(element.created());
  }

  @Test
  @Order(4)
  void shouldDelete()
  {
    var element = elementRepository.findById(1L).orElseThrow(AssertionFailedError::new);
    elementRepository.delete(element);
    assertTrue(elementRepository.findById(1L).isEmpty());
  }

  private static void assertElement(Element actual)
  {
    assertEquals(ELEMENT.type(), actual.type());
    assertEquals(ELEMENT.name(), actual.name());
  }

  @Nested
  class QueryTest
  {
    private Element root;
    private Element child1;
    private Element child2;
    private Element differentOwner;

    @BeforeEach
    void createElements()
    {
      root = elementRepository.save(new Element(OWNER_ID, "TEST", "Root"));
      child1 = elementRepository.save(new Element(root.id(), OWNER_ID, "TEST", "C1"));
      child2 = elementRepository.save(new Element(root.id(), OWNER_ID, "TEST", "C2"));

      differentOwner = elementRepository.save(new Element(OWNER_ID + 1, "TEST", "Unrelated By Owner"));
    }

    @AfterEach
    void deleteElements()
    {
      elementRepository.deleteAll(List.of(child1, child2));
      elementRepository.deleteAll(List.of(root, differentOwner));
    }

    @Test
    void shouldFindByType()
    {
      assertWhere("type = " + root.type(), root, child1, child2, differentOwner);
    }

    @Test
    void shouldFindByName()
    {
      assertWhere("name = " + root.name(), root);
      assertWhere("name = " + child1.name(), child1);
      assertWhere("name = " + child2.name(), child2);
    }

    @Test
    void shouldFindByOwnerId()
    {
      assertWhere("ownerId = " + root.ownerId(), root, child1, child2);
      assertWhere("ownerId = " + differentOwner.ownerId(), differentOwner);
    }

    @Test
    void shouldFindByParentId()
    {
      assertWhere("parentId = " + root.id(), child1, child2);
    }

    private void assertWhere(String query, Element... expected)
    {
      var elements = StreamSupport.stream(elementRepository.findWhere(query).spliterator(), false).collect(toSet());
      assertEquals(expected.length, elements.size());
      assertEquals(Set.of(expected), elements);
    }
  }

  @Nested
  class ViolationTest
  {
    @AfterEach
    void deleteElements()
    {
      elementRepository.deleteAll(
        StreamSupport.stream(elementRepository.findAll().spliterator(), false)
          .sorted(comparing(Element::id).reversed())
          .toList()
      );
    }

    @Test
    void shouldThrowForUnknownParent()
    {
      assertThrows(DataIntegrityViolationException.class, () -> elementRepository.save(new Element(100L, OWNER_ID, "TEST", "Invalid")));
    }

    @Test
    void shouldThrowForDuplicateRootElement()
    {
      elementRepository.save(new Element(OWNER_ID, "TEST", "Duplicate"));
      assertThrows(DataIntegrityViolationException.class, () -> elementRepository.save(new Element(OWNER_ID, "TEST", "Duplicate")));
    }

    @Test
    void shouldThrowForDuplicateChildElement()
    {
      var root = elementRepository.save(new Element(OWNER_ID, "TEST", "Root"));
      elementRepository.save(new Element(root.id(), OWNER_ID, "TEST", "Duplicate"));
      assertThrows(DataIntegrityViolationException.class, () -> elementRepository.save(new Element(root.id(), OWNER_ID, "TEST", "Duplicate")));
    }

    @Test
    void shouldThrowWhenDeletingNonEmptyRootElement()
    {
      var root = elementRepository.save(new Element(OWNER_ID, "TEST", "Root"));
      elementRepository.save(new Element(root.id(), OWNER_ID, "TEST", "Child"));
      assertThrows(DataIntegrityViolationException.class, () -> elementRepository.delete(root));
    }

    @Test
    void shouldThrowWhenAddingToRootWithDifferentOwner()
    {
      var root = elementRepository.save(new Element(OWNER_ID, "TEST", "Root"));
      assertThrows(DataIntegrityViolationException.class, () -> elementRepository.save(new Element(root.id(), OWNER_ID + 1, "TEST", "Child")));
    }
  }
}
