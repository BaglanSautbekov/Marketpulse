create extension if not exists pgcrypto;
create extension if not exists citext;

create table if not exists users (
  id uuid primary key default gen_random_uuid(),
  email citext not null unique,
  password_hash text not null,
  is_active boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists workspaces (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  plan text not null default 'FREE',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists workspace_users (
  workspace_id uuid not null references workspaces(id) on delete cascade,
  user_id uuid not null references users(id) on delete cascade,
  role text not null,
  created_at timestamptz not null default now(),
  primary key (workspace_id, user_id),
  constraint workspace_users_role_chk check (role in ('OWNER','ADMIN','MEMBER'))
);

create table if not exists marketplaces (
  id uuid primary key default gen_random_uuid(),
  code text not null unique,
  name text not null,
  is_enabled boolean not null default true,
  created_at timestamptz not null default now()
);

create table if not exists categories (
  id uuid primary key default gen_random_uuid(),
  marketplace_id uuid not null references marketplaces(id) on delete cascade,
  category_key text not null,
  name text not null,
  parent_key text null,
  created_at timestamptz not null default now(),
  unique (marketplace_id, category_key)
);

create table if not exists search_queries (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid not null references workspaces(id) on delete cascade,
  marketplace_id uuid not null references marketplaces(id) on delete cascade,
  query_text text not null,
  is_enabled boolean not null default true,
  created_at timestamptz not null default now()
);

create table if not exists watchlists (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid not null references workspaces(id) on delete cascade,
  name text not null,
  created_at timestamptz not null default now()
);

create table if not exists watchlist_items (
  id uuid primary key default gen_random_uuid(),
  watchlist_id uuid not null references watchlists(id) on delete cascade,
  marketplace_id uuid not null references marketplaces(id) on delete cascade,
  external_product_key text not null,
  canonical_product_id uuid null,
  created_at timestamptz not null default now(),
  unique (watchlist_id, marketplace_id, external_product_key)
);

create table if not exists alert_rules (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid not null references workspaces(id) on delete cascade,
  rule_type text not null,
  params jsonb not null default '{}'::jsonb,
  is_enabled boolean not null default true,
  created_at timestamptz not null default now()
);

create table if not exists raw_fetch_meta (
  id uuid primary key default gen_random_uuid(),
  marketplace_id uuid not null references marketplaces(id) on delete cascade,
  kind text not null,
  source_url text not null,
  storage_key text null,
  http_status int not null,
  checksum text not null,
  collected_at timestamptz not null default now(),
  parser_hint text null,
  constraint raw_fetch_meta_kind_chk check (kind in ('CATEGORY','SEARCH','PRODUCT'))
);

create table if not exists job_queue (
  id uuid primary key default gen_random_uuid(),
  workspace_id uuid null references workspaces(id) on delete set null,
  marketplace_id uuid null references marketplaces(id) on delete set null,
  job_type text not null,
  status text not null,
  run_at timestamptz not null default now(),
  payload jsonb not null default '{}'::jsonb,
  attempts int not null default 0,
  max_attempts int not null default 5,
  last_error text null,
  created_at timestamptz not null default now(),
  started_at timestamptz null,
  finished_at timestamptz null,
  constraint job_queue_status_chk check (status in ('QUEUED','RUNNING','SUCCEEDED','FAILED','DEAD')),
  constraint job_queue_type_chk check (job_type in ('COLLECT_CATEGORY','COLLECT_SEARCH','COLLECT_PRODUCT','RUN_SCORING','RUN_ALERTS','AI_JOB'))
);

create index if not exists idx_job_queue_status_runat on job_queue (status, run_at);
create index if not exists idx_raw_fetch_market_kind_time on raw_fetch_meta (marketplace_id, kind, collected_at desc);
create index if not exists idx_watchlist_items_watchlist on watchlist_items (watchlist_id);
