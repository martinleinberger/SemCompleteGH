/**
    Read configuration from a file. <b>The existing configuration is
    not cleared nor reset.</b> If you require a different behavior,
    then call {@link  LogManager#resetConfiguration
    resetConfiguration} method before calling
    <code>doConfigure</code>.

    <p>The configuration file consists of statements in the format
    <code>key=value</code>. The syntax of different configuration
    elements are discussed below.

    <h3>Repository-wide threshold</h3>

    <p>The repository-wide threshold filters logging requests by level
    regardless of logger. The syntax is:

    <pre>
    log4j.threshold=[level]
    </pre>

    <p>The level value can consist of the string values OFF, FATAL,
    ERROR, WARN, INFO, DEBUG, ALL or a <em>custom level</em> value. A
    custom level value can be specified in the form
    level#classname. By default the repository-wide threshold is set
    to the lowest possible value, namely the level <code>ALL</code>.
    </p>


    <h3>Appender configuration</h3>

    <p>Appender configuration syntax is:
    <pre>
    # For appender named <i>appenderName</i>, set its class.
    # Note: The appender name can contain dots.
    log4j.appender.appenderName=fully.qualified.name.of.appender.class

    # Set appender specific options.
    log4j.appender.appenderName.option1=value1
    ...
    log4j.appender.appenderName.optionN=valueN
    </pre>

    For each named appender you can configure its {@link Layout}. The
    syntax for configuring an appender's layout is:
    <pre>
    log4j.appender.appenderName.layout=fully.qualified.name.of.layout.class
    log4j.appender.appenderName.layout.option1=value1
    ....
    log4j.appender.appenderName.layout.optionN=valueN
    </pre>

    <h3>Configuring loggers</h3>

    <p>The syntax for configuring the root logger is:
    <pre>
      log4j.rootLogger=[level], appenderName, appenderName, ...
    </pre>

    <p>This syntax means that an optional <em>level</em> can be
    supplied followed by appender names separated by commas.

    <p>The level value can consist of the string values OFF, FATAL,
    ERROR, WARN, INFO, DEBUG, ALL or a <em>custom level</em> value. A
    custom level value can be specified in the form
    <code>level#classname</code>.

    <p>If a level value is specified, then the root level is set
    to the corresponding level.  If no level value is specified,
    then the root level remains untouched.

    <p>The root logger can be assigned multiple appenders.

    <p>Each <i>appenderName</i> (separated by commas) will be added to
    the root logger. The named appender is defined using the
    appender syntax defined above.

    <p>For non-root categories the syntax is almost the same:
    <pre>
    log4j.logger.logger_name=[level|INHERITED|NULL], appenderName, appenderName, ...
    </pre>

    <p>The meaning of the optional level value is discussed above
    in relation to the root logger. In addition however, the value
    INHERITED can be specified meaning that the named logger should
    inherit its level from the logger hierarchy.

    <p>If no level value is supplied, then the level of the
    named logger remains untouched.

    <p>By default categories inherit their level from the
    hierarchy. However, if you set the level of a logger and later
    decide that that logger should inherit its level, then you should
    specify INHERITED as the value for the level value. NULL is a
    synonym for INHERITED.

    <p>Similar to the root logger syntax, each <i>appenderName</i>
    (separated by commas) will be attached to the named logger.

    <p>See the <a href="../../../../manual.html#additivity">appender
    additivity rule</a> in the user manual for the meaning of the
    <code>additivity</code> flag.

    <h3>ObjectRenderers</h3>

    You can customize the way message objects of a given type are
    converted to String before being logged. This is done by
    specifying an {@link org.apache.log4j.or.ObjectRenderer ObjectRenderer}
    for the object type would like to customize.

    <p>The syntax is:

    <pre>
    log4j.renderer.fully.qualified.name.of.rendered.class=fully.qualified.name.of.rendering.class
    </pre>

    As in,
    <pre>
    log4j.renderer.my.Fruit=my.FruitRenderer
    </pre>

    <h3>Logger Factories</h3>

    The usage of custom logger factories is discouraged and no longer
    documented.

    <h3>Resetting Hierarchy</h3>

    The hierarchy will be reset before configuration when
    log4j.reset=true is present in the properties file.

    <h3>Example</h3>

    <p>An example configuration is given below. Other configuration
    file examples are given in the <code>examples</code> folder.

    <pre>

    # Set options for appender named "A1".
    # Appender "A1" will be a SyslogAppender
    log4j.appender.A1=org.apache.log4j.net.SyslogAppender

    # The syslog daemon resides on www.abc.net
    log4j.appender.A1.SyslogHost=www.abc.net

    # A1's layout is a PatternLayout, using the conversion pattern
    # <b>%r %-5p %c{2} %M.%L %x - %m\n</b>. Thus, the log output will
    # include # the relative time since the start of the application in
    # milliseconds, followed by the level of the log request,
    # followed by the two rightmost components of the logger name,
    # followed by the callers method name, followed by the line number,
    # the nested disgnostic context and finally the message itself.
    # Refer to the documentation of {@link PatternLayout} for further information
    # on the syntax of the ConversionPattern key.
    log4j.appender.A1.layout=org.apache.log4j.PatternLayout
    log4j.appender.A1.layout.ConversionPattern=%-4r %-5p %c{2} %M.%L %x - %m\n

    # Set options for appender named "A2"
    # A2 should be a RollingFileAppender, with maximum file size of 10 MB
    # using at most one backup file. A2's layout is TTCC, using the
    # ISO8061 date format with context printing enabled.
    log4j.appender.A2=org.apache.log4j.RollingFileAppender
    log4j.appender.A2.MaxFileSize=10MB
    log4j.appender.A2.MaxBackupIndex=1
    log4j.appender.A2.layout=org.apache.log4j.TTCCLayout
    log4j.appender.A2.layout.ContextPrinting=enabled
    log4j.appender.A2.layout.DateFormat=ISO8601

    # Root logger set to DEBUG using the A2 appender defined above.
    log4j.rootLogger=DEBUG, A2

    # Logger definitions:
    # The SECURITY logger inherits is level from root. However, it's output
    # will go to A1 appender defined above. It's additivity is non-cumulative.
    log4j.logger.SECURITY=INHERIT, A1
    log4j.additivity.SECURITY=false

    # Only warnings or above will be logged for the logger "SECURITY.access".
    # Output will go to A1.
    log4j.logger.SECURITY.access=WARN


    # The logger "class.of.the.day" inherits its level from the
    # logger hierarchy.  Output will go to the appender's of the root
    # logger, A2 in this case.
    log4j.logger.class.of.the.day=INHERIT
    </pre>

    <p>Refer to the <b>setOption</b> method in each Appender and
    Layout for class specific options.

    <p>Use the <code>#</code> or <code>!</code> characters at the
    beginning of a line for comments.

   <p>
   @param configFileName The name of the configuration file where the
   configuration information is stored.

  */
public void doConfigure(String configFileName, LoggerRepository hierarchy) {
    Properties props = new Properties();
    FileInputStream istream = null;
    try {
        istream = new FileInputStream(configFileName);
        props.load(istream);
        istream.close();
    } catch (Exception e) {
        LogLog.error("Could not read configuration file [" + configFileName + "].", e);
        LogLog.error("Ignoring configuration file [" + configFileName + "].");
        return;
    } finally {
        if (istream != null) {
            try {
                istream.close();
            } catch (Throwable ignore) {
            }
        }
    }
    doConfigure(props, hierarchy);
}
