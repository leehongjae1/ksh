@echo off
setlocal
 
set SERVICE_NAME=CmsPcSubscriber

set PR_INSTALL=%~dp0%SERVICE_NAME%.exe
 
REM current file
set "SELF=%~dp0%SERVICE_NAME%.bat"
REM current directory
set "CURRENT_DIR=%cd%"
 
REM start - This takes the input from installService and places it between x's
REM       - if there are none then you get xx as a null check
if "x%1x" == "xx" goto displayUsage
set SERVICE_CMD=%1
REM ahift moves to next field
shift
if "x%1x" == "xx" goto checkServiceCmd

:checkServiceCmd
if /i %SERVICE_CMD% == install goto doInstall
if /i %SERVICE_CMD% == remove goto doRemove
if /i %SERVICE_CMD% == uninstall goto doRemove
echo Unknown parameter "%SERVICE_CMD%"

:displayUsage
echo.
echo Usage: CmsPcSubscriber.bat install/remove
goto end

:doRemove
echo Removing the service '%PR_INSTALL%' '%SERVICE_NAME%' ...
%PR_INSTALL% //DS//%SERVICE_NAME%
if not errorlevel 1 goto removed
echo Failed removing '%SERVICE_NAME%' service
goto end

:removed
echo The service '%SERVICE_NAME%' has been removed
goto end

:doInstall
echo Installing the service '%PR_INSTALL%' '%SERVICE_NAME%' ...
%PR_INSTALL% //IS//%SERVICE_NAME% ^
    --Install=%PR_INSTALL% ^
    --Description="%SERVICE_NAME% Service" ^
    --LogPrefix=%SERVICE_NAME% ^
    --LogPath=%~dp0%\ ^
    --LogLevel=Debug ^
    --StdOutput=%~dp0%\stdout.txt ^
    --StdError=%~dp0%\stderr.txt ^
    --Jvm=auto ^
    --Classpath=%SERVICE_NAME%.jar ^
    --Startup=auto ^
    --StartMode=jvm ^
    --StartClass=com.choistec.cmspc.subscriber.InitKt ^
    --StartMethod=main ^
    --StartParams=-yml; ^
    --StopMode=jvm ^
    --StopClass=com.choistec.cmspc.subscriber.InitKt ^
    --StopMethod=stop ^
    --StopTimeout=120 ^
    --JvmMs=256 ^
    --JvmMx=1024 ^
    --JvmSs=4000

goto end

:end
echo Exiting service.bat ...
cd "%CURRENT_DIR%"