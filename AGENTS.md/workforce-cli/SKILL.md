---
name: workforce-cli
version: 0.45.0
description: >
  Agent-friendly Workforce CLI for people, org charts, and Alliance/Fleet/Squad structure. Use when user mentions: workforce, people, org chart, org structure, alliance, fleet, squad.
metadata:
  generated: true
  requires:
    bins: ["workforce"]
---

# Agent-friendly Workforce CLI for people, org charts, and Alliance/Fleet/Squad structure

## Quick Reference

```bash
# Auth
workforce --agent auth status
workforce --agent auth status --offline
workforce auth login

# Person Search
workforce --agent search query --query "John Smith"
workforce --agent search query --query "Ad Decisioning"
workforce --agent person me
workforce --agent person get --uuid <uuid>

# Org Chart
workforce --agent org chart
workforce --agent org chart --person-id top
workforce --agent org chart --person-id <uuid>

# Portfolios & Alliances
workforce --agent org portfolios
workforce --agent org alliances
workforce --agent org alliances --portfolio-id <id>
workforce --agent org alliance --id <uuid>

# Fleets & Squads
workforce --agent org fleet --id <uuid>

# Reports
workforce --agent report download --type org --output report.xlsx
workforce --agent report download --output delivery.xlsx

```

## Authentication

- Method: SAML SSO
- Setup: workforce auth login (browser SAML login via Playwright; session cached at ~/.cliuniverse/workforce-session.json)
- Provision the managed browser with `aix setup`; do NOT install Playwright by hand, because direct browser downloads are blocked on locked-down corporate networks.
- `auth status` reports `authenticated` (a usable credential exists), `session_present` (something is stored at all -- true even when expired), and `verified` (the API gave a verdict on this session).
- `--offline` makes no network call: it skips the /person/me probe, so `verified` is ALWAYS false under it. Do not read `authenticated:true` from `--offline` as proof the session works.
- Offline verdicts: an expired cached session reports `authenticated:false` with `session_present:true` (expiry is readable locally); an unexpired one is optimistic -- `authenticated:true` even though the server may still reject it.
- Prefer `--offline` for polling or a fast signed-in/signed-out read; use the default probe for a real verdict -- it also separates a rejected session (`verified:true`) from an unreachable API, and only a transport failure yields VPN advice. Flag details: `workforce agent schema`.

## Workflows

### Find a Person

```bash
workforce --agent search query --query "<name>"
workforce --agent person get --uuid <uuid>
```

### Explore Org Structure

```bash
workforce --agent org portfolios
workforce --agent org alliances --portfolio-id <id>
workforce --agent org alliance --id <uuid>
workforce --agent org fleet --id <uuid>
```

### Check Org Chart

```bash
workforce --agent search query --query "<name>"
workforce --agent org chart --person-id <uuid>
```

## Tips

- Always use --agent for structured JSON output
- Use search first to find person UUIDs, then get details with person get
- Org hierarchy: Service Portfolio > Alliance > Fleet > Squad
- Use org chart --person-id top to see the full org tree from the top
- Use --fields to select only needed fields for token efficiency
- Run `workforce agent schema` for the complete command/flag reference
- In agent mode a `--help` that NAMES a command returns just that command's schema entry (`workforce --agent auth status --help`); a bare `workforce --agent --help` returns the whole schema

## Common Mistakes

| Pattern | Why It's Wrong |
|---------|---------------|
| guess UUIDs | Use search or org chart to discover valid IDs first |
| download reports without specifying --output | The --output flag is required for report download |
| people search --query ... (or workforce --agent schema / --agent api ...) | There is no `people` subcommand -- person search is `search query --query "..."`. The full command/flag reference is the separate subcommand `workforce agent schema` (no leading --agent) |
| treat `--offline` authenticated:true as proof the session works | `--offline` runs no probe, so `verified` is always false; an unexpired session the server would reject still reads authenticated. Use the default `auth status` when you need a verdict |

---

## Full Command Reference

*Auto-generated from CLI source. Do not edit manually.*

### `workforce search query`

Search people, alliances, fleets, and squads

- `--query`: Search term (name, email, team, or keyword) **(required)**

### `workforce person me`

Get current user's profile

### `workforce person get`

Get person details by UUID

- `--uuid`: Person UUID (from search results or org chart) **(required)**

### `workforce org chart`

View org chart centered on a person, self, or top of org

- `--person-id`: Person ID to center on. Omit for current user. Use "top" for top of org

### `workforce org portfolios`

List all Service Portfolios (top-level groupings)

### `workforce org alliances`

List Alliances (optionally filtered by Service Portfolio)

- `--portfolio-id`: Filter to a specific Service Portfolio by ID

### `workforce org alliance`

Get Alliance details with nested Fleets

- `--id`: Alliance UUID **(required)**

### `workforce org fleet`

Get Fleet details with nested Squads and members

- `--id`: Fleet UUID **(required)**

### `workforce org squad`

Get Squad details (metadata; the member roster lives on the parent Fleet)

- `--id`: Squad UUID **(required)**

### `workforce auth login`

Log in to Workforce via browser SAML SSO

### `workforce auth logout`

Clear stored Workforce session

### `workforce auth status`

Show current authentication status

- `--offline`: Skip the /person/me network probe and report the offline sign-in state (does a usable cached session exist) with no network call. An expired session is reported as authenticated:false — its expiry is readable locally. An unexpired one is optimistic: reported as authenticated even though the server might reject it — that cannot be known without the network — and always sets verified:false. Use for a fast, side-effect-free "signed in vs signed out" read

### `workforce report download`

Download manager report

- `--type`: Report type: org or org-delivery
- `--output`: Output file path **(required)**

### Global Flags

| Flag | Description | Default |
|------|-------------|---------|
| `--agent` | Force agent mode (structured JSON, auto-approve) | - |
| `--format` | Output format: json, json-pretty, table, csv | - |
| `--fields` | Select specific output fields (comma-separated) | - |
| `--summary` | Summary mode (counts and statuses only). Alias: --brief | - |
| `--concise` | Concise mode: all fields except body/description/content | - |
| `--full` | Preserve full API payloads, including null-valued fields | - |
| `--limit` | Max items to return | 25 |
| `--offset` | Pagination offset | 0 |
| `--no-cache` | Bypass file cache | - |
| `--no-proxy` | Bypass connection proxy | - |
| `--token` | Override auth cookie header. On argv, so visible in `ps` and shell history; prefer $WORKFORCE_TOKEN or `workforce auth login` | - |
| `--server` | Override Workforce base URL | - |
| `--timeout` | Request timeout in milliseconds | 30000 |
| `--no-rate-limit` | Disable client-side rate limiting (for deployed agents/automation) | - |
| `--debug` | Print debug info to stderr | - |
| `--dry-run` | Dry run: print the URL that would be requested, then exit (for testing) | - |
