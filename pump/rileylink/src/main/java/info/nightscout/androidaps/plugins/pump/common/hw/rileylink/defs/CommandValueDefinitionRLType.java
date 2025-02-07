package info.nightscout.androidaps.plugins.pump.common.hw.rileylink.defs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by andy on 4/5/19.
 */

public enum CommandValueDefinitionRLType implements CommandValueDefinitionType {
    Name, //
    Firmware, //
    SignalStrength, //
    ConnectionState, //
    Frequency, //
    ;

    @NonNull @Override
    public String getName() {
        return this.name();
    }


    @Override
    public String getDescription() {
        return null;
    }


    @Nullable @Override
    public String commandAction() {
        return null;
    }

}
