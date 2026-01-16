-- ============================================
-- CHANGE_HISTORY TABLE (Flyway Migration V3)
-- ============================================
-- This table tracks status changes for Change requests
-- Replaces auto-generated Hibernate table with correct schema

CREATE TABLE IF NOT EXISTS change_history (
    id SERIAL PRIMARY KEY,
    change_id UUID NOT NULL REFERENCES changes(id) ON DELETE CASCADE,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by VARCHAR(100),
    comments TEXT,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT change_history_new_status_check CHECK (new_status IS NOT NULL)
);

CREATE INDEX IF NOT EXISTS idx_change_history_change ON change_history(change_id);
CREATE INDEX IF NOT EXISTS idx_change_history_changed_at ON change_history(changed_at);
CREATE INDEX IF NOT EXISTS idx_change_history_changed_by ON change_history(changed_by);