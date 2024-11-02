package com.tn.element.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "elements")
@Cacheable(false)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Getter
@EqualsAndHashCode
@ToString
public class Element
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "elementId")
  @SequenceGenerator(name = "elementId", sequenceName = "element_id_seq", allocationSize = 1)
  @Column(name = "element_id")
  @JsonProperty
  private Long id;

  @Column(name = "parent_element_id")
  @JsonProperty
  private Long parentId;

  @Column(name = "owner_id", nullable = false)
  @JsonProperty
  @NotNull
  private String ownerId;

  @Column(nullable = false, unique = true)
  @JsonProperty
  @NotNull
  private String type;

  @Column(nullable = false, unique = true)
  @JsonProperty
  @NotNull
  private String name;

  @CreationTimestamp
  @JsonProperty
  private LocalDateTime created;

  public Element(@NotNull String ownerId, @NotNull String type, @NotNull String name)
  {
    this(null, null, ownerId, type, name, null);
  }

  public Element(Long parentId, @NotNull String ownerId, @NotNull String type, @NotNull String name)
  {
    this(null, parentId, ownerId, type, name, null);
  }
}
