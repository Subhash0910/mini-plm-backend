-- ============================================
-- Mini PLM Backend Initial Database Schema
-- Version: 1.0.0
-- ============================================

-- Enable UUID Extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- USERS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(is_active);

-- ============================================
-- ROLES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_roles_name ON roles(name);

-- ============================================
-- USER_ROLES JUNCTION TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);

-- ============================================
-- PARTS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS parts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    part_number VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'DRAFT',
    category VARCHAR(100),
    revision INT DEFAULT 1,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_at TIMESTAMP,
    changed_by VARCHAR(255)
);

CREATE INDEX idx_parts_number ON parts(part_number);
CREATE INDEX idx_parts_status ON parts(status);
CREATE INDEX idx_parts_category ON parts(category);
CREATE INDEX idx_parts_created_by ON parts(created_by);
CREATE INDEX idx_parts_changed_at ON parts(changed_at);

-- ============================================
-- CHANGES TABLE (Lifecycle Tracking)
-- ============================================
CREATE TABLE IF NOT EXISTS changes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    change_number VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50),
    status VARCHAR(50) DEFAULT 'PENDING',
    priority VARCHAR(50) DEFAULT 'MEDIUM',
    created_by UUID REFERENCES users(id),
    assigned_to UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_at TIMESTAMP,
    changed_by VARCHAR(255)
);

CREATE INDEX idx_changes_number ON changes(change_number);
CREATE INDEX idx_changes_status ON changes(status);
CREATE INDEX idx_changes_type ON changes(type);
CREATE INDEX idx_changes_priority ON changes(priority);
CREATE INDEX idx_changes_created_by ON changes(created_by);
CREATE INDEX idx_changes_assigned_to ON changes(assigned_to);
CREATE INDEX idx_changes_changed_at ON changes(changed_at);

-- ============================================
-- CHANGE_ITEMS TABLE (Part-Change Relationship)
-- ============================================
CREATE TABLE IF NOT EXISTS change_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    change_id UUID NOT NULL REFERENCES changes(id) ON DELETE CASCADE,
    part_id UUID NOT NULL REFERENCES parts(id) ON DELETE CASCADE,
    item_type VARCHAR(50),
    sequence INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_change_items_change ON change_items(change_id);
CREATE INDEX idx_change_items_part ON change_items(part_id);

-- ============================================
-- WORKFLOWS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS workflows (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workflows_active ON workflows(is_active);

-- ============================================
-- LIFECYCLE STATES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS lifecycle_states (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    workflow_id UUID NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    sequence INT,
    is_initial BOOLEAN DEFAULT false,
    is_final BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lifecycle_states_workflow ON lifecycle_states(workflow_id);
CREATE INDEX idx_lifecycle_states_name ON lifecycle_states(name);

-- ============================================
-- LIFECYCLE_TRANSITIONS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS lifecycle_transitions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workflow_id UUID NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    from_state_id UUID NOT NULL REFERENCES lifecycle_states(id),
    to_state_id UUID NOT NULL REFERENCES lifecycle_states(id),
    condition VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lifecycle_transitions_workflow ON lifecycle_transitions(workflow_id);
CREATE INDEX idx_lifecycle_transitions_from ON lifecycle_transitions(from_state_id);
CREATE INDEX idx_lifecycle_transitions_to ON lifecycle_transitions(to_state_id);

-- ============================================
-- AUDIT LOG TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entity_type VARCHAR(255) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    action VARCHAR(50) NOT NULL,
    changes TEXT,
    changed_by UUID REFERENCES users(id),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_changed_at ON audit_logs(changed_at);
CREATE INDEX idx_audit_logs_changed_by ON audit_logs(changed_by);

-- ============================================
-- INSERT DEFAULT ROLES
-- ============================================
INSERT INTO roles (name, description) VALUES 
    ('ADMIN', 'System Administrator with full access'),
    ('MANAGER', 'Project Manager with management privileges'),
    ('ENGINEER', 'Design Engineer with view/edit access'),
    ('VIEWER', 'Read-only access to parts and changes')
ON CONFLICT DO NOTHING;

-- ============================================
-- INSERT DEFAULT WORKFLOWS
-- ============================================
INSERT INTO workflows (name, description) VALUES
    ('Standard Part Lifecycle', 'Standard lifecycle for parts'),
    ('Change Management', 'Change request workflow')
ON CONFLICT DO NOTHING;
