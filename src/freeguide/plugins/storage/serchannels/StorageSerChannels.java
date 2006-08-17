package freeguide.plugins.storage.serchannels;

import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.IModuleStorage;

/**
 * Storage for store data in ser files splitted by channels only.
 * 
 * @author Alex Buloichik
 */
public class StorageSerChannels extends BaseModule implements IModuleStorage {
    public void store(final TVData data) {
    }

    public void cleanup() {
    }

    public Object getConfig() {
        return null;
    }

    public Info getInfo() {
        return null;
    }

    public TVProgramme findEarliest(long minDate, EarliestCheckAllow check) throws Exception {
        return null;
    }

    public TVData get(Info loadInfo) throws Exception {
        return null;
    }
}
