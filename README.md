# SpawnerMeta

This plugin implements fully customizable and upgradeable spawners.

## Using with Maven

```
<dependency>
  <groupId>mc.rellox</groupId>
  <artifactId>SpawnerMeta</artifactId>
  <version>25.1</version>
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
APIInstance api = sm.getAPI();
```
Getting API directly:
```java
APIInstance api = APIInstance.api();
```
Registering event listeners:
```java
api.register(SpawnerPlaceEvent.class, event -> {
    // do stuff here
});
```
## Contacting me

Discord server: https://discord.com/invite/NU9aVbb79d
