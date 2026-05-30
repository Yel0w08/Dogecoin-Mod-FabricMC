# Dogecoin Mod

[![Fabric](https://img.shields.io/badge/Mod%20Loader-Fabric-dbd0b4?style=flat-square)](https://fabricmc.net)
[![MC Versions](https://img.shields.io/badge/MC-1.20.1%20%7C%201.21.1%20%7C%201.21.4-4aa95c?style=flat-square)](#supported-versions)
[![License](https://img.shields.io/badge/License-MIT-9154c2?style=flat-square)](LICENSE)

A lightweight Fabric mod that adds **Dogecoin** to Minecraft — craft it by feeding gold to a wolf, store it in your wallet, and use an ATM-style GUI to manage your crypto.

---

## Features

- **Dogecoin item** — 2D flat item model, smeltable back to a gold ingot
- **Wolf crafting** — right-click a wolf with a gold ingot to receive a Dogecoin (the dog "mines" it for you)
- **Wallet system** — persistent per-player wallet stored in `config/dogecoin_wallets.json`
- **ATM GUI** — `/wallet open` opens a custom ATM screen (no vanilla container textures)
  - Deposit / Withdraw / Deposit All / Withdraw All
  - Quick amount buttons (1, 10, 64, All)
  - Custom amount input field
  - Real-time balance display
- **Commands** — `/wallet`, `/wallet open`, `/wallet deposit <amount>`, `/wallet withdraw <amount>`
- **Configurable** — `config/dogecoin.json` with `walletEnabled` and `startingBalance`

---

## Installation

1. Install **Fabric Loader** for your Minecraft version
2. Download the **Fabric API** jar for your MC version
3. Download the **Dogecoin Mod** jar from [Releases](https://github.com/yourname/dogecoin-mod/releases)
4. Place all jars in your `mods/` folder
5. Launch the game

**Requirements:**
- Minecraft 1.20.1, 1.21.1, or 1.21.4
- Fabric Loader ≥0.16
- Fabric API ≥0.92.0 (1.20.1) / ≥0.100.0 (1.21.x)

---

## Usage

### Dogecoin Item

| Action | Result |
|--------|--------|
| Right-click a wolf with a **gold ingot** | Gold consumed → 1 Dogecoin dropped/added to inventory |
| Smelt Dogecoin in a furnace | 1 Dogecoin → 1 gold ingot (200 ticks) |
| Blast Dogecoin in a blast furnace | 1 Dogecoin → 1 gold ingot (100 ticks) |

### Wallet Commands

| Command | Description |
|---------|-------------|
| `/wallet` | Show your wallet balance and inventory count |
| `/wallet open` | Open the ATM GUI |
| `/wallet deposit <amount>` | Deposit Dogecoins from inventory to wallet |
| `/wallet withdraw <amount>` | Withdraw Dogecoins from wallet to inventory |

### ATM GUI

Run `/wallet open` to open the ATM. The screen shows:
- **Balance** — your wallet balance in DOGE
- **Inventory** — how many Dogecoins you're carrying
- **Amount field** — type a custom amount
- Quick-set buttons: 1, 10, 64, All
- **DEPOSIT** — move Dogecoins from inventory → wallet
- **WITHDRAW** — move Dogecoins from wallet → inventory
- **DEPOSIT ALL** — move all carried Dogecoins to wallet
- **WITHDRAW ALL** — move entire wallet balance to inventory
- **CLOSE** — close the ATM

All operations update the screen in real time via Fabric CustomPayload networking.

---

## Configuration

Config file: `config/dogecoin.json`

```json
{
  "walletEnabled": true,
  "startingBalance": 0
}
```

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `walletEnabled` | boolean | `true` | Enable/disable the wallet system |
| `startingBalance` | int | `0` | Initial wallet balance for new players |

Wallet data is stored in `config/dogecoin_wallets.json` (per-player UUID → balance).

---

## Building from Source

**Prerequisites:** JDK 21+

```bash
# Clone the repository
git clone https://github.com/yourname/dogecoin-mod.git
cd dogecoin-mod

# Build for all supported versions
./gradlew buildAll

# Build for a specific version
./gradlew build -PmcVersion=1.21.1

# Output jars in build/libs/
```

> [!NOTE]
> Multi-version builds download separate Minecraft jars for each target version.
> The first build will take longer.

---

## Supported Versions

| MC Version | Status | Notes |
|------------|--------|-------|
| 1.20.1 | ✅ | Old networking API (`PacketByteBuf`) |
| 1.21.1 | ✅ | Current target (CustomPayload API) |
| 1.21.4 | ✅ | Latest 1.21.x (CustomPayload API) |

Support for additional versions can be added by creating a version-specific source directory in `src/versioned/`.

---

## Project Structure

```
src/
├── main/java/          # Shared code (items, commands, config, GUI)
│   └── com/pikmintea/dogecoin/
└── versioned/          # Version-specific implementations
    ├── 1.20.1/         # Fabric networking with PacketByteBuf
    ├── 1.21.1/         # Fabric CustomPayload networking
    └── 1.21.4/         # Fabric CustomPayload networking
```

---

## License

[MIT](LICENSE) — feel free to use, modify, and distribute.
