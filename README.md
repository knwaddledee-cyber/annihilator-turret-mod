# Annihilator Turret Mod

## Overview

This mod implements an **Improved Annihilator Energy Cannon** - a stationary turret with rapid-fire capabilities based on SuperbWarfare's advanced GunShootGoal system.

## Features

### Annihilator Turret Entity
- **Rapid-Fire System**: Continuous automatic firing with configurable fire rate (Rounds Per Second)
- **Energy-Based Ammo**: Requires energy to fire, automatically regenerates
- **Advanced Targeting**: Precise targeting system with spread mechanics
- **SuperbWarfare Integration**: Uses the proven rapid-fire logic from SuperbWarfare's GunShootGoal

### Technical Implementation

The `AnnihilatorTurretEntity` is built on the following architecture:

#### Rapid-Fire Mechanism
Adapted directly from SuperbWarfare's `GunShootGoal.java`:
```
RPM to RPS conversion: rps = RPM / 60
Cooldown calculation: cooldown_ms = 1000 / rps
Fire condition: if (current_time - last_fire_time >= cooldown_ms) then fire()
```

#### Energy System
- Energy regeneration: +1 per tick (20 ticks/second = 1 per tick)
- Configurable energy per shot (default: 5)
- Max energy capacity: 100

#### Configuration Properties
- `fireRate`: Rounds per second (e.g., 10.0 = 10 shots/sec)
- `spreadAmount`: Bullet spread in degrees (e.g., 2.0f)
- `energyPerShot`: Energy cost per shot (default: 5)
- `maxEnergy`: Maximum energy capacity (default: 100)

## Installation

1. Place the mod JAR in your mods folder
2. The Annihilator Turret entity will be automatically registered
3. Entity ID: `examplemod:annihilator_turret`

## Usage

### Spawning a Turret
```
/summon examplemod:annihilator_turret ~ ~ ~
```

### Controlling via Commands
- Set target: Use NBT data to modify `TargetX`, `TargetY`, `TargetZ`
- Enable firing: Set `isFiring` to true
- Adjust fire rate: Modify the entity's `fireRate` property

### Example NBT Command
```
/summon examplemod:annihilator_turret ~ ~ ~ {TargetX:100d,TargetY:64d,TargetZ:100d,isFiring:1b}
```

## Source Code Reference

### Key Classes
- `AnnihilatorTurretEntity.kt`: Main turret entity implementation
- `ModEntities.kt`: Entity type registry
- Original reference: SuperbWarfare's `GunShootGoal.java`

### Rapid-Fire Algorithm
The firing system uses a time-based cooldown mechanism that ensures:
1. Consistent fire rate regardless of server tick rate
2. Low-framerate compensation (multiple shots per tick if needed)
3. Smooth energy consumption matching fire rate

## Future Improvements

- [ ] Energy beam/projectile visuals
- [ ] Particle effects on firing
- [ ] Sound effects
- [ ] AI targeting system
- [ ] Ammunition types with different spread/damage
- [ ] Block-based turret placement
- [ ] Network synchronization for multiplayer

## Credits

- **Rapid-Fire System**: Based on SuperbWarfare's proven `GunShootGoal` implementation
- **Mod Framework**: Kotlin Forge
- **Minecraft Version**: 1.20.1

## License

This mod is provided as-is for educational and gameplay purposes.
