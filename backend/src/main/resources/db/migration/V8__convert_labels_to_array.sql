-- convert labels from comma-separated text to proper postgres text array
-- existing data is migrated by splitting on comma

alter table issues add column labels_arr text[];

update issues set labels_arr = string_to_array(labels, ',')
    where labels is not null and labels != '';

alter table issues drop column labels;
alter table issues rename column labels_arr to labels;
