alter table raw_fetch_meta
  add column if not exists workspace_id uuid null references workspaces(id) on delete set null;

create index if not exists idx_raw_fetch_meta_ws_market_kind_time
  on raw_fetch_meta (workspace_id, marketplace_id, kind, collected_at desc);