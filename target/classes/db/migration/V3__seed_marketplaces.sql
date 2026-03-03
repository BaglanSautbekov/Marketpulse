insert into marketplaces(code, name, is_enabled)
values
  ('KASPI_KZ', 'Kaspi.kz Shop', true),
  ('WB_KZ', 'Wildberries KZ', true),
  ('OZON_KZ', 'Ozon KZ', true),
  ('HALYK_KZ', 'Halyk Market KZ', true),
  ('FORTE_KZ', 'Forte Market KZ', true)
on conflict (code) do nothing;
