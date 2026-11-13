CREATE OR REPLACE FUNCTION reject_time_adjustment_mutation() RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'PMS_GUARD: time adjustments rows are append-only; % is not permitted', TG_OP;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_reject_time_adjustment_update
    BEFORE UPDATE ON time_adjustments
    FOR EACH ROW EXECUTE FUNCTION reject_time_adjustment_mutation();

CREATE TRIGGER trg_reject_time_adjustment_delete
    BEFORE DELETE ON time_adjustments
    FOR EACH ROW EXECUTE FUNCTION reject_time_adjustment_mutation();