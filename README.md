# ClearLag

A plugin to help manage lag on BTA servers.

Its just removes types of entities, nothing too fancy.

### Usage

#### ClearLag Command

Aliases: `lag`, `clearlag`, `cl`

`/lag [range]`

`/lag [chunkRange]`

`/lag [lagType] [chunkRange]`

`/lag [targetPlayer] [lagType] [chunkRange]`

#### TPS Command

`/tps`

### Specifics
The `chunkRange` defines a square area of chunks, centered on the specified player.

This should not remove named entities, tamed dogs, or saddled pigs.

#### Lag types:
- mobs
- items
- all

#### Default values:
- chunkRange = 0
- lagType = all
