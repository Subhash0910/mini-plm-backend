ALTER TABLE parts ADD COLUMN lifecycle_state VARCHAR(50) DEFAULT 'IN_WORK' NOT NULL;
ALTER TABLE parts ADD COLUMN revision_number INTEGER DEFAULT 1;
ALTER TABLE parts ADD COLUMN revision_letter VARCHAR(1);
ALTER TABLE parts ADD COLUMN revision_sequence VARCHAR(10) DEFAULT '1.0' NOT NULL;
ALTER TABLE parts ADD COLUMN created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE parts ADD COLUMN released_date TIMESTAMP;
ALTER TABLE parts ADD COLUMN obsolete_date TIMESTAMP;
ALTER TABLE parts ADD COLUMN created_by VARCHAR(100);
ALTER TABLE parts ADD COLUMN last_modified_by VARCHAR(100);
ALTER TABLE parts ADD COLUMN last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

CREATE TABLE state_transition_history (
    id SERIAL PRIMARY KEY,
    part_id BIGINT NOT NULL,
    from_state VARCHAR(50) NOT NULL,
    to_state VARCHAR(50) NOT NULL,
    transition_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transitioned_by VARCHAR(100),
    reason TEXT,
    FOREIGN KEY (part_id) REFERENCES parts(id)
);

CREATE INDEX idx_state_history_part_id ON state_transition_history(part_id);
CREATE INDEX idx_parts_lifecycle_state ON parts(lifecycle_state);
