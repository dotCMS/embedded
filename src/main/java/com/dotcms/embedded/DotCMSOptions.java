package com.dotcms.embedded;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class DotCMSOptions extends OptionsBase {



    @Option(name = "host", abbrev = 'o', help = "The server host.", category = "startup", defaultValue = "localhost")
    public String host;

    @Option(name = "port", abbrev = 'p', help = "The server port to listen on.", category = "startup", defaultValue = "8080")
    public int port;

    @Option(name = "fileOrFolder", abbrev = 'f', help = ".war file or path to the exploded war folder.", category = "startup",
            defaultValue = "ROOT.war")
    public String fileOrFolder;

    @Option(name = "home", abbrev = 'h', help = "Sets the CATALINA_HOME", category = "startup", defaultValue = "catalina")
    public String home;



    public String getString(final String var, final String defaultVal) {
        return (System.getProperty(var) != null) ? System.getProperty(var)
                : (System.getenv(var) != null) ? System.getenv(var) : defaultVal;
    }

    public boolean getBoolean(final String var, final boolean defaultVal) {
        String val = (System.getProperty(var) != null) ? System.getProperty(var)
                : (System.getenv(var) != null) ? System.getenv(var) : String.valueOf(defaultVal);
        return Boolean.valueOf(val);
    }

}
