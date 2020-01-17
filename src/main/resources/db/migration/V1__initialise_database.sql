DROP SCHEMA IF EXISTS mcve CASCADE;

CREATE SCHEMA mcve;

CREATE TABLE mcve.reviewers (
  id              SERIAL PRIMARY KEY,
  department_name VARCHAR NOT NULL
);

CREATE TABLE mcve.orders (
  id              SERIAL PRIMARY KEY,
  department_name VARCHAR NOT NULL
);

CREATE TABLE mcve.order_lines (
  id              SERIAL PRIMARY KEY,
  order_id        INTEGER NOT NULL,
  FOREIGN KEY (order_id) REFERENCES mcve.orders (id)
);

CREATE TABLE mcve.approvals (
  order_line_id   INTEGER NOT NULL,
  reviewer_id     INTEGER NOT NULL
);

INSERT INTO mcve.reviewers (department_name) VALUES ('IT');
INSERT INTO mcve.orders (department_name) VALUES ('IT');
INSERT INTO mcve.order_lines (order_id) (SELECT MAX(id) FROM mcve.orders);