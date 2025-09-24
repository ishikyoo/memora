# Memora

Memora is a modern API designed for creating, managing, and distributing **in-memory resource packs** in Minecraft.  
Instead of relying solely on static files, Memora allows developers to dynamically generate and manipulate assets at runtime, making it easier to build flexible content pipelines, testing environments, and advanced modding workflows.

By exposing a clean and minimal API surface, Memora integrates seamlessly into existing Minecraft mod projects, giving you fine-grained control over resource pack composition without the overhead of manual file management. Whether you need to inject textures, models, language files, or any other resource, Memora provides the foundation for handling assets directly in memory.

### Asset generation supported:
* Raw (bytes and text).
* Pack metadata.
* Blockstate.

---

## 🛠️ Building from Source

Memora uses [Gradle](https://gradle.org/) with the [Fabric Loom](https://fabricmc.net/develop/) plugin to build and package the mod.  
The build system also integrates optional publishing tasks for Modrinth, CurseForge, and GitHub Releases, though these are only required in CI environments.

### Prerequisites
- [Java 21](https://adoptium.net/) (required for compilation and runtime)
- [Git](https://git-scm.com/) (used for versioning and changelog generation)
- A working internet connection (to fetch dependencies)

### Clone the Repository
```bash
git clone https://github.com/ishikyoo/memora.git
cd memora
```

### Build the Mod

To build a development JAR locally, run:
```bash
./gradlew build
```

The compiled JAR will be available under:

```
build/libs/
```

### Running in Development

You can run a Minecraft development environment with:

``` bash
./gradlew runClient
```

and a dedicated server environment with:

``` bash
./gradlew runServer
```

### Project Properties

Certain properties must be provided for the build (via gradle.properties or -P flags).
Key properties include:

* `mod_name` – Display name of the mod.
* `mod_id` – Internal mod ID.
* `mod_version` – Semantic version of the mod (e.g. `1.0.0`).
* `minecraft_version` – Target Minecraft version (e.g. `1.21.8`).
* `supported_minecraft_versions` – Comma-separated list of supported MC versions.
* `loader_version` – Fabric Loader version.
* `fabric_version` – Fabric API version.
* `publish_channel` – One of dev, snap, alpha, beta, pre, rc, or release channels.
* `publish_version` – Optional property representing the numeric version of the publish channel (e.g. `alpha.1`).

**⚠️ CI-only tasks such as changelog generation and artifact publishing will fail if executed outside of a CI environment. For local development you usually only need `./gradlew build`.**

---

## 📄 License

Memora is licensed under the [GNU Lesser General Public License v3.0 only (LGPL-3.0-only)](LICENSE).

This license allows you to use, modify, and redistribute the mod under LGPL terms. See the [`LICENSE`](LICENSE) file for full details.

## 🧾 Third-Party Notices

This mod’s build process uses the [git-changelog-gradle-plugin](https://github.com/tomasbjerre/git-changelog-gradle-plugin) ([Apache License 2.0](thirdparty/licenses/LICENSE-APACHE-2.0.txt)) to automatically generate changelogs and release notes. This plugin is a build-time tool and is not included in the distributed mod.

For more information, see the [LICENSE](LICENSE) and [NOTICE](thirdparty/NOTICE) files included in this repository.