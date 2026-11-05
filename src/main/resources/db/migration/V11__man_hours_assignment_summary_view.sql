CREATE VIEW man_hours_assignment_summary AS
SELECT
    ta.id AS task_assignment_id,
    ta.task_id,
    ta.assigned_to_membership_id,
    ta.department_id_at_assignment,
    COALESCE(ws_totals.worked_seconds, 0) AS worked_seconds,
    COALESCE(adj_totals.adjustment_seconds, 0) AS adjustment_seconds,
    COALESCE(ws_totals.worked_seconds, 0) + COALESCE(adj_totals.adjustment_seconds, 0)
        AS total_man_seconds
FROM task_assignments ta
         LEFT JOIN (
    SELECT task_assignment_id, SUM(duration_seconds) AS worked_seconds
    FROM work_sessions
    WHERE session_status = 'ENDED'
    GROUP BY task_assignment_id
) ws_totals ON ws_totals.task_assignment_id = ta.id
         LEFT JOIN (
    SELECT ws.task_assignment_id, SUM(adj.adjustment_seconds) AS adjustment_seconds
    FROM time_adjustments adj
             JOIN work_sessions ws ON ws.id = adj.work_session_id
    GROUP BY ws.task_assignment_id
) adj_totals ON adj_totals.task_assignment_id = ta.id;
