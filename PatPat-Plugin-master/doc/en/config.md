# Configuration Documentation

This configuration file defines two main aspects:
1. **List Mode (listMode)** – determines how filtering or list application works (e.g., allowlist/blocklist).
2. **Rate Limiting (rateLimit)** – configures request/action throttling parameters, including token limits and replenishment.

---

## 1. List Mode (listMode)

The `listMode` parameter controls filtering behavior based on the `player-list.json`. Valid values:

- **DISABLED**: All players can pat.
- **WHITELIST**: Only players whose UUIDs are explicitly listed can pat.
- **BLACKLIST**: Players whose UUIDs are **not** listed can pat.

---

## 2. Rate Limiting (rateLimit)

The `rateLimit` parameter configures patting throttling for players. It includes the following fields:

- **enabled** (`boolean`):
  - Enables or disables rate limiting. If set to `false`, no restrictions are applied.

- **tokenLimit** (`number`):
  - Maximum number of tokens available to a player. These tokens represent the number of allowed pats.

- **tokenIncrement** (`number`):
  - Number of tokens replenished at a specified interval. This determines how many tokens are added to the player's current count.

- **tokenIncrementInterval** (`string`):
  - Time interval at which tokens are replenished. Examples: `"1sec", "2min", "1hour", "1day"`.

- **permissionBypass** (`string`):
  - Permission node that allows a player to bypass rate limiting. Players with this permission are exempt from restrictions.

Example configuration:
```json
{
  "rateLimit": {
    "enabled": false,
    "tokenLimit": 20,
    "tokenIncrement": 1,
    "tokenIncrementInterval": "1sec",
    "permissionBypass": "patpat.ratelimit.bypass"
  }
}
```