# MarketPulse data sources and definitions

## What we measure

Top
- top is a ranked list of products from public category pages and or search results
- source is category listing pages and search listing pages
- top is always tied to marketplace, category or query, timestamp, and rank position

Trend
- trend is not sales in units
- trend is an index built from proxy signals
    - rank change in category and or search
    - reviews count change (review velocity)
    - price and discount change
    - stock status stability
- every trend output must include confidence and evidence

Confidence
- 0..1 number based on data coverage and stability
    - how many snapshots exist in the window
    - how fresh data is
    - how stable rank sources are

Evidence
- each claim references snapshot ids and a time window
- no claim without evidence

## Source types

Category listing
- input: marketplace + category_key + page + city_code(optional)
- output: list of products with rank positions

Search listing
- input: marketplace + query_text + page + city_code(optional)
- output: list of products with rank positions

Product page
- input: marketplace + product_key + city_code(optional)
- output: price, discount, stock status, rating, reviews_count, seller (if visible), title, attributes

## Kaspi KZ specifics

City param
- Kaspi uses query param c for city/region
    - Almaty: c=750000000
    - Astana: c=710000000
- collector must support passing cityCode in payload and must upsert ?c= into the final URL
- raw_fetch_meta.source_url should store the final URL used for collection (with c and page applied)

## Frequencies for MVP

Category listing: 1 time per day
Search listing: 1 time per day
Watchlist product pages: 1 time per day

## Hard constraints

- do not state or imply exact competitor sales without official seller data
- always show confidence and evidence in UI and exports

## Stage 0 outcomes

- list of marketplaces for MVP
- list of target categories and queries for MVP
- mapping of required fields per marketplace and per source type