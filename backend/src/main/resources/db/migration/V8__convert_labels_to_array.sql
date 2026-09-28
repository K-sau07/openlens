-- Convert labels from comma-separated text to a proper postgres text array.
--
-- This is a patch for databases created before V2 stored labels as text[]. On
-- a database that is already in the target state the guard matters: comparing
-- a text[] column against '' asks postgres to parse '' as an array literal and
-- fails with `malformed array literal: ""`, which aborts the whole migration.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'issues'
          AND column_name = 'labels'
          AND data_type <> 'ARRAY'
    ) THEN
        ALTER TABLE issues ADD COLUMN labels_arr text[];

        UPDATE issues
        SET labels_arr = string_to_array(labels, ',')
        WHERE labels IS NOT NULL AND labels <> '';

        ALTER TABLE issues DROP COLUMN labels;
        ALTER TABLE issues RENAME COLUMN labels_arr TO labels;
    END IF;
END $$;
