# BrickRegions

A lightweight system for defining and protecting regions.

## Commands

### Selection
| Command                                              | Pbrmission                            |
|------------------------------------------------------|---------------------------------------|
| /br select wand                                      | brickregions.select.wand              |
| /br select clear                                     | brickregions.select.reset             |
| /br select [cuboid/poly]                             | brickregions.select.type              |
| /br select undo                                      | brickregions.select.undo              |
| /br select expand                                    | brickregions.select.expand            |

### Regions
| Command                                      | Pbrmission                 |
|----------------------------------------------|----------------------------|
| /br region list                              | brickregions.region.list   | 
| /br region create (name)                     | brickregions.region.create |
| /br region delete (region)                   | brickregions.region.delete |

### Region rules
| Command                        | Pbrmission                     |
|--------------------------------|--------------------------------|
| /br region rules list (region) | brickregions.region.rules.list |
| /br region rules add (region) (status) (target) (type) | brickregions.region.rules.add |
| /br region rules remove (region) (rule) | brickregions.region.rules.remove |

## Protection

A region can be protected by applying rules. Rules consist of 3 values:
- A status: ALLOW or DENY
- A target, to whom the rule applies
- A type, defines what is protected

## API

### Maven
```
repositories {
    maven { url "https://repo.jorisg.com/snapshots" }
}

dependencies {
    implementation 'com.guflimc.brick.regions:api:1.0-SNAPSHOT'
}
```

### Usage

Check the [javadocs](https://guflimc.github.io/BrickRegions/)