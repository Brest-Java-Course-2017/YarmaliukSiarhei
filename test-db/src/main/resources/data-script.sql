INSERT INTO investigation (investigation_number, title, description, start_date, end_date)
VALUES (NULL,
        'Murders of Sharon Tate',
        'American actress and sex symbol Sharon Tate was murdered on August 1969 by members of the Charles Manson’s family.',
        '1969-09-13 16:09:00', '1972-02-04 13:26:16');


INSERT INTO investigation (investigation_number, title, description, start_date, end_date)
VALUES (NULL,
        'Assassination of Martin Luther King Jr.',
        'Martin Luther King Jr. was assassinated by James Earl Ray in Memphis, Tennessee on April 4, 1968.',
        '1968-04-04 08:50:00', '1968-04-10');

INSERT INTO investigation (investigation_number, title, description, start_date)
VALUES (NULL,
        'Murders of Charles Moore and Henry Dee',
        'Charles Moore and Henry Dee were tortured and drowned by members of Ku Klux Klan in Franklin County, Mississippi.',
        '1964-05-25 22:16:30');


INSERT INTO staff (name, age, start_working_date) VALUES ('Nick Jeyrom', '1936-03-26', '1956-09-25');
INSERT INTO staff (name, age, start_working_date) VALUES ('Cartman Fingerbang', '1937-11-02', '1963-04-16');
INSERT INTO staff (name, age, start_working_date) VALUES ('Frank Columbo', '1936-04-16', '1953-11-16');
INSERT INTO staff (name, age, start_working_date) VALUES ('Mike Fithz', '1950-07-28', '1975-03-26');

INSERT INTO investigation_staff_relations (investigation_id, employee_id)
VALUES (1, 1);
INSERT INTO investigation_staff_relations (investigation_id, employee_id)
VALUES (1, 3);

INSERT INTO investigation_staff_relations (investigation_id, employee_id)
VALUES (2, 1);
INSERT INTO investigation_staff_relations (investigation_id, employee_id)
VALUES (2, 2);
INSERT INTO investigation_staff_relations (investigation_id, employee_id)
VALUES (2, 3);

INSERT INTO investigation_staff_relations (investigation_id, employee_id)
VALUES (3, 3);
INSERT INTO investigation_staff_relations (investigation_id, employee_id)
VALUES (3, 2);