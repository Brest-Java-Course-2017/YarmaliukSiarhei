DROP TABLE IF EXISTS app_user;
CREATE TABLE app_user (
  user_id       INT           NOT NULL AUTO_INCREMENT,
  login         VARCHAR(255)   NOT NULL,
  password      VARCHAR(255)   NOT NULL,
  description   VARCHAR(255)  NULL,
  PRIMARY KEY (user_id)
);