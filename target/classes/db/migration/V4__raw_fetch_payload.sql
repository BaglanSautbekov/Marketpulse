create table if not exists raw_fetch_payload (
  id uuid primary key references raw_fetch_meta(id) on delete cascade,
  payload bytea not null,
  content_type text null,
  content_encoding text null,
  content_length int not null
);

create index if not exists idx_raw_fetch_meta_market_kind_time on raw_fetch_meta (marketplace_id, kind, collected_at desc);