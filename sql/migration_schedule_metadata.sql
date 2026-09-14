USE finpeer;

ALTER TABLE loans
    ADD COLUMN schedule_created_at TIMESTAMP NULL;