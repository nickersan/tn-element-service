package com.tn.element.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import com.tn.element.domain.Element;
import com.tn.element.repository.ElementRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ElementControllerIntegrationTest
{
  private static final long ELEMENT_ID = 2L;
  private static final long PARENT_ELEMENT_ID = 1L;
  private static final String OWNER_ID = "OWN1";
  private static final Element UNSAVED_ELEMENT = new Element(PARENT_ELEMENT_ID, OWNER_ID, "TEST", "ELEMENT");
  private static final Element ELEMENT = new Element(ELEMENT_ID, PARENT_ELEMENT_ID, OWNER_ID, "TEST", "ELEMENT", LocalDateTime.now());
  private static final ParameterizedTypeReference<List<Element>> ELEMENT_LIST = new ParameterizedTypeReference<>() {};

  @MockBean
  ElementRepository elementRepository;

  @Autowired
  TestRestTemplate testRestTemplate;

  @Test
  void shouldReturnAllElements()
  {
    when(elementRepository.findAll()).thenReturn(List.of(ELEMENT));

    ResponseEntity<List<Element>> response = testRestTemplate.exchange("/v1", GET, null, ELEMENT_LIST);

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(List.of(ELEMENT), response.getBody());
  }

  @Test
  void shouldReturnElementForIdWithPath()
  {
    when(elementRepository.findById(ELEMENT_ID)).thenReturn(Optional.of(ELEMENT));

    ResponseEntity<Element> response = testRestTemplate.exchange("/v1/{elementId}", GET, null, Element.class, ELEMENT_ID);

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(ELEMENT, response.getBody());
  }

  @Test
  void shouldReturnNotFoundForIdWithPathWhenNotFound()
  {
    when(elementRepository.findById(ELEMENT_ID)).thenReturn(Optional.empty());

    ResponseEntity<Element> response = testRestTemplate.exchange("/v1/{elementId}", GET, null, Element.class, ELEMENT_ID);

    assertTrue(response.getStatusCode().is4xxClientError());
  }

  @Test
  void shouldReturnElementForIdWithParam()
  {
    when(elementRepository.findWhere("id=" + ELEMENT.id())).thenReturn(List.of(ELEMENT));

    ResponseEntity<List<Element>> response = testRestTemplate.exchange("/v1?id={elementId}", GET, null, ELEMENT_LIST, ELEMENT.id());

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(List.of(ELEMENT), response.getBody());
  }

  @Test
  void shouldReturnElementForIdWithQuery()
  {
    when(elementRepository.findWhere("id=" + ELEMENT.id())).thenReturn(List.of(ELEMENT));

    ResponseEntity<List<Element>> response = testRestTemplate.exchange("/v1?q=id={elementId}", GET, null, ELEMENT_LIST, ELEMENT.id());

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(List.of(ELEMENT), response.getBody());
  }

  @Test
  void shouldReturnElementForParentIdWithParam()
  {
    when(elementRepository.findWhere("parentId=" + ELEMENT.parentId())).thenReturn(List.of(ELEMENT));

    ResponseEntity<List<Element>> response = testRestTemplate.exchange("/v1?parentId={elementParentId}", GET, null, ELEMENT_LIST, ELEMENT.parentId());

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(List.of(ELEMENT), response.getBody());
  }

  @Test
  void shouldReturnElementForParentIdWithQuery()
  {
    when(elementRepository.findWhere("parentId=" + ELEMENT.parentId())).thenReturn(List.of(ELEMENT));

    ResponseEntity<List<Element>> response = testRestTemplate.exchange("/v1?q=parentId={elementParentId}", GET, null, ELEMENT_LIST, ELEMENT.parentId());

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(List.of(ELEMENT), response.getBody());
  }


  @Test
  void shouldReturnElementForTypeWithParam()
  {
    when(elementRepository.findWhere("type=" + ELEMENT.type())).thenReturn(List.of(ELEMENT));

    ResponseEntity<List<Element>> response = testRestTemplate.exchange("/v1?type={elementType}", GET, null, ELEMENT_LIST, ELEMENT.type());

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(List.of(ELEMENT), response.getBody());
  }

  @Test
  void shouldReturnElementForTypeWithQuery()
  {
    when(elementRepository.findWhere("type=" + ELEMENT.type())).thenReturn(List.of(ELEMENT));

    ResponseEntity<List<Element>> response = testRestTemplate.exchange("/v1?q=type={elementType}", GET, null, ELEMENT_LIST, ELEMENT.type());

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(List.of(ELEMENT), response.getBody());
  }

  @Test
  void shouldReturnElementForNameWithParam()
  {
    when(elementRepository.findWhere("name=" + ELEMENT.name())).thenReturn(List.of(ELEMENT));

    ResponseEntity<List<Element>> response = testRestTemplate.exchange("/v1?name={elementName}", GET, null, ELEMENT_LIST, ELEMENT.name());

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(List.of(ELEMENT), response.getBody());
  }

  @Test
  void shouldReturnElementForNameWithQuery()
  {
    when(elementRepository.findWhere("name=" + ELEMENT.name())).thenReturn(List.of(ELEMENT));

    ResponseEntity<List<Element>> response = testRestTemplate.exchange("/v1?q=name={elementName}", GET, null, ELEMENT_LIST, ELEMENT.name());

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(List.of(ELEMENT), response.getBody());
  }

  @Test
  void shouldReturnElementForCreatedWithParam()
  {
    when(elementRepository.findWhere("created=" + ELEMENT.created())).thenReturn(List.of(ELEMENT));

    ResponseEntity<List<Element>> response = testRestTemplate.exchange("/v1?created={elementCreated}", GET, null, ELEMENT_LIST, ELEMENT.created());

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(List.of(ELEMENT), response.getBody());
  }

  @Test
  void shouldReturnElementForCreatedWithQuery()
  {
    when(elementRepository.findWhere("created=" + ELEMENT.created())).thenReturn(List.of(ELEMENT));

    ResponseEntity<List<Element>> response = testRestTemplate.exchange("/v1?q=created={elementCreated}", GET, null, ELEMENT_LIST, ELEMENT.created());

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(List.of(ELEMENT), response.getBody());
  }

  @Test
  void shouldReturnBadRequestForGetWithInvalidParam()
  {
    ResponseEntity<Void> response = testRestTemplate.exchange("/v1?invalid=X", GET, null, Void.class);

    assertEquals(BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void shouldReturnBadRequestForGetWithInvalidQuery()
  {
    ResponseEntity<Void> response = testRestTemplate.exchange("/v1?q=invalid=X", GET, null, Void.class);

    assertEquals(BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void shouldReturnInternalServerErrorForGetOnUncaughtException()
  {
    when(elementRepository.findWhere("name=X")).thenThrow(new RuntimeException());

    ResponseEntity<Void> response = testRestTemplate.exchange("/v1?name=X", GET, null, Void.class);

    assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  void shouldSaveElementWithPost()
  {
    when(elementRepository.save(UNSAVED_ELEMENT)).thenReturn(ELEMENT);

    ResponseEntity<Element> response = testRestTemplate.exchange("/v1", POST, new HttpEntity<>(UNSAVED_ELEMENT), Element.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(ELEMENT, response.getBody());
  }

  @Test
  void shouldBadRequestForSaveElementWithPostAndMissingType()
  {
    ResponseEntity<Element> response = testRestTemplate.exchange("/v1", POST, new HttpEntity<>(new Element(OWNER_ID, null, "Test")), Element.class);

    assertTrue(response.getStatusCode().is4xxClientError());
  }

  @Test
  void shouldBadRequestForSaveElementWithPostAndMissingName()
  {
    ResponseEntity<Element> response = testRestTemplate.exchange("/v1", POST, new HttpEntity<>(new Element(OWNER_ID, "TEST", null)), Element.class);

    assertTrue(response.getStatusCode().is4xxClientError());
  }

  @Test
  void shouldReturnBadRequestWithPostWhenDataIntegrityViolated()
  {
    when(elementRepository.save(UNSAVED_ELEMENT)).thenThrow(new DataIntegrityViolationException("Test"));

    ResponseEntity<Element> response = testRestTemplate.exchange("/v1", POST, new HttpEntity<>(UNSAVED_ELEMENT), Element.class, ELEMENT_ID);
    assertEquals(BAD_REQUEST, response.getStatusCode());
  }


  @Test
  void shouldSaveElementWithPut()
  {
    when(elementRepository.findById(ELEMENT_ID)).thenReturn(Optional.of(ELEMENT));
    when(elementRepository.save(ELEMENT)).thenReturn(ELEMENT);

    ResponseEntity<Element> response = testRestTemplate.exchange("/v1/{elementId}", PUT, new HttpEntity<>(UNSAVED_ELEMENT), Element.class, ELEMENT_ID);

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(ELEMENT, response.getBody());
  }

  @Test
  void shouldReturnNotFoundWithPutForUnknownIds()
  {
    var unknownElementId = ELEMENT_ID + 1;

    when(elementRepository.findById(unknownElementId)).thenReturn(Optional.empty());

    ResponseEntity<Element> response = testRestTemplate.exchange("/v1/{elementId}", PUT, new HttpEntity<>(ELEMENT), Element.class, unknownElementId);

    assertEquals(NOT_FOUND, response.getStatusCode());
  }

  @Test
  void shouldReturnBadRequestWithPutWhenDataIntegrityViolated()
  {
    when(elementRepository.findById(ELEMENT_ID)).thenReturn(Optional.of(ELEMENT));
    when(elementRepository.save(ELEMENT)).thenThrow(new DataIntegrityViolationException("Test"));

    ResponseEntity<Element> response = testRestTemplate.exchange("/v1/{elementId}", PUT, new HttpEntity<>(UNSAVED_ELEMENT), Element.class, ELEMENT_ID);
    assertEquals(BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void shouldDeleteElement()
  {
    ResponseEntity<Void> response = testRestTemplate.exchange("/v1/{elementId}", DELETE, null, Void.class, ELEMENT_ID);

    assertTrue(response.getStatusCode().is2xxSuccessful());
    verify(elementRepository).deleteById(ELEMENT_ID);
  }


}
