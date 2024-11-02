CREATE SEQUENCE IF NOT EXISTS element_id_seq;

CREATE TABLE IF NOT EXISTS elements
(
    element_id        INT          NOT NULL PRIMARY KEY,
    parent_element_id INT          NULL,
    owner_id          VARCHAR(100) NOT NULL,
    type              VARCHAR(100) NOT NULL,
    name              VARCHAR(100) NOT NULL,
    created           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE NULLS NOT DISTINCT (element_id, owner_id),
    UNIQUE NULLS NOT DISTINCT (parent_element_id, owner_id, type, name),
    FOREIGN KEY (parent_element_id, owner_id) REFERENCES elements(element_id, owner_id)
);