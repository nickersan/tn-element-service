package com.tn.element.api;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import static com.tn.lang.Strings.isNullOrWhitespace;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tn.element.domain.Element;
import com.tn.element.repository.ElementRepository;
import com.tn.query.QueryParseException;
import com.tn.service.IllegalParameterException;
import com.tn.service.query.QueryBuilder;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ElementController
{
  private static final QueryBuilder QUERY_BUILDER = new QueryBuilder(Element.class);

  private final ElementRepository elementRepository;

  @GetMapping("/{id}")
  public Element elementForId(@PathVariable("id") long id)
  {
    return elementRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Element not found for id: " + id));
  }

  @GetMapping
  public Iterable<Element> elementsFor(@RequestParam(required = false) MultiValueMap<String, String> params)
  {
    try
    {
      String query = QUERY_BUILDER.build(params);

      return isNullOrWhitespace(query) ? elementRepository.findAll() : elementRepository.findWhere(query);
    }
    catch (IllegalParameterException | QueryParseException e)
    {
      throw new ResponseStatusException(BAD_REQUEST, e.getMessage(), e);
    }
  }

  @PostMapping  
  public Element create(@Validated @RequestBody ElementRequest request)
  {
    return elementRepository.save(new Element(request.parentId, request.ownerId, request.type, request.name));
  }

  
  @PutMapping("/{id}")
  @Transactional
  public Element update(@PathVariable("id") long id, @Validated @RequestBody ElementRequest request)
  {
    var existingElement = elementRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Element not found with ID: " + id));
    
    return elementRepository.save(new Element(id, request.parentId, request.ownerId, request.type, request.name, existingElement.created()));
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable("id") long id)
  {
    elementRepository.deleteById(id);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e)
  {
    return ResponseEntity.badRequest().body(new ErrorResponse(e.getMostSpecificCause().getMessage()));
  }

  public record ElementRequest(
    Long parentId,
    @NotNull(message = "ownerId required")
    String ownerId,
    @NotNull(message = "type required")
    String type,
    @NotNull(message = "name required")
    String name
  ) {}

  public record ErrorResponse(String message) {}
}
