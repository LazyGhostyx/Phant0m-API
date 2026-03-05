package xyz.lazyghosty.phant0m.server;

import frb.phant0m.server.util.Logger;

public abstract class ConfigPackageEntry {

    protected static final Logger LOGGER = new Logger("ConfigPackageEntry");

    public ConfigPackageEntry() {
    }

    public abstract boolean isAllowed();

    public abstract boolean isDenied();
}
