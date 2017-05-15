set TCP_LISTEN=0.0.0.0
set TCP_PORT=9001

set _PRG_DIR=%~dp0
set APP_HOME=%_PRG_DIR%\data

rem If the JRE which configured with environment variables does not satisfy the minimal requirement of this app,
rem change the _JRE_HOME variable to a fixed path.
set JRE_HOME=%JAVA_HOME%

rem More arguments passed to spring boot or jvm
set EXTRA_ARGS=""