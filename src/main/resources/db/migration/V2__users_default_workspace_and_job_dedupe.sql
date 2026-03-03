alter table users
  add column if not exists default_workspace_id uuid null;

do $$
begin
  if not exists (
    select 1
    from pg_constraint
    where conname = 'fk_users_default_workspace'
  ) then
    alter table users
      add constraint fk_users_default_workspace
      foreign key (default_workspace_id) references workspaces(id) on delete set null;
  end if;
end $$;

alter table job_queue
  add column if not exists dedupe_key text null;

do $$
begin
  if not exists (
    select 1
    from pg_constraint
    where conname = 'uq_job_queue_dedupe'
  ) then
    alter table job_queue
      add constraint uq_job_queue_dedupe
      unique (workspace_id, job_type, dedupe_key);
  end if;
end $$;