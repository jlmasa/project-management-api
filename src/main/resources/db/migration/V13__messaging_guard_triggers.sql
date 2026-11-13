CREATE OR REPLACE FUNCTION guard_message_insert() RETURNS TRIGGER AS $$
DECLARE
    v_conversation_status VARCHAR(20);
    v_conversation_type VARCHAR(20);
    v_task_id UUID;
    v_task_status VARCHAR(20);
    v_is_participant BOOLEAN;
BEGIN
    SELECT conversation_status, conversation_type, task_id
        INTO v_conversation_status, v_conversation_type, v_task_id
        FROM conversation WHERE id = NEW.conversation_id;

    IF v_conversation_status <> 'ACTIVE' THEN
       RAISE EXCEPTION 'PMS_GUARD: cannot insert a message into a non-ACTIVE conversation (id=%)',
    NEW.conversation_id;
    END IF;

       SELECT EXISTS (
           SELECT 1 FROM conversation_participants
            WHERE conversation_id = NEW.conversation_id
                AND organization_membership_id = NEW.sender_membership_id
                AND removed_at IS NULL
       ) INTO v_is_participant;
        IF NOT v_is_participant THEN
           RAISE EXCEPTION 'PMS_GUARD: sender % is not an active participant of conversation %',
           NEW.sender_membership_id, NEW.conversation_id;
        END IF;

        IF v_conversation_type = 'TASK' THEN
           SELECT task_status INTO v_task_status FROM tasks WHERE id = v_task_id;
            IF v_task_status IN ('COMPLETED', 'CANCELLED') THEN
               RAISE EXCEPTION 'PMS_GUARD: cannot insert a message into a conversation for a closed task (task_id=%)', v_task_id;
            END IF;
        END IF;

        RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_guard_message_insert
    BEFORE INSERT ON messages
    FOR EACH ROW EXECUTE FUNCTION guard_message_insert();

CREATE OR REPLACE FUNCTION guard_message_update() RETURNS TRIGGER AS $$
DECLARE
    v_conversation_status VARCHAR(20);
BEGIN
    SELECT conversation_status INTO v_conversation_status FROM conversation WHERE id = OLD.conversation_id;
    IF v_conversation_status <> 'ACTIVE' THEN
       RAISE EXCEPTION 'PMS_GUARD: cannot modify a message in a non-ACTIVE conversation (id=%)',
        OLD.conversation_id;
       END IF;
       RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_guard_message_update
    BEFORE UPDATE ON messages
    FOR EACH ROW EXECUTE FUNCTION guard_message_update();

CREATE OR REPLACE FUNCTION guard_participant_insert() RETURNS TRIGGER AS $$
DECLARE
    v_conversation_type  VARCHAR(20);
    v_new_department UUID;
    v_mismatch_count INT;
BEGIN
    SELECT conversation_type INTO v_conversation_type FROM conversation WHERE id = NEW.conversation_id;
    IF v_conversation_type = 'TASK' THEN
       RETURN NEW;
    END IF;

    SELECT count(*) INTO v_mismatch_count
        FROM conversation_participants cp
        JOIN organization_memberships om ON om.id = cp.organization_membership_id
        WHERE cp.conversation_id = NEW.conversation_id
            AND cp.removed_at IS NULL
            AND (om.department_id IS DISTINCT FROM v_new_department);

    IF v_mismatch_count > 0 THEN
    RAISE EXCEPTION 'PMS_GUARD: participant % department does not match existing participants of conversation %',
            NEW.organization_membership_id, NEW.conversation_id;
    END IF;
    RETURN NEW;
END;

$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_guard_participant_insert
    BEFORE INSERT ON conversation_participants
    FOR EACH ROW EXECUTE FUNCTION guard_participant_insert();

CREATE OR REPLACE FUNCTION guard_conversation_tenant() RETURNS TRIGGER AS $$
DECLARE
       v_project_org UUID;
BEGIN
    IF NEW.project_id IS NOT NULL THEN
        SELECT organization_id INTO v_project_org FROM projects WHERE id = NEW.project_id;
        IF v_project_org <> NEW.organization_id THEN
            RAISE EXCEPTION 'PMS_GUARD: conversation organization_id does not match its project''s organization_id';
        END IF;
    END IF;
    RETURN NEW;
END;

$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_guard_conversation_tenant
    BEFORE INSERT OR UPDATE ON conversation
    FOR EACH ROW EXECUTE FUNCTION guard_conversation_tenant();
