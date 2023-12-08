# SpawnerMeta

This plugin implements fully customizable and upgradeable spawners.

## Using with Maven

```
<dependency>
  <groupId>com.github.OfficialRell</groupId>
  <artifactId>SpawnerMeta</artifactId>
  <version>22.3</version>
  <scope>provided</scope>
</dependency>

<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
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
