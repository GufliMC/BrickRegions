# ElderRegions

Region system including:
* Database peristence
* Wand selection
* TODO - Placeholders
* Capture system

## Commands

| Command                                              | Permission                            |
|------------------------------------------------------|---------------------------------------|
| /er select wand                                      | elderregions.select.wand              |
| /er select reset                                     | elderregions.select.reset             |
| /er select type [cuboid/poly]                        | elderregions.select.type              |
| /er select undo                                      | elderregions.select.undo              |
| /er select expand                                    | elderregions.select.expand            |
| /er region list                                      | elderregions.region.list              | 
| /er region create capture (name)                     | elderregion.region.create.capture     |
| /er region create outpost (name)                     | elderregion.region.create.outpost     |
| /er region create capturezone (name) (captureregion) | elderregion.region.create.capturezone |
| /er region delete (region)                           | elderregions.region.delete            |
| /er region edit display (region) (display)           | elderregions.region.edit.display      |


## API

### Maven
```
repositories {
    maven { url "https://repo.jorisg.com/eldershore" }
}

dependencies {
    implementation 'com.gufli.eldershore.regions:api:1.0-SNAPSHOT'
}
```

### Usage

Check the [javadocs](https://eldershore.github.io/ElderRegions/)