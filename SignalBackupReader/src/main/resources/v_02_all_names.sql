drop view if exists v_all_names;

create view v_all_names as
select recipient_id, title 'full_name' from groups
union
select _id, system_display_name from recipient where group_id IS NULL
union
select -1, '@@MYSELF@@';
