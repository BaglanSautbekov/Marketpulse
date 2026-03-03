package com.marketpulse.api;

import com.marketpulse.store.MarketplaceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/marketplaces")
public class MarketplaceController {

  private final MarketplaceRepository marketplaces;

  public MarketplaceController(MarketplaceRepository marketplaces) {
    this.marketplaces = marketplaces;
  }

  @GetMapping
  public List<MarketplaceItem> list() {
    return marketplaces.findAll().stream()
        .filter(m -> m.isEnabled())
        .map(m -> new MarketplaceItem(m.getId(), m.getCode(), m.getName()))
        .toList();
  }

  public record MarketplaceItem(UUID id, String code, String name) {}
}
