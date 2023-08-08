# SpawnerMeta

This plugin implements customizable and upgradeable spawners.

## Using with Maven

```
<dependency>
    <groupId>mc.rellox</groupId>
    <artifactId>SpawnerMeta</artifactId>
    <version>20.2</version>
    <scope>provided</scope>
</dependency>
```

## API usage

Getting plugin API:
```java
SpawnerMeta sm = (SpawnerMeta) Bukkit.getPluginManager().getPlugin("SpawnerMeta");
```
Getting API instance:
```java
APIInstance api = sm.getAPI();
```
Registering event listeners:
```java
api.register(SpawnerPlaceEvent.class, event -> {
    // do stuff here
});
```
## Contacting me

Discord server: https://discord.com/invite/NU9aVbb79d
