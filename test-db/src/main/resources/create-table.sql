DROP TABLE IF EXISTS investigation;
CREATE TABLE investigation (
  investigation_id     INT          NOT NULL AUTO_INCREMENT,
  --   investigation_number INT          NOT NULL AUTO_INCREMENT,
  investigation_number INT NULL UNIQUE,
  title                VARCHAR(100) NULL,
  start_date           TIMESTAMP    NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  end_date             TIMESTAMP NULL,
  description          VARCHAR(255) NOT NULL,

  CHECK (start_date <= end_date),
  PRIMARY KEY (investigation_id)
);

CREATE OR REPLACE TRIGGER investigationInsert
BEFORE INSERT ON investigation
FOR EACH ROW
CALL "com.segniertomato.work.database.trigger.InvestigationInsertTrigger";


DROP TABLE IF EXISTS staff;
CREATE TABLE staff (
  employee_id        INT          NOT NULL AUTO_INCREMENT,
  name               VARCHAR(100) NOT NULL,
  age                DATE         NOT NULL,
  start_working_date DATE         NOT NULL DEFAULT (CURRENT_DATE),

  CHECK (start_working_date <= CURRENT_DATE),
  CHECK (age <= CURRENT_DATE),
  PRIMARY KEY (employee_id)
);

DROP TABLE IF EXISTS investigation_staff_relations;
CREATE TABLE investigation_staff_relations (
  investigation_id INT NOT NULL,
  employee_id      INT NOT NULL,

  FOREIGN KEY (investigation_id) REFERENCES investigation (investigation_id)
  ON DELETE CASCADE ON UPDATE CASCADE,

  FOREIGN KEY (employee_id) REFERENCES staff (employee_id)
  ON DELETE CASCADE ON UPDATE CASCADE,

  PRIMARY KEY (investigation_id, employee_id)
);